package com.dev.demo.repository.lancamento;

import com.dev.demo.model.Lancamento;
import com.dev.demo.repository.filter.LancamentoFilter;
import com.dev.demo.repository.projection.ResumoLancamento;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LancamentoRepositoryQuery {

    public Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable);
    public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable);
    
}
