
package com.example.DesarrolloTP.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PedidoDetalle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_pedido_detalle;
    
    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private ItemMenu producto; 
    
    private int cantidad;
    
    public double calcularPrecio() {
        return cantidad * producto.getPrecio();
    }
    
}
