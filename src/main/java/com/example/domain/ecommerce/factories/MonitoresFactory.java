package com.example.domain.ecommerce.factories;

import org.springframework.stereotype.Component;

import com.example.domain.ecommerce.dto.ProductDTO;
import com.example.domain.ecommerce.models.entities.Laptop;
import com.example.domain.ecommerce.models.entities.Monitor;
import com.example.domain.ecommerce.models.entities.Producto;
import com.example.domain.ecommerce.repositories.ProductoDAO;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class MonitoresFactory implements ProductoFactory {
    private final ProductoDAO productoDAO;

    @Override
    public boolean supports(String tipo) {
        return tipo.equalsIgnoreCase("Monitores");
    }

    @Override
    public Producto crearProducto(ProductDTO productDTO) {

        Monitor monitor = new Monitor();

        monitor.setTamañoPantalla(productDTO.getTamañoPantalla());
        monitor.setResolución(productDTO.getResolución());
        monitor.setTipoPanel(productDTO.getTipoPanel());
        monitor.setFrecuenciaActualizacion(productDTO.getFrecuenciaActualizacion());
        monitor.setPuertosEntrada(productDTO.getPuertosEntrada());

        return monitor;
    }

    @Override
    public Producto actualizar(ProductDTO productDTO, int id) {
        Monitor monitor = (Monitor) productoDAO.findById(Long.valueOf(id)).get();

        monitor.setTamañoPantalla(productDTO.getTamañoPantalla() != null ? productDTO.getTamañoPantalla() : monitor.getTamañoPantalla());
        monitor.setResolución(productDTO.getResolución() != null ? productDTO.getResolución() : monitor.getResolución());
        monitor.setTipoPanel(productDTO.getTipoPanel() != null ? productDTO.getTipoPanel() : monitor.getTipoPanel());
        monitor.setFrecuenciaActualizacion(productDTO.getFrecuenciaActualizacion() != null ? productDTO.getFrecuenciaActualizacion() : monitor.getFrecuenciaActualizacion());
        monitor.setPuertosEntrada(productDTO.getPuertosEntrada() != null ? productDTO.getPuertosEntrada() : monitor.getPuertosEntrada());

        return monitor;
    }

    @Override
    public Producto obtener(Long id){
        Monitor monitor = (Monitor) productoDAO.findById(id).get();
        return monitor;
    }
}
