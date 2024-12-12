package com.example.DesarrolloTP.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.example.DesarrolloTP.model.Pago;

@Repository
public interface PagoRepository extends CrudRepository<Pago,Integer>{
    
}
