package com.dev.demo.service;

import java.util.Optional;

import com.dev.demo.model.Pessoa;
import com.dev.demo.repository.PessoaRepository;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    public Pessoa atualizar(Long codigo, Pessoa pessoa) {
        Optional<Pessoa> resultUser = pessoaRepository.findById(codigo);

        if (resultUser.isEmpty()) {
            throw new EmptyResultDataAccessException(1);
        }
        Pessoa pessoaSalva = resultUser.get();
        BeanUtils.copyProperties(pessoa, pessoaSalva, "codigo");
        return pessoaRepository.save(pessoaSalva);
    }

    public Pessoa buscarPessoaPeloCodigo(Long codigo) {

        Optional<Pessoa> resultUser = pessoaRepository.findById(codigo);

        if (resultUser == null || resultUser.isEmpty()) {
            throw new EmptyResultDataAccessException(1);
        }
        return resultUser.get();
    }

}
