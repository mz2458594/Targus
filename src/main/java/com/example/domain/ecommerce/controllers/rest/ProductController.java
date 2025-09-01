package com.example.domain.ecommerce.controllers.rest;

import com.example.domain.ecommerce.dto.ProductFilterDTO;
import com.example.domain.ecommerce.dto.ProductRequest;
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

    @PostMapping("/create")
    public ResponseEntity<Producto> createProduct(@RequestBody ProductRequest productDTO) {

        Producto producto = productosService.agregarProducto(productDTO);

        return ResponseEntity.status(201).body(producto);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Producto> updateProduct(@RequestBody @Valid ProductRequest productDTO, @PathVariable Long id) {
        Producto producto = productosService.actualizarProducto(productDTO, id);
        return ResponseEntity.ok(producto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable int id) {
        productosService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/filter")
    public ResponseEntity<List<Producto>> getProductFilter(@RequestBody ProductFilterDTO productFilterDTO){
        return ResponseEntity.ok(productosService.obtenerProductosConFiltro(productFilterDTO));
    } 

}
