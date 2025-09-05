package com.example.domain.ecommerce.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.example.domain.ecommerce.dto.UserDTO;
import com.example.domain.ecommerce.dto.UsuarioPersonaDTO;
import com.example.domain.ecommerce.dto.request.RegistrerRequest;
import com.example.domain.ecommerce.models.entities.*;
import com.example.domain.ecommerce.models.enums.Estado;
import com.example.domain.ecommerce.repositories.ClienteDAO;
import com.example.domain.ecommerce.repositories.EmpleadoDAO;
import com.example.domain.ecommerce.repositories.RolDAO;
import com.example.domain.ecommerce.repositories.UsuarioDAO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UsuarioService {

    private final UsuarioDAO usuarioDAO;

    private final EmailService emailService;

    private final ClienteDAO clienteDAO;

    private final EmpleadoDAO empleadoDAO;

    private final RolDAO rolDAO;

    private final PasswordEncoder passwordEncoder;

    public List<Usuario> listarUsuario() {
        return (List<Usuario>) usuarioDAO.findAll();
    }

    public boolean emailExists(String email) {
        return usuarioDAO.findByEmail(email).isPresent();
    }

    public boolean usernameExists(String username) {
        return usuarioDAO.findByUsername(username).isPresent();
    }

    public List<UsuarioPersonaDTO> listarClientesYEmpleados() {
        List<UsuarioPersonaDTO> resultado = new ArrayList<>();

        List<Cliente> clientes = clienteDAO.findAll();
        for (Cliente c : clientes) {
            Usuario u = c.getUsuario();
            UsuarioPersonaDTO dto = new UsuarioPersonaDTO();

            dto.setIdUsuario(u.getIdUsuario());
            dto.setNombre(c.getNombre());
            dto.setApellido(c.getApellido());
            dto.setNum_documento(c.getDni());
            dto.setFecha(c.getFecha());
            dto.setCelular(c.getTelefono());
            dto.setCalle(c.getDireccion().getCalle());
            dto.setDepartamento(c.getDireccion().getDepartamento());
            dto.setDistrito(c.getDireccion().getDistrito());
            dto.setProvincia(c.getDireccion().getProvincia());
            dto.setCorreo(u.getEmail());
            dto.setUsername(u.getUsername());
            dto.setRol(u.getRol().getNombre());
            dto.setEstado(u.getEstado().name());
            dto.setComentario(u.getComentario());

            resultado.add(dto);
        }

        List<Empleado> empleados = empleadoDAO.findAll();
        for (Empleado e : empleados) {
            Usuario u = e.getUsuario();
            UsuarioPersonaDTO dto = new UsuarioPersonaDTO();

            dto.setIdUsuario(u.getIdUsuario());
            dto.setNombre(e.getNombre());
            dto.setApellido(e.getApellido());
            dto.setNum_documento(e.getDni());
            dto.setFecha(e.getFecha());
            dto.setCelular(e.getTelefono());
            dto.setCalle(e.getDireccion().getCalle());
            dto.setDepartamento(e.getDireccion().getDepartamento());
            dto.setDistrito(e.getDireccion().getDistrito());
            dto.setProvincia(e.getDireccion().getProvincia());
            dto.setCorreo(u.getEmail());
            dto.setUsername(u.getUsername());
            dto.setRol(u.getRol().getNombre());
            dto.setEstado(u.getEstado().name());
            dto.setComentario(u.getComentario());

            resultado.add(dto);

        }

        return resultado;
    }

    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioPorId(Long id) {

        Optional<Usuario> usuario = usuarioDAO.findById(id);

        if (usuario.isEmpty()) {
            throw new EntityNotFoundException("Usuario con id " + id + " no encontrado");

        }

        return usuario.get();
    }

    @Transactional
    public Usuario createUser(RegistrerRequest user) {

        Usuario usuario = new Usuario();
        usuario.setUsername(user.getUsername());
        if (usuarioDAO.findByEmail(user.getCorreo()).isPresent()) {
            throw new RuntimeException("El correo ingresado ya existe");
        }

        usuario.setEmail(user.getCorreo());

        usuario.setPassword(passwordEncoder.encode(user.getContraseña()));

        rolDAO.findByNombre(user.getRol()).ifPresent(usuario::setRol);

        if (user.getEstado() == null) {

            usuario.setEstado(Estado.INACTIVO);

        } else {
            switch (user.getEstado()) {
                case "ACTIVO":
                    usuario.setEstado(Estado.ACTIVO);
                    break;
                case "INACTIVO":
                    usuario.setEstado(Estado.INACTIVO);
                    break;
                default:
                    usuario.setEstado(Estado.INACTIVO);
                    break;
            }
        }

        return usuarioDAO.save(usuario);

    }

    @Transactional
    public Usuario actualizarUsuarios(UserDTO userDTO, Long id) {

        Usuario usuario = usuarioDAO.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setEmail(userDTO.getCorreo());
        usuario.setUsername(userDTO.getUsername());
        usuario.setComentario(userDTO.getComentario());

        if (userDTO.getEstado() != null) {

            if (userDTO.getEstado().equals("ACTIVO")) {
                usuario.setEstado(Estado.ACTIVO);
            } else if (userDTO.getEstado().equals("INACTIVO")) {
                usuario.setEstado(Estado.INACTIVO);
            }
        }

        return usuarioDAO.save(usuario);
    }

    @Transactional
    public void actualizarContraseña(String password, Long id_usuario) {

        Usuario usuario = usuarioDAO.findById(id_usuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id_usuario));

        usuario.setPassword(new BCryptPasswordEncoder().encode(password));

        usuarioDAO.save(usuario);

    }

    public void activar(Long id) {
        Optional<Usuario> usuario = usuarioDAO.findById(id);

        if (usuario.isEmpty()) {
            throw new RuntimeException("El usuario no esta asignado ni como cliente o empleado");
        }

        Usuario user = usuario.get();
        user.setEstado(Estado.ACTIVO);
        usuarioDAO.save(user);
    }

    @Transactional
    public void eliminarUsuario(int id) {
        Usuario usuario = usuarioDAO.findById(Long.valueOf(id)).get();
        Optional<Empleado> empleado = empleadoDAO.findByUsuario(usuario);
        Optional<Cliente> cliente = clienteDAO.findByUsuario(usuario);

        if (cliente.isPresent()) {
            Cliente cliente2 = cliente.get();
            clienteDAO.deleteById(Long.valueOf(cliente2.getId()));
        } else if (empleado.isPresent()) {
            Empleado empleado2 = empleado.get();
            empleadoDAO.deleteById(Long.valueOf(empleado2.getId()));
        } else {
            throw new RuntimeException("*El usuario no esta asociado a ninguna entidad");
        }

        usuarioDAO.deleteById(Long.valueOf(id));
    }

    private void setEmailRegistrar(Usuario usuario) {
        Email correo = new Email("mz2458594@gmail.com", usuario.getEmail(),
                "Registrar cuenta",
                usuario.getUsername(),
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");

        // emailService.sendEmailRegistrar(correo, usuario.getIdUsuario());
    }

    private void setEmailPassword(String email) {
        Optional<Usuario> usuario = usuarioDAO.findByEmail(email);

        if (usuario.isEmpty()) {
            throw new RuntimeException("El usuario no esta asignado ni como cliente o empleado");
        }

        Usuario user = usuario.get();

        // Email correo = new Email("mz2458594@gmail.com", email, "Recuperar
        // Contraseña", user.getUsername(),
        // "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");
        // emailService.sendEmailTemplate(correo, user.getIdUsuario());
    }

}
