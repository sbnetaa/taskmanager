package ru.terentyev.TaskManager.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import ru.terentyev.TaskManager.services.PersonDetailsService;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	private final PersonDetailsService personDetailsService;

	@Autowired
	public WebSecurityConfig(@Lazy PersonDetailsService personDetailsService) {
		this.personDetailsService = personDetailsService;
		}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
	    return new BCryptPasswordEncoder();
	}
	
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
		
		.cors(Customizer.withDefaults())
		.csrf(csrf -> csrf.ignoringRequestMatchers("/api/**")).httpBasic(Customizer.withDefaults()).authorizeHttpRequests((requests) -> requests
				.requestMatchers("/auth/registration", "/auth/login", "/api/v1/auth/registration").anonymous()
				.requestMatchers(HttpMethod.GET, "/api/v1/tasks/**", "/api/v1/tasks", "/tasks").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/v1/tasks/search").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/v1/tasks/**", "/api/v1/tasks").authenticated()
				.requestMatchers(HttpMethod.PATCH, "/api/v1/tasks/**", "/api/v1/tasks").authenticated()
				.requestMatchers(HttpMethod.DELETE, "/api/v1/tasks/**", "/api/v1/tasks").authenticated()
				.requestMatchers("/tasks/add", "/tasks/edit/**", "/tasks/delete/**", "/tasks/comment/**", "/tasks/change-status/**").authenticated()
				
				.anyRequest().permitAll()
			)
			.formLogin((form) -> form
				.loginPage("/auth/login")
				.usernameParameter("username")
	            .passwordParameter("password")
				.defaultSuccessUrl("/tasks/0")
				.loginProcessingUrl("/auth/login")
				.permitAll()
			)
			.logout((logout) -> logout.permitAll().logoutSuccessUrl("/auth/login"));

		return http.build();
	}
	
		
	 
	 protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	     auth.userDetailsService(personDetailsService).passwordEncoder(bCryptPasswordEncoder());
	 }
}
