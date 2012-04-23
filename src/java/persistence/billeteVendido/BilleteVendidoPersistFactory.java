package persistence.billeteVendido;

/**
 * Genera una factoria de discos siguiendo el patron DAO: RecordDAO
 */
public class BilleteVendidoPersistFactory {
    
    /**
     * Obten un sistema de persistencia del tipo declarado en persistenceMechanism
     * @param persistenceMechanism puede ser file, jdbc o pool
     * @return RecordDAO si todo va bien, sino null
     */
    public static BilleteVendidoDAO getBilleteVendidoDAO(String persistenceMechanism){
        if(persistenceMechanism.equals("jdbc")){
            return BilleteVendidoDAOJDBCImplementation.getBilleteVendidoDAOJDBCImplementation();
        }
        else if (persistenceMechanism.equals("pool")) {
            return BilleteVendidoDAOPoolImplementation.getBilleteVendidoDAOPoolImplementation();
        }
        else {
            return null;
        }
    }

}
