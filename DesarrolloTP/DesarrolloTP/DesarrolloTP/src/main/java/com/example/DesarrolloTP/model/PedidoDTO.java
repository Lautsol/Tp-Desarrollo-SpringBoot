package com.example.DesarrolloTP.model;

import java.util.Map;
import lombok.Data;

@Data
public class PedidoDTO {

    private Integer id_pedido;
    private Integer idCliente;
    private Integer idVendedor;
    private String metodoPago;
    private String estado;
    private Map<String, String> itemCantidad;
    private Map<String, String> itemIds;
}
