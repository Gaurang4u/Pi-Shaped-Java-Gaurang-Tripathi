package com.code.spring.taskmanagement.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtHelper jwtHelper;
    private final ApplicationContext applicationContext;
    private UserDetailsService userDetailsService;

    @Autowired
    public JwtAuthenticationFilter(JwtHelper jwtHelper, ApplicationContext applicationContext) {
        this.jwtHelper = jwtHelper;
        this.applicationContext = applicationContext;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (userDetailsService == null) {
            userDetailsService = applicationContext.getBean(UserDetailsService.class);
        }

        String requestURI = request.getRequestURI();

        // Skipping JWT validation for OAuth2 endpoints
        if (requestURI.startsWith("/oauth2") || requestURI.startsWith("/login/oauth2")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Bearer token handler
        String requestHeader = request.getHeader("Authorization");
        log.info("Header :  {}", requestHeader);
        String username = null;
        String token = null;

        if (requestHeader != null && requestHeader.startsWith("Bearer")) {
            token = requestHeader.substring(7);
            try {
                username = this.jwtHelper.getUsernameFromToken(token);
            } catch (IllegalArgumentException e) {
                log.info("Illegal Argument while fetching the username !!", e);
            } catch (ExpiredJwtException e) {
                log.info("Given jwt token is expired !!", e);
            } catch (MalformedJwtException e) {
                log.info("Some change has been made to the token !! Invalid Token", e);
            } catch (Exception e) {
                log.error("JWT Exception: ", e);
            }
        } else {
            log.info("Invalid Header Value !! ");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            boolean validateToken = this.jwtHelper.validateToken(token, userDetails);
            if (validateToken) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                log.info("Validation fails !!");
            }
        }

        filterChain.doFilter(request, response);
    }
}
