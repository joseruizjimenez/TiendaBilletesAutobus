package persistence.billeteVendido;

import java.util.ArrayList;
import model.BilleteVendido;

/**
 * Esta interfaz define un patron de persistencia DAO para los discos
 */
public interface BilleteVendidoDAO {
    
    public boolean createBilleteVendido(BilleteVendido billeteVendido);
    
    public BilleteVendido readBilleteVendido(String id);
    
    public ArrayList<BilleteVendido> listBilleteVendido(String localizador,
            String nombreViajero, String dniViajero);

    public boolean deleteBilleteVendido(String id);
    
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
