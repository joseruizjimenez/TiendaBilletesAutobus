package persistence.record;

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
import model.Record;
import org.apache.log4j.Logger;

/**
 * Implementacion de RecordDAO para persistir la informacion con un pool de conexiones
 * 
 * @param DataSource el pool de conexiones
 * @param recordPersistenceManager RecordDAO de pool
 * @param logger para generar las trazas
 */
public class RecordDAOPoolImplementation implements RecordDAO {
    private static RecordDAOPoolImplementation recordPersistenceManager = null;
    private DataSource pool;
    private static final Logger logger = Logger.getLogger(RecordDAOPoolImplementation.class.getName());
    
    private RecordDAOPoolImplementation() {
    }

    public static RecordDAO getRecordDAOPoolImplementation() {
        if(recordPersistenceManager == null)
            recordPersistenceManager = new RecordDAOPoolImplementation();
        
        return recordPersistenceManager;
    }

    @Override
    public boolean createRecord(Record record) {
        RecordDAO jDBCRecordDAO = prepareForExecutingQuery();
        if(jDBCRecordDAO == null){
            return false;
        }
        boolean isExecutedOK = jDBCRecordDAO.createRecord(record);
        releaseQueryResources(jDBCRecordDAO);
        return isExecutedOK;
    }

    @Override
    public Record readRecord(String id) {
        RecordDAO jDBCRecordDAO = prepareForExecutingQuery();
        if(jDBCRecordDAO == null){
            return null;
        }
        Record record = jDBCRecordDAO.readRecord(id);
        releaseQueryResources(jDBCRecordDAO);
        return record;
    }

    @Override
    public ArrayList<Record> listRecord(String name, String artist,
            String recordLabel, String type) {
        RecordDAO jDBCRecordDAO = prepareForExecutingQuery();
        if(jDBCRecordDAO == null){
            return (new ArrayList<Record>());
        }
        ArrayList<Record> list = jDBCRecordDAO.listRecord(name,artist,recordLabel,type);
        releaseQueryResources(jDBCRecordDAO);
        return list;
    }

    @Override
    public boolean updateRecord(String id, Record record) {
        RecordDAO jDBCRecordDAO = prepareForExecutingQuery();
        if(jDBCRecordDAO == null){
            return false;
        }
        boolean isExecutedOK = jDBCRecordDAO.updateRecord(id, record);
        releaseQueryResources(jDBCRecordDAO);
        return isExecutedOK;
    }

    @Override
    public boolean deleteRecord(String id) {
        RecordDAO jDBCRecordDAO = prepareForExecutingQuery();
        if(jDBCRecordDAO == null){
            return false;
        }
        boolean isExecutedOK = jDBCRecordDAO.deleteRecord(id);
        releaseQueryResources(jDBCRecordDAO);
        return isExecutedOK;
    }
    
    @Override
    public Map<UUID,Record> getRecordMap() {
        RecordDAO jDBCRecordDAO = prepareForExecutingQuery();
        if(jDBCRecordDAO == null){
            return (new HashMap<UUID,Record>());
        }
        HashMap<UUID,Record> recordMap = (HashMap<UUID,Record>) jDBCRecordDAO.getRecordMap();
        releaseQueryResources(jDBCRecordDAO);
        return recordMap;
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
    private RecordDAO prepareForExecutingQuery() {
        RecordDAOJDBCImplementation jDBCpersistenceManager = new RecordDAOJDBCImplementation();
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

    private void releaseQueryResources(RecordDAO  recordDAO) {
        recordDAO.disconnect();
    }

}
