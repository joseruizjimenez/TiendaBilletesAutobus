package persistence.ruta;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import model.Ruta;

/**
 * Esta interfaz define un patron de persistencia DAO para los discos
 */
public interface RutaDAO {
    
    public boolean createRuta(Ruta ruta);

    public Ruta readRuta(String id);

    public ArrayList<Ruta> listRuta(String origen, String destino);

    public boolean deleteRuta(String id);
    
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
