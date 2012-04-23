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
public class Servicio implements Serializable {
    private UUID id = null;
    private Ruta ruta = null;
    //private Autobus autobus = null;
    private int totalPlazas = 0;
    private int plazasOcupadas = 0;
    private String claseServicio = null;
    private String horaSalida = null;
    private String horaLlegada = null;
    private String diasSemana = null;
    private BigDecimal precio = null;
    
    public Servicio() {
        this.id = UUID.randomUUID();
    }
    
    public Servicio(String id){
        this.id = UUID.fromString(id);
    }
    
    public Servicio(UUID id){
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
    public Servicio(Ruta ruta, boolean isServicioSupra, int totalPlazas, int plazasOcupadas,
            String claseServicio, String horaSalida, String horaLlegada,
            String diasSemana, BigDecimal precio){
        this();
        this.ruta = ruta;
        this.totalPlazas = totalPlazas;
        this.plazasOcupadas = plazasOcupadas;
        this.claseServicio = claseServicio;
        this.horaSalida = horaSalida;
        this.horaLlegada = horaLlegada;
        this.diasSemana = diasSemana;
        this.precio = precio;        
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
    public Servicio(String id, Ruta ruta, boolean isServicioSupra, int totalPlazas,
            int plazasOcupadas, String claseServicio, String horaSalida,
            String horaLlegada, String diasSemana, BigDecimal precio){
        this(id);
        this.ruta = ruta;
        this.totalPlazas = totalPlazas;
        this.plazasOcupadas = plazasOcupadas;
        this.claseServicio = claseServicio;
        this.horaSalida = horaSalida;
        this.horaLlegada = horaLlegada;
        this.diasSemana = diasSemana;
        this.precio = precio;        
    }
    
    @Override
    public boolean equals(Object object) {
        if(object instanceof Servicio) {
            Servicio servicio = (Servicio) object;
            return (servicio != null) && (this.getId().equals(servicio.getId()));
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

    public BigDecimal getPrecio() {
        return precio;
    }
    
    public String getPriceAsString() {
        return precio.toString();
    }
    
    /**
     * @return string con el precio formateado correctamente,  añadiendo unas
     *         separaciones en los miles, millones... y añadiendo EUR
     */
    public String getPriceAsFormattedString() {
        NumberFormat n = NumberFormat.getCurrencyInstance(Locale.FRANCE); 
        double doublePrice = precio.doubleValue();
        return n.format(doublePrice);
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
    
    public void setPrecio(String precio) {
        this.precio = new BigDecimal(precio);
    }

    /**
     * @return the ruta
     */
    public Ruta getRuta() {
        return ruta;
    }

    /**
     * @param ruta the ruta to set
     */
    public void setRuta(Ruta ruta) {
        this.ruta = ruta;
    }

    /**
     * @return the totalPlazas
     */
    public int getTotalPlazas() {
        return totalPlazas;
    }

    /**
     * @param totalPlazas the totalPlazas to set
     */
    public void setTotalPlazas(int totalPlazas) {
        this.totalPlazas = totalPlazas;
    }

    /**
     * @return the plazasOcupadas
     */
    public int getPlazasOcupadas() {
        return plazasOcupadas;
    }

    /**
     * @param plazasOcupadas the plazasOcupadas to set
     */
    public void setPlazasOcupadas(int plazasOcupadas) {
        this.plazasOcupadas = plazasOcupadas;
    }

    /**
     * @return the claseServicio
     */
    public String getClaseServicio() {
        return claseServicio;
    }

    /**
     * @param claseServicio the claseServicio to set
     */
    public void setClaseServicio(String claseServicio) {
        this.claseServicio = claseServicio;
    }

    /**
     * @return the horaSalida
     */
    public String getHoraSalida() {
        return horaSalida;
    }

    /**
     * @param horaSalida the horaSalida to set
     */
    public void setHoraSalida(String horaSalida) {
        this.horaSalida = horaSalida;
    }

    /**
     * @return the horaLlegada
     */
    public String getHoraLlegada() {
        return horaLlegada;
    }

    /**
     * @param horaLlegada the horaLlegada to set
     */
    public void setHoraLlegada(String horaLlegada) {
        this.horaLlegada = horaLlegada;
    }

    /**
     * @return the diasSemana
     */
    public String getDiasSemana() {
        return diasSemana;
    }

    /**
     * @param diasSemana the diasSemana to set
     */
    public void setDiasSemana(String diasSemana) {
        this.diasSemana = diasSemana;
    }

}
