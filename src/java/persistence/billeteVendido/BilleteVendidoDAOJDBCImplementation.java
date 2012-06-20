package persistence.billeteVendido;

import java.sql.*;
import java.util.ArrayList;
import model.BilleteVendido;
import model.Factura;
import model.Servicio;
import org.apache.log4j.Logger;

/**
 * Implementacion de BilleteVendidoDAO para persistir la informacion
 *   con JDBC en MySQL
 * 
 * @param lockOfConnection objeto para controlar los accesos no concurrentes
 * @param connection conexion con la base de datos
 * @param recordPersistenceManager BilleteVendidoDAO de jdbc
 * @param logger para generar las trazas
 */
public class BilleteVendidoDAOJDBCImplementation implements BilleteVendidoDAO{
    private final Object lockOfConnection = new Object();
    private Connection connection = null;
    private static BilleteVendidoDAOJDBCImplementation billeteVendidoPersistenceManager = null;
    private static final Logger logger = 
            Logger.getLogger(BilleteVendidoDAOJDBCImplementation.class.getName());
    
    //Visibilidad de paquete para usarlo con BilleteVendidoDAOPoolImplementation
    BilleteVendidoDAOJDBCImplementation() {
    }
    
    public static BilleteVendidoDAO getBilleteVendidoDAOJDBCImplementation() {
        if(billeteVendidoPersistenceManager == null)
            billeteVendidoPersistenceManager = new BilleteVendidoDAOJDBCImplementation();
        
        return billeteVendidoPersistenceManager;
    }
    
    @Override
    public boolean createBilleteVendido(BilleteVendido billeteVendido) {
        String query = "insert into BILLETESVENDIDOS values(?,?,?,?,?,?)";
        PreparedStatement statement;
        try{
            synchronized (lockOfConnection) {
                statement = connection.prepareStatement(query);
            }
            statement.setString(1,billeteVendido.getIdAsString());
            statement.setString(2,billeteVendido.getServicio().getIdAsString());
            statement.setString(3,billeteVendido.getFactura().getIdAsString());
            statement.setString(4,billeteVendido.getTotLocalizador());
            statement.setString(5,billeteVendido.getNombreViajero());
            statement.setString(6,billeteVendido.getDniViajero());
            statement.execute();
            logger.trace("Insertado el billete vendido en BD: " +
                    billeteVendido.getIdAsString());
            return true;
        } catch (SQLException ex) {
            logger.error("Error al crear el billete vendido", ex);
            return false;
        }
    }

    @Override
    public BilleteVendido readBilleteVendido(String id) {
        String query = "select * from BILLETESVENDIDOS where ID =?";
        PreparedStatement statement;
        ResultSet resultSet = null;
        BilleteVendido billeteVendido = null;
        try {
            synchronized (lockOfConnection) {
                statement = connection.prepareStatement(query);
            }
            statement.setString(1, id);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                billeteVendido = new BilleteVendido(id);
                billeteVendido.setServicio(new Servicio(resultSet.getString("SERVICIO")));
                billeteVendido.setFactura(new Factura(resultSet.getString("FACTURA")));
                billeteVendido.setLocalizador(resultSet.getString("LOCALIZADOR"));
                billeteVendido.setNombreViajero(resultSet.getString("NOMBRE_VIAJERO"));
                billeteVendido.setDniViajero(resultSet.getString("DNI_VIAJERO"));
            }
        } catch (SQLException ex) {
            logger.error("Error al recuperar un billete vendido", ex);
            billeteVendido = null;
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException ex) {
                    logger.error("Error al cerrar la conexon a la base de datos", ex);
                }
            }
        }
        return billeteVendido;
    }

    @Override
    public ArrayList<BilleteVendido> listBilleteVendido(String localizador,
            String nombreViajero, String dniViajero) {
        ArrayList<BilleteVendido> list = new ArrayList();
        String query = "select * from BILLETESVENDIDOS";
        PreparedStatement statement;
        ResultSet resultSet = null;
        BilleteVendido billeteVendido = null;
        int localizadorPosition=1, nombreViajeroPosition=2, dniViajeroPosition=3;
        boolean isWhereWritten = false;
              
        try {
            if(!localizador.equals("") || !nombreViajero.equals("") || !dniViajero.equals("")) { 
                if (localizador != null) {
                    query = query.concat(" where");
                    isWhereWritten = true;
                    query = query.concat(" LOCALIZADOR =?");
                } else {
                    localizadorPosition = 0;
                    nombreViajeroPosition--;
                    dniViajeroPosition--;
                }
                if (nombreViajero != null) {
                    if (!isWhereWritten) {
                        query = query.concat(" where");
                        isWhereWritten = true;
                    } else {
                        query = query.concat(" AND");
                    }
                    query = query.concat(" NOMBRE_VIAJERO =?");
                } else {
                    nombreViajeroPosition = 0;
                    dniViajeroPosition--;
                }
                if (dniViajero != null) {
                    if (!isWhereWritten) {
                        query = query.concat(" where");
                        isWhereWritten = true;
                    } else {
                        query = query.concat(" AND");
                    }
                    query = query.concat(" DNI_VIAJERO =?");
                } else {
                    dniViajeroPosition = 0;
                }

                synchronized (lockOfConnection) {
                    statement = connection.prepareStatement(query);
                }
                if (localizadorPosition != 0) {
                    statement.setString(localizadorPosition, localizador);
                }
                if (nombreViajeroPosition != 0) {
                    statement.setString(nombreViajeroPosition, nombreViajero);
                }
                if (dniViajeroPosition != 0) {
                    statement.setString(dniViajeroPosition, dniViajero);
                }
            } else {
                synchronized (lockOfConnection) {
                    statement = connection.prepareStatement(query);
                }
            }
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                billeteVendido = new BilleteVendido(resultSet.getString("ID"));
                billeteVendido.setServicio(new Servicio(resultSet.getString("SERVICIO")));
                billeteVendido.setFactura(new Factura(resultSet.getString("FACTURA")));
                billeteVendido.setLocalizador(resultSet.getString("LOCALIZADOR"));
                billeteVendido.setNombreViajero(resultSet.getString("NOMBRE_VIAJERO"));
                billeteVendido.setDniViajero(resultSet.getString("DNI_VIAJERO"));
                list.add(billeteVendido);
            }
        } catch (SQLException ex) {
            logger.error("Error al recuperar un billete vendido", ex);
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
    public boolean deleteBilleteVendido(String id) {
        String query = "delete from BILLETESVENDIDOS where ID = ?";
        PreparedStatement statement;
        try {
            synchronized (lockOfConnection) {
                statement = connection.prepareStatement(query);
            }
            statement.setString(1, id);
            statement.execute();
            logger.trace("Borrado el billete vendido en BD: "+id);
            return true;
        } catch (SQLException ex) {
            logger.error("Error al borrar un billete vendido", ex);
            return false;
        }
    }
    
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