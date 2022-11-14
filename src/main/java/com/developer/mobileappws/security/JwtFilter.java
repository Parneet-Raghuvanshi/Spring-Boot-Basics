package com.developer.mobileappws.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

public class JwtFilter extends BasicAuthenticationFilter {

    public JwtFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    public void doFilterInternal(HttpServletRequest req,
                                 HttpServletResponse res,
                                 FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(SecurityConstants.HEADER_STRING);

        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            chain.doFilter(req,res);
            return;
        }

        UsernamePasswordAuthenticationToken authenticationToken = getAuthentication(req);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        chain.doFilter(req,res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(SecurityConstants.HEADER_STRING);

        if (token != null) {
            token = token.replace(SecurityConstants.TOKEN_PREFIX, "");

            final JwtParser jwtParser = Jwts.parser().setSigningKey(SecurityConstants.getTokenSecret());
            final Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);
            final Claims claims = claimsJws.getBody();

            String user = claims.getSubject();
            final Collection<? extends GrantedAuthority> authorities =
                    Arrays.stream(claims.get(SecurityConstants.AUTHORITIES_KEY).toString().split(","))
                            .map(SimpleGrantedAuthority::new).toList();

            if (user != null && !authorities.isEmpty()) {
                return new UsernamePasswordAuthenticationToken(user, null, authorities);
            }
            return null;
        }
        return null;
    }
}
