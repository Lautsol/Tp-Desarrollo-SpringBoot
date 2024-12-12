package com.example.DesarrolloTP.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@DiscriminatorValue("GASEOSA")
public class Gaseosa extends Bebida {

     public double peso() {
        return volumen * 1.04 + volumen * 0.2;  
    }
    
}
