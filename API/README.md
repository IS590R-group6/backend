Instructions for booting up
---------------------------
1. Open the project:
	a. Open intelliJ
	b. File > Open
	c. Find and click the pom.xml in your project and click "Open as project".  This should be in your local git repository so if you don't have that, you'll need to pull first.

2. Start the database by doing the following:
	a. Open a terminal in IntelliJ (It's one of the tabs in the bottom left) and type the following
	b. docker run --name postgresdb -e POSTGRES_PASSWORD=group6password -d -p 5432:5432 postgres:alpine
		Note: after you are finished, you can stop this container with "docker stop postgresdb" and start it again with "docker start postgresdb".  No need to run step b again.
	c. Verify it is running by typing "docker ps"  ("docker ps -a" will show all running and stopped containers)

3. Create the database by doing the following (should only need to do this once, even if you stop and start the container again):
	a. docker exec -it postgresdb bin/bash
	b. psql -U postgres
   		*useful commands: "\l" to list databases, "\c insertDbName" to connect to a db
	c. CREATE DATABASE postgresdb
	d. Note: running the api in step 4 below will cause the database to update with the "user" table.  
   		You can verify this after step 4 by coming back here and typing "\c postgresdb" to connect to the db and then "\d" to show tables.


3. Start the API by doing the following:
	a. With the project open, click the run button in the top right corner (green triangle)
	b. Verify it is working by going to http://localhost:8080/api/v1/user in your browser.  You should see an empty array
   	c. I shared a postman collection with you.  Use those requests to perform CRUD operations



