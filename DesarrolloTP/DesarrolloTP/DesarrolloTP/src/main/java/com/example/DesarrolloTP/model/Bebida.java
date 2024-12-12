
package com.example.DesarrolloTP.model;

import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public abstract class Bebida extends ItemMenu {
    
    @Transient
    protected double volumen;
   
    public boolean esBebida() {
        return true;
    }
    
    public boolean esComida() {
        return false;
    }
    
    public boolean aptoVegano() {
        return true;
    }

}
