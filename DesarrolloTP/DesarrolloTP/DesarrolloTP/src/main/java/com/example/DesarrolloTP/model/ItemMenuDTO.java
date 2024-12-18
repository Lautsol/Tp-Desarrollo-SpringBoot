package com.example.DesarrolloTP.model;

import lombok.Data;

@Data
public class ItemMenuDTO {

    private String tipoItem; // Puede ser "gaseosa", "alcohol", "plato"
    private String nombre;
    private String descripcion;
    private Double precio;
    private int categoriaId;  
}
