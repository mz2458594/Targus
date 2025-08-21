package com.example.domain.ecommerce.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class PedidoFilterDTO {
    private Date fechaInicio;
    private Date fechaFinal;
    private Long idResponsable;
    private String tipoPedido;
    private String estado;
    private String idProveedor;
}
