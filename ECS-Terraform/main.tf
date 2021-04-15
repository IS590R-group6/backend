terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "3.29.0"
    }
  }
}

//variables set in terraform.tfvars which is in .gitignore
variable "awsAccessKey" {
  description = "AWS Access Key"
  type        = string
}
variable "awsSecretKey" {
  description = "AWS Secret Key"
  type        = string
}
variable "dbUsername" {
  description = "The username for the DB master user"
  type        = string
}
variable "dbPassword" {
  description = "The password for the DB master user"
  type        = string
}

provider "aws" {
  region     = "us-east-1"
  access_key = var.awsAccessKey
  secret_key = var.awsSecretKey
}

/*************************/
/*** Network Resources ***/
/*************************/
resource "aws_vpc" "vpc" {
  cidr_block           = "10.0.0.0/24"
  enable_dns_support   = true
  enable_dns_hostnames = true
  tags = {
    Name = "Terraform VPC"
  }
}

resource "aws_internet_gateway" "internet_gateway" {
  vpc_id = aws_vpc.vpc.id
}

resource "aws_subnet" "pub_subnet1" {
  vpc_id     = aws_vpc.vpc.id
  cidr_block = "10.0.0.0/26"
}

resource "aws_subnet" "pub_subnet2" {
  vpc_id     = aws_vpc.vpc.id
  cidr_block = "10.0.0.64/26"
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.internet_gateway.id
  }
}

resource "aws_route_table_association" "route_table_association1" {
  subnet_id      = aws_subnet.pub_subnet1.id
  route_table_id = aws_route_table.public.id
}

resource "aws_route_table_association" "route_table_association2" {
  subnet_id      = aws_subnet.pub_subnet2.id
  route_table_id = aws_route_table.public.id
}

/*** Security Groups ***/
resource "aws_security_group" "ecs_sg" {
  vpc_id = aws_vpc.vpc.id

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress{
    protocol = "tcp"
    from_port = 80
    to_port = 80
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress{
    protocol = "tcp"
    from_port = 8080
    to_port = 8080
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 65535
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
}


resource "aws_security_group" "rds_sg" {//
  vpc_id = aws_vpc.vpc.id

  ingress {
    protocol        = "tcp"
    from_port       = 5432 //default port for PostgreSQL
    to_port         = 5432
    cidr_blocks     = ["0.0.0.0/0"]
    security_groups = [aws_security_group.ecs_sg.id] //this allows the ECS cluster to access the DB
  }

  egress {
    from_port   = 0
    to_port     = 65535
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

/*************************/
/*** AutoScaling Group ***/
/*************************/

/*** IAM policy Resources ***/
data "aws_iam_policy_document" "ecs_agent" {//
  statement {
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["ec2.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "ecs_agent" {//
  name               = "ecs-agent"
  assume_role_policy = data.aws_iam_policy_document.ecs_agent.json
}


resource "aws_iam_role_policy_attachment" "ecs_agent" {//
  role       = aws_iam_role.ecs_agent.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role"
}

resource "aws_iam_instance_profile" "ecs_agent" {//
  name = "ecs-agent"
  role = aws_iam_role.ecs_agent.name
}

/*** AutoScaling group ***/
resource "aws_launch_configuration" "ecs_launch_config" {//
  image_id             = "ami-0ec7896dee795dfa9"
  iam_instance_profile = aws_iam_instance_profile.ecs_agent.name
  security_groups      = [aws_security_group.ecs_sg.id]
  user_data            = "#!/bin/bash\necho ECS_CLUSTER=my-cluster >> /etc/ecs/ecs.config"
  instance_type        = "t2.micro"
  associate_public_ip_address = true
  name_prefix = "is590"
  lifecycle {
    create_before_destroy = true
  }
  key_name = "main-key"
}

resource "aws_autoscaling_group" "failure_analysis_ecs_asg" {//
  name                 = "asg"
  vpc_zone_identifier  = [aws_subnet.pub_subnet1.id, aws_subnet.pub_subnet2.id]
  launch_configuration = aws_launch_configuration.ecs_launch_config.name

  desired_capacity          = 1
  min_size                  = 1
  max_size                  = 10
  health_check_grace_period = 300
  health_check_type         = "EC2"
  lifecycle {
    create_before_destroy = true
  }
}

/*** DB Instance ***/
resource "aws_db_subnet_group" "db_subnet_group" {
  subnet_ids = [aws_subnet.pub_subnet1.id, aws_subnet.pub_subnet2.id]
}

//750 Hours of db.t2.micro free each month
resource "aws_db_instance" "postgres" {
  identifier                = "postgres"
  allocated_storage         = 5
  multi_az                  = false
  engine                    = "postgres"
  engine_version            = "12.5"
  instance_class            = "db.t2.micro"
  name                      = "worker_db"
  username                  = var.dbUsername
  password                  = var.dbPassword
  port                      = "5432"
  db_subnet_group_name      = aws_db_subnet_group.db_subnet_group.id
  vpc_security_group_ids    = [aws_security_group.rds_sg.id, aws_security_group.ecs_sg.id]
  skip_final_snapshot       = true
  final_snapshot_identifier = "worker-final"
  publicly_accessible       = true
  apply_immediately = true
}

/*** ECS ***/
resource "aws_ecr_repository" "project590repo" {//
  name = "project590"
}

resource "aws_ecs_cluster" "ecs_cluster" {//
  name = "my-cluster"
}

resource "aws_ecs_task_definition" "task_definition" {//
  family                = "worker"
  container_definitions = <<DEFINITION
  [
  {
    "essential": true,
    "memory": 512,
    "name": "worker",
    "cpu": 2,
    "image": "${aws_ecr_repository.project590repo.repository_url}:latest",
    "environment": [],
    "portMappings": [
      {
        "containerPort": 8080,
        "hostPort": 8080
      }
    ]
  }
]
  DEFINITION
}

resource "aws_ecs_service" "worker" {//
  name            = "worker"
  cluster         = aws_ecs_cluster.ecs_cluster.id
  task_definition = aws_ecs_task_definition.task_definition.arn
  desired_count   = 1
  deployment_minimum_healthy_percent = 0
  deployment_maximum_percent = 100
  load_balancer {
    target_group_arn = aws_lb_target_group.my-target-group.arn
    container_name   = "worker"
    container_port   = 8080
  }
}

//target group
resource "aws_lb_target_group" "my-target-group" {//
  name     = "my-target-group"
  port     = 80
  protocol = "HTTP"
  vpc_id   = aws_vpc.vpc.id
  depends_on = [aws_lb.my-ALB]
  health_check {
    path = "/"
    matcher = 200
  }
}

//load balancer
resource "aws_lb" "my-ALB" {//
  name               = "my-ALB"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.ecs_sg.id]
  subnets            = [aws_subnet.pub_subnet1.id, aws_subnet.pub_subnet2.id]
}

resource "aws_lb_listener" "my-listener" {
  load_balancer_arn = aws_lb.my-ALB.arn
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.my-target-group.arn
  }
}


/*** outputs ***/
output "postgres_endpoint" {
  value = aws_db_instance.postgres.endpoint
}

output "ecr_repository_worker_endpoint" {
  value = aws_ecr_repository.project590repo.repository_url
}

output "ecr_repository_worker_name" {
  value = aws_ecr_repository.project590repo.name
}
