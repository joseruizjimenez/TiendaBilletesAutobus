package persistence.servicio;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import model.Servicio;

/**
 * Esta interfaz define un patron de persistencia DAO para los discos
 */
public interface ServicioDAO {

    public boolean createServicio(Servicio servicio);

    public Servicio readServicio(String id);

    public ArrayList<Servicio> listServicio(String origen, String destino);

    public boolean deleteServicio(String id);

    public Map<UUID,Servicio> getServicioMap();
    
    
    /**
     * Metodo para crear la conexion con el sistema de persistencia.
     * En el caso de trabajar contra ficheros la url define parte del nombre
     * @param url
     * @param driver
     * @param user
     * @param password
     * @return true si hay exito, false en caso contrario
     */
    public boolean setUp(String url, String driver, String user, String password);
    
    /**
     * Cierra la conexion con el sistema de persistencia.
     * @return true si hay exito, false en caso contrario
     */
    public boolean disconnect();
}
