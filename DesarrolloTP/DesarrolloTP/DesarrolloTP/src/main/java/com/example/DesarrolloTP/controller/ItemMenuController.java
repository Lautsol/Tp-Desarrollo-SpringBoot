
package com.example.DesarrolloTP.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.DesarrolloTP.model.Alcohol;
import com.example.DesarrolloTP.model.Categoria;
import com.example.DesarrolloTP.model.Gaseosa;
import com.example.DesarrolloTP.model.ItemMenu;
import com.example.DesarrolloTP.model.Plato;
import com.example.DesarrolloTP.service.CategoriaService;
import com.example.DesarrolloTP.service.ItemMenuNotFoundException;
import com.example.DesarrolloTP.service.ItemMenuService;

@Controller
public class ItemMenuController {
    
    @Autowired
    private ItemMenuService itemMenuService;

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping("/panelItemsMenu.html")
    public String mostrarItemsMenu(Model model) {
       
        model.addAttribute("itemsMenu", itemMenuService.obtenerTodosLosItemsMenu());
        
        return "panelItemsMenu"; 
    }

    @GetMapping("/items-menu/{id}/crear")
    public String mostrarFormulario(@PathVariable int id, Model model) {

        ItemMenu itemMenu = null;

        List<Categoria> categorias = categoriaService.obtenerTodasLasCategorias();
        model.addAttribute("itemMenu", itemMenu);  
        model.addAttribute("categorias", categorias);

        return "crearItemMenu"; 
    }

    @PostMapping("/items-menu/{id}/guardar")
    public String guardarItemMenu(@PathVariable int id, @RequestParam String tipoItem, 
                                @RequestParam String nombre, @RequestParam String descripcion,
                                @RequestParam(value = "precio", required = false) Double precio, @RequestParam int categoriaId,
                                Model model) {

        try {
            ItemMenu itemMenu = null;

            switch (tipoItem.toLowerCase()) {
                case "gaseosa":
                    itemMenu = new Gaseosa();  
                    break;
                case "alcohol":
                    itemMenu = new Alcohol();  
                    break;
                case "plato":
                    itemMenu = new Plato();    
                    break;
            }

            itemMenu.setId(id);
            itemMenu.setNombre(nombre);
            itemMenu.setDescripcion(descripcion);
            itemMenu.setPrecio(precio);
            Categoria categoria = categoriaService.buscarCategoria(categoriaId);  
            itemMenu.setCategoria(categoria); 

            Map<String, String> errores = itemMenuService.validarItemMenu(itemMenu);

            if(errores != null && !errores.isEmpty()) {
                model.addAttribute("errores", errores);
                model.addAttribute("itemMenu", itemMenu); // Mantener los datos ingresados
                List<Categoria> categorias = categoriaService.obtenerTodasLasCategorias();
                model.addAttribute("categorias", categorias);

                if(id == 0) return "crearItemMenu"; 
                else return "editarItemMenu";
            }

            if (id == 0) {
                itemMenuService.crearItemMenu(itemMenu); 
            } else {  
                itemMenuService.modificarItemMenu(itemMenu);  
            }

            return "redirect:/panelItemsMenu.html";  
        } catch (Exception e) {
            model.addAttribute("ERROR", "Ocurrió un error al guardar el item de menú.");
            return "errorPage";  
        }
    }

    @GetMapping("/items-menu/{id}/editar")
    public String editarItemMenu(@PathVariable int id, Model model) {

        try {
            ItemMenu itemMenu = itemMenuService.buscarPorId(id); 
            List<Categoria> categorias = categoriaService.obtenerTodasLasCategorias(); 

            model.addAttribute("itemMenu", itemMenu);
            model.addAttribute("categorias", categorias);

            return "editarItemMenu"; 

        } catch (Exception e) {
            model.addAttribute("ERROR", "Ocurrió un error al cargar el item menú.");
            return "errorPage";
        }
    }

    @GetMapping("/items-menu/{id}/eliminar")
    public String eliminarItemMenu(@PathVariable int id, Model model) {

        try {
            itemMenuService.eliminarItemMenu(id);
            return "redirect:/panelItemsMenu.html"; 
        } catch (Exception e) {
            model.addAttribute("ERROR", "Ocurrió un error al eliminar el item menú.");
            return "errorPage"; 
        }
    }

    @GetMapping("/items-menu")
    public String mostrarItemsMenu(@RequestParam(value = "search", required = false) String search, Model model) {
        
        List<ItemMenu> items = new ArrayList<>();
        
        try {
            if (search != null && !search.isEmpty()) {
                // Verificar si es un número para buscar por ID
                try {
                    int id = Integer.parseInt(search);
                    ItemMenu item = itemMenuService.buscarPorId(id);
                    if (item != null) {
                        items.add(item);  
                    }
                } catch (NumberFormatException e) {
                    // Si no es un número, buscar por nombre
                    items = itemMenuService.buscarPorNombre(search);
                }

            } else items = itemMenuService.obtenerTodosLosItemsMenu(); // Si no hay búsqueda, mostrar todos los items menú

            model.addAttribute("itemsMenu", items);
            model.addAttribute("search", search);  
            return "panelItemsMenu";  
        
        } catch (ItemMenuNotFoundException e) {
            model.addAttribute("ERROR", "No se encontraron items menú.");
            return "panelItemsMenu";  
        }
    }
    
}
