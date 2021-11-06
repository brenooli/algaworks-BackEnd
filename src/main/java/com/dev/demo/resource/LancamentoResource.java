package com.dev.demo.resource;

import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.dev.demo.event.RecursoCriadoEvent;
import com.dev.demo.exceptionhandler.Error;
import com.dev.demo.model.Lancamento;
import com.dev.demo.repository.LancamentoRepository;
import com.dev.demo.repository.filter.LancamentoFilter;
import com.dev.demo.repository.projection.ResumoLancamento;
import com.dev.demo.service.LancamentoService;
import com.dev.demo.service.exception.PessoaInexistenteOuInativaException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoResource {

    @Autowired
    private LancamentoRepository lancamentoRepository;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private LancamentoService lancamentoService;

    @Autowired
    private MessageSource messageSource;

    
    @GetMapping
    @PreAuthorize("hasRole('ROLE_PESQUISAR_LANCAMENTO')")
    public Page<Lancamento> pesquisar(LancamentoFilter lancamentoFilter, Pageable pageable) {
        
        return lancamentoRepository.filtrar(lancamentoFilter, pageable);
    }

    @GetMapping(params = "resumo")
    @PreAuthorize("hasRole('ROLE_PESQUISAR_LANCAMENTO')")
    public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable) {

        return lancamentoRepository.resumir(lancamentoFilter, pageable);
    }
    
    @GetMapping("/{codigo}")
    @PreAuthorize("hasRole('ROLE_PESQUISAR_LANCAMENTO')")
    public Lancamento buscarPeloCodigo(@PathVariable Long codigo) {

        Optional<Lancamento> resultUser = lancamentoRepository.findById(codigo);

        if (resultUser.isEmpty()) {
            throw new EmptyResultDataAccessException(1);
        }

        return resultUser.get();

    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_CADASTRAR_LANCAMENTO')")
    public ResponseEntity<Lancamento> criar(@Valid @RequestBody Lancamento lancamento, HttpServletResponse response) {

        Lancamento lancamentoSalvo = lancamentoService.salvar(lancamento);
        publisher.publishEvent(new RecursoCriadoEvent(this, response, lancamentoSalvo.getCodigo()));

        return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoSalvo);
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<Lancamento> atualizar(@PathVariable Long codigo, @Valid @RequestBody Lancamento lancamento) {

        Lancamento lancamentoSalvo = lancamentoService.atualizar(codigo, lancamento);

        return ResponseEntity.status(HttpStatus.OK).body(lancamentoSalvo);

    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_REMOVER_LANCAMENTO')")
    public void deletar(@PathVariable Long codigo) {

        lancamentoRepository.deleteById(codigo);
    }

    @ExceptionHandler({PessoaInexistenteOuInativaException.class})
    public ResponseEntity<Object> handlePessoaInexistenteOuInativaException(PessoaInexistenteOuInativaException ex) {

        Error erro = new Error();
        erro.setMessageUsuario(
                messageSource.getMessage("pessoa.inexistente-ou-inativa", null, LocaleContextHolder.getLocale()));
        erro.setMessageDesenvolvedor(ex.toString());

        return ResponseEntity.badRequest().body(erro);
    }
}