
package com.example.DesarrolloTP.repository;

import com.example.DesarrolloTP.model.Pedido;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends CrudRepository<Pedido,Integer> {
    
    public List<Pedido> findByClienteId(int id_cliente);
    public List<Pedido> findByVendedorId(int id_vendedor);

}
