
package com.example.DesarrolloTP.repository;

import com.example.DesarrolloTP.model.Categoria;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends CrudRepository<Categoria,Integer>{
   
}
