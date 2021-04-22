package SpringAPI.security;

import static SpringAPI.security.ApplicationUserRole.USER;
import static SpringAPI.security.ApplicationUserRole.ADMIN;

import java.util.Arrays;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import SpringAPI.auth.ApplicationUserService;
import SpringAPI.jwt.JwtConfig;
import SpringAPI.jwt.JwtTokenVerifier;
import SpringAPI.jwt.JwtUsernameAndPasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

	private final PasswordEncoder passwordEncoder;
	private final ApplicationUserService applicationUserService;
	private final SecretKey secretKey;
	private final JwtConfig jwtConfig;

	@Autowired
	public ApplicationSecurityConfig(
					PasswordEncoder passwordEncoder,
					ApplicationUserService applicationUserService,
					SecretKey secretKey,
					JwtConfig jwtConfig) {
		this.passwordEncoder = passwordEncoder;
		this.applicationUserService = applicationUserService;
		this.secretKey = secretKey;
		this.jwtConfig = jwtConfig;
	}

	@Override protected void configure(HttpSecurity http) throws Exception {
		http
						.cors()
						.and()
						.csrf().disable()
						.sessionManagement()
							.sessionCreationPolicy(SessionCreationPolicy.ALWAYS.STATELESS)
						.and()
						.addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(), jwtConfig, secretKey))
						.addFilterAfter(new JwtTokenVerifier(secretKey, jwtConfig), JwtUsernameAndPasswordAuthenticationFilter.class)
						.authorizeRequests()
						.antMatchers("/", "index", "/css/*", "/js/*").permitAll()
						.antMatchers(HttpMethod.POST, "/api/v1/user").permitAll()
						.antMatchers("/api/**").hasRole(USER.name())
						.anyRequest()
						.authenticated();
	}

	@Override protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(daoAuthenticationProvider());
	}

	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(passwordEncoder);
		provider.setUserDetailsService(applicationUserService);
		return provider;
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		final CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000","http://d1k0icii6pntqc.cloudfront.net"));
		configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH"));
		// When using setAllowCredentials(true), we cannot use '*' as the allowed origin header
		configuration.setAllowCredentials(true);
		// setAllowed is important! Without it, Options preflight request
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
		// Needed so that client can access the authorization headers
		configuration.setExposedHeaders(Arrays.asList("Authorization"));
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
