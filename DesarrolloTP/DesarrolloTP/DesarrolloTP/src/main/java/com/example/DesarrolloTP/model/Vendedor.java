
package com.example.DesarrolloTP.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Entity(name = "vendedores")
public class Vendedor {
    
    @Id
    @Column(name = "id_vendedor")
    @GeneratedValue(strategy = GenerationType.IDENTITY)  
    private int id;
    private String nombre;
    private String direccion;
    @Transient
    private Coordenada coordenadas;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "itemsMenuVendedor", // Tabla intermedia
        joinColumns = @JoinColumn(name = "id_vendedor"),  // FK hacia Vendedor
        inverseJoinColumns = @JoinColumn(name = "id_item") // FK hacia ItemMenu
    )
    private List<ItemMenu> items = new ArrayList<>();

    @OneToMany(mappedBy = "vendedor", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = false)
    private List<Pedido> pedidos;
    
    public Vendedor(int id, String nombre, String direccion, Coordenada coordenadas) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.coordenadas = coordenadas;
        pedidos = new ArrayList<>();
    }

    public Vendedor() {
        pedidos = new ArrayList<>();
    }
    
    public double distancia(Cliente unCliente) {
        double dLatitud =  (unCliente.getCoordenadas().getLatitud() - coordenadas.getLatitud())/2;
        double dLongitud = (unCliente.getCoordenadas().getLongitud() - coordenadas.getLongitud())/2;
        double radio = 6372.8;
        double distancia;
        double raiz = Math.sqrt(Math.pow((Math.sin(dLatitud)),2) + (Math.cos(coordenadas.getLatitud())*Math.cos(unCliente.getCoordenadas().getLatitud())*(Math.pow((Math.sin(dLongitud)),2))));
        distancia = 2*radio*Math.asin(raiz);
        
        return distancia; 
    }
        
    public ArrayList<Bebida> getBebidas() {
        ArrayList<Bebida> bebidas = new ArrayList<>();
        
        for(int i = 0 ; i < items.size(); i++){
            if(items.get(i) instanceof Bebida){
                bebidas.add((Bebida)items.get(i));
            }
        }
        return bebidas;
    }
    
    public ArrayList<Plato> getPlatos() {
        ArrayList<Plato> platos = new ArrayList<>();
        
        for(int i = 0 ; i< items.size(); i++){
            if(items.get(i) instanceof Plato){
                platos.add((Plato)items.get(i));
            }
        }
        return platos;
    }
    
    public ArrayList<Plato> getPlatosVeganos() {
        ArrayList<Plato> platos = getPlatos();
        Iterator iterador = platos.iterator();
        
        while(iterador.hasNext()){
           Plato p = (Plato)iterador.next();
           if(!p.aptoVegano()){
               iterador.remove();
           }
        }
        return platos;
    }
    
    public ArrayList<Bebida> getBebidasSinAlcohol() {
        ArrayList<Bebida> bebidas = getBebidas();
        Iterator<Bebida> iterador = bebidas.iterator();
        
        while(iterador.hasNext()){
           Bebida b = iterador.next();
           if(b instanceof Alcohol){
               iterador.remove();
           }
        }
        return bebidas;
    }
    
    public void agregarPedido(Pedido pedido) {
        pedido.setVendedor(this);
        pedidos.add(pedido);
    }
   
    public ArrayList<Pedido> buscarPedidosPorEstado(Estado estado) {
      ArrayList<Pedido> pedidosPorEstado = new ArrayList<>();
       
       for(Pedido p : pedidos){
           if(p.getEstado() == estado){
               pedidosPorEstado.add(p);
           }
       }
       return pedidosPorEstado;
    }
   
    public void agregarItems(ArrayList<ItemMenu> items) {
       
       for(ItemMenu im : items) {
           this.items.add(im);
       }
    }

    public void agregarItem(ItemMenu item) {
        items.add(item);
    }

    public void eliminarItem(ItemMenu item) {
        if (this.items.contains(item)) {
            this.items.remove(item);
        }
    }
    
    public void cambiarEstadoPedido(PedidoObservable pedido) {
     
     ArrayList<Pedido> pedidosEnProceso = buscarPedidosPorEstado(Estado.EN_PROCESO);
     
     if(pedido instanceof Pedido){
        Pedido p = (Pedido) pedido;

        if(pedidosEnProceso.contains(p)){
            p.setEstado(Estado.EN_ENVIO);
            p.setChanged();
            p.notifyObservers();
            }
        }
   }

  }
 
