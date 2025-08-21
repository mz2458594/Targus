package com.example.domain.ecommerce.factories;

import org.springframework.stereotype.Component;

import com.example.domain.ecommerce.dto.ProductDTO;
import com.example.domain.ecommerce.models.entities.Producto;
import com.example.domain.ecommerce.models.entities.Tablet;
import com.example.domain.ecommerce.repositories.ProductoDAO;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class TabletFactory implements ProductoFactory {
    private final ProductoDAO productoDAO;

    @Override
    public boolean supports(String tipo) {
        return tipo.equalsIgnoreCase("Tablets");
    }

    @Override
    public Producto crearProducto(ProductDTO productDTO) {

        Tablet tablet = new Tablet();

        tablet.setTamañoPantalla(productDTO.getTamañoPantalla());
        tablet.setMemoriaRam(productDTO.getMemoriaRam());
        tablet.setAlmacenamientoInterno(productDTO.getAlmacenamientoInterno());
        tablet.setResolucionCamara(productDTO.getResolucionCamara());
        tablet.setSistemaOperativo(productDTO.getSistemaOperativo());

        return tablet;
    }

    @Override
    public Producto actualizar(ProductDTO productDTO, int id) {
        Tablet tablet = (Tablet) productoDAO.findById(Long.valueOf(id)).get();

        tablet.setTamañoPantalla(productDTO.getTamañoPantalla() != null ? productDTO.getTamañoPantalla() : tablet.getTamañoPantalla());
        tablet.setMemoriaRam(productDTO.getMemoriaRam() != null ? productDTO.getMemoriaRam() : tablet.getMemoriaRam());
        tablet.setAlmacenamientoInterno(productDTO.getAlmacenamientoInterno() != null ? productDTO.getAlmacenamientoInterno(): tablet.getAlmacenamientoInterno());
        tablet.setResolucionCamara(productDTO.getResolucionCamara() != null ? productDTO.getResolucionCamara() : tablet.getResolucionCamara());
        tablet.setSistemaOperativo(productDTO.getSistemaOperativo() != null ? productDTO.getSistemaOperativo() : tablet.getSistemaOperativo());

        return tablet;
    }

    @Override
    public Producto obtener(Long id){
        Tablet tablet = (Tablet) productoDAO.findById(id).get();
        return tablet;
    }

}
