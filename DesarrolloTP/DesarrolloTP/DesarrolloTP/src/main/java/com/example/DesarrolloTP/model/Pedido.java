
package com.example.DesarrolloTP.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@Entity(name = "pedidos")
@EqualsAndHashCode(callSuper=false)
public class Pedido  extends PedidoObservable {

    @Id
    @Column(name = "id_pedido")
    @GeneratedValue(strategy = GenerationType.IDENTITY)  
    private int id_pedido;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = true)  
    private Cliente cliente;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_pedido")  
    private List<PedidoDetalle> pedidoDetalle = new ArrayList<>();
    
    private double total;
    
    @OneToOne 
    @JoinColumn(name = "id_pago", referencedColumnName = "id_pago") // El pago está vinculado a un solo pedido
    private Pago formaDePago;
    
    @Enumerated(EnumType.STRING)
    private Estado estado;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vendedor", nullable = true) 
    private Vendedor vendedor;
    
    @Transient
    private boolean cambioEstado;
    
    @Enumerated(EnumType.STRING)
    private TipoDePago tipoPago;
     
    public Pedido(int id, Cliente cliente, Vendedor vendedor) {
        id_pedido = id;
        this.cliente = cliente;
        this.vendedor = vendedor;
        pedidoDetalle = new ArrayList<>();
        estado = Estado.EN_PROCESO;
        clientesObserver = new ArrayList<>();
        formaDePago = null;
    }

    public Pedido(Cliente cliente, Vendedor vendedor) {
        this.cliente = cliente;
        this.vendedor = vendedor;
        pedidoDetalle = new ArrayList<>();
        estado = Estado.EN_PROCESO;
        clientesObserver = new ArrayList<>();
        formaDePago = null;
    }

    public Pedido() {
        clientesObserver = new ArrayList<>();
    }

    public double calcularPrecioPedido() {
        double precioTotal = 0;
        
        for(PedidoDetalle p : pedidoDetalle){
            precioTotal += p.calcularPrecio();
        }
        
        setTotal(precioTotal);
        
        return precioTotal;
    }
    
    public boolean pedidosDeUnVendedor(ArrayList<PedidoDetalle> pd) {
        
        boolean unVendedor = true;
        
        for(PedidoDetalle ped : pd){
           if(!vendedor.getItems().contains(ped.getProducto())){
               unVendedor = false;
           }
        }
        
        return unVendedor;
    }
    
    public Pago generarTransferencia() {
        
        try{
            if(formaDePago != null) throw new PagoYaEfectuadoException();
            formaDePago = new Transferencia(cliente.getCbu(), cliente.getCuit(), LocalDate.now());
            calcularPrecioFinal();
            setEstado(Estado.RECIBIDO);
        }
        catch(PagoYaEfectuadoException e){
           System.out.println(e.getMessage());
        }
        
        return formaDePago;
    }
    
    public Pago generarMercadoPago() {
        
         try{
            if(formaDePago != null) throw new PagoYaEfectuadoException();
            formaDePago = new MercadoPago(cliente.getAlias(), LocalDate.now());
            calcularPrecioFinal();
            setEstado(Estado.RECIBIDO);
        }
        catch(PagoYaEfectuadoException e){
           System.out.println(e.getMessage());
        }
        return formaDePago;
    }

    private double calcularPrecioFinal() {
        double montoFinal = formaDePago.calcularPrecio(total);
        formaDePago.setMonto(montoFinal);
        return montoFinal;
    }
    
    public double calcularPrecioFinal(double total) {
        total = formaDePago.calcularPrecio(total);
        setTotal(total);
        return total;
    }
    
    public void setPedidoDetalle(ArrayList<PedidoDetalle> pedidoDetalle) throws ProductoDeOtroVendedorException {
       
            if(!pedidosDeUnVendedor(pedidoDetalle)) throw new ProductoDeOtroVendedorException();
           
            this.pedidoDetalle = pedidoDetalle;
    }
    
    public void agregarPedidoDetalle(PedidoDetalle pd) {
        
        /* try{
            if(!vendedor.getItems().contains(pd.getProducto())) throw new ProductoDeOtroVendedorException(); */
            pedidoDetalle.add(pd);
        
        /* }
        catch(ProductoDeOtroVendedorException e){
            System.out.println(e.getMessage());
        } */
    }
    
    public void addObserver(ClienteObserver cliente) {
         clientesObserver.add(cliente);
    }
    
    public boolean removeObserver(ClienteObserver cliente) {
        return clientesObserver.remove(cliente);
    }
     
    public void notifyObservers() {
       
        for(ClienteObserver c : clientesObserver){
            c.update(this);
        }
        cambioEstado = false;
    }
    
    public void setChanged(){
        cambioEstado = true;
    }
}
