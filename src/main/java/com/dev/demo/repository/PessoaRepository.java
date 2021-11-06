package com.dev.demo.repository;

import com.dev.demo.model.Pessoa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {
    
}
