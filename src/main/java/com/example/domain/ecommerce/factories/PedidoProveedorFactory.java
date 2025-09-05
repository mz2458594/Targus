package com.example.domain.ecommerce.factories;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.domain.ecommerce.dto.EstadoRequestDTO;
import com.example.domain.ecommerce.dto.RequestDTO;
import com.example.domain.ecommerce.models.entities.Comprobante;
import com.example.domain.ecommerce.models.entities.DetallePedido;
import com.example.domain.ecommerce.models.entities.Pedido;
import com.example.domain.ecommerce.models.entities.PedidoProveedor;
import com.example.domain.ecommerce.models.entities.PedidoUsuario;
import com.example.domain.ecommerce.models.entities.Producto;
import com.example.domain.ecommerce.models.entities.Usuario;
import com.example.domain.ecommerce.models.enums.EstadoPedido;
import com.example.domain.ecommerce.repositories.PedidoProveedorDAO;
import com.example.domain.ecommerce.services.ComprobanteService;
import com.example.domain.ecommerce.services.ProductoService;
import com.example.domain.ecommerce.services.UsuarioService;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class PedidoProveedorFactory implements PedidoFactory {

    private final UsuarioService usuarioService;

    private final ProductoService productosService;

    private final PedidoProveedorDAO pedidoProveedorDAO;

    private final ComprobanteService comprobanteService;

    @Override
    public Pedido crearPedido(RequestDTO data) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(data.getId_usuario());

        PedidoProveedor pedido = new PedidoProveedor();
        pedido.setFechaPedido(Timestamp.from(Instant.now()));
        LocalDateTime fechaEntrega = LocalDateTime.now().plusDays(7);
        pedido.setFechaEntrega(Timestamp.valueOf(fechaEntrega));
        pedido.setUser(usuario);

        List<DetallePedido> lista_pedidos = new ArrayList<>();

        BigDecimal total = BigDecimal.ZERO;

        for (RequestDTO.ItemsVentaDTO productos : data.getItem()) {

            Producto p = productosService.obtenerProductoPorId(productos.getProducto().getIdProducto());

            BigDecimal cantidad = BigDecimal.valueOf(productos.getCantidad());
            BigDecimal precio = new BigDecimal(p.getPrecioVenta());

            DetallePedido vp = new DetallePedido();
            vp.setCantidad(productos.getCantidad());
            vp.setProducto(p);
            vp.setPedido(pedido);

            BigDecimal subtotal = cantidad.multiply(precio);
            vp.setSubtotal(subtotal);

            total = total.add(subtotal);

            lista_pedidos.add(vp);

        }

        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setTotal(total);
        pedido.setDetallePedidos(lista_pedidos);
        PedidoProveedor pedidoSave = pedidoProveedorDAO.save(pedido);

        Comprobante comprobante = comprobanteService.generarComprobantePedido(pedidoSave, data.getTipo(), data.getRuc(),
                data.getRazon());

        pedidoSave.setComprobante(comprobante);

        return pedidoProveedorDAO.save(pedidoSave);
    }

    @Override
    public void actualizarEstado(Long id, EstadoRequestDTO estadoRequestDTO) {
        PedidoProveedor pedido = pedidoProveedorDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido con id " + id + " no encontrado"));

        EstadoPedido nuevoEstado = EstadoPedido.valueOf(estadoRequestDTO.getEstado());

        if (estadoRequestDTO.getEstado() == null)
            return;

        if (pedido.getEstado().equals(EstadoPedido.ENTREGADO)
                || pedido.getEstado().equals(EstadoPedido.CANCELADO)) {
            throw new IllegalStateException("No se puede modificar un pedido " + pedido.getEstado());
        } else if (nuevoEstado.ordinal() > pedido.getEstado().ordinal()) {
            if (nuevoEstado.equals(EstadoPedido.ENTREGADO)) {
                for (DetallePedido pe : pedido.getDetallePedidos()) {
                    productosService.actualizarStock(pe.getProducto().getIdProducto(),
                            Integer.valueOf(pe.getProducto().getStock()) + pe.getCantidad());
                }
            } else if (nuevoEstado.equals(EstadoPedido.CANCELADO)) {
                pedido.setComentario(estadoRequestDTO.getMotivoCancelado());
            }
            pedido.setEstado(nuevoEstado);
        }

        pedidoProveedorDAO.save(pedido);

    }
}
