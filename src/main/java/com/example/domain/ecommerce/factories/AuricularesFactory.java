package com.example.domain.ecommerce.factories;

import org.springframework.stereotype.Component;
import com.example.domain.ecommerce.dto.ProductDTO;
import com.example.domain.ecommerce.models.entities.Auricular;
import com.example.domain.ecommerce.models.entities.Producto;
import com.example.domain.ecommerce.repositories.ProductoDAO;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class AuricularesFactory implements ProductoFactory {

    private final ProductoDAO productoDAO;

    @Override
    public boolean supports(String tipo) {
        return tipo.equalsIgnoreCase("Auriculares");
    }

    @Override
    public Producto crearProducto(ProductDTO productDTO) {

        Auricular auricular = new Auricular();

        auricular.setTipo(productDTO.getTipo());
        auricular.setDuracion(productDTO.getDuracion());
        auricular.setCancelacionRuido(productDTO.getCancelacionRuido());
        auricular.setConector(productDTO.getConector());

        return auricular;
    }

    @Override
    public Producto actualizar(ProductDTO productDTO, int id) {
        Auricular auricular = (Auricular) productoDAO.findById(Long.valueOf(id)).get();

        auricular.setTipo(productDTO.getTipo() != null ? productDTO.getTipo() : auricular.getTipo());
        auricular.setDuracion(productDTO.getDuracion() != null ? productDTO.getDuracion() : auricular.getDuracion());
        auricular.setCancelacionRuido(productDTO.getCancelacionRuido() != null ? productDTO.getCancelacionRuido()
                : auricular.getCancelacionRuido());
        auricular.setConector(productDTO.getConector() != null ? productDTO.getConector() : auricular.getConector());

        return auricular;
    }

    @Override
    public Producto obtener(Long id) {
        Auricular auricular = (Auricular) productoDAO.findById(id).get();
        return auricular;
    }

}
