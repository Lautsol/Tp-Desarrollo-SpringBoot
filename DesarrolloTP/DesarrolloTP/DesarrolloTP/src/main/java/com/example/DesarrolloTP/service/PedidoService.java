
package com.example.DesarrolloTP.service;

import com.example.DesarrolloTP.model.Pedido;
import java.util.List;
import java.util.Map;

public interface PedidoService {
    
    public Pedido crearPedido(Pedido pedido);
    public Pedido modificarPedido(Pedido pedidoActualizado);
    public void eliminarPedido(int id);
    public Pedido buscarPorIdPedido(int id) throws PedidoNotFoundException;
    public List<Pedido> buscarPorIdCliente(int id) throws PedidoNotFoundException;
    public List<Pedido> buscarPorIdVendedor(int id) throws PedidoNotFoundException;
    public List<Pedido> obtenerTodosLosPedidos();
    public Map<String, String> validarPedido(Integer idCliente, Integer idVendedor, List<Integer> itemIds);
}
