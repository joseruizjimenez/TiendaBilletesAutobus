package persistence.billeteVendido;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import model.BilleteVendido;
import org.apache.log4j.Logger;

/**
 * Implementacion de RecordDAO para persistir la informacion con un pool de conexiones
 * 
 * @param DataSource el pool de conexiones
 * @param recordPersistenceManager RecordDAO de pool
 * @param logger para generar las trazas
 */
public class BilleteVendidoDAOPoolImplementation implements BilleteVendidoDAO {
    private static BilleteVendidoDAOPoolImplementation billeteVendidoPersistenceManager = null;
    private DataSource pool;
    private static final Logger logger = Logger.getLogger(BilleteVendidoDAOPoolImplementation.class.getName());
    
    private BilleteVendidoDAOPoolImplementation() {
    }

    public static BilleteVendidoDAO getBilleteVendidoDAOPoolImplementation() {
        if(billeteVendidoPersistenceManager == null)
            billeteVendidoPersistenceManager = new BilleteVendidoDAOPoolImplementation();
        
        return billeteVendidoPersistenceManager;
    }

    @Override
    public boolean createBilleteVendido(BilleteVendido billeteVendido) {
        BilleteVendidoDAO jDBCBilleteVendidoDAO = prepareForExecutingQuery();
        if(jDBCBilleteVendidoDAO == null){
            return false;
        }
        boolean isExecutedOK = jDBCBilleteVendidoDAO.createBilleteVendido(billeteVendido);
        releaseQueryResources(jDBCBilleteVendidoDAO);
        return isExecutedOK;
    }

    @Override
    public BilleteVendido readBilleteVendido(String id) {
        BilleteVendidoDAO jDBCBilleteVendidoDAO = prepareForExecutingQuery();
        if(jDBCBilleteVendidoDAO == null){
            return null;
        }
        BilleteVendido billeteVendido = jDBCBilleteVendidoDAO.readBilleteVendido(id);
        releaseQueryResources(jDBCBilleteVendidoDAO);
        return billeteVendido;
    }

    @Override
    public ArrayList<BilleteVendido> listBilleteVendido(String localizador,
            String nombreViajero, String dniViajero) {
        BilleteVendidoDAO jDBCBilleteVendidoDAO = prepareForExecutingQuery();
        if(jDBCBilleteVendidoDAO == null){
            return (new ArrayList<BilleteVendido>());
        }
        ArrayList<BilleteVendido> list = jDBCBilleteVendidoDAO.listBilleteVendido(
                localizador, nombreViajero, dniViajero);
        releaseQueryResources(jDBCBilleteVendidoDAO);
        return list;
    }

    @Override
    public boolean deleteBilleteVendido(String id) {
        BilleteVendidoDAO jDBCBilleteVendidoDAO = prepareForExecutingQuery();
        if(jDBCBilleteVendidoDAO == null){
            return false;
        }
        boolean isExecutedOK = jDBCBilleteVendidoDAO.deleteBilleteVendido(id);
        releaseQueryResources(jDBCBilleteVendidoDAO);
        return isExecutedOK;
    }
    
    /*@Override
    public Map<UUID,Record> getRecordMap() {
        RecordDAO jDBCRecordDAO = prepareForExecutingQuery();
        if(jDBCRecordDAO == null){
            return (new HashMap<UUID,Record>());
        }
        HashMap<UUID,Record> recordMap = (HashMap<UUID,Record>) jDBCRecordDAO.getRecordMap();
        releaseQueryResources(jDBCRecordDAO);
        return recordMap;
    }*/

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
    private BilleteVendidoDAO prepareForExecutingQuery() {
        BilleteVendidoDAOJDBCImplementation jDBCpersistenceManager = new BilleteVendidoDAOJDBCImplementation();
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

    private void releaseQueryResources(BilleteVendidoDAO  billeteVendidoDAO) {
        billeteVendidoDAO.disconnect();
    }

}
