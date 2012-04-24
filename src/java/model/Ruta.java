package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 * Ruta
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
public class Ruta implements Serializable {
    private UUID id = null;
    private String origen = null;
    private String destino = null;
    private ArrayList<String> paradas = null;

    
    public Ruta() {
        this.id = UUID.randomUUID();
    }
    
    public Ruta(String id){
        this.id = UUID.fromString(id);
    }
    
    public Ruta(UUID id){
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
    public Ruta(String origen, String destino, ArrayList<String> paradas){
        this();
        this.origen = origen;
        this.destino = destino;
        this.paradas = paradas;
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
    public Ruta(String id, String origen, String destino, String footprint){
        this(id);
        this.origen = origen;
        this.destino = destino;
        this.paradas = new ArrayList<String>();
        String[] paradasArray = footprint.split(";");
        this.paradas.addAll(Arrays.asList(paradasArray));       
    }
    
    @Override
    public boolean equals(Object object) {
        if(object instanceof Ruta) {
            Ruta ruta = (Ruta) object;
            return (ruta != null) && (this.getId().equals(ruta.getId()));
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
     * @return the origen
     */
    public String getOrigen() {
        return origen;
    }

    /**
     * @param origen the origen to set
     */
    public void setOrigen(String origen) {
        this.origen = origen;
    }

    /**
     * @return the destino
     */
    public String getDestino() {
        return destino;
    }

    /**
     * @param destino the destino to set
     */
    public void setDestino(String destino) {
        this.destino = destino;
    }

    /**
     * @return the paradas
     */
    public ArrayList<String> getParadas() {
        return paradas;
    }
    
    public String getParadasAsString() {
        StringBuilder footprintBuilder = new StringBuilder("");
        if(!this.paradas.isEmpty()) {
            for(int i=0; i < paradas.size(); i++) {
                footprintBuilder.append(paradas.get(i));
                footprintBuilder.append(",");
                
            }
        }
        return footprintBuilder.toString();
    }

    /**
     * @param paradas the paradas to set
     */
    public void setParadas(ArrayList<String> paradas) {
        this.paradas = paradas;
    }
    
    public void setParadasFromString(String footprint) {
        this.paradas = new ArrayList<String>();
        String[] paradasArray = footprint.split(";");
        this.paradas.addAll(Arrays.asList(paradasArray));
    }

}

