
package com.example.DesarrolloTP.controller;

import com.example.DesarrolloTP.model.ItemMenu;
import com.example.DesarrolloTP.model.Vendedor;
import com.example.DesarrolloTP.model.VendedorDTO;
import com.example.DesarrolloTP.service.ItemMenuNotFoundException;
import com.example.DesarrolloTP.service.ItemMenuService;
import com.example.DesarrolloTP.service.VendedorNotFoundException;
import com.example.DesarrolloTP.service.VendedorService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class VendedorController {
    
    @Autowired
    private VendedorService vendedorService;

    @Autowired
    private ItemMenuService itemMenuService;
    
    @GetMapping("/panelVendedores.html")
    public String mostrarVendedores(Model model) {
       
        model.addAttribute("vendedores", vendedorService.obtenerTodosLosVendedores());
        
        return "panelVendedores"; 
    }
    
    /* 
    @GetMapping("/vendedores/{id}/crear")
    public String mostrarFormulario(@PathVariable int id, Model model) {
        
            Vendedor vendedor = new Vendedor(); 
            List<ItemMenu> itemsMenu = itemMenuService.obtenerTodosLosItemsMenu();

            model.addAttribute("vendedor", vendedor);
            model.addAttribute("itemsMenu", itemsMenu);

            return "crearVendedor"; 
    }

    @PostMapping("/vendedores/guardar")
    public String guardarVendedor(@ModelAttribute Vendedor vendedor,
                                   @RequestParam(value = "itemIds", required = false) List<Integer> itemsIds, 
                                   @RequestParam(value = "itemsEliminarIds", required = false) List<Integer> itemsEliminarIds,
                                   Model model) {

        try {
            // Verificar si hay items que agregar
            if (itemsIds != null && !itemsIds.isEmpty()) {
                for (Integer itemId : itemsIds) {
                    ItemMenu item = itemMenuService.buscarPorId(itemId);
                    vendedor.agregarItem(item); // Agregar item a la lista del vendedor
                }
            } 
    
            // Verificar si hay items que eliminar
            List<ItemMenu> itemsAEliminar = new ArrayList<>();
            if (itemsEliminarIds != null && !itemsEliminarIds.isEmpty()) {
                for (Integer itemId : itemsEliminarIds) {
                    ItemMenu item = itemMenuService.buscarPorId(itemId);
                    itemsAEliminar.add(item); // Agregar item a eliminar
                }
            }

            // Guardar o modificar el vendedor dependiendo de su id
            if (vendedor.getId() == 0) {

                Map<String, String> errores = vendedorService.validarVendedor(vendedor);

                if(errores != null && !errores.isEmpty()) {
                    model.addAttribute("errores", errores);
                    model.addAttribute("vendedor", vendedor); // Mantener los datos ingresados
                    List<ItemMenu> itemsMenu = itemMenuService.obtenerTodosLosItemsMenu();
                    model.addAttribute("itemsMenu", itemsMenu);
                    return "crearVendedor"; 
                }

                vendedorService.crearVendedor(vendedor); 
                
            } else {

                Map<String, String> errores = vendedorService.validarVendedor(vendedor, itemsEliminarIds, itemsIds);

                if(errores != null && !errores.isEmpty()) {

                    model.addAttribute("errores", errores);
                    Vendedor vendedorEditar = vendedorService.buscarPorId(vendedor.getId());
                    List<ItemMenu> itemsMenuVendedor = vendedorEditar.getItems();
                    List<ItemMenu> itemsMenuDisponibles = itemMenuService.obtenerTodosLosItemsMenu();
            
                    Iterator<ItemMenu> iterator = itemsMenuDisponibles.iterator();
            
                    while (iterator.hasNext()) {
                        ItemMenu itemDisponible = iterator.next();
                        for (ItemMenu itemVendedor : itemsMenuVendedor) {
                            if (itemDisponible.getId() == itemVendedor.getId()) {
                                iterator.remove();  // Elimina el item disponible que ya está asociado al vendedor
                                break;  
                            }
                        }
                    }

                    vendedorEditar.setNombre(vendedor.getNombre());
                    vendedorEditar.setDireccion(vendedor.getDireccion());
                    
                    model.addAttribute("vendedor", vendedorEditar);
                    model.addAttribute("itemsMenuDisponibles", itemsMenuDisponibles);
                    model.addAttribute("itemsEliminar", itemsEliminarIds);
                    model.addAttribute("itemsAgregar", itemsIds);
            
                    return "editarVendedor";
                }

                vendedorService.modificarVendedor(vendedor, itemsAEliminar); 
            }
    
            return "redirect:/panelVendedores.html"; 
    
        } catch (Exception e) {
            return "errorPage";
        }
    }
    
    @GetMapping("/vendedores/{id}/editar")
    public String editarVendedor(@PathVariable int id, Model model) {

        try {
            Vendedor vendedor = vendedorService.buscarPorId(id);
    
            List<ItemMenu> itemsMenuVendedor = vendedor.getItems();
            List<ItemMenu> itemsMenuDisponibles = itemMenuService.obtenerTodosLosItemsMenu();
    
            Iterator<ItemMenu> iterator = itemsMenuDisponibles.iterator();
    
            while (iterator.hasNext()) {
                ItemMenu itemDisponible = iterator.next();
                for (ItemMenu itemVendedor : itemsMenuVendedor) {
                    if (itemDisponible.getId() == itemVendedor.getId()) {
                        iterator.remove();  // Elimina el item disponible que ya está asociado al vendedor
                        break;  
                    }
                }
            }
    
            model.addAttribute("vendedor", vendedor);
            model.addAttribute("itemsMenuDisponibles", itemsMenuDisponibles);
    
            return "editarVendedor";
    
        } catch (Exception e) {
            return "errorPage"; 
        }
    }

    @GetMapping("/vendedores/{id}/eliminar")
    public String eliminarVendedor(@PathVariable int id, Model model) {

        try {
            vendedorService.eliminarVendedor(id);
            return "redirect:/panelVendedores.html"; 
        } catch (Exception e) {
            return "errorPage"; 
        }
    }
    */

    @PostMapping("/vendedores/crear")
    public ResponseEntity<String> crearVendedor(@RequestBody VendedorDTO vendedorDTO) {

        Integer itemID = null;
        try {
            Vendedor vendedor = new Vendedor();  
            vendedor.setNombre(vendedorDTO.getNombre());
            vendedor.setDireccion(vendedorDTO.getDireccion());

            if (vendedorDTO.getItemIds() != null && !vendedorDTO.getItemIds().isEmpty()) {
                for (Integer itemId : vendedorDTO.getItemIds()) {
                    itemID = itemId;
                    ItemMenu item = itemMenuService.buscarPorId(itemId);
                    vendedor.agregarItem(item);
                }
            }

            Map<String, String> errores = vendedorService.validarVendedor(vendedor);
            if (errores != null && !errores.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Errores: " + errores.toString());
            }

            vendedorService.crearVendedor(vendedor);
            return ResponseEntity.status(HttpStatus.CREATED).body("Vendedor creado correctamente.");

        } catch (ItemMenuNotFoundException e1) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No existe el item menú con ID: " + itemID + ".");

        } catch (Exception e2) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el vendedor.");
        }
    }

    @PutMapping("/vendedores/modificar/{id}")
    public ResponseEntity<String> modificarVendedor(@PathVariable Integer id, @RequestBody VendedorDTO vendedorDTO) {
        
        Integer itemID = null;
        try {
            Vendedor vendedor = vendedorService.buscarPorId(id);
            vendedor.setNombre(vendedorDTO.getNombre());
            vendedor.setDireccion(vendedorDTO.getDireccion());

            if (vendedorDTO.getItemIds() != null && !vendedorDTO.getItemIds().isEmpty()) {
                for (Integer itemId : vendedorDTO.getItemIds()) {
                    itemID = itemId;
                    ItemMenu item = itemMenuService.buscarPorId(itemId);
                    vendedor.agregarItem(item);
                }
            }

            List<ItemMenu> itemsAEliminar = new ArrayList<>();
            if (vendedorDTO.getItemsEliminarIds() != null && !vendedorDTO.getItemsEliminarIds().isEmpty()) {
                for (Integer itemId : vendedorDTO.getItemsEliminarIds()) {
                    ItemMenu item = itemMenuService.buscarPorId(itemId);
                    itemsAEliminar.add(item);
                }
            }

            Map<String, String> errores = vendedorService.validarVendedor(vendedor, vendedorDTO.getItemsEliminarIds(), vendedorDTO.getItemIds());
            if (errores != null && !errores.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Errores: " + errores.toString());
            }

            vendedorService.modificarVendedor(vendedor, itemsAEliminar);

            return ResponseEntity.status(HttpStatus.OK).body("Vendedor modificado correctamente.");
        
        } catch (VendedorNotFoundException e1) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("El vendedor no existe.");
        
        } catch (ItemMenuNotFoundException e2) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No existe el item menú con ID: " + itemID  + ".");
        
        } catch (Exception e3) {
            e3.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al modificar el vendedor.");
        }
    }

    @DeleteMapping("/vendedores/eliminar/{id}")
    public ResponseEntity<String> eliminarVendedor(@PathVariable int id) {
        try {
            vendedorService.eliminarVendedor(id);
            return ResponseEntity.status(HttpStatus.OK).body("Vendedor eliminado correctamente.");
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el vendedor.");
        }
    }

    @GetMapping("/vendedores")
    public String mostrarVendedores(@RequestParam(value = "search", required = false) String search, Model model) {
        
        List<Vendedor> vendedores = new ArrayList<>();
        
        try {
            if (search != null && !search.isEmpty()) {
                // Verificar si es un número para buscar por ID
                try {
                    int id = Integer.parseInt(search);
                    Vendedor vendedor = vendedorService.buscarPorId(id);
                    if (vendedor != null) {
                        vendedores.add(vendedor);  
                    }
                } catch (NumberFormatException e) {
                    // Si no es un número, buscar por nombre
                    vendedores = vendedorService.buscarPorNombre(search);
                }

            } else vendedores = vendedorService.obtenerTodosLosVendedores(); // Si no hay búsqueda, mostrar todos los vendedores

            if(vendedores.isEmpty()) {
                vendedores = vendedorService.obtenerTodosLosVendedores();
                model.addAttribute("ERROR", "No se encontraron vendedores.");
                model.addAttribute("search", search);
                model.addAttribute("vendedores", vendedores);

                return "panelVendedores";
            }

            model.addAttribute("vendedores", vendedores);
            model.addAttribute("search", search);  
            return "panelVendedores";  
        
        } catch (VendedorNotFoundException e) {
            vendedores = vendedorService.obtenerTodosLosVendedores();
            model.addAttribute("ERROR", "No se encontró el vendedor.");
            model.addAttribute("search", search);
            model.addAttribute("vendedores", vendedores);

            return "panelVendedores";  
        }
    }

}
