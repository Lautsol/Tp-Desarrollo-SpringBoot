
package com.example.DesarrolloTP.service;

import com.example.DesarrolloTP.model.Cliente;
import java.util.List;
import java.util.Map;

public interface ClienteService {
    
    public Cliente crearCliente(Cliente cliente);
    public Cliente modificarCliente(Cliente clienteActualizado);
    public void eliminarCliente(int id);
    public Cliente buscarPorId(int id) throws ClienteNotFoundException;
    public List<Cliente> buscarPorNombre(String nombre);
    public List<Cliente> obtenerTodosLosClientes();
    public Map<String, String> validarCliente(Cliente cliente);
}
