package com.dev.demo.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.dev.demo.model.Usuario;
import com.dev.demo.repository.UsuarioRepository;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder encoder;

    public Usuario buscarUsuarioPeloCodigo(Long codigo) {

        Optional<Usuario> resultUser = usuarioRepository.findById(codigo);

        if (resultUser == null || resultUser.isEmpty()) {
            throw new EmptyResultDataAccessException(1);
        }

        return resultUser.get();
    }

    public Usuario salvarUsuario(Usuario usuario) {

        usuario.setSenha(encoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    public Usuario buscarUsuarioPeloEmail(String email) {
        Optional<Usuario> resultUser = usuarioRepository.findByEmail(email);

        if (resultUser == null || resultUser.isEmpty()) {
            throw new EmptyResultDataAccessException(1);
        }

        return resultUser.get();
    }

    public Usuario atualizarUsuario(String email, Usuario usuario) {

        Optional<Usuario> resultUser = usuarioRepository.findByEmail(email);

        System.out.println(resultUser);

        if (resultUser == null || resultUser.isEmpty()) {
            throw new EmptyResultDataAccessException(1);
        }

        Usuario usuarioSalvo = resultUser.get();
        BeanUtils.copyProperties(usuario, usuarioSalvo, "codigo");

        return usuarioRepository.save(usuario);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);

        Usuario usuario = usuarioOptional
                .orElseThrow(() -> new UsernameNotFoundException("Usuário e/ou senha inválidos"));

        return new User(usuario.getEmail(), usuario.getSenha(), getPermissoes(usuario));

    }

    private Collection<? extends GrantedAuthority> getPermissoes(Usuario usuario) {

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        usuario.getPermisssoes().forEach(p -> {
            authorities.add(new SimpleGrantedAuthority(p.getDescricao().toUpperCase()));
        });

        return authorities;
    }
}
