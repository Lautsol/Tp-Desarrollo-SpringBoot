
package com.example.DesarrolloTP.service;

import com.example.DesarrolloTP.model.Pago;
import com.example.DesarrolloTP.model.Pedido;
import com.example.DesarrolloTP.repository.PedidoRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PedidoServiceImpl implements PedidoService {
    
    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PagoService pagoService;
   
    public Pedido crearPedido(Pedido pedido) {
        return pedidoRepository.save(pedido); 
    }
     
    public Pedido modificarPedido(Pedido pedidoActualizado) {
        Pedido pedidoExistente = pedidoRepository.findById(pedidoActualizado.getId_pedido())
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    
        pedidoExistente.setEstado(pedidoActualizado.getEstado());
        
        Pago pago = pedidoActualizado.getFormaDePago();
        pagoService.crearPago(pago);
        pedidoExistente.setFormaDePago(pago);
        
        return pedidoRepository.save(pedidoExistente);
    }
    
    public void eliminarPedido(int id) {
        pedidoRepository.findById(id);
        pedidoRepository.deleteById(id);
    }
     
    public Pedido buscarPorIdPedido(int id) throws PedidoNotFoundException {
        return pedidoRepository.findById(id).orElseThrow(()->new PedidoNotFoundException());
    }
    
    public List<Pedido> buscarPorIdCliente(int id) throws PedidoNotFoundException {
        return pedidoRepository.findByClienteId(id);
    }
    
    public List<Pedido> buscarPorIdVendedor(int id) throws PedidoNotFoundException {
        return pedidoRepository.findByVendedorId(id);
    }
    
    public List<Pedido> obtenerTodosLosPedidos() {
        Iterable<Pedido> iterablePedidos = pedidoRepository.findAll();
        List<Pedido> listaPedidos = new ArrayList<>();
        iterablePedidos.forEach(listaPedidos::add);
        return listaPedidos;
    }

    public Map<String, String> validarPedido(Integer idCliente, Integer idVendedor, List<Integer> itemIds) {
        Map<String, String> errores = new HashMap<>();

        if(idCliente == null) {
            errores.put("cliente", "El ID del cliente no puede ser nulo.");
        }

        if(idVendedor == null) {
            errores.put("vendedor", "El ID del vendedor no puede ser nulo.");
        }

        if(itemIds != null && itemIds.isEmpty()) {
            errores.put("items", "El pedido debe tener al menos un item.");
        }

        return errores;
    }

 }
