package com.example.domain.ecommerce.controllers.rest;

import com.example.domain.ecommerce.dto.ProductFilterDTO;
import com.example.domain.ecommerce.dto.request.ProductRequest;
import com.example.domain.ecommerce.models.entities.Producto;
import com.example.domain.ecommerce.services.ProductoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/products")
@RestController
@Slf4j
public class ProductController {
    @Autowired
    private ProductoService productosService;

    @GetMapping("/")
    public ResponseEntity<List<Producto>> getAllProducts() {
        return ResponseEntity.ok(productosService.listarProducto());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productosService.obtenerProductoPorId(id));
    }

    @GetMapping("/actives")
    public ResponseEntity<List<Producto>> getActiveProducts(){
        return ResponseEntity.ok(productosService.obtenerProductosActivos());
    }

    @GetMapping("/lowStock")
    public ResponseEntity<List<Producto>> getLowStock(){
        return ResponseEntity.ok(productosService.obtenerStockBajo());
    }

    @PostMapping("/create")
    public ResponseEntity<Producto> createProduct(@RequestBody ProductRequest productDTO) {

        Producto producto = productosService.agregarProducto(productDTO);

        return ResponseEntity.status(201).body(producto);
    }

    @PostMapping("/filter")
    public ResponseEntity<List<Producto>> getProductFilter(@RequestBody ProductFilterDTO productFilterDTO){
        return ResponseEntity.ok(productosService.obtenerProductosConFiltro(productFilterDTO));
    } 

    @PutMapping("/update/{id}")
    public ResponseEntity<Producto> updateProduct(@RequestBody @Valid ProductRequest productDTO, @PathVariable Long id) {
        Producto producto = productosService.actualizarProducto(productDTO, id);
        return ResponseEntity.ok(producto);
    }

    @PutMapping("/stock/{id}")
    public ResponseEntity<String> updateStockProduct(@PathVariable Long id, @RequestBody int quantity){
        productosService.actualizarStock(id, quantity);
        return ResponseEntity.status(200).build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable int id) {
        productosService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }


}
