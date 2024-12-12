
package com.example.DesarrolloTP.model;

import java.util.ArrayList;

public  abstract class PedidoObservable {
    
    protected ArrayList<ClienteObserver> clientesObserver;
    public abstract void addObserver(ClienteObserver cliente);
    public abstract boolean removeObserver(ClienteObserver cliente);
    public abstract void notifyObservers();
    public abstract void setChanged();
    
}
