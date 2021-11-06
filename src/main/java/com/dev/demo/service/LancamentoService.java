package com.dev.demo.service;

import java.util.Optional;

import com.dev.demo.model.Lancamento;
import com.dev.demo.model.Pessoa;
import com.dev.demo.repository.LancamentoRepository;
import com.dev.demo.repository.PessoaRepository;
import com.dev.demo.service.exception.PessoaInexistenteOuInativaException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class LancamentoService {

    @Autowired
    private LancamentoRepository lancamentoRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    public Lancamento atualizar(Long codigo, Lancamento lancamento) {

        Optional<Lancamento> resultUser = lancamentoRepository.findById(codigo);

        if (resultUser.isEmpty()) {
            throw new EmptyResultDataAccessException(1);
        }

        Lancamento lancamentoSalvo = resultUser.get();
        BeanUtils.copyProperties(lancamento, lancamentoSalvo, "codigo");

        return lancamentoRepository.save(lancamentoSalvo);
    }

    public Lancamento salvar(Lancamento lancamento) {

        Optional<Pessoa> pessoa = pessoaRepository.findById(lancamento.getPessoa().getCodigo());

        if (pessoa.isEmpty() || pessoa.get().isInativo()) {

            throw new PessoaInexistenteOuInativaException();
        }

        return lancamentoRepository.save(lancamento);
    }
}
