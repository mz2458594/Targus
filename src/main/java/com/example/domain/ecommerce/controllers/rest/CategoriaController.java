package com.example.domain.ecommerce.controllers.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.ecommerce.dto.CategoriaDTO;
import com.example.domain.ecommerce.models.entities.Categoria;
import com.example.domain.ecommerce.services.CategoriaService;

@RequestMapping("/api/category")
public class CategoriaController {
    @Autowired
    private CategoriaService categoriaService;

    @GetMapping("/")
    public ResponseEntity<List<Categoria>> getCategorias() {
        return ResponseEntity.ok(categoriaService.obtenerCategorias());
    }

    @PostMapping("/create")
    public ResponseEntity<Categoria> createCategoria(@RequestBody CategoriaDTO categoriaDTO){
        Categoria categoria = categoriaService.createCategory(categoriaDTO);
        return ResponseEntity.status(201).body(categoria);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> updateProveedor(@RequestBody CategoriaDTO categoriaDTO, @PathVariable int id) {
        try {
            Categoria categoria = categoriaService.updateCategoria(categoriaDTO, id);
            return ResponseEntity.ok(categoria);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategoria(@PathVariable int id) {
        categoriaService.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }
}
