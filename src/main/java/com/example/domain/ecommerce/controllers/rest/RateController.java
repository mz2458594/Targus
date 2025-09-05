package com.example.domain.ecommerce.controllers.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.ecommerce.dto.request.TarifaRequest;
import com.example.domain.ecommerce.models.entities.TarifaEnvio;
import com.example.domain.ecommerce.services.TarifaService;

import lombok.extern.slf4j.Slf4j;

@RequestMapping("/api/rate")
@RestController
@Slf4j
public class RateController {
    @Autowired
    private TarifaService tarifaService;

    @GetMapping("/")
    public ResponseEntity<List<TarifaEnvio>> getRates(){
        return ResponseEntity.ok(tarifaService.getRates());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<TarifaEnvio> updateRate(@PathVariable Long id, @RequestBody TarifaRequest tarifaRequest){
        return ResponseEntity.ok(tarifaService.updateRate(id, tarifaRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRate(@PathVariable Long id){
        tarifaService.deleteRate(id);
        return ResponseEntity.noContent().build();
    }
}
