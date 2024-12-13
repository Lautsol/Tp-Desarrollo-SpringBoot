
package com.example.DesarrolloTP.service;

import com.example.DesarrolloTP.model.Cliente;
import com.example.DesarrolloTP.model.Pedido;
import com.example.DesarrolloTP.repository.ClienteRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteServiceImpl implements ClienteService {
    
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PedidoService pedidoService;
    
    public Cliente crearCliente(Cliente cliente) {
        return clienteRepository.save(cliente); 
    }
     
    public Cliente modificarCliente(Cliente clienteActualizado) {
        return clienteRepository.save(clienteActualizado);
    }
    
    public void eliminarCliente(int id) {
        Cliente cliente = clienteRepository.findById(id).get();

        for(Pedido pedido : cliente.getPedidos()) {
            pedido.setCliente(null);
            pedidoService.modificarPedido(pedido);
        }

        clienteRepository.deleteById(id);
    }
    
    public Cliente buscarPorId(int id) throws ClienteNotFoundException {
        return clienteRepository.findById(id).orElseThrow(()->new ClienteNotFoundException());
    }
    
    public List<Cliente> buscarPorNombre(String nombre) {
        return clienteRepository.findByNombreStartingWithIgnoreCase(nombre);
    }
    
    public List<Cliente> obtenerTodosLosClientes() {
        Iterable<Cliente> iterableClientes = clienteRepository.findAll();
        List<Cliente> listaClientes = new ArrayList<>();
        iterableClientes.forEach(listaClientes::add);
        return listaClientes;
    }

    public Map<String, String> validarCliente(Cliente cliente) {
        Map<String, String> errores = new HashMap<>();
    
        if(cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            errores.put("nombre", "El nombre no puede estar vacío.");
        } 
        
        else if(!cliente.getNombre().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            errores.put("nombre", "El nombre sólo puede contener letras y espacios.");
        }
    
        if(cliente.getDireccion() == null || cliente.getDireccion().trim().isEmpty()) {
            errores.put("direccion", "La dirección no puede estar vacía.");
        } 
        
        else if(!cliente.getDireccion().matches("^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s]+$")) {
            errores.put("direccion", "La dirección sólo puede contener letras, números y espacios.");
        }
    
        if(cliente.getCuit() != null && !validarLongitud(cliente.getCuit(), 11)) {
            errores.put("cuit", "El CUIT debe contener exactamente 11 dígitos.");
        }
        
        else if(cliente.getCuit() == null) {
            errores.put("cuit", "El CUIT no puede estar vacío.");
        }

        else if(cliente.getId() == 0 && cliente.getCuit() != null && clienteRepository.findByCuit(cliente.getCuit()) != null) {
            errores.put("cuit", "Ya existe un cliente con ese CUIT.");
        }

        else if(cliente.getCuit() <= 0) {
            errores.put("cuit", "El CUIT debe ser un número positivo.");
        }
    
        if(cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
            errores.put("email", "El email no puede estar vacío.");
        } 
        
        else if(!cliente.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            errores.put("email", "El formato del email es inválido.");
        }

        if(cliente.getCbu() == null && (cliente.getAlias() == null || cliente.getAlias().isEmpty())) {
            errores.put("cbuAlias", "Debe completar al menos el CBU o el alias.");
        } else {

            if(cliente.getCbu() != null && (cliente.getAlias() == null || cliente.getAlias().isEmpty())) { 
                boolean cbuValido = validarLongitud(cliente.getCbu(), 6);
                if(cliente.getCbu() == 0 || !cbuValido) {
                    errores.put("cbu", "El CBU debe contener exactamente 6 dígitos.");
                }
            }

            else if((cliente.getAlias() != null && !cliente.getAlias().isEmpty()) && cliente.getCbu() == null) {
                boolean aliasValido = cliente.getAlias() != null && cliente.getAlias().matches("^[a-zA-Z0-9]+$");
                if(!aliasValido) { 
                    errores.put("alias", "El alias sólo puede contener letras y números, sin espacios.");
                }
            }

            else if(cliente.getCbu() != null && (cliente.getAlias() != null && !cliente.getAlias().isEmpty())) {
                boolean cbuValido = validarLongitud(cliente.getCbu(), 6);
                if(cliente.getCbu() == 0 || !cbuValido) {
                    errores.put("cbu", "El CBU debe contener exactamente 6 dígitos.");
                }

                boolean aliasValido = cliente.getAlias() != null && cliente.getAlias().matches("^[a-zA-Z0-9]+$");
                if(!aliasValido) { 
                    errores.put("alias", "El alias sólo puede contener letras y números, sin espacios.");
                }
            }
        }
    
        return errores;
    }
    
    private boolean validarLongitud(long numero, int longitudEsperada) {
        int contador = 0;
        while(numero != 0) {
            numero /= 10;
            contador++;
        }
        return contador == longitudEsperada;
    }
    
}
    

