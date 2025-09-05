package com.example.domain.ecommerce.models.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.example.domain.ecommerce.models.enums.Estado;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Data
@Table(name = "productos")
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Producto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;

    private String nombre;

    private String descripcion;
    private String precioVenta;
    private String stock;
    private String imagen;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Detalle_venta> ventaProductos = new ArrayList<>();

    @ManyToOne
    @JsonIgnoreProperties("productos")
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    private String marca;

    private String precioCompra;

    @Pattern(regexp = "\\d{13}", message = "El codigo de barras debe tener exactamente 13 dígitos numéricos")
    private String codigoBarras;

    private float peso;

    public boolean validarCodigo(String codigo) {
        if (codigo == null || !codigo.matches("\\d{13}")) {
            return false;
        }

        int suma = 0;

        for (int i = 0; i < 12; i++) {
            int digito = Character.getNumericValue(codigo.charAt(i));
            suma += (i % 2 == 0) ? digito : digito * 3;
        }

        int digitoControlCalculado = (10 - (suma % 10)) % 10;
        int digitoControlReal = Character.getNumericValue(codigo.charAt(12));

        return digitoControlCalculado == digitoControlReal;
    }

    @Enumerated(EnumType.STRING)
    private Estado estado;

    private String comentario;

    @ElementCollection
    @CollectionTable(
        name = "product_details",
        joinColumns = @JoinColumn(name = "id_producto")
    )
    @MapKeyColumn(name = "atributo")
    @Column(name = "valor")
    private Map<String, String> detail = new LinkedHashMap<>();


}
