package persistence.servicio;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import model.Ruta;
import model.Servicio;
import org.apache.log4j.Logger;

/**
 * Implementacion de ServicioDAO para persistir la informacion con JDBC en MySQL
 * 
 * @param lockOfConnection objeto para controlar los accesos no concurrentes
 * @param connection conexion con la base de datos
 * @param ServicioPersistenceManager ServicioDAO de jdbc
 * @param logger para generar las trazas
 */
public class ServicioDAOJDBCImplementation implements ServicioDAO{
    private final Object lockOfConnection = new Object();
    private Connection connection = null;
    private static ServicioDAOJDBCImplementation servicioPersistenceManager = null;
    private static final Logger logger = Logger.getLogger(
            ServicioDAOJDBCImplementation.class.getName());
    
    //Visibilidad de paquete para usarlo con ServicioDAOPoolImplementation
    ServicioDAOJDBCImplementation() {
    }
    
    public static ServicioDAO getServicioDAOJDBCImplementation() {
        if(servicioPersistenceManager == null)
            servicioPersistenceManager = new ServicioDAOJDBCImplementation();
        
        return servicioPersistenceManager;
    }
    
    @Override
    public boolean createServicio(Servicio servicio) {
        String query = "insert into SERVICIOS values(?,?,?,?,?,?,?,?,?)";
        PreparedStatement statement;
        try{
            synchronized (lockOfConnection) {
                statement = connection.prepareStatement(query);
            }
            statement.setString(1,servicio.getIdAsString());
            statement.setString(2,servicio.getRuta().getIdAsString());
            statement.setInt(3,servicio.getTotalPlazas());
            statement.setInt(4,servicio.getPlazasOcupadas());
            statement.setString(5,servicio.getClaseServicio());
            statement.setString(6,servicio.getHoraSalida());
            statement.setString(7,servicio.getHoraLlegada());
            statement.setString(8,servicio.getDiasSemana());
            statement.setString(9,servicio.getPriceAsString());
            statement.execute();
            logger.trace("Insertado el servicio en BD: " + servicio.getIdAsString());
            return true;
        } catch (SQLException ex) {
            logger.error("Error al crear el servicio", ex);
            return false;
        }
    }

    @Override
    public Servicio readServicio(String id) {
        String query = "select * from SERVICIOS where ID =?";
        PreparedStatement statement;
        ResultSet resultSet = null;
        Servicio servicio = null;
        try {
            synchronized (lockOfConnection) {
                statement = connection.prepareStatement(query);
            }
            statement.setString(1, id);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                servicio = new Servicio(id);
                servicio.setRuta(new Ruta(resultSet.getString("RUTA")));
                servicio.setTotalPlazas(resultSet.getInt("TOTAL_PLAZAS"));
                servicio.setPlazasOcupadas(resultSet.getInt("PLAZAS_OCUPADAS"));
                servicio.setClaseServicio(resultSet.getString("CLASE_SERVICIO"));
                servicio.setHoraSalida(resultSet.getString("HORA_SALIDA"));
                servicio.setHoraLlegada(resultSet.getString("HORA_LLEGADA"));
                servicio.setDiasSemana(resultSet.getString("DIAS_SEMANA"));
                servicio.setPrecio(resultSet.getString("PRECIO"));
            }
        } catch (SQLException ex) {
            logger.error("Error al recuperar un servicio", ex);
            servicio = null;
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException ex) {
                    logger.error("Error al cerrar la conexon a la base de datos", ex);
                }
            }
        }
        return servicio;
    }

    @Override
    public ArrayList<Servicio> listServicio(String origen, String destino) {
        ArrayList<Servicio> list = new ArrayList();
        String query = "select * from SERVICIOS";
        PreparedStatement statement;
        ResultSet resultSet = null;
        Servicio servicio = null;
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
                servicio = new Servicio(resultSet.getString("ID"));
                servicio.setRuta(new Ruta(resultSet.getString("RUTA")));
                servicio.setTotalPlazas(resultSet.getInt("TOTAL_PLAZAS"));
                servicio.setPlazasOcupadas(resultSet.getInt("PLAZAS_OCUPADAS"));
                servicio.setClaseServicio(resultSet.getString("CLASE_SERVICIO"));
                servicio.setHoraSalida(resultSet.getString("HORA_SALIDA"));
                servicio.setHoraLlegada(resultSet.getString("HORA_LLEGADA"));
                servicio.setDiasSemana(resultSet.getString("DIAS_SEMANA"));
                servicio.setPrecio(resultSet.getString("PRECIO"));
                list.add(servicio);
            }
        } catch (SQLException ex) {
            logger.error("Error al recuperar un servicio", ex);
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
    public boolean deleteServicio(String id) {
        String query = "delete from SERVICIOS where ID = ?";
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
    
    @Override
    public Map<UUID, Servicio> getServicioMap() {
        HashMap<UUID, Servicio> servicioMap = new HashMap();
        String query = "select * from SERVICIOS";
        PreparedStatement statement;
        ResultSet resultSet = null;
        Servicio servicio = null;
        
        try {
            synchronized (lockOfConnection) {
                statement = connection.prepareStatement(query);
            } 
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String serviceId = resultSet.getString("ID");
                servicio = new Servicio(serviceId);
                servicio.setRuta(new Ruta(resultSet.getString("RUTA")));
                servicio.setTotalPlazas(resultSet.getInt("TOTAL_PLAZAS"));
                servicio.setPlazasOcupadas(resultSet.getInt("PLAZAS_OCUPADAS"));
                servicio.setClaseServicio(resultSet.getString("CLASE_SERVICIO"));
                servicio.setHoraSalida(resultSet.getString("HORA_SALIDA"));
                servicio.setHoraLlegada(resultSet.getString("HORA_LLEGADA"));
                servicio.setDiasSemana(resultSet.getString("DIAS_SEMANA"));
                servicio.setPrecio(resultSet.getString("PRECIO"));
                servicioMap.put(UUID.fromString(serviceId), servicio);
            }
        } catch (SQLException ex) {
            logger.error("Error al recuperar un servicio", ex);
            servicioMap.clear();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException ex) {
                    logger.error("Error al cerrar la conexon a la base de datos", ex);
                }
            }
        }        
        return servicioMap;
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
     * Para establecer conexiones en el ServicioDAOPoolImplementation mediante esta clase
     * @param connection
     */
    public void setConnection(Connection connection) {
        synchronized (lockOfConnection) {
            this.connection = connection;
        }
    }

}