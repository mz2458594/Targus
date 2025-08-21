package com.example.domain.ecommerce.factories;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.domain.ecommerce.dto.EstadoRequestDTO;
import com.example.domain.ecommerce.dto.RequestDTO;
import com.example.domain.ecommerce.models.entities.Cliente;
import com.example.domain.ecommerce.models.entities.DetallePedido;
import com.example.domain.ecommerce.models.entities.Email;
import com.example.domain.ecommerce.models.entities.Pedido;
import com.example.domain.ecommerce.models.entities.PedidoProveedor;
import com.example.domain.ecommerce.models.entities.PedidoUsuario;
import com.example.domain.ecommerce.models.entities.Producto;
import com.example.domain.ecommerce.models.entities.TarifaEnvio;
import com.example.domain.ecommerce.models.entities.Usuario;
import com.example.domain.ecommerce.models.entities.VentaEcommerce;
import com.example.domain.ecommerce.models.enums.EstadoPedido;
import com.example.domain.ecommerce.repositories.ClienteDAO;
import com.example.domain.ecommerce.repositories.PedidoUsuarioDAO;
import com.example.domain.ecommerce.repositories.TarifaDAO;
import com.example.domain.ecommerce.repositories.VentaEcommerceDAO;
import com.example.domain.ecommerce.services.EmailService;
import com.example.domain.ecommerce.services.ProductoService;
import com.example.domain.ecommerce.services.UsuarioService;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class PedidoUsuarioFactory implements PedidoFactory {

    private final UsuarioService usuarioService;

    private final ProductoService productosService;

    private final PedidoUsuarioDAO pedidoUsuarioDAO;

    private final ClienteDAO clienteDAO;

    private final TarifaDAO tarifaDAO;

    private final EmailService emailService;

    private final VentaEcommerceDAO ventaEcommerceDAO;

    @Override
    public Pedido crearPedido(RequestDTO data) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(data.getId_usuario());

        PedidoUsuario pedido = new PedidoUsuario();
        pedido.setFechaPedido(Timestamp.from(Instant.now()));
        LocalDateTime fechaEntrega = LocalDateTime.now().plusDays(7);
        pedido.setFechaEntrega(Timestamp.valueOf(fechaEntrega));
        pedido.setUser(usuario);

        List<DetallePedido> lista_pedidos = new ArrayList<>();

        BigDecimal total = BigDecimal.ZERO;
        BigDecimal pesoTotal = BigDecimal.ZERO;

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

            BigDecimal peso = BigDecimal.valueOf(p.getPeso());
            pesoTotal = pesoTotal.add(peso.multiply(cantidad));

            lista_pedidos.add(vp);

        }
        BigDecimal costoEnvio = BigDecimal.ZERO;

        String departamento = clienteDAO.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado para el usuario"))
                .getDireccion()
                .getDepartamento();

        TarifaEnvio tarifaEnvio = tarifaDAO.findByDepartamento(departamento)
                .orElseThrow(() -> new RuntimeException(
                        "No se encontro ninguna tarifa para el departamento " + departamento));

        BigDecimal precioDepartamento = tarifaEnvio.getPrecio_envio();

        costoEnvio = pesoTotal.multiply(BigDecimal.valueOf(2.6)).add(precioDepartamento).setScale(2,
                RoundingMode.UP);

        BigDecimal totalFinal = total.add(costoEnvio).setScale(2, RoundingMode.UP);

        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setEnvio(costoEnvio);
        pedido.setTotal(totalFinal);
        pedido.setDetallePedidos(lista_pedidos);

        pedidoUsuarioDAO.save(pedido);

        crearEmail(pedido);

        return pedido;
    }

    @Override
    public void actualizarEstado(int id, EstadoRequestDTO estadoRequestDTO) {
        PedidoUsuario pedido = pedidoUsuarioDAO.findById(Long.valueOf(id))
                .orElseThrow(() -> new EntityNotFoundException("Pedido con id " + id + " no encontrado"));

        EstadoPedido nuevoEstado = EstadoPedido.valueOf(estadoRequestDTO.getEstado());

        if (estadoRequestDTO.getEstado() == null)
            return;

        if (pedido.getEstado().equals(EstadoPedido.ENTREGADO)
                || pedido.getEstado().equals(EstadoPedido.CANCELADO)) {
            throw new IllegalStateException("No se puede modificar un pedido " + pedido.getEstado());
        } else if (nuevoEstado.ordinal() > pedido.getEstado().ordinal()) {
            if (nuevoEstado.equals(EstadoPedido.CANCELADO)) {
                pedido.setComentario(estadoRequestDTO.getMotivoCancelado());
            }
            pedido.setEstado(nuevoEstado);
        }

        pedidoUsuarioDAO.save(pedido);

        crearEmail(pedido);

    }

    public void crearEmail(PedidoUsuario pedidoUsuario) {
        Cliente cliente = clienteDAO.findByUsuario(pedidoUsuario.getUser())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        // Email email = new Email();
        // email.setMailFrom("mz2458594@gmail.com");
        // email.setMailTo(pedidoUsuario.getUser().getEmail());
        // email.setSubject("Seguimiento de pedido | Targus");
        // email.setJwt(
        //         "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");
        // // emailService.sendEmailPedido(email, pedidoUsuario,
        // //         cliente);
    }
}
