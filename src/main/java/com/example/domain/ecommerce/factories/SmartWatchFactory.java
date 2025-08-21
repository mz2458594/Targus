package com.example.domain.ecommerce.factories;

import org.springframework.stereotype.Component;

import com.example.domain.ecommerce.dto.ProductDTO;
import com.example.domain.ecommerce.models.entities.Producto;
import com.example.domain.ecommerce.models.entities.Smartwatch;
import com.example.domain.ecommerce.repositories.ProductoDAO;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class SmartWatchFactory implements ProductoFactory {
    private final ProductoDAO productoDAO;

    @Override
    public boolean supports(String tipo) {
        return tipo.equalsIgnoreCase("Smartwatches");
    }

    @Override
    public Producto crearProducto(ProductDTO productDTO) {

        Smartwatch smartwatch = new Smartwatch();

        smartwatch.setCompatibilidad(productDTO.getCompatibilidad());
        smartwatch.setMonitoreoSalud(productDTO.getMonitoreoSalud());
        smartwatch.setResistenciaAgua(productDTO.getResistenciaAgua());
        smartwatch.setDuracion(productDTO.getDuracion());

        return smartwatch;
    }

    @Override
    public Producto actualizar(ProductDTO productDTO, int id) {
        Smartwatch smartwatch = (Smartwatch) productoDAO.findById(Long.valueOf(id)).get();

        smartwatch.setCompatibilidad(productDTO.getCompatibilidad() != null ? productDTO.getCompatibilidad() : smartwatch.getCompatibilidad());
        smartwatch.setMonitoreoSalud(productDTO.getMonitoreoSalud()!= null ? productDTO.getMonitoreoSalud() : smartwatch.getMonitoreoSalud());
        smartwatch.setResistenciaAgua(productDTO.getResistenciaAgua()!= null ? productDTO.getResistenciaAgua() : smartwatch.getResistenciaAgua());
        smartwatch.setDuracion(productDTO.getDuracion() != null ? productDTO.getDuracion() : smartwatch.getDuracion());

        return smartwatch;
    }

    @Override
    public Producto obtener(Long id){
        Smartwatch smartwatch = (Smartwatch) productoDAO.findById(id).get();
        return smartwatch;
    }
}
