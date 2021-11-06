package com.dev.demo.resource;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;



import com.dev.demo.event.RecursoCriadoEvent;
import com.dev.demo.model.Categoria;
import com.dev.demo.repository.CategoriaRepository;
import com.dev.demo.service.CategoriaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categorias")
public class CategoriaResource {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_PESQUISAR_CATEGORIA')")
    public List<Categoria> listar() {
        return categoriaRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_CADASTRAR_CATEGORIA')")
    public ResponseEntity<Categoria> criar(@Valid @RequestBody Categoria categoria, HttpServletResponse response) {

        Categoria categoriaSalva = categoriaRepository.save(categoria);
        publisher.publishEvent(new RecursoCriadoEvent(this, response, categoriaSalva.getCodigo()));
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaSalva);
    }

    @GetMapping("/{codigo}")
    @PreAuthorize("hasRole('ROLE_PESQUISAR_CATEGORIA')")
    public Categoria buscarPeloCodigo(@PathVariable Long codigo) {

        Optional<Categoria> resultUser = categoriaRepository.findById(codigo);

        if (resultUser.isEmpty()) {
            throw new EmptyResultDataAccessException(1);
        }
        return resultUser.get();
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long codigo) {
        categoriaRepository.deleteById(codigo);
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<Categoria> atualizar(@PathVariable Long codigo, @Valid @RequestBody Categoria categoria) {

        Categoria categoriaSalva = categoriaService.atualizar(codigo, categoria);

        return ResponseEntity.ok().body(categoriaSalva);
    }
}
