package com.example.domain.ecommerce.services;

import java.util.List;
import java.util.Optional;

import com.example.domain.ecommerce.dto.DireccionDTO;
import com.example.domain.ecommerce.dto.UserDTO;
import com.example.domain.ecommerce.dto.request.RegistrerRequest;
import com.example.domain.ecommerce.models.entities.Cliente;
import com.example.domain.ecommerce.models.entities.Direccion;
import com.example.domain.ecommerce.models.entities.Persona;
import com.example.domain.ecommerce.models.entities.Usuario;
import com.example.domain.ecommerce.repositories.ClienteDAO;
import com.example.domain.ecommerce.repositories.DireccionDAO;
import com.example.domain.ecommerce.repositories.UsuarioDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DireccionService {
    @Autowired
    private final DireccionDAO direccionDAO;

    @Autowired
    private final UsuarioDAO usuarioDAO;

    @Autowired
    private final ClienteDAO clienteDAO;

    public List<Direccion> obtenerDirecciones() {
        return direccionDAO.findAll();
    }

    public Direccion createDirection(RegistrerRequest user, Persona persona) {
        Direccion nueva_direccion = new Direccion();
        nueva_direccion.setCalle(user.getCalle());
        nueva_direccion.setDepartamento(user.getDepartamento());
        nueva_direccion.setDistrito(user.getDistrito());
        nueva_direccion.setProvincia(user.getProvincia());
        nueva_direccion.setPersona(persona);

        return nueva_direccion;
    }

    public Cliente updateDirection(DireccionDTO direccion, int id) {

        Optional<Usuario> user = usuarioDAO.findById(Long.valueOf(id));

        if (user.isEmpty()) {
            throw new EntityNotFoundException("*Usuario no encontrado");

        }

        Optional<Cliente> cliente = clienteDAO.findByUsuario(user.get());

        if (cliente.isEmpty()) {
            throw new EntityNotFoundException("*Cliente no encontrado");

        }

        Direccion direccionn = cliente.get().getDireccion();
        direccionn.setCalle(direccion.getCalle());
        direccionn.setDepartamento(direccion.getDepartamento());
        direccionn.setDistrito(direccion.getDistrito());
        direccionn.setProvincia(direccion.getProvincia());

        direccionDAO.save(direccionn);

        return cliente.get();
    }

}
