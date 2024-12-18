
package com.example.DesarrolloTP.service;

import com.example.DesarrolloTP.model.ItemMenu;
import com.example.DesarrolloTP.model.Pedido;
import com.example.DesarrolloTP.model.Vendedor;
import com.example.DesarrolloTP.repository.VendedorRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VendedorServiceImpl implements VendedorService {
    
    @Autowired
    private VendedorRepository vendedorRepository;

    @Autowired
    private PedidoService pedidoService;
    
    public Vendedor crearVendedor(Vendedor vendedor) {
        return vendedorRepository.save(vendedor);
    }
     
    public Vendedor modificarVendedor(Vendedor vendedor, List<ItemMenu> itemsAEliminar) {
        Vendedor vendedorExistente = vendedorRepository.findById(vendedor.getId())
                .orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));
    
        vendedorExistente.setNombre(vendedor.getNombre());
        vendedorExistente.setDireccion(vendedor.getDireccion());
    
        List<ItemMenu> itemsExistentes = vendedorExistente.getItems();
    
        // Crear una nueva lista para los items existentes y nuevos
        List<ItemMenu> itemsCompletos = new ArrayList<>(itemsExistentes);

        for (ItemMenu itemNuevo : vendedor.getItems()) {
            boolean existe = false;
            for (ItemMenu itemExistente : itemsCompletos) {
                if (itemExistente.getId() == (itemNuevo.getId())) {
                    existe = true;
                    break;
                }
            }
            
            if (!existe) {
                itemsCompletos.add(itemNuevo);
            }
        }

        vendedorExistente.setItems(itemsCompletos);
    
        Iterator<ItemMenu> iterator = vendedorExistente.getItems().iterator();
        while (iterator.hasNext()) {
            ItemMenu item = iterator.next();
            for (ItemMenu itemAEliminar : itemsAEliminar) {
                if (item.getId() == (itemAEliminar.getId())) {
                    iterator.remove();  
                    break;  
                }
            }
        }

        return vendedorRepository.save(vendedorExistente);
    }
    
    public void eliminarVendedor(int id) {
        Vendedor vendedor = vendedorRepository.findById(id).get();

        for(Pedido pedido : vendedor.getPedidos()) {
            pedido.setVendedor(null);
            pedidoService.modificarPedido(pedido);
        }

        vendedorRepository.deleteById(id);
    }
    
    public Vendedor buscarPorId(int id) throws VendedorNotFoundException {
        return vendedorRepository.findById(id).orElseThrow(() -> new VendedorNotFoundException());
    }
    
    public List<Vendedor> buscarPorNombre(String nombre) {
        return vendedorRepository.findByNombreStartingWithIgnoreCase(nombre);
    }
    
    public List<Vendedor> obtenerTodosLosVendedores() {
        Iterable<Vendedor> iterableVendedores = vendedorRepository.findAll();
        List<Vendedor> listaVendedores = new ArrayList<>();
        iterableVendedores.forEach(listaVendedores::add);
        return listaVendedores;
    }

    public Map<String, String> validarVendedor(Vendedor vendedor) {
        Map<String, String> errores = new HashMap<>();

        if(vendedor.getNombre() == null || vendedor.getNombre().trim().isEmpty()) {
            errores.put("nombre", "El nombre no puede estar vacío.");
        } 
        
        else if(!vendedor.getNombre().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            errores.put("nombre", "El nombre sólo puede contener letras y espacios.");
        }
    
        if(vendedor.getDireccion() == null || vendedor.getDireccion().trim().isEmpty()) {
            errores.put("direccion", "La dirección no puede estar vacía.");
        } 
        
        else if(!vendedor.getDireccion().matches("^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s]+$")) {
            errores.put("direccion", "La dirección sólo puede contener letras, números y espacios.");
        }

        if(vendedor.getItems() == null || vendedor.getItems().isEmpty()) {
            errores.put("itemsMenu", "El vendedor debe tener al menos un item.");
        }

        return errores;
    }

    public Map<String, String> validarVendedor(Vendedor vendedor, List<Integer> itemsAEliminar, List<Integer> itemsNuevos) {
        Map<String, String> errores = new HashMap<>();
        Vendedor vendedorExistente = vendedorRepository.findById(vendedor.getId()).get();
        List<ItemMenu> itemsVendedor = vendedorExistente.getItems();

        if(vendedor.getNombre() == null || vendedor.getNombre().trim().isEmpty()) {
            errores.put("nombre", "El nombre no puede estar vacío.");
        } 
        
        else if(!vendedor.getNombre().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            errores.put("nombre", "El nombre sólo puede contener letras y espacios.");
        }
    
        if(vendedor.getDireccion() == null || vendedor.getDireccion().trim().isEmpty()) {
            errores.put("direccion", "La dirección no puede estar vacía.");
        } 
        
        else if(!vendedor.getDireccion().matches("^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s]+$")) {
            errores.put("direccion", "La dirección sólo puede contener letras, números y espacios.");
        }

        if(itemsAEliminar != null && itemsAEliminar.size() == itemsVendedor.size() && (itemsNuevos == null || itemsNuevos.isEmpty())) {
            errores.put("itemsMenu", "El vendedor debe tener al menos un item.");
        }

        return errores;
    }
}
