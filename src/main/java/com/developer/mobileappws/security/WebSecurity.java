package com.developer.mobileappws.security;

import com.developer.mobileappws.SpringApplicationContext;
import com.developer.mobileappws.dto.UserDto;
import com.developer.mobileappws.services.interfaces.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private final UserService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public WebSecurity(UserService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
                .csrf().disable()
//                .ignoringAntMatchers(SecurityConstants.TEST_URL,
//                        SecurityConstants.SIGNUP_URL,
//                        SecurityConstants.LOGIN_URL)
//                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/customer").hasRole("CUSTOMER")
                .antMatchers(HttpMethod.GET, "/admin").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, SecurityConstants.TEST_URL).permitAll()
                .antMatchers(HttpMethod.GET, SecurityConstants.VERIFICATION_EMAIL_URL).permitAll()
                .antMatchers(HttpMethod.POST, SecurityConstants.SIGNUP_URL).permitAll()
                .antMatchers(HttpMethod.POST, SecurityConstants.PASSWORD_URL).permitAll()
                .antMatchers(HttpMethod.POST, SecurityConstants.PASSWORD_RESET_URL).permitAll()
                .anyRequest().authenticated()
                .and().addFilter(getAuthenticationFilter())
                .addFilter(new JwtFilter(authenticationManager()))
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    public AuthenticationFilter getAuthenticationFilter() throws Exception {
        final AuthenticationFilter filter = new AuthenticationFilter(authenticationManager(), new ObjectMapper());
        filter.setFilterProcessesUrl(SecurityConstants.LOGIN_URL);
        filter.setAuthenticationSuccessHandler((request, response, authentication) -> {
            System.err.println("AUTH SUCCESS ... ");

            String userName = ((User) authentication.getPrincipal()).getUsername();
            String authorities = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));

            String token = Jwts.builder()
                    .setSubject(userName)
                    .claim(SecurityConstants.AUTHORITIES_KEY, authorities)
                    .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                    .signWith(SignatureAlgorithm.HS256, SecurityConstants.getTokenSecret())
                    .compact();

            UserService userService = (UserService) SpringApplicationContext.getBean("userServiceImpl");
            UserDto userDto = userService.getUser(userName);

            String tokenRes = SecurityConstants.TOKEN_PREFIX + token;
            response.addHeader(SecurityConstants.HEADER_STRING, tokenRes);
            response.addHeader("userId", userDto.getUserId());

            // for sending proper response -- Custom Response
            Map<String, Object> details = new HashMap<>();
            details.put("token", tokenRes);

            // response.setStatus(HttpStatus.OK.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getWriter(), details);
        });
        filter.setAuthenticationFailureHandler((request, response, authentication) -> {
            System.err.println("AUTH FAILED ... ");
            String error = authentication.getClass().getSimpleName();

            // for sending proper response -- Custom Response
            Map<String, Object> details = new HashMap<>();
            details.put("error", error);

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getWriter(), details);
        });
        return filter;
    }

    // Simple Configurations for CORS Policy
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.setAllowedOrigins(List.of("*"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setExposedHeaders(List.of("Authorization"));

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}
