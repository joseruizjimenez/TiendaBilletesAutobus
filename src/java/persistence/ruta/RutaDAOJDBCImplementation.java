package persistence.ruta;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import model.Ruta;
import org.apache.log4j.Logger;

/**
 * Implementacion de RecordDAO para persistir la informacion con JDBC en MySQL
 * 
 * @param lockOfConnection objeto para controlar los accesos no concurrentes
 * @param connection conexion con la base de datos
 * @param recordPersistenceManager RecordDAO de jdbc
 * @param logger para generar las trazas
 */
public class RutaDAOJDBCImplementation implements RutaDAO{
    private final Object lockOfConnection = new Object();
    private Connection connection = null;
    private static RutaDAOJDBCImplementation rutaPersistenceManager = null;
    private static final Logger logger = Logger.getLogger(RutaDAOJDBCImplementation.class.getName());
    
    //Visibilidad de paquete para usarlo con RutaDAOPoolImplementation
    RutaDAOJDBCImplementation() {
    }
    
    public static RutaDAO getRutaDAOJDBCImplementation() {
        if(rutaPersistenceManager == null)
            rutaPersistenceManager = new RutaDAOJDBCImplementation();
        
        return rutaPersistenceManager;
    }
    
    @Override
    public boolean createRuta(Ruta ruta) {
        String query = "insert into RUTAS values(?,?,?,?)";
        PreparedStatement statement;
        try{
            synchronized (lockOfConnection) {
                statement = connection.prepareStatement(query);
            }
            statement.setString(1,ruta.getIdAsString());
            statement.setString(2,ruta.getOrigen());
            statement.setString(3,ruta.getDestino());
            statement.setString(4,ruta.getParadasAsString());
            statement.execute();
            logger.trace("Insertada la ruta en BD: " + ruta.getIdAsString());
            return true;
        } catch (SQLException ex) {
            logger.error("Error al crear la ruta en al BD", ex);
            return false;
        }
    }

    @Override
    public Ruta readRuta(String id) {
        String query = "select * from RUTAS where ID =?";
        PreparedStatement statement;
        ResultSet resultSet = null;
        Ruta ruta = null;
        try {
            synchronized (lockOfConnection) {
                statement = connection.prepareStatement(query);
            }
            statement.setString(1, id);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                ruta = new Ruta(id);
                ruta.setOrigen(resultSet.getString("ORIGEN"));
                ruta.setDestino(resultSet.getString("DESTINO"));
                ruta.setParadasFromString(resultSet.getString("PARADAS"));
            }
        } catch (SQLException ex) {
            logger.error("Error al recuperar un disco", ex);
            ruta = null;
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException ex) {
                    logger.error("Error al cerrar la conexon a la base de datos", ex);
                }
            }
        }
        return ruta;
    }

    @Override
    public ArrayList<Ruta> listRuta(String origen, String destino) {
        ArrayList<Ruta> list = new ArrayList();
        String query = "select * from RUTAS";
        PreparedStatement statement;
        ResultSet resultSet = null;
        Ruta ruta = null;
        int origenPosition=1, destinoPosition=2;
        boolean isWhereWritten = false;
        
        try {
            if (origen != null) {
                query = query.concat(" where");
                isWhereWritten = true;
                query = query.concat(" ORIGEN =?");
            } else {
                origenPosition = 0;
                destinoPosition--;
            }
            if (destino != null) {
                if (!isWhereWritten) {
                    query = query.concat(" where");
                    isWhereWritten = true;
                } else {
                    query = query.concat(" AND");
                }
                query = query.concat(" DESTINO =?");
            } else {
                destinoPosition = 0;
            }

            synchronized (lockOfConnection) {
                statement = connection.prepareStatement(query);
            }
            if (origenPosition != 0) {
                statement.setString(origenPosition, origen);
            }
            if (destinoPosition != 0) {
                statement.setString(destinoPosition, destino);
            }
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                ruta = new Ruta(resultSet.getString("ID"));
                ruta.setOrigen(resultSet.getString("ORIGEN"));
                ruta.setDestino(resultSet.getString("DESTINO"));
                ruta.setParadasFromString(resultSet.getString("PARADAS"));
                list.add(ruta);
            }
        } catch (SQLException ex) {
            logger.error("Error al recuperar una ruta", ex);
            list.clear();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException ex) {
                    logger.error("Error al cerrar la conexon a la base de datos", ex);
                }
            }
        }        
        return list;
    }

    @Override
    public boolean deleteRuta(String id) {
        String query = "delete from RUTAS where ID = ?";
        PreparedStatement statement;
        try {
            synchronized (lockOfConnection) {
                statement = connection.prepareStatement(query);
            }
            statement.setString(1, id);
            statement.execute();
            logger.trace("Borrada la ruta en BD: "+id);
            return true;
        } catch (SQLException ex) {
            logger.error("Error al borrar una ruta", ex);
            return false;
        }
    }
    
    /*@Override
    public Map<UUID,Record> getRecordMap() {
        HashMap<UUID,Record> recordMap = new HashMap();
        String query = "select * from RECORDS";
        PreparedStatement statement;
        ResultSet resultSet = null;
        Record record = null;
        
        try {
            synchronized (lockOfConnection) {
                statement = connection.prepareStatement(query);
            } 
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String recordId = resultSet.getString("ID");
                record = new Record(recordId);
                record.setName(resultSet.getString("NAME"));
                record.setArtist(resultSet.getString("ARTIST"));
                record.setRecordLabel(resultSet.getString("RECORDLABEL"));
                record.setShortComment(resultSet.getString("SHORTCOMMENT"));
                record.setFullComment(resultSet.getString("FULLCOMMENT"));
                record.setType(resultSet.getString("TYPE"));
                record.setPrice(resultSet.getString("PRICE"));
                recordMap.put(UUID.fromString(recordId), record);
            }
        } catch (SQLException ex) {
            logger.error("Error al recuperar un disco", ex);
            recordMap.clear();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException ex) {
                    logger.error("Error al cerrar la conexon a la base de datos", ex);
                }
            }
        }        
        return recordMap;
    }*/

    @Override
    public boolean setUp(String url, String driver, String user, String password) {
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException ex) {
            logger.error("No se encontro el driver para la base de datos", ex);
            return false;
        } catch (SQLException ex) {
            logger.error("No se pudo establecer la conexion con la base de datos", ex);
            return false;
        }
        return true;
    }

    @Override
    public boolean disconnect() {
       try {
            connection.close();
        } catch (SQLException ex) {
            logger.error("Conexión a la base de datos no cerrada", ex);
            return false;
        }
        return true;
    }
    
    /**
     * Para establecer conexiones en el RecordDAOPoolImplementation mediante esta clase
     * @param connection
     */
    public void setConnection(Connection connection) {
        synchronized (lockOfConnection) {
            this.connection = connection;
        }
    }

}