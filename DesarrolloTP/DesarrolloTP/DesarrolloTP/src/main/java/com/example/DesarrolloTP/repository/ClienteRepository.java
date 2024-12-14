
package com.example.DesarrolloTP.repository;

import com.example.DesarrolloTP.model.Cliente;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends CrudRepository<Cliente,Integer> {
    
    public List<Cliente> findByNombreStartingWithIgnoreCase(String nombre);
    public Cliente findByCuit(Long cuit);
  
}
