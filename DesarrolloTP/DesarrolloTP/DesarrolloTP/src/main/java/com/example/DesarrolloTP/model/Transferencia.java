
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
@DiscriminatorValue("TRANSFERENCIA")
public class Transferencia extends Pago {
    
    private long cbu;
    private long cuit;
    
    public Transferencia(long cbu, long cuit, LocalDate fecha) {
        this.cbu = cbu ;
        this.cuit = cuit; 
        this.fecha = fecha;
    }
  
    public double calcularPrecio(double total) {
       return total * 1.02;
    }
    
}
