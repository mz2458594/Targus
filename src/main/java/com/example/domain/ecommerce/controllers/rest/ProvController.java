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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.ecommerce.dto.ProveedorDTO;
import com.example.domain.ecommerce.models.entities.Proveedor;
import com.example.domain.ecommerce.services.ProveedorService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/provider")
@Slf4j
public class ProvController {
    @Autowired
    private ProveedorService proveedorService;


    @GetMapping("/")
    public ResponseEntity<List<Proveedor>> getProviders(){
        return ResponseEntity.ok(proveedorService.obtenerProveedores());
    }

    @GetMapping("/actives")
    public ResponseEntity<List<Proveedor>> getActivesProviders(){
        return ResponseEntity.ok(proveedorService.obtenerProveedoresActivos());
    }

    @GetMapping("/ruc")
    public ResponseEntity<Boolean> rucExists(@RequestParam String ruc){
        boolean exists = proveedorService.rucExists(ruc);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/nombre")
    public ResponseEntity<Boolean> nombreExists(@RequestParam String nombre){
        boolean exists = proveedorService.nombreExists(nombre);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/telefono")
    public ResponseEntity<Boolean> telefonoExists(@RequestParam String telefono){
        boolean exists = proveedorService.telefonoExists(telefono);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/email")
    public ResponseEntity<Boolean> emailExists(@RequestParam String email){
        boolean exists = proveedorService.emailExists(email);
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/create")
    public ResponseEntity<Proveedor> createProvider(@RequestBody ProveedorDTO proveedorDTO) {
        Proveedor proveedor = proveedorService.createProv(proveedorDTO);
        return ResponseEntity.status(201).body(proveedor);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Proveedor> updateProvider(@RequestBody ProveedorDTO proveedorDTO, @PathVariable Long id){
        return ResponseEntity.ok(proveedorService.updateProv(proveedorDTO, id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProvider(@PathVariable Long id){
        proveedorService.eliminarProveedor(id);
        return ResponseEntity.noContent().build();
    }


}
