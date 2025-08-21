package com.example.domain.ecommerce.factories;

import com.example.domain.ecommerce.dto.ProductDTO;
import com.example.domain.ecommerce.models.entities.Producto;

public interface ProductoFactory {
    boolean supports(String tipo);
    Producto crearProducto(ProductDTO productDTO);
    Producto actualizar(ProductDTO productDTO, int id);
    Producto obtener(Long id);
}
