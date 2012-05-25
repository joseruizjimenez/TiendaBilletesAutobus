package controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;
import model.BilleteVendido;
import model.Servicio;
import org.apache.log4j.Logger;
import persistence.factura.FacturaDAO;
import persistence.factura.FacturaPersistFactory;
import persistence.billeteVendido.BilleteVendidoDAO;
import persistence.billeteVendido.BilleteVendidoPersistFactory;
import persistence.ruta.RutaDAO;
import persistence.ruta.RutaPersistFactory;
import persistence.servicio.ServicioDAO;
import persistence.servicio.ServicioPersistFactory;

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
    private BilleteVendidoDAO billeteVendidoDAO;
    private FacturaDAO facturaDAO;
    private RutaDAO rutaDAO;
    private ServicioDAO servicioDAO;

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
        billeteVendidoDAO = BilleteVendidoPersistFactory.getBilleteVendidoDAO(persistenceMechanism);
        facturaDAO = FacturaPersistFactory.getFacturaDAO(persistenceMechanism);
        rutaDAO = RutaPersistFactory.getRutaDAO(persistenceMechanism);
        servicioDAO = ServicioPersistFactory.getServicioDAO(persistenceMechanism);
        boolean connected = billeteVendidoDAO.setUp(url, driver, user, password) &&
                            facturaDAO.setUp(url, driver, user, password) &&
                            rutaDAO.setUp(url, driver, user, password) &&
                            servicioDAO.setUp(url, driver, user, password);
        if (!connected) {
            Logger.getLogger(StartUpListener.class.getName()).error( 
                "Error conectando a la BD");
            context.setAttribute("persistenceMechanism", "none");
            context.setAttribute("servicios", "none");
            context.setAttribute("estaciones", "none");
        } else {
            Logger.getLogger(StartUpListener.class.getName()).info( 
                "Conexion con la BD realizada con exito");
            HashMap<UUID, Servicio> servicios = (HashMap<UUID,Servicio>)
                    servicioDAO.getServicioMap();
            if( servicios != null ) {
                Logger.getLogger(StartUpListener.class.getName()).info( 
                    "Catalogo de servicios cargado con exito");
                context.setAttribute("servicios", servicios);
            } else {
                Logger.getLogger(StartUpListener.class.getName()).error( 
                    "Error cargando los servicios");
                context.setAttribute("servicios", "none");
            }
            context.setAttribute("billeteVendidoDAO", billeteVendidoDAO);
            context.setAttribute("facturaDAO", facturaDAO);
            context.setAttribute("rutaDAO", rutaDAO);
            context.setAttribute("servicioDAO", servicioDAO);
            
            /*
            ArrayList<BilleteVendido> billetesVendidos = (ArrayList<BilleteVendido>)
                    billeteVendidoDAO.listBilleteVendido("", "", "");
            if( billetesVendidos != null ) {
                Logger.getLogger(StartUpListener.class.getName()).info( 
                    "Billetes vendidos cargados con exito");
                context.setAttribute("billetesVendidos", billetesVendidos);
            } else {
                Logger.getLogger(StartUpListener.class.getName()).error( 
                    "Error cargando los billetesVendidos");
                context.setAttribute("billetesVendidos", "none");
            }
            */
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent evt) {
        boolean ok = billeteVendidoDAO.disconnect() &&
                     facturaDAO.disconnect() &&
                     rutaDAO.disconnect() &&
                     servicioDAO.disconnect();
        if (!ok) {
            Logger.getLogger(StartUpListener.class.getName()).error(
                    "No se encontro el driver para la base de datos");
        } else {
            Logger.getLogger(StartUpListener.class.getName()).info(
                "Cerrada correctamente la conexion con la base de datos");
        }
    }
    
}