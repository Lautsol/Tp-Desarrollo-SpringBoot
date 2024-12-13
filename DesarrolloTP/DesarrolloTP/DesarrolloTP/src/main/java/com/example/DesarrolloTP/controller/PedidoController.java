
package com.example.DesarrolloTP.controller;

import com.example.DesarrolloTP.model.Cliente;
import com.example.DesarrolloTP.model.Estado;
import com.example.DesarrolloTP.model.ItemMenu;
import com.example.DesarrolloTP.model.Pedido;
import com.example.DesarrolloTP.model.PedidoDetalle;
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

                if(errores != null && !errores.isEmpty()) {
            
                    List<ItemMenu> itemsMenu = vendedor.getItems();  
                
                    model.addAttribute("errores", errores);
                    model.addAttribute("idCliente", idCliente);
                    model.addAttribute("idVendedor", idVendedor);
                    model.addAttribute("metodoPago", metodoPago);
                    model.addAttribute("estado", "EN_PROCESO");
                    model.addAttribute("itemsMenu", itemsMenu);

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

    @GetMapping("/pedidos/{id}/eliminar")
    public String eliminarPedido(@PathVariable int id, Model model) {
        
        try {
            pedidoService.eliminarPedido(id);
            return "redirect:/panelPedidos.html";
        } catch (Exception e) {
            return "errorPage"; 
        }
    }

    @GetMapping("/pedidos")
    public String buscarPedidos(@RequestParam(value = "searchType", required = false) String searchType,
                                @RequestParam(value = "searchQuery", required = false) String searchQuery,
                                Model model) {

        try { 
            List<Pedido> pedidos = new ArrayList<>();

            if (searchType == null || searchType.isEmpty() || searchQuery == null || searchQuery.trim().isEmpty()) {
                pedidos = pedidoService.obtenerTodosLosPedidos();
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

            model.addAttribute("pedidos", pedidos);
            return "panelPedidos"; 

        } catch(PedidoNotFoundException e) {
            model.addAttribute("ERROR", "No se encontró el pedido.");
            return "panelPedidos"; 
        }
    }
}

