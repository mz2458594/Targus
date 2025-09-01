package com.example.domain.ecommerce.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.ecommerce.dto.CategoriaDTO;
import com.example.domain.ecommerce.models.entities.Categoria;
import com.example.domain.ecommerce.repositories.CategoriaDAO;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CategoriaService {

    private final CategoriaDAO categoriaDAO;

    @Transactional(readOnly = true)
    public List<Categoria> obtenerCategorias() {
        return categoriaDAO.findAll();
    }

    @Transactional
    public Categoria createCategory(CategoriaDTO categoriaDTO) {
        Categoria nueva_categoria = new Categoria();
        nueva_categoria.setNombre(categoriaDTO.getNombre());
        nueva_categoria.setDescripcion(categoriaDTO.getDescripcion());
        nueva_categoria.setImagen(categoriaDTO.getImagen());

        return categoriaDAO.save(nueva_categoria);

    }

    @Transactional
    public Categoria updateCategoria(CategoriaDTO cate, int id) {

        Optional<Categoria> cat = categoriaDAO.findById(Long.valueOf(id));

        if (cat.isEmpty()) {
            throw new EntityNotFoundException("Categoria con id " + id + " no encontrado");

        }

        Categoria categoria = cat.get();
        categoria.setNombre(cate.getNombre());
        categoria.setDescripcion(cate.getDescripcion());
        categoria.setImagen(cate.getImagen());

        return categoriaDAO.save(categoria);
    }

    @Transactional
    public void eliminarCategoria(int id) {
        categoriaDAO.deleteById(Long.valueOf(id));
    }

}
