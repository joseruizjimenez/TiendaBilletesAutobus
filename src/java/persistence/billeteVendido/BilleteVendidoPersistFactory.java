package persistence.record;

/**
 * Genera una factoria de discos siguiendo el patron DAO: RecordDAO
 */
public class RecordPersistFactory {
    
    /**
     * Obten un sistema de persistencia del tipo declarado en persistenceMechanism
     * @param persistenceMechanism puede ser file, jdbc o pool
     * @return RecordDAO si todo va bien, sino null
     */
    public static RecordDAO getRecordDAO(String persistenceMechanism){
        if (persistenceMechanism.equals("file")){
            return RecordDAOFileImplementation.getRecordDAOFileImplementation();
        }
        else if(persistenceMechanism.equals("jdbc")){
            return RecordDAOJDBCImplementation.getRecordDAOJDBCImplementation();
        }
        else if (persistenceMechanism.equals("pool")) {
            return RecordDAOPoolImplementation.getRecordDAOPoolImplementation();
        }
        else {
            return null;
        }
    }

}
