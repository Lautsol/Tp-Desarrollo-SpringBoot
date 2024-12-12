
package com.example.DesarrolloTP.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Principal {
    
    @GetMapping("/")
    public String mostrarPaginaPrincipal() {
        return "panelGeneral";
    }
}
