package com.example.domain.ecommerce.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.ecommerce.services.ProveedorService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/provider")
@Slf4j
public class ProvController {
    @Autowired
    private ProveedorService proveedorService;


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

}
