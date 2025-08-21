package com.example.domain.ecommerce.factories;

import org.springframework.stereotype.Component;

import com.example.domain.ecommerce.dto.ProductDTO;
import com.example.domain.ecommerce.models.entities.Impresora;
import com.example.domain.ecommerce.models.entities.Producto;
import com.example.domain.ecommerce.repositories.ProductoDAO;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ImpresoraFactory implements ProductoFactory {
    private final ProductoDAO productoDAO;

    @Override
    public boolean supports(String tipo) {
        return tipo.equalsIgnoreCase("Impresoras");
    }

    @Override
    public Producto crearProducto(ProductDTO productDTO) {

        Impresora impresora = new Impresora();

        impresora.setTipo(productDTO.getTipo());
        impresora.setFunciones(productDTO.getFunciones());
        impresora.setConectividad(productDTO.getConectividad());
        impresora.setVelocidadImpresion(productDTO.getVelocidadImpresion());
        impresora.setDobleCaraAutomatica(productDTO.getDobleCaraAutomatica());

        return impresora;
    }

    @Override
    public Producto actualizar(ProductDTO productDTO, int id) {
        Impresora impresora = (Impresora) productoDAO.findById(Long.valueOf(id)).get();

        impresora.setTipo(productDTO.getTipo() != null ? productDTO.getTipo() : impresora.getTipo());
        impresora.setFunciones(productDTO.getFunciones() != null ? productDTO.getFunciones() : impresora.getFunciones());
        impresora.setConectividad(productDTO.getConectividad() != null ? productDTO.getConectividad() : impresora.getConectividad());
        impresora.setVelocidadImpresion(productDTO.getVelocidadImpresion() != null ? productDTO.getVelocidadImpresion() : impresora.getVelocidadImpresion());
        impresora.setDobleCaraAutomatica(productDTO.getDobleCaraAutomatica() != null ? productDTO.getDobleCaraAutomatica() : impresora.getDobleCaraAutomatica());

        return impresora;
    }

    @Override
    public Producto obtener(Long id){
        Impresora impresora = (Impresora) productoDAO.findById(id).get();
        return impresora;
    }
}
