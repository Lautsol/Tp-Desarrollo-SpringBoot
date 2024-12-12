
package com.example.DesarrolloTP.controller;

import com.example.DesarrolloTP.model.Cliente;
import com.example.DesarrolloTP.service.ClienteNotFoundException;
import com.example.DesarrolloTP.service.ClienteService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ClienteController {
    
    @Autowired
    private ClienteService clienteService;
    
    @GetMapping("/panelClientes.html")
    public String mostrarClientes(Model model) {
        
        model.addAttribute("clientes", clienteService.obtenerTodosLosClientes()); // Añade la lista de clientes al modelo
       
        return "panelClientes"; 
    }

    @GetMapping("/clientes/{id}/crear")
    public String mostrarFormulario(@PathVariable int id, Model model) {

            Cliente cliente = new Cliente();
        
            model.addAttribute("cliente", cliente);
            
            return "crearCliente"; 
    }

    @PostMapping("/clientes/guardar")
    public String guardarCliente(@ModelAttribute Cliente cliente, Model model) {

        Map<String, String> errores = clienteService.validarCliente(cliente);

        if(errores != null && !errores.isEmpty()) {
            model.addAttribute("errores", errores);
            model.addAttribute("cliente", cliente); // Mantener los datos ingresados
            
            if(cliente.getId() == 0) return "crearCliente"; 
            else return "editarCliente";
        }

        try {
             // Guardar o modificar el cliente dependiendo de su id
            if (cliente.getId() == 0) {
                clienteService.crearCliente(cliente); 
            } else {
                clienteService.modificarCliente(cliente); 
            }

            return "redirect:/panelClientes.html"; 

        } catch (Exception e) {
            model.addAttribute("ERROR", "Ocurrió un error al guardar el cliente.");
            return "errorPage"; 
        }
    }

    @GetMapping("/clientes/{id}/eliminar")
    public String eliminarCliente(@PathVariable int id, Model model) {
        
        try {
            clienteService.eliminarCliente(id);;
            return "redirect:/panelClientes.html"; 
        } catch (Exception e) {
            model.addAttribute("ERROR", "Ocurrió un error al eliminar el cliente.");
            return "errorPage"; 
        }
    }

    @GetMapping("/clientes/{id}/editar")
    public String editarCliente(@PathVariable int id, Model model) {

        try {
            Cliente cliente = clienteService.buscarPorId(id);

            model.addAttribute("cliente", cliente);
            
            return "editarCliente";
    
        } catch (Exception e) {
            model.addAttribute("ERROR", "Ocurrió un error al cargar el cliente.");
            return "errorPage"; 
        }
    }

    @GetMapping("/clientes")
    public String mostrarClientes(@RequestParam(value = "search", required = false) String search, Model model) {
        List<Cliente> clientes = new ArrayList<>();
        
        try {
            if (search != null && !search.isEmpty()) {
                // Verificar si es un número para buscar por ID
                try {
                    int id = Integer.parseInt(search);
                    Cliente cliente = clienteService.buscarPorId(id);
                    if (cliente != null) {
                        clientes.add(cliente);  
                    }
                } catch (NumberFormatException e) {
                    // Si no es un número, buscar por nombre
                    clientes = clienteService.buscarPorNombre(search);
                }
                
            } else clientes = clienteService.obtenerTodosLosClientes(); // Si no hay búsqueda, mostrar todos los clientes

            model.addAttribute("clientes", clientes);
            model.addAttribute("search", search);  
            return "panelClientes";  

        } catch (ClienteNotFoundException e) {
            model.addAttribute("ERROR", "No se encontraron clientes.");
            return "panelClientes";  
        }
    }

}
