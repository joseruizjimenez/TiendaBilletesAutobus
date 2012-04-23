package persistence.servicio;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import model.Servicio;
import org.apache.log4j.Logger;

/**
 * Implementacion de RecordDAO para persistir la informacion con un pool de conexiones
 * 
 * @param DataSource el pool de conexiones
 * @param recordPersistenceManager RecordDAO de pool
 * @param logger para generar las trazas
 */
public class ServicioDAOPoolImplementation implements ServicioDAO {
    private static ServicioDAOPoolImplementation servicioPersistenceManager = null;
    private DataSource pool;
    private static final Logger logger = Logger.getLogger(ServicioDAOPoolImplementation.class.getName());
    
    private ServicioDAOPoolImplementation() {
    }

    public static ServicioDAO getServicioDAOPoolImplementation() {
        if(servicioPersistenceManager == null)
            servicioPersistenceManager = new ServicioDAOPoolImplementation();
        
        return servicioPersistenceManager;
    }

    @Override
    public boolean createServicio(Servicio servicio) {
        ServicioDAO jDBCServicioDAO = prepareForExecutingQuery();
        if(jDBCServicioDAO == null){
            return false;
        }
        boolean isExecutedOK = jDBCServicioDAO.createServicio(servicio);
        releaseQueryResources(jDBCServicioDAO);
        return isExecutedOK;
    }

    @Override
    public Servicio readServicio(String id) {
        ServicioDAO jDBCServicioDAO = prepareForExecutingQuery();
        if(jDBCServicioDAO == null){
            return null;
        }
        Servicio servicio = jDBCServicioDAO.readServicio(id);
        releaseQueryResources(jDBCServicioDAO);
        return servicio;
    }

    @Override
    public ArrayList<Servicio> listServicio(String origen, String destino) {
        ServicioDAO jDBCServicioDAO = prepareForExecutingQuery();
        if(jDBCServicioDAO == null){
            return (new ArrayList<Servicio>());
        }
        ArrayList<Servicio> list = jDBCServicioDAO.listServicio(origen, destino);
        releaseQueryResources(jDBCServicioDAO);
        return list;
    }

    @Override
    public boolean deleteServicio(String id) {
        ServicioDAO jDBCServicioDAO = prepareForExecutingQuery();
        if(jDBCServicioDAO == null){
            return false;
        }
        boolean isExecutedOK = jDBCServicioDAO.deleteServicio(id);
        releaseQueryResources(jDBCServicioDAO);
        return isExecutedOK;
    }
    
    @Override
    public Map<UUID,Servicio> getServicioMap() {
        ServicioDAO jDBCServicioDAO = prepareForExecutingQuery();
        if(jDBCServicioDAO == null){
            return (new HashMap<UUID,Servicio>());
        }
        HashMap<UUID,Servicio> servicioMap = (HashMap<UUID,Servicio>) jDBCServicioDAO.getServicioMap();
        releaseQueryResources(jDBCServicioDAO);
        return servicioMap;
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
     * Las consultas individuales se hace creando un RecordDAOJDBCImplementation
     * @return RecordDAO
     */
    private ServicioDAO prepareForExecutingQuery() {
        ServicioDAOJDBCImplementation jDBCpersistenceManager = new ServicioDAOJDBCImplementation();
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

    private void releaseQueryResources(ServicioDAO  servicioDAO) {
        servicioDAO.disconnect();
    }

}
