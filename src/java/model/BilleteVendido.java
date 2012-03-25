package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

/**
 * Producto de la tienda: Servicio
 * 
 * @param id identificador del disco
 * @param name nombre del disco
 * @param artist artista del disco
 * @param recordLabel distribuidora
 * @param shortComment descripcion corta
 * @param fullComment descripcion larga
 * @param type estilo musical
 * @param price precio del disco
 * @param creationDate fecha de creacion
 */
public class BilleteVendido implements Serializable {
    private UUID id = null;
    private Servicio servicio = null;
    private Factura factura = null;
    private String localizador = null;
    private String nombreViajero = null;
    private String dniViajero = null;
    
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
     * Constructor de un nuevo servicio
     * @param name nombre del disco
     * @param artist artista del disco
     * @param recordLabel discografica
     * @param shortComment comentario corto
     * @param fullComment comentario largo
     * @param type tipo de musica
     * @param price precio del disco
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
     * Constructor para recrear discos ya existentes
     * @param id identificador del disco original
     * @param name nombre del disco
     * @param artist artista del disco
     * @param recordLabel discografica
     * @param shortComment comentario corto
     * @param fullComment comentario largo
     * @param type tipo de musica
     * @param price precio del disco
     */
    public BilleteVendido(Servicio servicio, Factura factura, String localizador,
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

}

