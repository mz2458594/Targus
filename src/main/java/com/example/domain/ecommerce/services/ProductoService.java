package com.example.domain.ecommerce.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.example.domain.ecommerce.dto.ProductFilterDTO;
import com.example.domain.ecommerce.dto.request.ProductRequest;
import com.example.domain.ecommerce.models.entities.Categoria;
import com.example.domain.ecommerce.models.entities.Producto;
import com.example.domain.ecommerce.models.entities.Proveedor;
import com.example.domain.ecommerce.models.enums.Estado;
import com.example.domain.ecommerce.repositories.CategoriaDAO;
import com.example.domain.ecommerce.repositories.ProductoDAO;
import com.example.domain.ecommerce.repositories.ProveedorDAO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductoService {

    private final ProductoDAO productoDAO;

    private final CategoriaDAO categoriaDAO;

    private final ProveedorDAO proveedorDAO;

    @Transactional(readOnly = true)
    public List<Producto> listarProducto() {
        return (List<Producto>) productoDAO.findAll();
    }

    public Producto obtenerProductoPorId(Long id) {

        Optional<Producto> producto = productoDAO.findById(Long.valueOf(id));

        if (producto.isEmpty()) {
            throw new EntityNotFoundException("Producto con id " + id + " no encontrado");
        }

        return producto.get();
    }

    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosActivos() {
        List<Producto> activos = new ArrayList<>();
        for (Producto producto : productoDAO.findAll()) {
            if (producto.getEstado().equals(Estado.ACTIVO)) {
                activos.add(producto);
            }
        }
        return activos;
    }

    @Transactional
    public Producto agregarProducto(ProductRequest productRequest) {

        Producto producto = new Producto();

        Categoria categoria = categoriaDAO.findByNombre(productRequest.getCategoria())
                .orElseThrow(() -> new RuntimeException("No se encontro la categoría"));
        Proveedor proveedor = proveedorDAO.findByNombre(productRequest.getProveedor())
                .orElseThrow(() -> new RuntimeException("No se encontro al proveedor"));
        ;

        producto.setCategoria(categoria);
        producto.setDescripcion(productRequest.getDescripcion());
        producto.setImagen(productRequest.getImagen());
        producto.setNombre(productRequest.getNombre());
        producto.setPrecioVenta(productRequest.getPrecioVenta());
        producto.setProveedor(proveedor);
        producto.setStock(productRequest.getStock());
        producto.setMarca(productRequest.getMarca());
        producto.setPrecioCompra(productRequest.getPrecioCompra());
        producto.setPeso(Float.parseFloat(productRequest.getPeso()));
        producto.setEstado(Estado.valueOf(productRequest.getEstado()));
        producto.setCodigoBarras(productRequest.getCodigoBarras());

        // EN EL FRONT VALIDAR EL FORMATO DEL NUMERO DE BARRAS
        // if (producto.validarCodigo(productDTO.getCodigoBarras())) {
        // producto.setCodigoBarras(productDTO.getCodigoBarras());
        // } else {
        // throw new IllegalArgumentException("El codigo de barras ingresado no es
        // válido.");
        // }

        Map<String, String> detailList = new LinkedHashMap<>();

        for (Map.Entry<String, String> detalle : productRequest.getDetail().entrySet()) {
            detailList.put(detalle.getKey(), detalle.getValue());
        }

        producto.setDetail(detailList);

        return productoDAO.save(producto);

    }

    @Transactional
    public Producto actualizarProducto(ProductRequest productRequest, Long id) {

        Producto producto = productoDAO.findById(id).orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Categoria categoria = categoriaDAO.findByNombre(productRequest.getCategoria())
                .orElseThrow(() -> new RuntimeException("No se encontro la categoría"));
        Proveedor proveedor = proveedorDAO.findByNombre(productRequest.getProveedor())
                .orElseThrow(() -> new RuntimeException("Error al actualizar el producto"));

        producto.setCategoria(categoria);
        producto.setProveedor(proveedor);

        if (productRequest.getImagen() != null)
            producto.setImagen(productRequest.getImagen());
        if (productRequest.getDescripcion() != null)
            producto.setDescripcion(productRequest.getDescripcion());
        if (productRequest.getNombre() != null)
            producto.setNombre(productRequest.getNombre());
        if (productRequest.getPrecioVenta() != null)
            producto.setPrecioVenta(productRequest.getPrecioVenta());
        if (productRequest.getMarca() != null)
            producto.setMarca(productRequest.getMarca());
        if (productRequest.getPrecioCompra() != null)
            producto.setPrecioCompra(productRequest.getPrecioCompra());
        if (productRequest.getPeso() != null)
            producto.setPeso(Float.parseFloat(productRequest.getPeso()));
        if (productRequest.getEstado() != null)
            producto.setEstado(Estado.valueOf(productRequest.getEstado()));
        if (productRequest.getComentario() != null)
            producto.setComentario(productRequest.getComentario());

        if (productRequest.getCodigoBarras() != null)
            producto.setCodigoBarras(productRequest.getCodigoBarras());

        // EN EL FRONT VALIDAR EL FORMATO DEL NUMERO DE BARRAS
        // if (producto.validarCodigo(productDTO.getCodigoBarras())) {
        // producto.setCodigoBarras(productDTO.getCodigoBarras());
        // } else {
        // throw new IllegalArgumentException("El codigo de barras ingresado no es
        // válido.");
        // }

        Map<String, String> detailList = new LinkedHashMap<>();

        for (Map.Entry<String, String> detalle : productRequest.getDetail().entrySet()) {
            detailList.put(detalle.getKey(), detalle.getValue());
        }

        producto.setDetail(detailList);

        return productoDAO.save(producto);

    }

    @Transactional
    public void eliminarProducto(int id) {
        productoDAO.deleteById(Long.valueOf(id));
    }

    @Transactional
    public void actualizarStock(Long idProducto, int cantidad) {
        Producto product = productoDAO.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        product.setStock(String.valueOf(Integer.valueOf(cantidad)));
        productoDAO.save(product);
    }

    @Transactional(readOnly = true)
    public List<Producto> obtenerStockBajo() {
        return productoDAO.obtenerProductosStockBajo();
    }

    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosConFiltro(ProductFilterDTO productFilterDTO) {

        String proveedor = null;
        String categoria = null;

        if (productFilterDTO.getProveedor() != null && !productFilterDTO.getProveedor().isEmpty()) {
            Proveedor prov = proveedorDAO.findByNombre(productFilterDTO.getProveedor())
                    .orElseThrow(() -> new RuntimeException("Error al actualizar el producto"));
            ;
            if (prov != null) {
                proveedor = prov.getNombre();
            }
        }

        if (productFilterDTO.getCategoria() != null) {
            Categoria cat = categoriaDAO.findByNombre(productFilterDTO.getCategoria())
                    .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));
            if (cat != null) {
                categoria = cat.getNombre();
            }
        }

        return productoDAO.findByFiltro(categoria, proveedor);
    }

}