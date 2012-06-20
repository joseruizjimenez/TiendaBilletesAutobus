package persistence.ruta;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import model.Ruta;
import org.apache.log4j.Logger;

/**
 * Implementacion de RutaDAO para persistir la informacion con un pool de conexiones
 * 
 * @param DataSource el pool de conexiones
 * @param RutaPersistenceManager RutaDAO de pool
 * @param logger para generar las trazas
 */
public class RutaDAOPoolImplementation implements RutaDAO {
    private static RutaDAOPoolImplementation rutaPersistenceManager = null;
    private DataSource pool;
    private static final Logger logger = Logger.getLogger(RutaDAOPoolImplementation.class.getName());
    
    private RutaDAOPoolImplementation() {
    }

    public static RutaDAO getRutaDAOPoolImplementation() {
        if(rutaPersistenceManager == null)
            rutaPersistenceManager = new RutaDAOPoolImplementation();
        
        return rutaPersistenceManager;
    }

    @Override
    public boolean createRuta(Ruta ruta) {
        RutaDAO jDBCRutaDAO = prepareForExecutingQuery();
        if(jDBCRutaDAO == null){
            return false;
        }
        boolean isExecutedOK = jDBCRutaDAO.createRuta(ruta);
        releaseQueryResources(jDBCRutaDAO);
        return isExecutedOK;
    }

    @Override
    public Ruta readRuta(String id) {
        RutaDAO jDBCRutaDAO = prepareForExecutingQuery();
        if(jDBCRutaDAO == null){
            return null;
        }
        Ruta ruta = jDBCRutaDAO.readRuta(id);
        releaseQueryResources(jDBCRutaDAO);
        return ruta;
    }

    @Override
    public ArrayList<Ruta> listRuta(String origen, String destino) {
        RutaDAO jDBCRutaDAO = prepareForExecutingQuery();
        if(jDBCRutaDAO == null){
            return (new ArrayList<Ruta>());
        }
        ArrayList<Ruta> list = jDBCRutaDAO.listRuta(origen, destino);
        releaseQueryResources(jDBCRutaDAO);
        return list;
    }

    @Override
    public boolean deleteRuta(String id) {
        RutaDAO jDBCRutaDAO = prepareForExecutingQuery();
        if(jDBCRutaDAO == null){
            return false;
        }
        boolean isExecutedOK = jDBCRutaDAO.deleteRuta(id);
        releaseQueryResources(jDBCRutaDAO);
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
     * Las consultas individuales se hace creando un RutaDAOJDBCImplementation
     * @return RutaDAO
     */
    private RutaDAO prepareForExecutingQuery() {
        RutaDAOJDBCImplementation jDBCpersistenceManager = new RutaDAOJDBCImplementation();
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

    private void releaseQueryResources(RutaDAO  rutaDAO) {
        rutaDAO.disconnect();
    }

}
