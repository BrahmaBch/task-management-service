package com.task.management.service.security;

import com.task.management.service.security.jwt.AuthEntryPointJwt; // Import for unauthorized access handler
import com.task.management.service.security.jwt.AuthTokenFilter; // Import for JWT token filter
import com.task.management.service.security.services.UserDetailsServiceImpl; // Import for user details service implementation
import org.springframework.beans.factory.annotation.Autowired; // Import for dependency injection
import org.springframework.context.annotation.Bean; // Import for Spring configuration
import org.springframework.context.annotation.Configuration; // Import for configuration class
import org.springframework.security.authentication.AuthenticationManager; // Import for authentication manager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider; // Import for authentication provider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; // Import for authentication configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // Import for method security
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // Import for HTTP security configuration
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy; // Import for session creation policies
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Import for password encoding
import org.springframework.security.crypto.password.PasswordEncoder; // Import for password encoder interface
import org.springframework.security.web.SecurityFilterChain; // Import for security filter chain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // Import for username/password authentication filter

/**
 * Security configuration class to set up Spring Security.
 */
@Configuration // Marks the class as a source of bean definitions
@EnableMethodSecurity // Enables method-level security annotations
public class WebSecurityConfig {

  @Autowired
  private UserDetailsServiceImpl userDetailsService; // Injects the user details service for authentication

  @Autowired
  private AuthEntryPointJwt unauthorizedHandler; // Injects the entry point for unauthorized requests

  /**
   * Creates a bean for the authentication JWT token filter.
   *
   * @return AuthTokenFilter instance
   */
  @Bean
  public AuthTokenFilter authenticationJwtTokenFilter() {
    return new AuthTokenFilter(); // Returns a new instance of AuthTokenFilter
  }

  /**
   * Creates a bean for the DAO authentication provider.
   *
   * @return DaoAuthenticationProvider instance
   */
  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(); // Create a new authentication provider

    authProvider.setUserDetailsService(userDetailsService); // Set the user details service
    authProvider.setPasswordEncoder(passwordEncoder()); // Set the password encoder

    return authProvider; // Return the configured authentication provider
  }

  /**
   * Creates a bean for the authentication manager.
   *
   * @param authConfig Authentication configuration
   * @return AuthenticationManager instance
   * @throws Exception if there is an error getting the authentication manager
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager(); // Returns the authentication manager from the configuration
  }

  /**
   * Creates a bean for the password encoder.
   *
   * @return PasswordEncoder instance
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(); // Returns a new instance of BCryptPasswordEncoder
  }

  /**
   * Configures the security filter chain for HTTP requests.
   *
   * @param http HttpSecurity configuration
   * @return SecurityFilterChain instance
   * @throws Exception if there is an error configuring the security filter chain
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
            .csrf(csrf -> csrf.disable()) // Disable CSRF protection
            .exceptionHandling(exceptionHandling ->
                    exceptionHandling.authenticationEntryPoint(unauthorizedHandler)) // Set unauthorized handler
            .sessionManagement(sessionManagement ->
                    sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Set session policy to stateless
            .authorizeRequests(authorizeRequests ->
                    authorizeRequests
                            .antMatchers("/api/auth/**").permitAll() // Allow public access to specific endpoints
                            .antMatchers("/api/test/**").permitAll()
                            .antMatchers("/api/task/**").permitAll()
                            .antMatchers("/api/admin/**").hasRole("ADMIN") // Require ADMIN role for specific endpoints
                            .antMatchers("/api/user/**").hasRole("USER") // Require USER role for specific endpoints
                            .antMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/webjars/**").permitAll() // Allow access to Swagger UI and docs
                            .anyRequest().authenticated()); // Require authentication for any other request

    http.authenticationProvider(authenticationProvider()); // Set the authentication provider
    http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class); // Add JWT token filter

    return http.build(); // Build and return the security filter chain
  }

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> web.ignoring()
            .antMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/webjars/**"); // Ignore Swagger-related paths from security
  }
}
