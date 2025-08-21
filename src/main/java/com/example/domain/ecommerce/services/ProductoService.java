package com.example.domain.ecommerce.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.example.domain.ecommerce.dto.ProductDTO;
import com.example.domain.ecommerce.dto.ProductFilterDTO;
import com.example.domain.ecommerce.factories.ProductoFactory;
import com.example.domain.ecommerce.models.entities.Auricular;
import com.example.domain.ecommerce.models.entities.Camara;
import com.example.domain.ecommerce.models.entities.Categoria;
import com.example.domain.ecommerce.models.entities.Impresora;
import com.example.domain.ecommerce.models.entities.Laptop;
import com.example.domain.ecommerce.models.entities.Monitor;
import com.example.domain.ecommerce.models.entities.Mouse;
import com.example.domain.ecommerce.models.entities.Producto;
import com.example.domain.ecommerce.models.entities.Proveedor;
import com.example.domain.ecommerce.models.entities.Smartphone;
import com.example.domain.ecommerce.models.entities.Smartwatch;
import com.example.domain.ecommerce.models.entities.Tablet;
import com.example.domain.ecommerce.models.entities.Teclado;
import com.example.domain.ecommerce.models.enums.Estado;
import com.example.domain.ecommerce.repositories.CategoriaDAO;
import com.example.domain.ecommerce.repositories.ProductoDAO;
import com.example.domain.ecommerce.repositories.ProveedorDAO;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductoService {

    private final ProductoDAO productoDAO;

    private final CategoriaDAO categoriaDAO;

    private final ProveedorDAO proveedorDAO;

    private final List<ProductoFactory> factories;

    public List<Categoria> obtenerCategorias() {
        return categoriaDAO.findAll();
    }

    public List<Producto> listarProducto() {
        return (List<Producto>) productoDAO.findAll();
    }

    public Producto obtenerProductoPorId(Long id) {

        Optional<Producto> producto = productoDAO.findById(Long.valueOf(id));

        if (producto.isEmpty()) {
            throw new EntityNotFoundException("Producto con id " + id + " no encontrado");
        }

        return factories.stream()
                .filter(f -> f.supports(producto.get().getCategoria().getNombre()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Tipo de producto no soportado"))
                .obtener(id);
    }

    public Map<String, String> obtenerDetalleProducto(Producto producto) {
        Map<String, String> detalles = new LinkedHashMap<>();
        if (producto instanceof Laptop laptop) {
            detalles.put("Memoria RAM", laptop.getMemoriaRam());
            detalles.put("Procesador", laptop.getProcesador());
            detalles.put("Tarjeta Grafica", laptop.getTarjetaGrafica());
            detalles.put("Sistema Operativo", laptop.getSistemaOperativo());
            detalles.put("Tamaño de Pantalla", laptop.getTamañoPantalla());
            detalles.put("Color", laptop.getColor());
        } else if (producto instanceof Smartphone smartphone) {

            detalles.put("Tamaño de pantalla", smartphone.getTamañoPantalla());
            detalles.put("Memoria Ram", smartphone.getMemoriaRam());
            detalles.put("Almacenamiento interno", smartphone.getAlmacenamientoInterno());
            detalles.put("Resolucion de camara", smartphone.getResolucionCamara());
            detalles.put("Capacidad de bateria", smartphone.getCapacidadBateria());
            detalles.put("Sistemas operativo", smartphone.getSistemaOperativo());

        } else if (producto instanceof Auricular auricular) {
            detalles.put("Tipo", auricular.getTipo());
            detalles.put("Duracion", auricular.getDuracion());
            detalles.put("Cancelacion de ruido", auricular.getCancelacionRuido());
            detalles.put("Conector", auricular.getConector());

        } else if (producto instanceof Monitor monitor) {

            detalles.put("Tamaño de pantalla", monitor.getTamañoPantalla());
            detalles.put("Resolucion", monitor.getResolución());
            detalles.put("Tipo de panel", monitor.getTipoPanel());
            detalles.put("Frecuencia de actualizacion", monitor.getFrecuenciaActualizacion());
            detalles.put("Puertos de entrada", monitor.getPuertosEntrada());

        } else if (producto instanceof Teclado teclado) {

            detalles.put("Tipo", teclado.getTipo());
            detalles.put("Conectividad", teclado.getConectividad());
            detalles.put("Distribucion", teclado.getDistribución());
            detalles.put("Retroiluminacion", teclado.getRetroiluminación());

        } else if (producto instanceof Mouse mouse) {

            detalles.put("Tipo", mouse.getTipo());
            detalles.put("Conectividad", mouse.getConectividad());
            detalles.put("DPI", mouse.getDpi());
            detalles.put("Cantidad de botones", mouse.getCantidadBotones());

        } else if (producto instanceof Smartwatch smartwatch) {

            detalles.put("Compatibilidad", smartwatch.getCompatibilidad());
            detalles.put("Monitoreo de salud", smartwatch.getMonitoreoSalud());
            detalles.put("Resistencia al agua", smartwatch.getResistenciaAgua());
            detalles.put("Duracion", smartwatch.getDuracion());

        } else if (producto instanceof Tablet tablet) {

            detalles.put("Tamaño de pantalla", tablet.getTamañoPantalla());
            detalles.put("Memoria RAM", tablet.getMemoriaRam());
            detalles.put("Almacenamiento interno", tablet.getAlmacenamientoInterno());
            detalles.put("Resolucion de camara", tablet.getResolucionCamara());
            detalles.put("Sistema operativo", tablet.getSistemaOperativo());

        } else if (producto instanceof Camara camara) {

            detalles.put("Tipo", camara.getTipo());
            detalles.put("Resolucion", camara.getResolución());
            detalles.put("Zoom optico", camara.getZoomOptico());
            detalles.put("Estabilizacion de imagen", camara.getEstabilizacionImagen());

        } else if (producto instanceof Impresora impresora) {

            detalles.put("Tipo", impresora.getTipo());
            detalles.put("Funciones", impresora.getFunciones());
            detalles.put("Conectividad", impresora.getConectividad());
            detalles.put("Velocidad de impresion", impresora.getVelocidadImpresion());
            detalles.put("Doble cara automatica", impresora.getDobleCaraAutomatica());

        }

        return detalles;
    }

    public Producto agregarProducto(ProductDTO productDTO) {

        Producto producto = factories.stream()
                .filter(f -> f.supports(productDTO.getNombre_categoria()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Tipo de producto no soportado"))
                .crearProducto(productDTO);

        Categoria categoria = categoriaDAO.findByNombre(productDTO.getNombre_categoria());
        Proveedor proveedor = proveedorDAO.findByNombre(productDTO.getProveedor())
                .orElseThrow(() -> new RuntimeException("Error al actualizar el producto"));
        ;

        producto.setCategoria(categoria);
        producto.setDescripcion(productDTO.getDescripcion());
        producto.setImagen(productDTO.getImagen1());
        producto.setNombre(productDTO.getNombre());
        producto.setPrecioVenta(productDTO.getPrecio());
        producto.setProveedor(proveedor);
        producto.setStock(productDTO.getStock());
        producto.setMarca(productDTO.getMarca());
        producto.setPrecioCompra(productDTO.getPrecioCompra());
        producto.setPeso(Float.parseFloat(productDTO.getPeso()));
        producto.setEstado(Estado.valueOf(productDTO.getEstado()));

        if (producto.validarCodigo(productDTO.getCodigoBarras())) {
            producto.setCodigoBarras(productDTO.getCodigoBarras());
        } else {
            throw new IllegalArgumentException("El codigo de barras ingresado no es válido.");
        }

        return productoDAO.save(producto);

    }

    public Producto actualizarProducto(ProductDTO productDTO, int id) {

        Producto producto = factories.stream()
                .filter(f -> f.supports(productDTO.getNombre_categoria()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Tipo de producto no soportado"))
                .actualizar(productDTO, id);

        Categoria categoria = categoriaDAO.findByNombre(productDTO.getNombre_categoria());
        Proveedor proveedor = proveedorDAO.findByNombre(productDTO.getProveedor())
                .orElseThrow(() -> new RuntimeException("Error al actualizar el producto"));

        producto.setCategoria(categoria);
        producto.setDescripcion(productDTO.getDescripcion());
        producto.setNombre(productDTO.getNombre());
        producto.setPrecioVenta(productDTO.getPrecio());
        producto.setProveedor(proveedor);
        producto.setMarca(productDTO.getMarca());
        producto.setPrecioCompra(productDTO.getPrecioCompra());
        producto.setPeso(Float.parseFloat(productDTO.getPeso()));
        producto.setEstado(Estado.valueOf(productDTO.getEstado()));

        if (producto.validarCodigo(productDTO.getCodigoBarras())) {
            producto.setCodigoBarras(productDTO.getCodigoBarras());
        } else {
            throw new IllegalArgumentException("El codigo de barras ingresado no es válido.");
        }

        return productoDAO.save(producto);

    }

    public List<Producto> obtenerProductosActivos() {
        List<Producto> activos = new ArrayList<>();
        for (Producto producto : productoDAO.findAll()) {
            if (producto.getEstado().equals(Estado.ACTIVO)) {
                activos.add(producto);
            }
        }
        return activos;
    }

    public void eliminarProducto(int id) {
        productoDAO.deleteById(Long.valueOf(id));
    }

    public void actualizarStockProducto(Producto producto, int cantidad) {
        Producto product = producto;
        product.setStock(String.valueOf(Integer.valueOf(product.getStock()) - cantidad));
        productoDAO.save(product);
    }

    public void aumentarStock(Producto producto, int cantidad) {
        Producto product = producto;
        product.setStock(String.valueOf(Integer.valueOf(product.getStock()) + cantidad));
        productoDAO.save(product);
    }

    public List<Producto> obtenerStockBajo() {
        return productoDAO.obtenerProductosStockBajo();
    }

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
            Categoria cat = categoriaDAO.findByNombre(productFilterDTO.getCategoria());
            if (cat != null) {
                categoria = cat.getNombre();
            }
        }

        return productoDAO.findByFiltro(categoria, proveedor);
    }

}