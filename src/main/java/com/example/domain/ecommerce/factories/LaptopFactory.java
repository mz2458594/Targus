package com.example.domain.ecommerce.factories;

import org.springframework.stereotype.Component;
import com.example.domain.ecommerce.dto.ProductDTO;
import com.example.domain.ecommerce.models.entities.Laptop;
import com.example.domain.ecommerce.models.entities.Producto;
import com.example.domain.ecommerce.repositories.ProductoDAO;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class LaptopFactory implements ProductoFactory {

    private final ProductoDAO productoDAO;

    @Override
    public boolean supports(String tipo) {
        return tipo.equalsIgnoreCase("Laptops");
    }

    @Override
    public Producto crearProducto(ProductDTO productDTO) {

        Laptop laptop = new Laptop();

        laptop.setProcesador(productDTO.getProcesador());
        laptop.setTarjetaGrafica(productDTO.getTarjetaGrafica());
        laptop.setSistemaOperativo(productDTO.getSistemaOperativo());
        laptop.setTamañoPantalla(productDTO.getTamañoPantalla());
        laptop.setMemoriaRam(productDTO.getMemoriaRam());
        laptop.setColor(productDTO.getColor());
        laptop.setAlmacenamiento(productDTO.getAlmacenamiento());
        return laptop;
    }

    @Override
    public Producto actualizar(ProductDTO productDTO, int id) {
        Laptop laptop = (Laptop) productoDAO.findById(Long.valueOf(id)).get();

        laptop.setProcesador(productDTO.getProcesador() != null ? productDTO.getProcesador() : laptop.getProcesador());
        laptop.setTarjetaGrafica(
                productDTO.getTarjetaGrafica() != null ? productDTO.getTarjetaGrafica() : laptop.getTarjetaGrafica());
        laptop.setSistemaOperativo(productDTO.getSistemaOperativo() != null ? productDTO.getSistemaOperativo()
                : laptop.getSistemaOperativo());
        laptop.setTamañoPantalla(
                productDTO.getTamañoPantalla() != null ? productDTO.getTamañoPantalla() : laptop.getTamañoPantalla());
        laptop.setMemoriaRam(productDTO.getMemoriaRam() != null ? productDTO.getMemoriaRam() : laptop.getMemoriaRam());
        laptop.setColor(productDTO.getColor() != null ? productDTO.getColor() : laptop.getColor());
        laptop.setAlmacenamiento(
                productDTO.getAlmacenamiento() != null ? productDTO.getAlmacenamiento() : laptop.getAlmacenamiento());

        return laptop;
    }

    @Override
    public Producto obtener(Long id) {
        Laptop laptop = (Laptop) productoDAO.findById(id).get();
        return laptop;
    }
}
