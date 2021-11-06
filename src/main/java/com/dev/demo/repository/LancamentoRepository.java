package com.dev.demo.repository;

import com.dev.demo.model.Lancamento;
import com.dev.demo.repository.lancamento.LancamentoRepositoryQuery;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>, LancamentoRepositoryQuery{
    
}
