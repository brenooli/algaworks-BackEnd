package com.dev.demo.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
@Entity
@Table(name = "permissao")
public class Permissao {

    @Id
    private Long codigo;

    @NotNull
    private String descricao;
    
}
