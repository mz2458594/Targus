package com.example.domain.ecommerce.controllers.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.ecommerce.dto.PersonaFilterDTO;
import com.example.domain.ecommerce.models.entities.Persona;
import com.example.domain.ecommerce.services.PersonaService;

@RequestMapping("/api/person")
@RestController
public class PersonaController {
    
    @Autowired
    private PersonaService personaService;


    @PostMapping("/filter")
    public ResponseEntity<List<Persona>> getPersonFilter(@RequestBody PersonaFilterDTO personaFilterDTO){
        return ResponseEntity.ok(personaService.obtenerPersonasConFiltros(personaFilterDTO));
    }

    @GetMapping("/dni")
    public ResponseEntity<Boolean> dniExists(@RequestParam String dni){
        boolean exist = personaService.dniExists(dni);
        return ResponseEntity.ok(exist);
    }

    @GetMapping("/telefono")
    public ResponseEntity<Boolean> telefonoExists(@RequestParam String telefono){
        boolean exist = personaService.telefonoExists(telefono);
        return ResponseEntity.ok(exist);
    }

}
