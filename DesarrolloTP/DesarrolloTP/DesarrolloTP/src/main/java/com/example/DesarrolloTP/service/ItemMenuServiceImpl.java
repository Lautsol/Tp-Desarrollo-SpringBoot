
package com.example.DesarrolloTP.service;

import com.example.DesarrolloTP.model.Bebida;
import com.example.DesarrolloTP.model.ItemMenu;
import com.example.DesarrolloTP.model.Plato;
import com.example.DesarrolloTP.model.TipoItem;
import com.example.DesarrolloTP.repository.ItemMenuRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemMenuServiceImpl implements ItemMenuService {
    
    @Autowired
    private ItemMenuRepository itemMenuRepository;
    
    public ItemMenu crearItemMenu(ItemMenu itemMenu) {
        return itemMenuRepository.save(itemMenu);
    }
    
    public ItemMenu modificarItemMenu(ItemMenu itemMenuActualizado) {
        return itemMenuRepository.save(itemMenuActualizado);
    }

    public void eliminarItemMenu(int id) {
        itemMenuRepository.findById(id);
        itemMenuRepository.deleteById(id);
    }
    
    public ItemMenu buscarPorId(int id) throws ItemMenuNotFoundException {
        return itemMenuRepository.findById(id).orElseThrow(()-> new ItemMenuNotFoundException());
    }
    
    public List<ItemMenu> buscarPorNombre(String nombre) {
        return itemMenuRepository.findByNombreStartingWithIgnoreCase(nombre);
    }
    
    public List<ItemMenu> obtenerTodosLosItemsMenu() {
        Iterable<ItemMenu> iterableItemsMenu = itemMenuRepository.findAll();
        List<ItemMenu> listaItemsMenu = new ArrayList<>();
        iterableItemsMenu.forEach(listaItemsMenu::add);
        return listaItemsMenu;
    }

    public Map<String, String> validarItemMenu(ItemMenu itemMenu) {
        Map<String, String> errores = new HashMap<>();

        if(itemMenu.getNombre() == null || itemMenu.getNombre().trim().isEmpty()) {
            errores.put("nombre", "El nombre no puede estar vacío.");
        } 
        
        else if(!itemMenu.getNombre().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            errores.put("nombre", "El nombre sólo puede contener letras y espacios.");
        }
    
        if(itemMenu.getDescripcion() == null || itemMenu.getDescripcion().trim().isEmpty()) {
            errores.put("descripcion", "La descripción no puede estar vacía.");
        } 
        
        else if(!itemMenu.getDescripcion().matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s,]+")) {
            errores.put("descripcion", "La descripción sólo puede contener letras, comas y espacios.");
        }

        if(itemMenu.getCategoria().getTipo_item() == TipoItem.BEBIDA && !(itemMenu instanceof Bebida)) {
            errores.put("categoria", "El tipo de item no corresponde a esa categoría.");
        }

        else if(itemMenu.getCategoria().getTipo_item() == TipoItem.COMIDA && !(itemMenu instanceof Plato)) {
            errores.put("categoria", "El tipo de item no corresponde a esa categoría.");
        }

        if(itemMenu.getPrecio() == null) {
            errores.put("precio", "El precio no puede ser nulo.");
        }

        else if(!itemMenu.getPrecio().toString().matches("\\d+(\\.\\d+)?")) {
            errores.put("precio", "El precio no es válido.");
        }

        else if(itemMenu.getPrecio() <= 0) {
            errores.put("precio", "El precio debe ser mayor a cero.");
        }

        return errores;
    }
    
}
