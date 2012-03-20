package controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;
import model.Catalog;
import org.apache.log4j.Logger;
import persistence.comment.CommentDAO;
import persistence.comment.CommentPersistFactory;
import persistence.record.RecordDAO;
import persistence.record.RecordPersistFactory;
import persistence.sale.SaleDAO;
import persistence.sale.SalePersistFactory;
import persistence.stock.StockDAO;
import persistence.stock.StockPersistFactory;
import persistence.user.UserDAO;
import persistence.user.UserPersistFactory;

/**
 * Listener encargado de establecer la conexion con el sistema de persistencia,
 * asi como sus parametros de configuracion, establecidos en el fichero de propiedades
 * 'persistenceConfigFile' (descrito en el web.xml). En caso de error al leerlo
 * cargara unos parametros por defecto directamente del web.xml
 * Ademas cargara en el contexto el catalogo de productos y las categorias del menu
 * 
 * @author Jose Ruiz Jimenez
 */
@WebListener
public class StartUpListener implements ServletContextListener {
    private CommentDAO commentDAO;
    private RecordDAO recordDAO;
    private SaleDAO saleDAO;
    private StockDAO stockDAO;
    private UserDAO userDAO;

    @Override
    public void contextInitialized(ServletContextEvent evt) {
        String url, driver, user, password, persistenceMechanism;
        ServletContext context = evt.getServletContext();
        String persistenceConfigFile = context.getInitParameter("persistenceConfigFile");
        InputStream is = context.getResourceAsStream(persistenceConfigFile);
        Properties persistenceConfig = new Properties();
        try {
            persistenceConfig.load(is);
            url = persistenceConfig.getProperty("url");
            driver = persistenceConfig.getProperty("driver");
            user = persistenceConfig.getProperty("user");
            password = persistenceConfig.getProperty("password");
        } catch (IOException ex) {
            Logger.getLogger(StartUpListener.class.getName()).error(
                    "Error cargando los parametros del fichero de configuracion de BD", ex);
            //En este caso sacamos los parametros directamente del descriptor
            url = context.getInitParameter("databaseURL");
            driver = context.getInitParameter("databaseDriver");
            user = context.getInitParameter("databaseUser");
            password = context.getInitParameter("databasePassword");
        }
        persistenceMechanism = context.getInitParameter("persistenceMechanism");
        
        Logger.getLogger(StartUpListener.class.getName()).info( 
                "Estableciendo conexion con la BD empleando: "+persistenceMechanism+"...");
        commentDAO = CommentPersistFactory.getCommentDAO(persistenceMechanism);
        recordDAO = RecordPersistFactory.getRecordDAO(persistenceMechanism);
        saleDAO = SalePersistFactory.getSaleDAO(persistenceMechanism);
        stockDAO = StockPersistFactory.getStockDAO(persistenceMechanism);
        userDAO = UserPersistFactory.getUserDAO(persistenceMechanism);
        boolean connected = commentDAO.setUp(url, driver, user, password) &&
                            recordDAO.setUp(url, driver, user, password) &&
                            saleDAO.setUp(url, driver, user, password) &&
                            stockDAO.setUp(url, driver, user, password) &&
                            userDAO.setUp(url, driver, user, password);
        if (!connected) {
            Logger.getLogger(StartUpListener.class.getName()).error( 
                "Error conectando a la BD");
            context.setAttribute("persistenceMechanism", "none");
            context.setAttribute("catalog", "none");
        } else {
            Logger.getLogger(StartUpListener.class.getName()).info( 
                "Conexion con la BD realizada con exito");
            Catalog catalog = new Catalog();
            if( catalog.setUp(persistenceMechanism) ) {
                Logger.getLogger(StartUpListener.class.getName()).info( 
                    "Catalogo de la aplicacion cargado con exito");
                context.setAttribute("catalog", catalog);
            } else {
                Logger.getLogger(StartUpListener.class.getName()).error( 
                    "Error cargando los datos en el Catalogo");
                context.setAttribute("catalog", "none");
            }
        }
        if( loadCategoriesToContext(context) ) {
            Logger.getLogger(StartUpListener.class.getName()).info(
                "Cargadas las categorias del menu");
        } else {
            Logger.getLogger(StartUpListener.class.getName()).warn(
                "Error cargando las categorias del menu");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent evt) {
        boolean ok = commentDAO.disconnect() &&
                     recordDAO.disconnect() &&
                     saleDAO.disconnect() &&
                     stockDAO.disconnect() &&
                     userDAO.disconnect();
                     //adminDAO.disconnect();
        if (!ok) {
            Logger.getLogger(StartUpListener.class.getName()).error(
                    "No se encontro el driver para la base de datos");
        } else {
            Logger.getLogger(StartUpListener.class.getName()).info(
                "Cerrada correctamente la conexion con la base de datos");
        }
    }

    private boolean loadCategoriesToContext(ServletContext context) {
        String categories = context.getInitParameter("categories");
        if(categories != null) {
            HashMap<String,String> categoriesMap = new HashMap<String,String>();
            String[] pairs = categories.split(",");
            for(int i=0;i<pairs.length;i++) {
                String[] par = pairs[i].split("=");
                categoriesMap.put(par[0],par[1]);
            }
            context.setAttribute("categories",categoriesMap);
            return true;
        }
        return false;
    }
}