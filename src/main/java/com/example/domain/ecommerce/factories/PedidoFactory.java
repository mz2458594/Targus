package com.example.domain.ecommerce.factories;

import com.example.domain.ecommerce.dto.RequestDTO;
import com.example.domain.ecommerce.models.entities.Pedido;

public interface PedidoFactory {
    Pedido crearPedido(RequestDTO data);
}
