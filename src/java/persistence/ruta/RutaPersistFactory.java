package persistence.ruta;

/**
 * Genera una factoria de discos siguiendo el patron DAO: RutaDAO
 */
public class RutaPersistFactory {
    
    /**
     * Obten un sistema de persistencia del tipo declarado en persistenceMechanism
     * @param persistenceMechanism puede ser file, jdbc o pool
     * @return RutaDAO si todo va bien, sino null
     */
    public static RutaDAO getRutaDAO(String persistenceMechanism){
        if(persistenceMechanism.equals("jdbc")){
            return RutaDAOJDBCImplementation.getRutaDAOJDBCImplementation();
        }
        else if (persistenceMechanism.equals("pool")) {
            return RutaDAOPoolImplementation.getRutaDAOPoolImplementation();
        }
        else {
            return null;
        }
    }

}
