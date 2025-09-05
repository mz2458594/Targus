package com.example.domain.ecommerce.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.ecommerce.models.entities.ControlComprobante;
import com.example.domain.ecommerce.models.enums.TipoComprobante;
import com.example.domain.ecommerce.repositories.ControlComprobanteDAO;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ControlComprobanteService {
    private ControlComprobanteDAO controlComprobanteDAO;

    @Transactional(readOnly = true)
    public String getNumeroTransaccion(String tipo) {

        TipoComprobante tip;

        try {
            tip = TipoComprobante.valueOf(tipo);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Tipo de comprobante no válido");
        }

        ControlComprobante control = controlComprobanteDAO.findByTipo(tip)
                .orElseThrow(() -> new RuntimeException("Control de " + tipo + "no figurado"));

        int nuevoNumero = control.getUltimoNumero() + 1;

        String transaccion = control.getSerie() + "-" + String.format("%07d", nuevoNumero);

        return transaccion;
    }

}
