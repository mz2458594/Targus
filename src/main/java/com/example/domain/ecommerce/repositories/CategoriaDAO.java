package com.example.domain.ecommerce.repositories;

import com.example.domain.ecommerce.models.entities.Categoria;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaDAO extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNombre(String nombre);
}
