package com.dev.demo.exceptionhandler;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class DemoExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {

        Error erro = new Error();
        erro.setMessageUsuario(messageSource.getMessage("mensagem.invalida", null, LocaleContextHolder.getLocale()));
        erro.setMessageDesenvolvedor(ex.getCause().toString());
        return handleExceptionInternal(ex, erro, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {

        List<Error> erros = criarListaDeErros(ex.getBindingResult());
        return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
    }

    private List<Error> criarListaDeErros(BindingResult bindingResult) {
        List<Error> erros = new ArrayList<>();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            erros.add(new Error(messageSource.getMessage(fieldError, LocaleContextHolder.getLocale()),
                    fieldError.toString()));
        }

        return erros;
    }

    @ExceptionHandler({ EmptyResultDataAccessException.class })
    public ResponseEntity<Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex,
            WebRequest request) {

        Error erro = new Error();
        erro.setMessageUsuario(
                messageSource.getMessage("recurso.nao-encontrado", null, LocaleContextHolder.getLocale()));
        erro.setMessageDesenvolvedor(ex.toString());

        return handleExceptionInternal(ex, erro, new HttpHeaders(), HttpStatus.NOT_FOUND, request);

    }

    @ExceptionHandler({ DataIntegrityViolationException.class })
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex,
            WebRequest request) {

        Error erro = new Error();
        erro.setMessageUsuario(
                messageSource.getMessage("recurso.operacao-nao-permitida", null, LocaleContextHolder.getLocale()));
        erro.setMessageDesenvolvedor(ExceptionUtils.getRootCauseMessage(ex));

        return handleExceptionInternal(ex, erro, new HttpHeaders(), HttpStatus.NOT_FOUND, request);

    }
}
