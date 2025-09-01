package com.example.domain.ecommerce.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.ecommerce.dto.EstadoRequestDTO;
import com.example.domain.ecommerce.dto.PedidoDTO;
import com.example.domain.ecommerce.dto.PedidoFilterDTO;
import com.example.domain.ecommerce.dto.RequestDTO;
import com.example.domain.ecommerce.factories.PedidoProveedorFactory;
import com.example.domain.ecommerce.factories.PedidoUsuarioFactory;
import com.example.domain.ecommerce.models.entities.DetallePedido;
import com.example.domain.ecommerce.models.entities.Empleado;
import com.example.domain.ecommerce.models.entities.Pedido;
import com.example.domain.ecommerce.models.entities.PedidoProveedor;
import com.example.domain.ecommerce.models.entities.PedidoUsuario;
import com.example.domain.ecommerce.models.entities.Proveedor;
import com.example.domain.ecommerce.models.enums.EstadoPedido;
import com.example.domain.ecommerce.models.enums.TipoPedido;
import com.example.domain.ecommerce.repositories.EmpleadoDAO;
import com.example.domain.ecommerce.repositories.PedidoDAO;
import com.example.domain.ecommerce.repositories.PedidoProveedorDAO;
import com.example.domain.ecommerce.repositories.PedidoUsuarioDAO;
import com.example.domain.ecommerce.repositories.ProveedorDAO;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PedidoService {

    private final PedidoProveedorDAO pedidoProveedorDAO;

    private final PedidoUsuarioDAO pedidoUsuarioDAO;

    private final PedidoProveedorFactory pedidoProveedorFactory;

    private final PedidoUsuarioFactory pedidoUsuarioFactory;

    private final PedidoDAO pedidoDAO;

    private final EmpleadoDAO empleadoDAO;

    private final ProveedorDAO proveedorDAO;

    @Transactional(readOnly = true)
    public List<Pedido> getPedidos() {
        return (List<Pedido>) pedidoDAO.findAll();
    }

    @Transactional(readOnly = true)
    public List<PedidoProveedor> getPedidosProveedor() {
        return (List<PedidoProveedor>) pedidoProveedorDAO.findAll();
    }

    @Transactional(readOnly = true)
    public List<PedidoUsuario> getPedidosUsuario() {
        return (List<PedidoUsuario>) pedidoUsuarioDAO.findAll();
    }

    @Transactional(readOnly = true)
    public Pedido obtenerPedidoPorId(int id) {
        Optional<Pedido> pedidos = pedidoDAO.findById(Long.valueOf(id));

        if (pedidos.isEmpty()) {
            throw new RuntimeException("Pedido con id " + id + " no encontrado");
        }

        return pedidos.get();
    }

    @Transactional(readOnly = true)
    public PedidoProveedor obtenerPedidoProveedorPorId(int id) {

        Optional<PedidoProveedor> pedido = pedidoProveedorDAO.findById(Long.valueOf(id));

        if (pedido.isEmpty()) {
            throw new EntityNotFoundException("Pedido con id " + id + " no encontrado");
        }

        return pedido.get();
    }

    @Transactional(readOnly = true)
    public PedidoUsuario obtenerPedidoUsuarioPorId(int id) {
        Optional<PedidoUsuario> pedido = pedidoUsuarioDAO.findById((Long.valueOf(id)));
        if (pedido.isEmpty()) {
            throw new EntityNotFoundException("Pedido con id " + id + " no encontrado");
        }

        return pedido.get();
    }

    @Transactional(readOnly = true)
    public List<PedidoUsuario> getPedidosUsuarioPorIdUsuario(int idUsuario) {
        List<PedidoUsuario> pedidos = pedidoUsuarioDAO.obtenerPedidosPorIdUsuario(Long.valueOf(idUsuario));

        if (pedidos.isEmpty()) {
            throw new RuntimeException("No se encontro pedidos para el usuario con ID: " + idUsuario);
        }
        return pedidos;
    }

    @Transactional
    public Pedido crearPedidoProveedor(RequestDTO data) {
        return pedidoProveedorFactory.crearPedido(data);
    }

    @Transactional
    public Pedido crearPedidoUsuario(RequestDTO data) {
        return pedidoUsuarioFactory.crearPedido(data);
    }

    @Transactional
    public void deletePedido(int id) {
        Optional<PedidoProveedor> pedido = pedidoProveedorDAO.findById(Long.valueOf(id));

        if (pedido.isEmpty()) {
            throw new EntityNotFoundException("Pedido con id " + id + " no encontrado");
        }

        pedidoProveedorDAO.deleteById(Long.valueOf(id));

    }

    @Transactional
    public void actualizarEstadoProveedor(int id, EstadoRequestDTO estadoRequestDTO) {
        pedidoProveedorFactory.actualizarEstado(id, estadoRequestDTO);
    }

    @Transactional
    public void actualizarEstadoUsuario(int id, EstadoRequestDTO estadoRequestDTO) {
        pedidoUsuarioFactory.actualizarEstado(id, estadoRequestDTO);
    }

    @Transactional(readOnly = true)
    public List<Pedido> obtenerPedidosConFiltro(PedidoFilterDTO pedidoFilterDTO) {

        Timestamp fechaInicio = pedidoFilterDTO.getFechaInicio() != null
                ? new Timestamp(pedidoFilterDTO.getFechaInicio().getTime())
                : null;

        Timestamp fechaFinal = pedidoFilterDTO.getFechaFinal() != null
                ? new Timestamp(pedidoFilterDTO.getFechaFinal().getTime())
                : null;

        EstadoPedido estadoPedido = null;

        if (pedidoFilterDTO.getEstado() != null) {
            try {
                estadoPedido = EstadoPedido.valueOf(pedidoFilterDTO.getEstado());
            } catch (IllegalArgumentException | NullPointerException e) {

            }
        }

        String username = null;

        if (pedidoFilterDTO.getIdResponsable() != null) {
            Empleado empleado = empleadoDAO.findById(Long.valueOf(pedidoFilterDTO.getIdResponsable()))
                    .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
            username = empleado.getUsuario().getUsername();
        }

        TipoPedido tipo;

        try {
            tipo = pedidoFilterDTO.getTipoPedido() != null ? TipoPedido.valueOf(pedidoFilterDTO.getTipoPedido())
                    : TipoPedido.Normal;
        } catch (IllegalArgumentException | NullPointerException e) {
            tipo = TipoPedido.Normal;
        }

        switch (tipo) {
            case Ecommerce:
                return pedidoUsuarioDAO.findbyFiltro(fechaInicio, fechaFinal, estadoPedido, username);
            case Inventario:
                String nombreProveedor = null;

                if (pedidoFilterDTO.getIdProveedor() != null && !pedidoFilterDTO.getIdProveedor().isEmpty()) {
                    Proveedor proveedor = proveedorDAO.findById(Long.valueOf(pedidoFilterDTO.getIdProveedor()))
                            .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
                    nombreProveedor = proveedor.getNombre();
                }

                final String nombre = nombreProveedor;

                List<Pedido> pedidosProveedor = pedidoProveedorDAO.findbyFiltro(fechaInicio, fechaFinal, estadoPedido,
                        username);

                List<Pedido> filtradas = pedidosProveedor.stream().filter(p -> {

                    if (nombre == null) {
                        return true;
                    }

                    return p.getDetallePedidos().stream()
                            .anyMatch(detalle -> detalle.getProducto().getProveedor().getNombre().equals(nombre));

                }).collect(Collectors.toList());

                return filtradas;
            case Normal:
            default:
                return pedidoDAO.findbyFiltro(fechaInicio, fechaFinal, estadoPedido, username);
        }

    }

}
