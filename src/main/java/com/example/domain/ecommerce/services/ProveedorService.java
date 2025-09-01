package com.example.domain.ecommerce.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.ecommerce.dto.ProveedorDTO;
import com.example.domain.ecommerce.models.entities.Proveedor;
import com.example.domain.ecommerce.models.enums.Estado;
import com.example.domain.ecommerce.repositories.ProveedorDAO;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProveedorService {

    
    private final ProveedorDAO proveedorDAO;

    @Transactional(readOnly = true)
    public Iterable<Proveedor> obtenerProveedores() {
        return proveedorDAO.findAll();
    }

    @Transactional(readOnly = true)
    public List<Proveedor> obtenerProveedoresActivos(){
        List<Proveedor> activos = new ArrayList<>();
        for (Proveedor proveedor : proveedorDAO.findAll()) {
            if (proveedor.getEstado().equals(Estado.ACTIVO)) {
                activos.add(proveedor);
            }
        }
        return activos;
    }

    @Transactional
    public void createProv(ProveedorDTO proveedorDTO) {
        Proveedor nuevo_proveedor = new Proveedor();
        nuevo_proveedor.setRuc(proveedorDTO.getRuc());
        nuevo_proveedor.setNombre(proveedorDTO.getNombre());
        nuevo_proveedor.setEmail(proveedorDTO.getEmail());
        nuevo_proveedor.setTelefono(proveedorDTO.getTelefono());
        nuevo_proveedor.setEstado(Estado.ACTIVO);

        proveedorDAO.save(nuevo_proveedor);

    }

    @Transactional
    public void updateProv(ProveedorDTO proveedorDTO, int id) {

        Optional<Proveedor> prov = proveedorDAO.findById(Long.valueOf(id));

        if (prov.isEmpty()) {
            throw new EntityNotFoundException("Usuario con id " + id + " no encontrado");

        }

        Proveedor proveedor = prov.get();
        proveedor.setRuc(proveedorDTO.getRuc());
        proveedor.setNombre(proveedorDTO.getNombre());
        proveedor.setEmail(proveedorDTO.getEmail());
        proveedor.setTelefono(proveedorDTO.getTelefono());
        proveedor.setComentario(proveedorDTO.getComentario());

        if (proveedor.getEstado() != null) {
            if (proveedorDTO.getEstado().equals("ACTIVO")) {
                proveedor.setEstado(Estado.ACTIVO);
            } else if (proveedorDTO.getEstado().equals("INACTIVO")) {
                proveedor.setEstado(Estado.INACTIVO);
            }
        }

        proveedorDAO.save(proveedor);
    }

    @Transactional
    public void eliminarProveedor(int id) {
        proveedorDAO.deleteById(Long.valueOf(id));
    }

    public boolean nombreExists(String nombre){
        return proveedorDAO.findByNombre(nombre).isPresent();
    }

    public boolean emailExists(String email){
        return proveedorDAO.findByEmail(email).isPresent();
    }

    public boolean rucExists(String ruc){
        return proveedorDAO.findByRuc(Long.valueOf(ruc)).isPresent();
    }

    public boolean telefonoExists(String telefono){
        return proveedorDAO.findByTelefono(Integer.parseInt(telefono)).isPresent();
    }

}
