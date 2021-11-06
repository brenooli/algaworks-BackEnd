package com.dev.demo.repository;

import java.util.Optional;

import com.dev.demo.model.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

    public Optional<Usuario> findByEmail(String email);
    
}
