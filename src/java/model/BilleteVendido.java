package model;

import java.io.Serializable;
import java.util.UUID;

/**
 * Billete Vendido, con su localizador, servicio asociado y factura
 * 
 * @param id identificador del billete vendido
 * @param servicio asociado al billete
 * @param factura de compra
 * @param localizador asociado a la compra
 * @param nombreViajero nombre del viajero
 * @param dniViajero dni del viajero
 * @param numPlaza numero de plaza del billete
 */
public class BilleteVendido implements Serializable {
    private UUID id = null;
    private Servicio servicio = null;
    private Factura factura = null;
    private String localizador = null;
    private String nombreViajero = null;
    private String dniViajero = null;
    //num plaza
    //otra info
    
    public BilleteVendido() {
        this.id = UUID.randomUUID();
    }
    
    public BilleteVendido(String id){
        this.id = UUID.fromString(id);
    }
    
    public BilleteVendido(UUID id){
        this.id = id;
    }
    
    /**
     * Constructor de un nuevo billete vendido
     */
    public BilleteVendido(Servicio servicio, Factura factura, String localizador,
            String nombreViajero, String dniViajero){
        this();
        this.servicio = servicio;
        this.factura = factura;
        this.localizador = localizador;
        this.nombreViajero = nombreViajero;
        this.dniViajero = dniViajero;
    }
    
    /**
     * Constructor para recrear Billetes vendidos ya existentes
     */
    public BilleteVendido(String id, Servicio servicio, Factura factura, String localizador,
            String nombreViajero, String dniViajero){
        this(id);
        this.servicio = servicio;
        this.factura = factura;
        this.localizador = localizador;
        this.nombreViajero = nombreViajero;
        this.dniViajero = dniViajero;     
    }
    
    @Override
    public boolean equals(Object object) {
        if(object instanceof BilleteVendido) {
            BilleteVendido billeteVendido = (BilleteVendido) object;
            return (billeteVendido != null) && (this.getId().equals(billeteVendido.getId()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public UUID getId() {
        return id;
    }
    
    public String getIdAsString() {
        return id.toString();
    }

    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * @return the servicio
     */
    public Servicio getServicio() {
        return servicio;
    }

    /**
     * @param servicio the servicio to set
     */
    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }

    /**
     * @return the factura
     */
    public Factura getFactura() {
        return factura;
    }

    /**
     * @param factura the factura to set
     */
    public void setFactura(Factura factura) {
        this.factura = factura;
    }
    
    /**
     * @return el numero de asiento (dos cifras)
     */
    public String getNumAsiento() {
        return localizador.substring(0,2);
    }
    
    /**
     * @return campo con otra informacion del viajero y fam. numerosa
     */
    public String getOtraInfo() {
        return localizador.substring(9);
    }
    
    public String getTotLocalizador() {
        return localizador;
    }

    /**
     * @return the localizador (7 chars)
     */
    public String getLocalizador() {
        return localizador.substring(2,9);
    }

    /**
     * @param localizador the localizador to set
     */
    public void setLocalizador(String localizador) {
        this.localizador = localizador;
    }

    /**
     * @return the nombreViajero
     */
    public String getNombreViajero() {
        return nombreViajero;
    }

    /**
     * @param nombreViajero the nombreViajero to set
     */
    public void setNombreViajero(String nombreViajero) {
        this.nombreViajero = nombreViajero;
    }

    /**
     * @return the dniViajero
     */
    public String getDniViajero() {
        return dniViajero;
    }

    /**
     * @param dniViajero the dniViajero to set
     */
    public void setDniViajero(String dniViajero) {
        this.dniViajero = dniViajero;
    }
    
}

