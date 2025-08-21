package com.example.domain.ecommerce.dto;

import java.util.Date;

import lombok.Data;

@Data
public class UsuarioPersonaDTO {
    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String num_documento;
    private String celular;
    private String calle;
    private String departamento;
    private String distrito;
    private String provincia;
    private String correo;
    private String username;
    private String rol;
    private String estado;
    private String comentario;
    private Date fecha;
}
