package com.example.domain.ecommerce.dto.request;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductRequest {

    private String nombre;
    private String descripcion;
    private String categoria;
    private String precioVenta;
    private String stock;
    private String imagen;
    private String proveedor;
    private String marca;
    private String precioCompra;
    private String codigoBarras;
    private String peso;
    private String estado;
    private String comentario;
    private Map<String, String> detail;
}
