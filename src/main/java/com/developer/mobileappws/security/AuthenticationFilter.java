package com.developer.mobileappws.security;

import com.developer.mobileappws.SpringApplicationContext;
import com.developer.mobileappws.dto.UserDto;
import com.developer.mobileappws.models.request.UserLoginRequestModel;
import com.developer.mobileappws.services.interfaces.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private final ObjectMapper mapper;

    public AuthenticationFilter(AuthenticationManager authenticationManager,ObjectMapper mapper) {
        this.authenticationManager = authenticationManager;
        this.mapper = mapper;
    }

    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        try {
            UserLoginRequestModel cred = new ObjectMapper()
                    .readValue(req.getInputStream(),UserLoginRequestModel.class);

            UserService userService = (UserService) SpringApplicationContext.getBean("userServiceImpl");
            UserDto userDto = userService.getUser(cred.getEmail());

            Set<SimpleGrantedAuthority> authorities = new HashSet<>();
            authorities.add(new SimpleGrantedAuthority(userDto.getRole()));

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            cred.getEmail(),
                            cred.getPassword(),
                            authorities)
            );
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

//    @Override
//    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) throws IOException, ServletException {
//        String userName = ((User) auth.getPrincipal()).getUsername();
//
//        String authorities = auth.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.joining(","));
//
//        String token = Jwts.builder()
//                .setSubject(userName)
//                .claim(SecurityConstants.AUTHORITIES_KEY, authorities)
//                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
//                .signWith(SignatureAlgorithm.HS256,SecurityConstants.getTokenSecret())
//                .compact();
//
//        UserService userService = (UserService) SpringApplicationContext.getBean("userServiceImpl");
//        UserDto userDto = userService.getUser(userName);
//
//        String tokenRes = SecurityConstants.TOKEN_PREFIX + token;
//        res.addHeader(SecurityConstants.HEADER_STRING,tokenRes);
//        res.addHeader("userId",userDto.getUserId());
//
//        // for sending proper response -- Custom Response
//        Map<String, Object> details = new HashMap<>();
//        details.put("token", tokenRes);
//
//        res.setStatus(HttpStatus.ACCEPTED.value());
//        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        mapper.writeValue(res.getWriter(), details);
//    }
}
