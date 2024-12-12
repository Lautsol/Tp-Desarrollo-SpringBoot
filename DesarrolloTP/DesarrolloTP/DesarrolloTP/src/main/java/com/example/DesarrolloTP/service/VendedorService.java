
package com.example.DesarrolloTP.service;

import com.example.DesarrolloTP.model.ItemMenu;
import com.example.DesarrolloTP.model.Vendedor;
import java.util.List;
import java.util.Map;

public interface VendedorService {
    
    public Vendedor crearVendedor(Vendedor vendedor);
    public Vendedor modificarVendedor(Vendedor vendedor, List<ItemMenu> itemsAEliminar);
    public void eliminarVendedor(int id);
    public Vendedor buscarPorId(int id) throws VendedorNotFoundException;
    public List<Vendedor> buscarPorNombre(String nombre);
    public List<Vendedor> obtenerTodosLosVendedores();
    public Map<String, String> validarVendedor(Vendedor vendedor);
    public Map<String, String> validarVendedor(Vendedor vendedor, List<Integer> itemsAEliminar, List<Integer> itemsNuevos);
}
