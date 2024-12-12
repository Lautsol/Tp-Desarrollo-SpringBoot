package com.example.DesarrolloTP.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DiscriminatorValue("ALCOHOL")
@EqualsAndHashCode(callSuper=false)
public class Alcohol extends Bebida {
    
    @Transient
    private double graduacion_alcoholica;
    
    public double peso() {
        return volumen * 0.99 + volumen * 0.2;
    }
}
