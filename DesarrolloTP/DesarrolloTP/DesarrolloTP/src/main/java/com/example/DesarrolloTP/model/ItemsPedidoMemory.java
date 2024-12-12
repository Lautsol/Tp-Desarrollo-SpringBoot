
package com.example.DesarrolloTP.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class ItemsPedidoMemory implements ItemsPedidoDAO{
    
    public ArrayList<PedidoDetalle> filtrarPorCliente(ArrayList<Cliente> clientes, int idCliente) throws ItemNoEncontradoException {
       
       ArrayList<PedidoDetalle> pedidosDetalles = new ArrayList<>();
       ArrayList<PedidoDetalle> retorno = new ArrayList<>();
       
       Cliente c1;
       
       if(!clientes.stream().filter(c -> c.getId() == idCliente).collect(Collectors.toList()).isEmpty()){
           
        c1 = clientes.stream().filter(c -> c.getId() == idCliente).collect(Collectors.toList()).get(0);
        
        for(Pedido p : c1.getPedidos()){
           
             for(PedidoDetalle pd : p.getPedidoDetalle()){
               
                   pedidosDetalles.add(pd);    
            }
       }
        
        retorno.addAll(pedidosDetalles.stream().distinct().collect(Collectors.toList()));
       
      }
      
        else throw new ItemNoEncontradoException();
       
        if(retorno.isEmpty()) throw new ItemNoEncontradoException();
      
        return retorno;
    }
    
    
    public ArrayList<ItemMenu> filtrarPorVendedor(ArrayList<Vendedor> vendedores, int idVendedor) throws ItemNoEncontradoException {
        
        Vendedor v1;
        
        if(!vendedores.stream().filter(v -> v.getId() == idVendedor).collect(Collectors.toList()).isEmpty()){
          v1 = vendedores.stream().filter(v -> v.getId() == idVendedor).collect(Collectors.toList()).get(0);
        }
        
        else throw new ItemNoEncontradoException();
        
        if((v1.getItems()).isEmpty()) throw new ItemNoEncontradoException();
        
        ArrayList<ItemMenu> items = new ArrayList<>(v1.getItems());

        return items;
    }
    
    
    public ArrayList<Cliente> ordenarPorNombreCliente(ArrayList<Cliente> clientes) throws ItemNoEncontradoException {
        
        ArrayList<Cliente> aux = new ArrayList<>();
        
        if(clientes.isEmpty()) throw new ItemNoEncontradoException();
        
        aux.addAll(clientes.stream().sorted(Comparator.comparing(Cliente::getNombre)).collect(Collectors.toList()));
        
        return aux;
    }
    
    
    public ArrayList<Vendedor> ordenarPorNombreVendedor(ArrayList<Vendedor> vendedores)throws ItemNoEncontradoException {
        
        ArrayList<Vendedor> aux = new ArrayList<>();
        
        if(vendedores.isEmpty()) throw new ItemNoEncontradoException();
        
        aux.addAll(vendedores.stream().sorted(Comparator.comparing(Vendedor::getNombre)).collect(Collectors.toList()));
        
        return aux;
    }
    
    
    public ArrayList<ItemMenu> busquedaPorRangoPrecios(ArrayList<ItemMenu> itemMenu, double precioInicial, double precioFinal)throws ItemNoEncontradoException {
        
        ArrayList<ItemMenu> items = new ArrayList<>();
        
        if(itemMenu.isEmpty()) throw new ItemNoEncontradoException();
         
        if(!itemMenu.stream().filter(im -> im.getPrecio() >= precioInicial && im.getPrecio() <= precioFinal).collect(Collectors.toList()).isEmpty()){
            items.addAll(itemMenu.stream().filter(im -> im.getPrecio() >= precioInicial && im.getPrecio() <= precioFinal).collect(Collectors.toList()));
        }
        
        else throw new ItemNoEncontradoException();
        
        return items;
        
    }
    
    
    public ArrayList<Plato> busquedaPorRestaurante(ArrayList<Vendedor> vendedores, int idVendedor) throws ItemNoEncontradoException {
        
        Vendedor v1;
        
        if(!vendedores.stream().filter(v -> v.getId() == idVendedor).collect(Collectors.toList()).isEmpty()){
            v1 = vendedores.stream().filter(v -> v.getId() == idVendedor).collect(Collectors.toList()).get(0);
        }
        
        else throw new ItemNoEncontradoException();
        
        if(v1.getPlatos().isEmpty()) throw new ItemNoEncontradoException();
        
        return v1.getPlatos();
        
    }
    
}