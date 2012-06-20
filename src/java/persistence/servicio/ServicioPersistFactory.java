package persistence.servicio;

/**
 * Genera una factoria de discos siguiendo el patron DAO: ServicioDAO
 */
public class ServicioPersistFactory {
    
    /**
     * Obten un sistema de persistencia del tipo declarado en persistenceMechanism
     * @param persistenceMechanism puede ser file, jdbc o pool
     * @return ServicioDAO si todo va bien, sino null
     */
    public static ServicioDAO getServicioDAO(String persistenceMechanism){
        if(persistenceMechanism.equals("jdbc")){
            return ServicioDAOJDBCImplementation.getServicioDAOJDBCImplementation();
        }
        else if (persistenceMechanism.equals("pool")) {
            return ServicioDAOPoolImplementation.getServicioDAOPoolImplementation();
        }
        else {
            return null;
        }
    }

}
