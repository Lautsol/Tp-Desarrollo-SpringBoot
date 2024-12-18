package com.example.DesarrolloTP.model;

import java.util.List;
import lombok.Data;

@Data
public class VendedorDTO {
    
    private Integer id;
    private String nombre;
    private String direccion;
    private List<Integer> itemIds; 
    private List<Integer> itemsEliminarIds; 
}
