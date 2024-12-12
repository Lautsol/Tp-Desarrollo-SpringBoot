
package com.example.DesarrolloTP.model;

import java.time.LocalDate;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Entity
@DiscriminatorValue("MERCADOPAGO")
public class MercadoPago extends Pago {
    
    private String alias;

    public MercadoPago(String alias, LocalDate fecha) {
        this.alias = alias;
        this.fecha = fecha;
    }
    
    public double calcularPrecio(double total) {
       return total * 1.04; 
    }
}
