
package com.example.DesarrolloTP.service;

import com.example.DesarrolloTP.model.Categoria;
import com.example.DesarrolloTP.repository.CategoriaRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoriaServiceImpl implements CategoriaService {
    
    @Autowired
    private CategoriaRepository categoriaRepository;
    
    public Categoria buscarCategoria(int id) throws CategoriaNotFoundException {
        return categoriaRepository.findById(id).orElseThrow(()-> new CategoriaNotFoundException());
    }
    
    public List<Categoria> obtenerTodasLasCategorias() {
        Iterable<Categoria> iterableCategorias = categoriaRepository.findAll();
        List<Categoria> listaCategorias = new ArrayList<>();
        iterableCategorias.forEach(listaCategorias::add);
        return listaCategorias;
    }

}
