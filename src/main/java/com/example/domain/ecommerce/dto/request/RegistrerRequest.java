package com.example.domain.ecommerce.dto.request;

import java.sql.Date;

import lombok.Data;

@Data
public class RegistrerRequest {
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
    private String contraseña;
    private String rol;
    private String cargo;
    private String estado;
    private Date fecha_nac;
    private String comentario;
}
