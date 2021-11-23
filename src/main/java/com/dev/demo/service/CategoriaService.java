package com.dev.demo.service;

import java.util.Optional;

import com.dev.demo.model.Categoria;
import com.dev.demo.repository.CategoriaRepository;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class CategoriaService 

    @Autowired
    private CategoriaRepository categoriaRepository;

    public Categoria atualizar(Long codigo, Categoria categoria) {

        Optional<Categoria> resultUser = categoriaRepository.findById(codigo);
        if (resultUser.isEmpty()) {
            throw new EmptyResultDataAccessException(1);
        }
        Categoria categoriaSalva = resultUser.get();
        BeanUtils.copyProperties(categoria, categoriaSalva, "codigo");
        
        return categoriaRepository.save(categoriaSalva);
    }

}
