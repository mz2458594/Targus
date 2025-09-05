package com.example.domain.ecommerce.dto;

import java.util.List;

import com.example.domain.ecommerce.models.entities.Producto;

import lombok.Data;

@Data
public class RequestDTO {
    private Long id_usuario;

    private List<ItemsVentaDTO> item;

    private String ruc;

    private String razon;

    private String tipo;

    private float vuelto;
    private float efectivo;

    @Data
    public static class ItemsVentaDTO {
        private Producto producto;
        private int cantidad;
        private float total;
        
    }

}