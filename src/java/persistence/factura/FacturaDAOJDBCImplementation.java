package persistence.factura;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import model.Factura;
import org.apache.log4j.Logger;

/**
 * Implementacion de RecordDAO para persistir la informacion con JDBC en MySQL
 * 
 * @param lockOfConnection objeto para controlar los accesos no concurrentes
 * @param connection conexion con la base de datos
 * @param recordPersistenceManager RecordDAO de jdbc
 * @param logger para generar las trazas
 */
public class FacturaDAOJDBCImplementation implements FacturaDAO{
    private final Object lockOfConnection = new Object();
    private Connection connection = null;
    private static FacturaDAOJDBCImplementation facturaPersistenceManager = null;
    private static final Logger logger = Logger.getLogger(FacturaDAOJDBCImplementation.class.getName());
    
    //Visibilidad de paquete para usarlo con FacturaDAOPoolImplementation
    FacturaDAOJDBCImplementation() {
    }
    
    public static FacturaDAO getFacturaDAOJDBCImplementation() {
        if(facturaPersistenceManager == null)
            facturaPersistenceManager = new FacturaDAOJDBCImplementation();
        
        return facturaPersistenceManager;
    }
    
    @Override
    public boolean createFactura(Factura factura) {
        String query = "insert into FACTURAS values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement statement;
        try{
            synchronized (lockOfConnection) {
                statement = connection.prepareStatement(query);
            }
            statement.setString(1,factura.getIdAsString());
            statement.setString(2,factura.getCIF());
            statement.setString(3,factura.getNombreComprador());
            statement.setString(4,factura.getDNI());
            statement.setString(5,factura.getEmail());
            statement.setString(6,factura.getMvl());
            statement.setString(7,factura.getNumTarjeta());
            statement.setString(8,factura.getCalle());
            statement.setString(9,factura.getPoblacion());
            statement.setString(10,factura.getProvincia());
            statement.setString(11,factura.getCodPostal());
            statement.setString(12,factura.getTransactionDateAsString());
            statement.setString(13,factura.getTotalAsString());
            statement.execute();
            logger.trace("Insertada la factura en BD: "+ factura.getIdAsString());
            return true;
        } catch (SQLException ex) {
            logger.error("Error al crear la factura", ex);
            return false;
        }
    }

    @Override
    public Factura readFactura(String id) {
        String query = "select * from FACTURAS where ID =?";
        PreparedStatement statement;
        ResultSet resultSet = null;
        Factura factura = null;
        try {
            synchronized (lockOfConnection) {
                statement = connection.prepareStatement(query);
            }
            statement.setString(1, id);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                factura = new Factura(id);
                factura.setCIF(resultSet.getString("CIF"));
                factura.setNombreComprador(resultSet.getString("NOMBRE_COMPRADOR"));
                factura.setDNI(resultSet.getString("DNI"));
                factura.setEmail(resultSet.getString("EMAIL"));
                factura.setMvl(resultSet.getString("MVL"));
                factura.setNumTarjeta(resultSet.getString("NUM_TARJETA"));
                factura.setCalle(resultSet.getString("CALLE"));
                factura.setPoblacion(resultSet.getString("POBLACION"));
                factura.setProvincia(resultSet.getString("PROVINCIA"));
                factura.setCodPostal(resultSet.getString("COD_POSTAL"));
                try {
                    factura.setTransactionDate(resultSet.getString("TRANSACTION_DATE"));
                } catch (ParseException ex) {
                    logger.warn("Error parseando la fecha de transaccion de la factura", ex);
                }
                factura.setTotal(resultSet.getString("TOTAL"));
            }
        } catch (SQLException ex) {
            logger.error("Error al recuperar una factura", ex);
            factura = null;
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException ex) {
                    logger.error("Error al cerrar la conexon a la base de datos", ex);
                }
            }
        }
        return factura;
    }

    @Override
    public ArrayList<Factura> listFactura(String dni, String numTarjeta) {
        ArrayList<Factura> list = new ArrayList();
        String query = "select * from FACTURAS";
        PreparedStatement statement;
        ResultSet resultSet = null;
        Factura factura = null;
        int dniPosition=1, numTarjetaPosition=2;
        boolean isWhereWritten = false;
        
        try {
            if (dni != null) {
                query = query.concat(" where");
                isWhereWritten = true;
                query = query.concat(" DNI =?");
            } else {
                dniPosition = 0;
                numTarjetaPosition--;
            }
            if (numTarjeta != null) {
                if (!isWhereWritten) {
                    query = query.concat(" where");
                    isWhereWritten = true;
                } else {
                    query = query.concat(" AND");
                }
                query = query.concat(" NUM_TARJETA =?");
            } else {
                numTarjetaPosition = 0;
            }

            synchronized (lockOfConnection) {
                statement = connection.prepareStatement(query);
            }
            if (dniPosition != 0) {
                statement.setString(dniPosition, dni);
            }
            if (numTarjetaPosition != 0) {
                statement.setString(numTarjetaPosition, numTarjeta);
            }
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                factura = new Factura(resultSet.getString("ID"));
                factura.setCIF(resultSet.getString("CIF"));
                factura.setNombreComprador(resultSet.getString("NOMBRE_COMPRADOR"));
                factura.setDNI(resultSet.getString("DNI"));
                factura.setEmail(resultSet.getString("EMAIL"));
                factura.setMvl(resultSet.getString("MVL"));
                factura.setNumTarjeta(resultSet.getString("NUM_TARJETA"));
                factura.setCalle(resultSet.getString("CALLE"));
                factura.setPoblacion(resultSet.getString("POBLACION"));
                factura.setProvincia(resultSet.getString("PROVINCIA"));
                factura.setCodPostal(resultSet.getString("COD_POSTAL"));
                list.add(factura);
            }
        } catch (SQLException ex) {
            logger.error("Error al recuperar una factura", ex);
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
    public boolean deleteFactura(String id) {
        String query = "delete from FACTURAS where ID = ?";
        PreparedStatement statement;
        try {
            synchronized (lockOfConnection) {
                statement = connection.prepareStatement(query);
            }
            statement.setString(1, id);
            statement.execute();
            logger.trace("Borrado el disco en BD: "+id);
            return true;
        } catch (SQLException ex) {
            logger.error("Error al borrar un disco", ex);
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