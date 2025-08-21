package com.example.domain.ecommerce.controllers.rest;

import com.example.domain.ecommerce.dto.DireccionDTO;
import com.example.domain.ecommerce.dto.UserDTO;
import com.example.domain.ecommerce.dto.UsuarioPersonaDTO;
import com.example.domain.ecommerce.dto.request.RegistrerRequest;
import com.example.domain.ecommerce.services.DireccionService;
import com.example.domain.ecommerce.services.EmailService;
import com.example.domain.ecommerce.services.UsuarioService;
import com.example.domain.ecommerce.models.entities.Usuario;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {
    @Autowired
    UsuarioService usuarioService;

    @Autowired
    DireccionService direccionService;

    @Autowired
    EmailService emailService;

    @GetMapping("/")
    public ResponseEntity<List<UsuarioPersonaDTO>> obteneUsuarios() {
        return ResponseEntity.ok(usuarioService.listarClientesYEmpleados());
    }

    @PostMapping("/createUser")
    public ResponseEntity<Usuario> createUser(@RequestBody RegistrerRequest user) {
        Usuario usuario = usuarioService.createUser(user);

        return ResponseEntity.status(201).body(usuario);
    }

    @PutMapping("/updateDirection/{id}")
    public ResponseEntity<?> updateDirection(
            @PathVariable("id") int id_usuario,
            @RequestBody DireccionDTO direccion) {

        direccionService.updateDirection(direccion, id_usuario);
        return ResponseEntity.ok("Dirección actualizada con éxito");

    }

    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/username")
    public ResponseEntity<Boolean> usernameExists(@RequestParam String username) {

        boolean exist = usuarioService.usernameExists(username);

        return ResponseEntity.ok(exist);

    }

    @GetMapping("/email")
    public ResponseEntity<Boolean> emailExists(@RequestParam String email) {

        boolean exist = usuarioService.emailExists(email);

        return ResponseEntity.ok(exist);

    }

}
