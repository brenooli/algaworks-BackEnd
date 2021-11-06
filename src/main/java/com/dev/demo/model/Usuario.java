package com.dev.demo.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Long codigo;

    @NotNull
    private String nome;

    @NotNull
    private String email;

    @NotNull
    private String senha;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuario_permissao", joinColumns = @JoinColumn(name = "codigo_usuario"),
     inverseJoinColumns = @JoinColumn(name = "codigo_permissao"))
    private List<Permissao> permisssoes;

}
