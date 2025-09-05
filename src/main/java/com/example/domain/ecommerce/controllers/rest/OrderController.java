package com.example.domain.ecommerce.controllers.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.ecommerce.dto.EstadoRequestDTO;
import com.example.domain.ecommerce.dto.PedidoFilterDTO;
import com.example.domain.ecommerce.dto.RequestDTO;
import com.example.domain.ecommerce.models.entities.Pedido;
import com.example.domain.ecommerce.models.entities.PedidoProveedor;
import com.example.domain.ecommerce.models.entities.PedidoUsuario;
import com.example.domain.ecommerce.services.PedidoService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/order")
public class OrderController {
    @Autowired
    private PedidoService pedidoService;

    @GetMapping("/")
    public ResponseEntity<List<Pedido>> getOrders() {
        return ResponseEntity.ok(pedidoService.getPedidos());
    }

    @GetMapping("/users")
    public ResponseEntity<List<PedidoUsuario>> getOrderUsers() {
        return ResponseEntity.ok(pedidoService.getPedidosUsuario());
    }

    @GetMapping("/providers")
    public ResponseEntity<List<PedidoProveedor>> getOrderProvider() {
        return ResponseEntity.ok(pedidoService.getPedidosProveedor());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> getOrderId(@PathVariable Long id){
        return ResponseEntity.ok(pedidoService.obtenerPedidoPorId(id));
    }

    @GetMapping("/provider/{id}")
    public ResponseEntity<PedidoProveedor> getOrderProvider(@PathVariable("id") Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPedidoProveedorPorId(id));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<PedidoUsuario> getOrderUser(@PathVariable("id") Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPedidoUsuarioPorId(id));
    }

    @GetMapping("/userId/{id}")
    public ResponseEntity<List<PedidoUsuario>> getOrderUserId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(pedidoService.getPedidosUsuarioPorIdUsuario(id));
    }

    @PostMapping("/createUserOrder")
    public ResponseEntity<Pedido> createUserOrder(@RequestBody RequestDTO dto){
        return ResponseEntity.ok(pedidoService.crearPedidoUsuario(dto));
    }

    @PostMapping("/createProviderOrder")
    public ResponseEntity<Pedido> createProviderOrder(@RequestBody RequestDTO dto){
        return ResponseEntity.ok(pedidoService.crearPedidoProveedor(dto));
    }

    @PostMapping("/filter")
    public ResponseEntity<List<Pedido>> getOrderFilter(@RequestBody PedidoFilterDTO pedidoFilterDTO) {
        return ResponseEntity.ok(pedidoService.obtenerPedidosConFiltro(pedidoFilterDTO));
    }

    @PutMapping("/userOrder/{id}")
    public ResponseEntity<String> updateUserOrderState(@PathVariable Long id, @RequestBody EstadoRequestDTO estadoRequestDTO){ 
        pedidoService.actualizarEstadoUsuario(id, estadoRequestDTO);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/ProviderOrder/{id}")
    public ResponseEntity<String> updateProviderOrderState(@PathVariable Long id, @RequestBody EstadoRequestDTO estadoRequestDTO){ 
        pedidoService.actualizarEstadoProveedor(id, estadoRequestDTO);
        return ResponseEntity.status(201).build();
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePedido(@PathVariable Long id){
        pedidoService.deletePedido(id);
        return ResponseEntity.noContent().build();
    }
}
