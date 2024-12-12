package com.example.DesarrolloTP.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "categorias")
public class Categoria {
    
    @Id
    @Column(name = "id_categoria")
    private int id;
    private String descripcion;
    @Enumerated(EnumType.STRING)
    private TipoItem tipo_item;
    
}
