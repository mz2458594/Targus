package com.example.domain.ecommerce.controllers.rest;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.ecommerce.dto.RequestDTO;
import com.example.domain.ecommerce.dto.VentaDTO;
import com.example.domain.ecommerce.models.entities.Venta;
import com.example.domain.ecommerce.models.entities.VentaEcommerce;
import com.example.domain.ecommerce.models.entities.VentaInventario;
import com.example.domain.ecommerce.services.VentaService;

import lombok.extern.slf4j.Slf4j;

@RequestMapping("/api/sale")
@RestController
@Slf4j
public class SaleController {
    
    @Autowired
    private VentaService ventaService;

    @GetMapping("/")
    public ResponseEntity<List<Venta>> getSales(){
        return ResponseEntity.ok(ventaService.getVentas());
    }

    @GetMapping("/inventario")
    public ResponseEntity<List<VentaInventario>> getInventorySales(){
        return ResponseEntity.ok(ventaService.getVentaInventario());
    }

    @GetMapping("/ecommerce")
    public ResponseEntity<List<VentaEcommerce>> getEcommerceSales(){
        return ResponseEntity.ok(ventaService.getVentaEcommerce());
    }


    @PostMapping("/filtro")
    public ResponseEntity<List<Venta>> getVentasRango(@RequestBody VentaDTO ventaDTO){
        return ResponseEntity.ok(ventaService.obtenerVentasConFiltro(ventaDTO));
    }

    @PostMapping("/createEcommerce")
    public ResponseEntity<Venta> createEcommerceSale(@RequestBody RequestDTO requestDTO){
        return ResponseEntity.ok(ventaService.crearVentaEcommerce(requestDTO));
    }

    @PostMapping("/createInventory")
    public ResponseEntity<Venta> createInventorySale(@RequestBody RequestDTO requestDTO){
        return ResponseEntity.ok(ventaService.crearVentaInventario(requestDTO));
    }



}
