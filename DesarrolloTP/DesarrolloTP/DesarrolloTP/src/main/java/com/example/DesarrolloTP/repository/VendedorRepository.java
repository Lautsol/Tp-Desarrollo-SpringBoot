
package com.example.DesarrolloTP.repository;

import com.example.DesarrolloTP.model.Vendedor;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendedorRepository extends CrudRepository<Vendedor,Integer> {
    
    public List<Vendedor> findByNombreStartingWithIgnoreCase(String nombre);
   
}
