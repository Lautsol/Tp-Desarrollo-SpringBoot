
package com.example.DesarrolloTP.service;

import com.example.DesarrolloTP.model.ItemMenu;
import java.util.List;
import java.util.Map;

public interface ItemMenuService {
    
    public ItemMenu crearItemMenu(ItemMenu itemMenu);
    public ItemMenu modificarItemMenu(ItemMenu itemMenu);
    public void eliminarItemMenu(int id);
    public ItemMenu buscarPorId(int id) throws ItemMenuNotFoundException;
    public List<ItemMenu> buscarPorNombre(String nombre);
    public List<ItemMenu> obtenerTodosLosItemsMenu();
    public Map<String, String> validarItemMenu(ItemMenu itemMenu);
}
