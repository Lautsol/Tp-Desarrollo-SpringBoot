
package com.example.DesarrolloTP.service;

import com.example.DesarrolloTP.model.Pago;
import com.example.DesarrolloTP.model.Pedido;
import com.example.DesarrolloTP.repository.PagoRepository;
import com.example.DesarrolloTP.repository.PedidoRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PedidoServiceImpl implements PedidoService {
    
    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PagoRepository pagoRepository;
   
    public Pedido crearPedido(Pedido pedido) {
        return pedidoRepository.save(pedido); 
    }
     
    public Pedido modificarPedido(Pedido pedidoActualizado) {
        Pedido pedidoExistente = pedidoRepository.findById(pedidoActualizado.getId_pedido())
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    
        pedidoExistente.setEstado(pedidoActualizado.getEstado());
        
        Pago pago = pedidoActualizado.getFormaDePago();
        pagoRepository.save(pago);
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
}
