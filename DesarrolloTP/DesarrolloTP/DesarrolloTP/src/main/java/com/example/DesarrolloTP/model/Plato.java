
package com.example.DesarrolloTP.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Entity
@DiscriminatorValue("PLATO")
public class Plato extends ItemMenu {

    @Transient
    private double peso;
    @Transient
    private int calorias;
    @Transient
    private boolean apto_vegano;
    @Transient
    private boolean apto_vegetariano;
    @Transient
    private boolean apto_celiaco;
    
    public Plato(int id, String nombre, String descripcion, double precio, Categoria categoria, double peso, int calorias, boolean vegano, boolean vegetariano, boolean celiaco){
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoria = categoria;
        this.peso = peso;
        this.calorias = calorias;
        apto_vegano = vegano;
        apto_vegetariano = vegetariano;
        apto_celiaco = celiaco;
    }
    
    public boolean esComida() {
        return true;
    }
    
    public boolean esBebida() {
        return false;
    }
    
    public boolean aptoVegano() {
        return apto_vegano;
    }
    
    public boolean aptoVegetariano() {
        return apto_vegetariano;
    }
    
    public boolean aptoCeliaco() {
        return apto_celiaco;
    }
    
    public double peso() {
        peso = peso + peso * 0.1;
        return peso;
    }
    
}
