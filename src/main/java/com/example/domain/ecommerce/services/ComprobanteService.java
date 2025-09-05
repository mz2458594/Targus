package com.example.domain.ecommerce.services;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.domain.ecommerce.models.entities.Comprobante;
import com.example.domain.ecommerce.models.entities.ControlComprobante;
import com.example.domain.ecommerce.models.entities.PedidoProveedor;
import com.example.domain.ecommerce.models.entities.Venta;
import com.example.domain.ecommerce.models.enums.TipoComprobante;
import com.example.domain.ecommerce.repositories.ComprobanteDAO;
import com.example.domain.ecommerce.repositories.ControlComprobanteDAO;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ComprobanteService {
    private final ComprobanteDAO comprobanteDAO;

    private final ControlComprobanteDAO controlComprobanteDAO;

    @Transactional
    public Comprobante generarComprobante(String tipo, String ruc, String razon) {

        TipoComprobante tip = TipoComprobante.valueOf(tipo);

        ControlComprobante control = controlComprobanteDAO.findByTipo(tip)
                .orElseThrow(() -> new RuntimeException("Control de boleta no figurado"));

        int nuevoNumero = control.getUltimoNumero() + 1;
        control.setUltimoNumero(nuevoNumero);
        control.setActualizacion(LocalDateTime.now());
        controlComprobanteDAO.save(control);

        String formato = control.getSerie() + "-" + String.format("%07d", nuevoNumero);

        Comprobante comprobante = new Comprobante();
        comprobante.setNumero(formato);
        comprobante.setFechaEmision(LocalDateTime.now());
        comprobante.setTipo(control.getTipo());

        if (comprobante.getTipo() == TipoComprobante.FACTURA) {
            comprobante.setRazonSocial(razon);
            comprobante.setRucCliente(ruc);
        }

        return comprobante;
    }

    @Transactional
    public Comprobante generarComprobanteVenta(Venta venta, String tipo, String ruc, String razon) {
        Comprobante comprobante = generarComprobante(tipo, ruc, razon);
        comprobante.setVenta(venta);
        return comprobanteDAO.save(comprobante);
    }

    @Transactional
    public Comprobante generarComprobantePedido(PedidoProveedor pedido, String tipo, String ruc, String razon) {

        Comprobante comprobante = generarComprobante(tipo, ruc, razon);
        comprobante.setPedidoProveedor(pedido);
        return comprobanteDAO.save(comprobante);

    }

    @Transactional(readOnly = true)
    public Comprobante obtenerComprobantePorId(int id) {
        return comprobanteDAO.findById(Long.valueOf(id)).get();
    }

}
