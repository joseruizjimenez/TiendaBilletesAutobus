package persistence.factura;

import java.util.ArrayList;
import model.Factura;

/**
 * Esta interfaz define un patron de persistencia DAO para los discos
 */
public interface FacturaDAO {
    
    public boolean createFactura(Factura factura);

    public Factura readFactura(String id);
    
    public ArrayList<Factura> listFactura(String dni, String numTarjeta);

    public boolean deleteFactura(String id);
    
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
