
package com.example.DesarrolloTP.controller;

import com.example.DesarrolloTP.model.Cliente;
import com.example.DesarrolloTP.model.Estado;
import com.example.DesarrolloTP.model.ItemMenu;
import com.example.DesarrolloTP.model.Pedido;
import com.example.DesarrolloTP.model.PedidoDTO;
import com.example.DesarrolloTP.model.PedidoDetalle;
import com.example.DesarrolloTP.model.ProductoDeOtroVendedorException;
import com.example.DesarrolloTP.model.TipoDePago;
import com.example.DesarrolloTP.model.Vendedor;
import com.example.DesarrolloTP.service.ClienteNotFoundException;
import com.example.DesarrolloTP.service.ClienteService;
import com.example.DesarrolloTP.service.ItemMenuNotFoundException;
import com.example.DesarrolloTP.service.ItemMenuService;
import com.example.DesarrolloTP.service.PedidoNotFoundException;
import com.example.DesarrolloTP.service.PedidoService;
import com.example.DesarrolloTP.service.VendedorNotFoundException;
import com.example.DesarrolloTP.service.VendedorService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PedidoController {
    
    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private VendedorService vendedorService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ItemMenuService itemMenuService;
    
    @GetMapping("/panelPedidos.html")
    public String mostrarPedidos(Model model) {

        model.addAttribute("pedidos", pedidoService.obtenerTodosLosPedidos()); 

        return "panelPedidos"; 
    }
    
    @GetMapping("/pedidos/{id}/crear")
    public String mostrarFormularioCrearPedido(@PathVariable int id, Model model) {

        Pedido pedido = new Pedido();
        model.addAttribute("pedido", pedido);
        model.addAttribute("idCliente", "");
        model.addAttribute("idVendedor", "");
        model.addAttribute("metodoPago", "TRANSFERENCIA");  
        model.addAttribute("estado", "EN_PROCESO");
        model.addAttribute("itemsMenu", new ArrayList<ItemMenu>());

        return "crearPedido";
    }

    @PostMapping("/pedidos/{id}/crear")
    public String crearPedido(@ModelAttribute Pedido pedido,
                                @RequestParam(value = "idCliente", required = false) Integer idCliente,
                                @RequestParam(value = "idVendedor", required = false) Integer idVendedor,
                                @RequestParam("metodoPago") String metodoPago,
                                Model model) {
            
            Map<String, String> errores = pedidoService.validarPedido(idCliente, idVendedor, null);
            
            if(errores != null && !errores.isEmpty()) {
                model.addAttribute("errores", errores);
                model.addAttribute("idCliente", idCliente);
                model.addAttribute("idVendedor", idVendedor);
                model.addAttribute("metodoPago", metodoPago);
                model.addAttribute("estado", "EN_PROCESO");

                return "crearPedido";
            }

            try { 
                Vendedor vendedor = vendedorService.buscarPorId(idVendedor);
        
                List<ItemMenu> itemsMenu = vendedor.getItems();  
            
                model.addAttribute("idCliente", idCliente);
                model.addAttribute("idVendedor", idVendedor);
                model.addAttribute("metodoPago", metodoPago);
                model.addAttribute("estado", "EN_PROCESO");
                model.addAttribute("itemsMenu", itemsMenu);

                return "crearPedido";  // Se recarga la página con los datos cargados

        } catch (VendedorNotFoundException e) {
            errores.put("vendedor", "El vendedor no existe.");
            model.addAttribute("errores", errores);
            model.addAttribute("idCliente", idCliente);
            model.addAttribute("idVendedor", idVendedor);
            model.addAttribute("metodoPago", metodoPago);
            model.addAttribute("estado", "EN_PROCESO");
            return "crearPedido";  
        } 
    }

    @PostMapping("/pedidos/guardar")
    public String guardarPedido(@ModelAttribute Pedido pedido,
                                @RequestParam(value = "idCliente", required = false) Integer idCliente,
                                @RequestParam(value = "idVendedor", required = false) Integer idVendedor,
                                @RequestParam("metodoPago") String metodoPago,
                                @RequestParam("estado") String estado,
                                @RequestParam Map<String, String> itemCantidad,
                                @RequestParam Map<String, String> itemIds,
                                Model model) {
        
        List<Integer> selectedItemIds = new ArrayList<>();

        // Obtener items seleccionados
        for (String key : itemIds.keySet()) {
            String value = itemIds.get(key);
            if(value != null && !value.trim().isEmpty()) {
                if(key.startsWith("itemIds_")) {
                        selectedItemIds.add(Integer.parseInt(value)); 
                }
            }
        }
        
        Map<String, String> errores = pedidoService.validarPedido(idCliente, idVendedor, selectedItemIds);

        try {
            Cliente cliente = clienteService.buscarPorId(idCliente);
            Vendedor vendedor = vendedorService.buscarPorId(idVendedor);

            if(pedido.getId_pedido() == 0) { 

                if(cliente.getCbu() == null && metodoPago.equals("TRANSFERENCIA")) {
                    errores.put("cbu", "El cliente debe registrar su CBU para utilizar esa forma de pago.");
                }
                if((cliente.getAlias() == null || cliente.getAlias().isEmpty()) && metodoPago.equals("MERCADOPAGO")) {
                    errores.put("alias", "El cliente debe registrar su alias para utilizar esa forma de pago.");
                }
                
                if(errores != null && !errores.isEmpty()) {
            
                    List<ItemMenu> itemsMenu = vendedor.getItems();  
                
                    model.addAttribute("errores", errores);
                    model.addAttribute("idCliente", idCliente);
                    model.addAttribute("idVendedor", idVendedor);
                    model.addAttribute("metodoPago", metodoPago);
                    model.addAttribute("estado", "EN_PROCESO");
                    model.addAttribute("itemsMenu", itemsMenu);
                    model.addAttribute("itemIds", selectedItemIds);
                    model.addAttribute("itemCantidad", itemCantidad);

                    return "crearPedido";  
                }
                
                pedido = cliente.iniciarPedido(vendedor);

                for (Map.Entry<String, String> entry : itemCantidad.entrySet()) {
                    if (entry.getKey().startsWith("cantidad_")) {
                        int itemId = Integer.parseInt(entry.getKey().substring("cantidad_".length()));

                        if (itemIds.containsKey("itemIds_" + itemId)) {
                            ItemMenu item = itemMenuService.buscarPorId(itemId);
                            int cantidad = Integer.parseInt(entry.getValue());

                            PedidoDetalle pedidoDetalle = new PedidoDetalle();
                            pedidoDetalle.setProducto(item);
                            pedidoDetalle.setCantidad(cantidad);
                            pedido.agregarPedidoDetalle(pedidoDetalle);
                        }
                    }
                }

                cliente.confirmarPedido(TipoDePago.valueOf(metodoPago));
                pedidoService.crearPedido(pedido);
            
            } else if(Estado.valueOf(estado) == Estado.EN_ENVIO) {
                    pedido.setEstado(Estado.EN_PROCESO);
                    cliente.agregarPedido(pedido);
                    vendedor.agregarPedido(pedido);
                    cliente.suscripcionEstadoPedido(pedido);
                    vendedor.cambiarEstadoPedido(pedido);
                    cliente.getPedidos().remove(pedido);
                    vendedor.getPedidos().remove(pedido);
                    pedido = pedidoService.modificarPedido(pedido);
                }

        return "redirect:/panelPedidos.html";  

        } catch (VendedorNotFoundException e1) {
            errores.put("vendedor", "El vendedor no existe.");
            model.addAttribute("errores", errores);
            model.addAttribute("idCliente", idCliente);
            model.addAttribute("idVendedor", idVendedor);
            model.addAttribute("metodoPago", metodoPago);
            model.addAttribute("estado", "EN_PROCESO");

            return "crearPedido";  

        } catch (ClienteNotFoundException e2) {
            errores.put("cliente", "El cliente no existe.");

            try { 
                Vendedor vendedor = vendedorService.buscarPorId(idVendedor);
            
                List<ItemMenu> itemsMenu = vendedor.getItems();  
                
                model.addAttribute("errores", errores);
                model.addAttribute("idCliente", idCliente);
                model.addAttribute("idVendedor", idVendedor);
                model.addAttribute("metodoPago", metodoPago);
                model.addAttribute("estado", "EN_PROCESO");
                model.addAttribute("itemsMenu", itemsMenu);
                model.addAttribute("itemIds", selectedItemIds);
                model.addAttribute("itemCantidad", itemCantidad);

                return "crearPedido";

            } catch (VendedorNotFoundException e) {
                errores.put("vendedor", "El vendedor no existe.");
                model.addAttribute("errores", errores);
                model.addAttribute("idCliente", idCliente);
                model.addAttribute("idVendedor", idVendedor);
                model.addAttribute("metodoPago", metodoPago);
                model.addAttribute("estado", "EN_PROCESO");

                return "crearPedido";  
            }

        } catch (ItemMenuNotFoundException e3) {
            model.addAttribute("ERROR", "Item no encontrado.");
            return "crearPedido"; 
        }
    }

    @GetMapping("/pedidos/{id}/editar")
    public String editarPedido(@PathVariable int id, Model model) {

        try {
            Pedido pedido = pedidoService.buscarPorIdPedido(id);
            List<PedidoDetalle> pedidosDetalles = pedido.getPedidoDetalle();
        
            model.addAttribute("pedido", pedido);
            model.addAttribute("pedidosDetalles", pedidosDetalles);

            // Determina los estados permitidos según el estado actual
            List<Estado> estadosPermitidos = new ArrayList<>();

            if (pedido.getEstado() == Estado.RECIBIDO) {
                estadosPermitidos.add(Estado.RECIBIDO); // No se puede cambiar

            } else if (pedido.getEstado() == Estado.EN_PROCESO) {
                estadosPermitidos.add(Estado.EN_PROCESO);
                estadosPermitidos.add(Estado.EN_ENVIO);
            }

            double recargo = 0;
            if(pedido.getTipoPago() == TipoDePago.MERCADOPAGO) {
                recargo = pedido.getTotal() * 0.4;
            }
            else if(pedido.getTipoPago() == TipoDePago.TRANSFERENCIA) {
                recargo = pedido.getTotal() * 0.2;
            }
            
            model.addAttribute("estadosPermitidos", estadosPermitidos);
            model.addAttribute("recargo", recargo);

            return "editarPedido";
    
        } catch (Exception e) {
            return "errorPage"; 
        }
    }

    @GetMapping("/pedidos/{id}/eliminar")
    public String eliminarPedido(@PathVariable int id, Model model) {
        
        try {
            pedidoService.eliminarPedido(id);
            return "redirect:/panelPedidos.html";
        } catch (Exception e) {
            return "errorPage"; 
        }
    }

    @PostMapping("/pedidos/crear")
    public ResponseEntity<?> crearPedido(@RequestBody PedidoDTO pedidoDTO) {

        Integer idItem = null;
        try {
            List<Integer> selectedItemIds = new ArrayList<>();
            Integer vendedorId = null;

            // Obtener items seleccionados y verificar que todos sean del mismo vendedor
            for (String key : pedidoDTO.getItemIds().keySet()) {
                String value = pedidoDTO.getItemIds().get(key);
                if(value != null && !value.trim().isEmpty()) {
                    if(key.startsWith("itemIds_")) {
                        int itemId = Integer.parseInt(value);
                        selectedItemIds.add(itemId);

                        // Obtener el vendedor del primer producto
                        if (vendedorId == null) {
                            idItem = itemId;
                            ItemMenu item = itemMenuService.buscarPorId(itemId);
                            vendedorId = buscarVendedorDelItem(item);  // Obtener el vendedor del item
                        } else {
                            ItemMenu item = itemMenuService.buscarPorId(itemId);
                            Integer itemVendedorId = buscarVendedorDelItem(item);

                            if (!itemVendedorId.equals(vendedorId)) throw new ProductoDeOtroVendedorException();
                        }
                    }
                }
            }

            Map<String, String> errores = pedidoService.validarPedido(pedidoDTO.getIdCliente(), pedidoDTO.getIdVendedor(), selectedItemIds);
            Cliente cliente = clienteService.buscarPorId(pedidoDTO.getIdCliente());
            Vendedor vendedor = vendedorService.buscarPorId(pedidoDTO.getIdVendedor());

            // Validaciones de CBU y alias según el método de pago
            if (cliente.getCbu() == null && pedidoDTO.getMetodoPago().equals("TRANSFERENCIA")) {
                errores.put("cbu", "El cliente debe registrar su CBU para utilizar esa forma de pago.");
            }
            if ((cliente.getAlias() == null || cliente.getAlias().isEmpty()) && pedidoDTO.getMetodoPago().equals("MERCADOPAGO")) {
                errores.put("alias", "El cliente debe registrar su alias para utilizar esa forma de pago.");
            }

            if (errores != null && !errores.isEmpty()) {
                return ResponseEntity.badRequest().body(errores);
            }

            // Crear el pedido
            Pedido pedido = cliente.iniciarPedido(vendedor);
            for (Map.Entry<String, String> entry : pedidoDTO.getItemCantidad().entrySet()) {
                if (entry.getKey().startsWith("cantidad_")) {
                    int itemId = Integer.parseInt(entry.getKey().substring("cantidad_".length()));
                    if (pedidoDTO.getItemIds().containsKey("itemIds_" + itemId)) {
                        ItemMenu item = itemMenuService.buscarPorId(itemId);
                        int cantidad = Integer.parseInt(entry.getValue());

                        PedidoDetalle pedidoDetalle = new PedidoDetalle();
                        pedidoDetalle.setProducto(item);
                        pedidoDetalle.setCantidad(cantidad);
                        pedido.agregarPedidoDetalle(pedidoDetalle);
                    }
                }
            }

            cliente.confirmarPedido(TipoDePago.valueOf(pedidoDTO.getMetodoPago()));
            pedidoService.crearPedido(pedido);

            return ResponseEntity.ok("Pedido creado con éxito.");

        } catch (VendedorNotFoundException e1) {
            Map<String, String> error = new HashMap<>();
            error.put("vendedor", "El vendedor no existe.");
            return ResponseEntity.badRequest().body(error);
            
        } catch (ClienteNotFoundException e2) {
            Map<String, String> error = new HashMap<>();
            error.put("cliente", "El cliente no existe.");
            return ResponseEntity.badRequest().body(error);
            
        } catch (ItemMenuNotFoundException e3) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe el item con ID " + idItem + ".");

        } catch (ProductoDeOtroVendedorException e4) {
            return ResponseEntity.badRequest().body("Todos los productos deben pertenecer al mismo vendedor.");
        }
    }

    // Método para obtener el vendedor de un ítem
    private Integer buscarVendedorDelItem(ItemMenu item) {
        for (Vendedor vendedor : vendedorService.obtenerTodosLosVendedores()) {
            for (ItemMenu itemMenu : vendedor.getItems()) {
                if (itemMenu.getId() == item.getId()) {  
                    return vendedor.getId();  
                }
            }
        }
        return null;  
    }

    @PutMapping("/pedidos/actualizar/{id}")
    public ResponseEntity<?> modificarPedido(@PathVariable("id") Integer id) {

        try {
            Pedido pedido = pedidoService.buscarPorIdPedido(id);
            Cliente cliente = pedido.getCliente();
            Vendedor vendedor = pedido.getVendedor();

            cliente.agregarPedido(pedido);
            vendedor.agregarPedido(pedido);
            cliente.suscripcionEstadoPedido(pedido);
            vendedor.cambiarEstadoPedido(pedido);
            cliente.getPedidos().remove(pedido);
            vendedor.getPedidos().remove(pedido);
            pedido = pedidoService.modificarPedido(pedido);
            return ResponseEntity.ok("Pedido actualizado correctamente.");

        } catch (PedidoNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pedido no encontrado.");
        } 
    }

    @DeleteMapping("/pedidos/eliminar/{id}")
    public ResponseEntity<?> eliminarPedido(@PathVariable int id) {
        try {
            pedidoService.eliminarPedido(id);
            return ResponseEntity.ok("Pedido eliminado exitosamente.");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el pedido.");
        }
    }

    @GetMapping("/pedidos/{id}/detalles")
    public String mostrarPedido(@PathVariable int id, Model model) {

        try {
            Pedido pedido = pedidoService.buscarPorIdPedido(id);
            List<PedidoDetalle> pedidosDetalles = pedido.getPedidoDetalle();
        
            model.addAttribute("pedido", pedido);
            model.addAttribute("pedidosDetalles", pedidosDetalles);

            // Determina los estados permitidos según el estado actual
            List<Estado> estadosPermitidos = new ArrayList<>();

            if (pedido.getEstado() == Estado.RECIBIDO) {
                estadosPermitidos.add(Estado.RECIBIDO); // No se puede cambiar

            } else if (pedido.getEstado() == Estado.EN_PROCESO) {
                estadosPermitidos.add(Estado.EN_PROCESO);
                estadosPermitidos.add(Estado.EN_ENVIO);
            }

            double recargo = 0;
            if(pedido.getTipoPago() == TipoDePago.MERCADOPAGO) {
                recargo = pedido.getTotal() * 0.4;
            }
            else if(pedido.getTipoPago() == TipoDePago.TRANSFERENCIA) {
                recargo = pedido.getTotal() * 0.2;
            }
            
            model.addAttribute("estadosPermitidos", estadosPermitidos);
            model.addAttribute("recargo", recargo);

            return "verPedido";
    
        } catch (Exception e) {
            return "errorPage"; 
        }
    }

    @GetMapping("/pedidos")
    public String buscarPedidos(@RequestParam(value = "searchType", required = false) String searchType,
                                @RequestParam(value = "searchQuery", required = false) String searchQuery,
                                @RequestParam(value = "reset", required = false) String reset,
                                Model model) {

        try { 
            List<Pedido> pedidos = new ArrayList<>();

            // Verificar si el parámetro 'reset' está presente
            if (reset != null && reset.equals("true")) {
                pedidos = pedidoService.obtenerTodosLosPedidos();
                model.addAttribute("pedidos", pedidos);
                
                return "panelPedidos";
            }

            if (searchType == null || searchType.isEmpty() || searchQuery == null || searchQuery.trim().isEmpty()) {
                pedidos = pedidoService.obtenerTodosLosPedidos();
                model.addAttribute("ERROR", "Seleccione un criterio de búsqueda e ingrese un valor.");
                model.addAttribute("searchQuery", searchQuery);
                model.addAttribute("searchType", searchType);
                model.addAttribute("pedidos", pedidos);
                
                return "panelPedidos";
            }
            
            switch (searchType) {
                case "idPedido":
                    pedidos.add(pedidoService.buscarPorIdPedido(Integer.parseInt(searchQuery)));
                    break;
                case "idCliente":
                    pedidos = pedidoService.buscarPorIdCliente(Integer.parseInt(searchQuery));
                    break;
                case "idVendedor":
                    pedidos = pedidoService.buscarPorIdVendedor(Integer.parseInt(searchQuery));
                    break;
                default:
                    pedidos = pedidoService.obtenerTodosLosPedidos();
                    break;
            }

            if (pedidos.isEmpty()) {
                pedidos = pedidoService.obtenerTodosLosPedidos();
                model.addAttribute("ERROR", "No se encontraron pedidos.");
                model.addAttribute("searchQuery", searchQuery);
                model.addAttribute("searchType", searchType);
                model.addAttribute("pedidos", pedidos);
                
                return "panelPedidos";
            }

            model.addAttribute("pedidos", pedidos);
            return "panelPedidos"; 

        } catch(PedidoNotFoundException e) {
            List<Pedido> pedidos = pedidoService.obtenerTodosLosPedidos();
            model.addAttribute("ERROR", "No se encontró el pedido.");
            model.addAttribute("searchQuery", searchQuery);
            model.addAttribute("searchType", searchType);
            model.addAttribute("pedidos", pedidos);
            
            return "panelPedidos"; 
        }
    }
}

