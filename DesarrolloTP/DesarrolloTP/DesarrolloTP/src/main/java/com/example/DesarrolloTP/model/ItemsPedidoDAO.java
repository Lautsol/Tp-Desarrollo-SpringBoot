
package com.example.DesarrolloTP.model;

import java.util.ArrayList;

public interface ItemsPedidoDAO {
    
    //FILTROS
    public ArrayList<PedidoDetalle> filtrarPorCliente(ArrayList<Cliente> clientes,int idCliente) throws ItemNoEncontradoException;
    public ArrayList<ItemMenu> filtrarPorVendedor(ArrayList<Vendedor> vendedores, int idVendedor) throws ItemNoEncontradoException;
    
    //ORDEN
    public ArrayList<Cliente> ordenarPorNombreCliente(ArrayList<Cliente> clientes) throws ItemNoEncontradoException;
    public ArrayList<Vendedor> ordenarPorNombreVendedor(ArrayList<Vendedor> vendedores) throws ItemNoEncontradoException;
    
    //BÚSQUEDA POR RANGO DE PRECIOS
    public ArrayList<ItemMenu> busquedaPorRangoPrecios(ArrayList<ItemMenu> itemMenu, double precioInicial, double precioFinal) throws ItemNoEncontradoException;
    
    //BÚSQUEDA POR RESTAURANTE
    public ArrayList<Plato> busquedaPorRestaurante(ArrayList<Vendedor> vendedores, int idVendedor) throws ItemNoEncontradoException;
    
}
