package com.example.domain.ecommerce.factories;

import org.springframework.stereotype.Component;

import com.example.domain.ecommerce.dto.ProductDTO;
import com.example.domain.ecommerce.models.entities.Producto;
import com.example.domain.ecommerce.models.entities.Smartphone;
import com.example.domain.ecommerce.repositories.ProductoDAO;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class SmarthphoneFactory implements ProductoFactory {

    private final ProductoDAO productoDAO;

    @Override
    public boolean supports(String tipo) {
        return tipo.equalsIgnoreCase("Smartphones");
    }

    @Override
    public Producto crearProducto(ProductDTO productDTO) {

        Smartphone smartphone = new Smartphone();

        smartphone.setTamañoPantalla(productDTO.getTamañoPantalla());
        smartphone.setMemoriaRam(productDTO.getMemoriaRam());
        smartphone.setAlmacenamientoInterno(productDTO.getAlmacenamientoInterno());
        smartphone.setResolucionCamara(productDTO.getResolucionCamara());
        smartphone.setCapacidadBateria(productDTO.getCapacidadBateria());
        smartphone.setSistemaOperativo(productDTO.getSistemaOperativo());

        return smartphone;
    }

    @Override
    public Producto actualizar(ProductDTO productDTO, int id) {
        Smartphone smartphone = (Smartphone) productoDAO.findById(Long.valueOf(id)).get();

        smartphone.setTamañoPantalla(productDTO.getTamañoPantalla() != null ? productDTO.getTamañoPantalla() : smartphone.getTamañoPantalla());
        smartphone.setMemoriaRam(productDTO.getMemoriaRam() != null ? productDTO.getMemoriaRam() : smartphone.getMemoriaRam());
        smartphone.setAlmacenamientoInterno(productDTO.getAlmacenamientoInterno()!= null ? productDTO.getAlmacenamientoInterno() : smartphone.getAlmacenamientoInterno());
        smartphone.setResolucionCamara(productDTO.getResolucionCamara()!= null ? productDTO.getResolucionCamara() : smartphone.getResolucionCamara());
        smartphone.setCapacidadBateria(productDTO.getCapacidadBateria() != null ? productDTO.getCapacidadBateria() : smartphone.getCapacidadBateria());
        smartphone.setSistemaOperativo(productDTO.getSistemaOperativo() != null ? productDTO.getSistemaOperativo() : smartphone.getSistemaOperativo());

        return smartphone;
    }

    @Override
    public Producto obtener(Long id){
        Smartphone smartphone = (Smartphone) productoDAO.findById(id).get();
        return smartphone;
    }

}
