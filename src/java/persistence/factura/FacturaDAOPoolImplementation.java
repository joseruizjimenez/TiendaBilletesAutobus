package persistence.factura;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import model.Factura;
import org.apache.log4j.Logger;

/**
 * Implementacion de FacturaDAO para persistir la informacion con un pool de conexiones
 * 
 * @param DataSource el pool de conexiones
 * @param FacturaPersistenceManager FacturaDAO de pool
 * @param logger para generar las trazas
 */
public class FacturaDAOPoolImplementation implements FacturaDAO {
    private static FacturaDAOPoolImplementation facturaPersistenceManager = null;
    private DataSource pool;
    private static final Logger logger = Logger.getLogger(FacturaDAOPoolImplementation.class.getName());
    
    private FacturaDAOPoolImplementation() {
    }

    public static FacturaDAO getFacturaDAOPoolImplementation() {
        if(facturaPersistenceManager == null)
            facturaPersistenceManager = new FacturaDAOPoolImplementation();
        
        return facturaPersistenceManager;
    }

    @Override
    public boolean createFactura(Factura factura) {
        FacturaDAO jDBCFacturaDAO = prepareForExecutingQuery();
        if(jDBCFacturaDAO == null){
            return false;
        }
        boolean isExecutedOK = jDBCFacturaDAO.createFactura(factura);
        releaseQueryResources(jDBCFacturaDAO);
        return isExecutedOK;
    }

    @Override
    public Factura readFactura(String id) {
        FacturaDAO jDBCFacturaDAO = prepareForExecutingQuery();
        if(jDBCFacturaDAO == null){
            return null;
        }
        Factura factura = jDBCFacturaDAO.readFactura(id);
        releaseQueryResources(jDBCFacturaDAO);
        return factura;
    }

    @Override
    public ArrayList<Factura> listFactura(String dni, String numTarjeta) {
        FacturaDAO jDBCFacturaDAO = prepareForExecutingQuery();
        if(jDBCFacturaDAO == null){
            return (new ArrayList<Factura>());
        }
        ArrayList<Factura> list = jDBCFacturaDAO.listFactura(dni, numTarjeta);
        releaseQueryResources(jDBCFacturaDAO);
        return list;
    }

    @Override
    public boolean deleteFactura(String id) {
        FacturaDAO jDBCFacturaDAO = prepareForExecutingQuery();
        if(jDBCFacturaDAO == null){
            return false;
        }
        boolean isExecutedOK = jDBCFacturaDAO.deleteFactura(id);
        releaseQueryResources(jDBCFacturaDAO);
        return isExecutedOK;
    }

    @Override
    public boolean setUp(String url, String driver, String user, String password) {
        Context env = null;
        try {
            env = (Context) new InitialContext().lookup("java:comp/env");
            pool = (DataSource) env.lookup("jdbc/tiendaonline");
            if(pool == null){
                logger.error("No se encontro el DataSource");
                return false;
            }
        } catch (NamingException ex) {
            logger.error("No se pudo abrir la conexión contra base de datos", ex);
            return false;
        }
        return true;     
    }

    @Override
    public boolean disconnect() {
       return true;
    }
    
    /**
     * Las consultas individuales se hace creando un FacturaDAOJDBCImplementation
     * @return FacturaDAO
     */
    private FacturaDAO prepareForExecutingQuery() {
        FacturaDAOJDBCImplementation jDBCpersistenceManager = new FacturaDAOJDBCImplementation();
        Connection connection;
        try {
            connection = pool.getConnection();
        } catch (SQLException ex) {
            logger.error("No se pudo abrir la conexion contra la base de datos", ex);
            return null;
        }
        jDBCpersistenceManager.setConnection(connection);
        return jDBCpersistenceManager;
    }

    private void releaseQueryResources(FacturaDAO  recordDAO) {
        recordDAO.disconnect();
    }

}
