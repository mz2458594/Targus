package com.example.domain.ecommerce.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.example.domain.ecommerce.dto.RequestDTO;
import com.example.domain.ecommerce.dto.VentaDTO;
import com.example.domain.ecommerce.models.entities.Comprobante;
import com.example.domain.ecommerce.models.entities.Detalle_venta;
import com.example.domain.ecommerce.models.entities.Empleado;
import com.example.domain.ecommerce.models.entities.Producto;
import com.example.domain.ecommerce.models.entities.TarifaEnvio;
import com.example.domain.ecommerce.models.entities.Usuario;
import com.example.domain.ecommerce.models.entities.Venta;
import com.example.domain.ecommerce.models.entities.VentaEcommerce;
import com.example.domain.ecommerce.models.entities.VentaInventario;
import com.example.domain.ecommerce.models.enums.TipoComprobante;
import com.example.domain.ecommerce.models.enums.TipoVenta;
import com.example.domain.ecommerce.repositories.ClienteDAO;
import com.example.domain.ecommerce.repositories.EmpleadoDAO;
import com.example.domain.ecommerce.repositories.TarifaDAO;
import com.example.domain.ecommerce.repositories.VentaEcommerceDAO;
import com.example.domain.ecommerce.repositories.VentasDAO;
import com.example.domain.ecommerce.repositories.VentasInventarioDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class VentaService {

    private final VentasDAO ventasDAO;

    private final UsuarioService usuarioService;

    private final ProductoService productosService;

    private final ComprobanteService comprobanteService;

    private final PedidoService pedidoService;

    private final VentaEcommerceDAO ventaEcommerceDAO;

    private final VentasInventarioDAO ventasInventarioDAO;

    private final ClienteDAO clienteDAO;

    private final TarifaDAO tarifaDAO;

    private final EmpleadoDAO empleadoDAO;

    @Transactional(readOnly = true)
    public List<Venta> getVentas() {
        return (List<Venta>) ventasDAO.findAll();
    }

    @Transactional(readOnly = true)
    public List<VentaInventario> getVentaInventario() {
        return (List<VentaInventario>) ventasInventarioDAO.findAll();
    }

    @Transactional(readOnly = true)
    public List<VentaEcommerce> getVentaEcommerce() {
        return (List<VentaEcommerce>) ventaEcommerceDAO.findAll();
    }

    @Transactional(readOnly = true)
    public List<Venta> obtenerVentasConFiltro(VentaDTO ventaDTO) {

        Timestamp fechaInicio = ventaDTO.getFechaInicio() != null ? new Timestamp(ventaDTO.getFechaInicio().getTime())
                : null;
        Timestamp fechaFinal = ventaDTO.getFechaFinal() != null ? new Timestamp(ventaDTO.getFechaFinal().getTime())
                : null;
        TipoComprobante tipoComprobante = null;
        if (ventaDTO.getComprobante() != null && !ventaDTO.getComprobante().isEmpty()) {
            tipoComprobante = TipoComprobante.valueOf(ventaDTO.getComprobante());
        }

        String username = null;

        if (ventaDTO.getIdResponsable() != null) {
            Empleado empleado = empleadoDAO.findById(Long.valueOf(ventaDTO.getIdResponsable()))
                    .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
            username = empleado.getUsuario().getUsername();

        }

        TipoVenta tipo;
        try {
            tipo = ventaDTO.getTipoVenta() != null
                    ? TipoVenta.valueOf(ventaDTO.getTipoVenta())
                    : TipoVenta.Normal;
        } catch (IllegalArgumentException | NullPointerException e) {
            tipo = TipoVenta.Normal;
        }

        switch (tipo) {
            case Ecommerce:
                return ventaEcommerceDAO.findByUsuarioAndTipoComprobanteAndFechaVentaBetween(fechaInicio, fechaFinal,
                        username, tipoComprobante);
            case Inventario:
                return ventasInventarioDAO.findByUsuarioAndTipoComprobanteAndFechaVentaBetween(fechaInicio, fechaFinal,
                        username, tipoComprobante);
            case Normal:
            default:
                return ventasDAO.findByUsuarioAndTipoComprobanteAndFechaVentaBetween(fechaInicio, fechaFinal, username,
                        tipoComprobante);
        }
    }

    @Transactional
    public Venta crearVentaEcommerce(RequestDTO data) {
        VentaEcommerce ventaEcommerce = new VentaEcommerce();
        Venta venta = crearVenta(data, ventaEcommerce);
        pedidoService.crearPedidoUsuario(data);
        return venta;
    }

    @Transactional
    public Venta crearVentaInventario(RequestDTO data) {
        VentaInventario ventaInventario = new VentaInventario();

        BigDecimal efectivo = BigDecimal.valueOf(data.getEfectivo());
        BigDecimal vuelto = BigDecimal.valueOf(data.getVuelto());

        ventaInventario.setEfectivo(efectivo);
        ventaInventario.setVuelto(vuelto);
        return crearVenta(data, ventaInventario);
    }

    @Transactional
    public Venta crearVenta(RequestDTO data, Venta venta) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(data.getId_usuario());

        venta.setFechaVenta(Timestamp.from(Instant.now()));
        venta.setUsuario(usuario);

        List<Detalle_venta> listasProductos = new ArrayList<>();

        BigDecimal total = BigDecimal.ZERO;

        BigDecimal pesoTotal = BigDecimal.ZERO;

        for (RequestDTO.ItemsVentaDTO productos : data.getItem()) {

            Producto p = productosService.obtenerProductoPorId(productos.getProducto().getIdProducto());

            BigDecimal cantidad = BigDecimal.valueOf(productos.getCantidad());
            BigDecimal precio = new BigDecimal(p.getPrecioVenta());

            Detalle_venta vp = new Detalle_venta();
            vp.setCantidad(productos.getCantidad());
            vp.setProducto(p);
            vp.setVenta(venta);

            // Para disminuir la cantidad de productos luego de registrar una venta
            productosService.actualizarStock(p.getIdProducto(),
                    (Integer.valueOf(p.getStock()) - productos.getCantidad()));

            BigDecimal subtotal = cantidad.multiply(precio);
            vp.setSubtotal(subtotal);

            total = total.add(subtotal);

            BigDecimal peso = BigDecimal.valueOf(p.getPeso());
            pesoTotal = pesoTotal.add(peso.multiply(cantidad));

            listasProductos.add(vp);

        }

        BigDecimal costoEnvio = BigDecimal.ZERO;
        if (venta instanceof VentaEcommerce ecommerce) {
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
            ecommerce.setEnvio(costoEnvio);

        }

        BigDecimal totalFinal = total.add(costoEnvio).setScale(2, RoundingMode.UP);

        venta.setTotal(totalFinal);

        venta.setVentaProductos(listasProductos);

        Venta ventaGuardada = ventasDAO.save(venta);

        Comprobante comprobante = comprobanteService.generarComprobanteVenta(ventaGuardada, data.getTipo(),
                data.getRuc(),
                data.getRazon());

        ventaGuardada.setComprobante(comprobante);

        return ventaGuardada;
    }

}
