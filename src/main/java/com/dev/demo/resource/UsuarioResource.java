package com.dev.demo.resource;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.dev.demo.event.RecursoCriadoEvent;
import com.dev.demo.model.Permissao;
import com.dev.demo.model.Usuario;
import com.dev.demo.repository.UsuarioRepository;
import com.dev.demo.service.UsuarioService;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/usuarios")
public class UsuarioResource {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    public ApplicationEventPublisher publisher;

    @GetMapping
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    @GetMapping("/{email}")
    public ResponseEntity<Usuario> buscarPeloEmail(@PathVariable String email) {
        Usuario usuario = usuarioService.buscarUsuarioPeloEmail(email);

        return ResponseEntity.status(HttpStatus.OK).body(usuario);
    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws JsonGenerationException, JsonMappingException, IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {

            try {
                String refresh_token = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refresh_token);
                String username = decodedJWT.getSubject();
                Usuario usuario = usuarioService.buscarUsuarioPeloEmail(username);

                String access_token = JWT.create()
                .withSubject(usuario.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("permissoes", usuario.getPermisssoes().stream().map(Permissao::getDescricao).collect(Collectors.toList()))
                .sign(algorithm);                

                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refresh_token);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);

            } catch (Exception exception) {
                log.error("Erro ao logar no refresh {} ", exception.getMessage());
                response.setHeader("Error", exception.getMessage());
                response.setStatus(FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("Error message", exception.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(),error);
            }
        } else {
            throw new RuntimeException("Refresh Token está ausente");
        }

        

    }

    @PostMapping
    public ResponseEntity<Usuario> criar(@Valid @RequestBody Usuario usuario, HttpServletResponse response) {

        // Usuario usuarioSalvo = usuarioService.salvarUsuario(usuario);
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        publisher.publishEvent(new RecursoCriadoEvent(this, response, usuarioSalvo.getCodigo()));

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioSalvo);
    }

    @PutMapping("/{email}")
    public ResponseEntity<Usuario> atualizar(@PathVariable String email, @Valid @RequestBody Usuario usuario) {

        Usuario usuarioSalvo = usuarioService.atualizarUsuario(email, usuario);

        return ResponseEntity.status(HttpStatus.OK).body(usuarioSalvo);

    }

    @DeleteMapping
    public void remover(@PathVariable Long codigo) {

        usuarioRepository.deleteById(codigo);
    }

}
