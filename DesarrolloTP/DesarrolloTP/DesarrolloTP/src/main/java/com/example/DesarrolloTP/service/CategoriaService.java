
package com.example.DesarrolloTP.service;

import com.example.DesarrolloTP.model.Categoria;
import java.util.List;

public interface CategoriaService {
    
    public Categoria buscarCategoria(int id)throws CategoriaNotFoundException;
    public List<Categoria> obtenerTodasLasCategorias();
}
