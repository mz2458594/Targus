package com.example.domain.ecommerce.factories;

import org.springframework.stereotype.Component;

import com.example.domain.ecommerce.dto.ProductDTO;
import com.example.domain.ecommerce.models.entities.Camara;
import com.example.domain.ecommerce.models.entities.Producto;
import com.example.domain.ecommerce.repositories.ProductoDAO;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CamaraFactory implements ProductoFactory{
    private final ProductoDAO productoDAO;

    @Override
    public boolean supports(String tipo) {
        return tipo.equalsIgnoreCase("Cámaras");
    }

    @Override
    public Producto crearProducto(ProductDTO productDTO) {

        Camara camara = new Camara();

        camara.setTipo(productDTO.getTipo());
        camara.setResolución(productDTO.getResolución());
        camara.setZoomOptico(productDTO.getZoomOptico());
        camara.setEstabilizacionImagen(productDTO.getEstabilizacionImagen());

        return camara;
    }

    @Override
    public Producto actualizar(ProductDTO productDTO, int id) {
        Camara camara = (Camara) productoDAO.findById(Long.valueOf(id)).get();

        camara.setTipo(productDTO.getTipo() != null ? productDTO.getTipo() : camara.getTipo());
        camara.setResolución(productDTO.getResolución() != null ? productDTO.getResolución() : camara.getResolución());
        camara.setZoomOptico(productDTO.getZoomOptico() != null ? productDTO.getZoomOptico() : camara.getZoomOptico());
        camara.setEstabilizacionImagen(productDTO.getEstabilizacionImagen() != null ? productDTO.getEstabilizacionImagen() : camara.getEstabilizacionImagen());

        return camara;
    }

    @Override
    public Producto obtener(Long id){
        Camara camara = (Camara) productoDAO.findById(id).get();
        return camara;
    }
}
