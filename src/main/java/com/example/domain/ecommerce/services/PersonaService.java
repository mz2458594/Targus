package com.example.domain.ecommerce.services;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.ecommerce.dto.PersonaFilterDTO;
import com.example.domain.ecommerce.dto.UserDTO;
import com.example.domain.ecommerce.dto.request.RegistrerRequest;
import com.example.domain.ecommerce.models.entities.Cliente;
import com.example.domain.ecommerce.models.entities.Direccion;
import com.example.domain.ecommerce.models.entities.Empleado;
import com.example.domain.ecommerce.models.entities.Persona;
import com.example.domain.ecommerce.models.entities.Usuario;
import com.example.domain.ecommerce.models.enums.Estado;
import com.example.domain.ecommerce.models.enums.TipoPersona;
import com.example.domain.ecommerce.repositories.ClienteDAO;
import com.example.domain.ecommerce.repositories.EmpleadoDAO;
import com.example.domain.ecommerce.repositories.PersonaDAO;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PersonaService {

    private final EmpleadoDAO empleadoDAO;

    private final ClienteDAO clienteDAO;

    private final PersonaDAO personaDAO;

    private final DireccionService direccionService;

    @Transactional
    public void createPersona(RegistrerRequest user, Usuario usuario) {
        Persona persona;

        LocalDate fechaNacimineto = user.getFecha_nac().toLocalDate();

        // if (calcularEdad(fechaNacimineto) < 18) {
        //     throw new RuntimeException("No se puede registrar a un empleado menor de 18 años");
        // }

        if (user.getRol().equals("Empleado") || user.getRol().equals("Administrador")) {

            Empleado empleado = new Empleado();
            empleado.setCargo(user.getCargo());
            empleado.setUsuario(usuario);
            persona = empleado;

        } else {

            Cliente cliente = new Cliente();
            cliente.setUsuario(usuario);
            persona = cliente;
        }

        persona.setDni(user.getNum_documento());
        persona.setApellido(user.getApellido());
        persona.setNombre(user.getNombre());
        persona.setTelefono(user.getCelular());
        persona.setFecha(user.getFecha_nac());

        Direccion nueva_direccion = direccionService.createDirection(user, persona);

        persona.setDireccion(nueva_direccion);

        if (persona instanceof Cliente) {
            clienteDAO.save((Cliente) persona);
        } else if (persona instanceof Empleado) {
            empleadoDAO.save((Empleado) persona);
        }

    }

    @Transactional
    public Persona actualizarPersona(UserDTO userDTO, Usuario usuario) {

        Persona persona;

        Optional<Cliente> cliente = clienteDAO.findByUsuario(usuario);
        Optional<Empleado> empleado = empleadoDAO.findByUsuario(usuario);

        if (cliente.isPresent()) {
            Cliente cliente2 = cliente.get();
            persona = cliente2;
        } else if (empleado.isPresent()) {
            Empleado empleado2 = empleado.get();
            empleado2.setCargo(userDTO.getCargo());
            persona = empleado2;

        } else {
            throw new RuntimeException("*El usuario no tiene ningun rol en el sistema");
        }

        persona.setNombre(userDTO.getNombre() != null && !userDTO.getNombre().isEmpty() ? userDTO.getNombre() : persona.getNombre());
        persona.setApellido(userDTO.getApellido() != null && !userDTO.getApellido().isEmpty() ? userDTO.getApellido() : persona.getApellido());
        persona.setDni(userDTO.getNum_documento() != null && !userDTO.getNum_documento().isEmpty() ? userDTO.getNum_documento() : persona.getDni());
        persona.setTelefono(userDTO.getCelular() != null && !userDTO.getCelular().isEmpty() ? userDTO.getCelular() : persona.getTelefono());
        persona.setFecha(userDTO.getFecha_nac() != null ? userDTO.getFecha_nac() : persona.getFecha());

        if ((userDTO.getCalle() != null && !userDTO.getCalle().isEmpty())
                && (userDTO.getDepartamento() != null && !userDTO.getDepartamento().isEmpty())
                && (userDTO.getDistrito() != null && !userDTO.getDistrito().isEmpty())
                && (userDTO.getProvincia() != null && !userDTO.getProvincia().isEmpty())) {
            persona.getDireccion().setCalle(userDTO.getCalle());
            persona.getDireccion().setDepartamento(userDTO.getDepartamento());
            persona.getDireccion().setDistrito(userDTO.getDistrito());
            persona.getDireccion().setProvincia(userDTO.getProvincia());
        }

        if (persona instanceof Cliente) {
            return clienteDAO.save((Cliente) persona);
        } else if (persona instanceof Empleado) {
            return empleadoDAO.save((Empleado) persona);
        } else {
            throw new RuntimeException("Error en el sistema, comuniquese con el administrador");
        }

    }

    @Transactional(readOnly = true)
    public List<Persona> obtenerPersonasConFiltros(PersonaFilterDTO personaFilterDTO) {

        Estado estado = null;

        try {
            estado = personaFilterDTO.getEstado() != null ? Estado.valueOf(personaFilterDTO.getEstado()) : null;
        } catch (IllegalArgumentException | NullPointerException e) {
            estado = null;
        }

        TipoPersona tipo;
        try {
            tipo = personaFilterDTO.getTipo() != null ? TipoPersona.valueOf(personaFilterDTO.getTipo())
                    : TipoPersona.Cliente;
        } catch (IllegalArgumentException | NullPointerException e) {
            tipo = TipoPersona.Cliente;
        }

        String departamento = personaFilterDTO.getDepartamento().equals("") ? null : personaFilterDTO.getDepartamento();

        switch (tipo) {
            case Empleado:
                return empleadoDAO.findByFiltro(estado, departamento);
            case Administrador:
                return clienteDAO.findByFiltro(estado, departamento);
            case Cliente:
            default:
                final Estado estadoFinal = estado;
                List<Persona> personas = personaDAO.findByFiltro(estado, departamento);
                List<Persona> filtradas = personas.stream()
                        .filter(p -> {

                            if (estadoFinal == null) {
                                return true;
                            }

                            if (p instanceof Cliente c && c.getUsuario() != null) {
                                return c.getUsuario().getEstado() == estadoFinal;
                            } else if (p instanceof Empleado e && e.getUsuario() != null) {
                                return e.getUsuario().getEstado() == estadoFinal;
                            } else {
                                return false;
                            }
                        }).collect(Collectors.toList());
                return filtradas;
        }

    }

    @Transactional(readOnly = true)
    public boolean dniExists(String dni) {
        return personaDAO.findByDni(dni).isPresent();
    }

    @Transactional(readOnly = true)
    public boolean telefonoExists(String telefono) {
        return personaDAO.findByTelefono(telefono).isPresent();
    }

    private int calcularEdad(LocalDate fecha) {
        return Period.between(fecha, LocalDate.now()).getYears();
    }

}
