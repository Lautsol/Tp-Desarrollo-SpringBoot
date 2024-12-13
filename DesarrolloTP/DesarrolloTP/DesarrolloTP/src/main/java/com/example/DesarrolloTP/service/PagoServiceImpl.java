package com.example.DesarrolloTP.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.DesarrolloTP.model.Pago;
import com.example.DesarrolloTP.repository.PagoRepository;

@Service
public class PagoServiceImpl implements PagoService {

    @Autowired 
    PagoRepository pagoRepository;

    public Pago crearPago(Pago pago) {
        return pagoRepository.save(pago);
    }
}
