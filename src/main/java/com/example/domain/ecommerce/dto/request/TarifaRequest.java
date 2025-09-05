package com.example.domain.ecommerce.dto.request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class TarifaRequest {
    private String departamento;
    private BigDecimal precio;
}
