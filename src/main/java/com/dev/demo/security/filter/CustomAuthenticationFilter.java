package com.dev.demo.security.filter;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Autowired
    private AuthenticationManager authenticationManager;

   

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        String username = request.getParameter("email");
        String password = request.getParameter("senha");

        log.info("Username para logar: {} ", username);
        log.info("Senha para logar: {} ", password);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
                password);

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authentication) throws IOException, ServletException {

        User user = (User) authentication.getPrincipal();
        log.info("Usuario principal {}", user);
        log.info("Username do acess token {}", user.getUsername());

        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());

        String access_token = JWT.create()
            .withSubject(user.getUsername())
            .withExpiresAt(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
            .withIssuer(request.getRequestURL().toString())
            .withClaim("permissoes", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
            .sign(algorithm);

        String refresh_token = JWT.create()
            .withSubject(user.getUsername())
            .withExpiresAt(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
            .withIssuer(request.getRequestURL().toString())
            .sign(algorithm);

        // response.setHeader("access_token", access_token);
        // response.setHeader("refresh_token", refresh_token);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", access_token);
        tokens.put("refresh_token", refresh_token);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.addCookie(adicionarRefreshTokenNoCookie(refresh_token, response, request));
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);

    }

    private Cookie adicionarRefreshTokenNoCookie(String refresh_token, HttpServletResponse response,
            HttpServletRequest request) {

        Cookie cookie = new Cookie("refresh_token", refresh_token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath(request.getContextPath() + "/usuarios/token/refresh");
        cookie.setMaxAge(2592000);
        response.addCookie(cookie);

        return cookie;
    }

}
