package com.example.domain.ecommerce.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.domain.ecommerce.dto.request.TarifaRequest;
import com.example.domain.ecommerce.models.entities.TarifaEnvio;
import com.example.domain.ecommerce.repositories.TarifaDAO;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TarifaService {
    private final TarifaDAO tarifaDAO;

    public List<TarifaEnvio> getRates(){
        return tarifaDAO.findAll();
    }

    public TarifaEnvio updateRate(Long id, TarifaRequest tarifaRequest){
        TarifaEnvio tarifaEnvio = tarifaDAO.findById(id).orElseThrow(() -> new RuntimeException("Tarifa no encontrada"));
        tarifaEnvio.setDepartamento(tarifaRequest.getDepartamento());
        tarifaEnvio.setPrecio_envio(tarifaRequest.getPrecio());
        return tarifaDAO.save(tarifaEnvio);
    }

    public void deleteRate(Long id){
        tarifaDAO.deleteById(id);
    }

}
