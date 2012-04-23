package persistence.factura;

/**
 * Genera una factoria de discos siguiendo el patron DAO: RecordDAO
 */
public class FacturaPersistFactory {
    
    /**
     * Obten un sistema de persistencia del tipo declarado en persistenceMechanism
     * @param persistenceMechanism puede ser file, jdbc o pool
     * @return RecordDAO si todo va bien, sino null
     */
    public static FacturaDAO getFacturaDAO(String persistenceMechanism){
        if(persistenceMechanism.equals("jdbc")){
            return FacturaDAOJDBCImplementation.getFacturaDAOJDBCImplementation();
        }
        else if (persistenceMechanism.equals("pool")) {
            return FacturaDAOPoolImplementation.getFacturaDAOPoolImplementation();
        }
        else {
            return null;
        }
    }

}
