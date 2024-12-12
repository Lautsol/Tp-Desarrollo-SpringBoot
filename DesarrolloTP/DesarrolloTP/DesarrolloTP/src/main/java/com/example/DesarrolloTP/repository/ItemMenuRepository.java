
package com.example.DesarrolloTP.repository;

import com.example.DesarrolloTP.model.ItemMenu;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemMenuRepository extends CrudRepository<ItemMenu,Integer> {
    
   public List<ItemMenu> findByNombreStartingWithIgnoreCase(String nombre);
    
}
