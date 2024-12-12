
package com.example.DesarrolloTP.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Entity(name = "clientes")
public class Cliente implements ClienteObserver {

    @Id
    @Column(name = "id_cliente")
    @GeneratedValue(strategy = GenerationType.IDENTITY)  
    private int id;
    private Long cuit;
    private String email;
    private String direccion;
    private String nombre;
    @Transient
    private Coordenada coordenadas;

    @OneToMany(mappedBy = "cliente", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = false)
    private List<Pedido> pedidos;

    @Transient
    private Pedido pedidoActual;
    private Long cbu;
    private String alias;
   
    public Cliente(int id, Long cuit, String nombre, String email, String direccion, Coordenada coordenadas, Long cbu) {
        this.id = id;
        this.cuit = cuit;
        this.nombre = nombre;
        this.email = email;
        this.direccion = direccion;
        this.coordenadas = coordenadas;
        pedidos = new ArrayList<>();
        this.cbu = cbu;
    }
    
    public Cliente(int id, Long cuit, String nombre, String email, String direccion, Coordenada coordenadas, String alias) {
        this.id = id;
        this.cuit = cuit;
        this.nombre = nombre;
        this.email = email;
        this.direccion = direccion;
        this.coordenadas = coordenadas;
        pedidos = new ArrayList<>();
        this.alias = alias;
    }

    public Cliente() {
        pedidos = new ArrayList<>();
    }
    
    public Pedido iniciarPedido(Vendedor vendedor) {
        if(pedidoActual == null){
            pedidoActual = new Pedido(this, vendedor);
        }
        
        return pedidoActual;
    }
 
    public boolean agregarProducto(PedidoDetalle pd) {
        boolean exito = false;
        
        if(pedidoActual != null){
            pedidoActual.agregarPedidoDetalle(pd);
            exito = true;
        } 
        return exito;
    }
    
    public boolean confirmarPedido(TipoDePago tipoPago) {
        boolean confirmado = false;
        
        if(pedidoActual != null && !pedidoActual.getPedidoDetalle().isEmpty()){
           pedidoActual.setTipoPago(tipoPago);
           pedidos.add(pedidoActual);
           pedidoActual.getVendedor().agregarPedido(pedidoActual);
           pedidoActual.calcularPrecioPedido();
           pedidoActual = null;
           confirmado = true;
        }
        return confirmado;
    }
    
    public void agregarPedido(Pedido p) {
        p.setCliente(this);
        pedidos.add(p);
    }
    
    public void update(PedidoObservable pedidoObservable) {
        
     if(pedidoObservable instanceof Pedido){
        Pedido p = (Pedido) pedidoObservable;
        
            if(pedidos.contains(p)){
            
                if(p.getTipoPago() == TipoDePago.MERCADOPAGO){
                    p.generarMercadoPago();
                }
                else p.generarTransferencia();
            }
        }
    }
    
    public boolean suscripcionEstadoPedido(PedidoObservable pedidoObservable) {
        
        boolean suscripto = false;
        
        if(pedidoObservable instanceof Pedido){
            Pedido p = (Pedido) pedidoObservable;
            
            if(pedidos.contains(p)){
                p.addObserver(this);
                suscripto = true;
            }
        }
        return suscripto;
    }
    
    public Estado verEstadoPedido(int indice) {
        return pedidos.get(indice).getEstado();
    }
}
   
   
   
