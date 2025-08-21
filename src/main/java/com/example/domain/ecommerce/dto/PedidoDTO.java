package com.example.domain.ecommerce.dto;

import java.util.List;

import lombok.Data;

@Data
public class PedidoDTO {
    private Long idPedido;
    private String fechaPedido;
    private String estado;
    private double total;
    private List<DetalleDTO> detallePedidos;

    @Data
    public static class DetalleDTO {
        private String nombreProducto;
        private String imagen;
        private int cantidad;
        private String precioVenta;
        private double subtotal;

    }

}
