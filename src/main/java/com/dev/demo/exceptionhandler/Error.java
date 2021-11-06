package com.dev.demo.exceptionhandler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Error {

    private String messageUsuario;
    private String messageDesenvolvedor;    
}
