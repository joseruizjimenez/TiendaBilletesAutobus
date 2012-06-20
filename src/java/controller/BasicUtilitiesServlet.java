package controller;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.owasp.esapi.ESAPI;
import org.owasp.validator.html.*;

/**
 * Servlet que implementa una serie de metodos utiles si la clase es extendida
 * 
 */
public abstract class BasicUtilitiesServlet extends HttpServlet {
      
    //Recursos accesibles sin credenciales
    protected String frontPage = null;
    protected String ticketSearchForm = null;
    protected String ticketSearch = null;
    protected String ticketSelectionSearch = null;
    protected String ticketSelectionForm = null;
    protected String ticketSitForm = null;
    protected String ticketSelectionInfo = null;
    protected String ticketCheckoutForm = null;
    protected String ticketCheckoutError = null;
    protected String ticketCheckoutOK = null;
    protected String ticketOpSearch = null;
    protected String ticketOpSearchList = null;
    protected String ticketOpSearchError = null;
    protected String ticketOpCancelOK = null;
    protected String ticketOpChangeOK = null;
    protected String clubBusCreateForm = null;
    protected String clubBusCreateOK = null;
    //Recursos accesibles solo identificando billete adquirido
    protected String ticketOpMenu = null;
    protected String ticketOpInfo = null;
    protected String ticketOpCancel = null;
    protected String ticketOpChange = null;
    //Recursos accesibles solo con credenciales de administrador
    
    //Solicitud incorrecta por parte del cliente o error por excepcion
    protected String errorForm = null;
    protected String exceptionError = null;
    //Servlets de la aplicacion
    protected String frontController = null;
    protected String ticketSearchServlet = null;
    protected String ticketSelectionServlet = null;
    protected String ticketSelectionFormServlet = null;
    protected String ticketSitServlet = null;
    protected String ticketCheckoutServlet = null;
    protected String ticketOpSearchServlet = null;
    protected String ticketOpSearchListServlet = null;
    protected String ticketOpCancelServlet = null;
    protected String ticketOpChangeSelectionServlet = null;
    protected String ticketOpChangeServlet = null;
    //Modelo de persistencia empleado
    protected String persistenceMechanism = null;
    //Fichero con los credenciales del administrador
    //protected String adminConfigFile = null;
    //Fichero con la configuracion del servidor de correo
    protected String emailConfigFile = null;
    //Fichero con la politica de prevencion de ataques XSS
    protected String xssPolicyConfigFile = null;

    protected abstract void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException;
    
    /**
     * Permite redireccionar a un servlet dada su direccion
     * @param address direccion del Servlet
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    protected void gotoNamedResource(String address, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = getServletContext().getNamedDispatcher(address);
        dispatcher.forward(request, response);
    }
    
    /**
     * Permite redireccionar a una URL dada su direccion
     * @param address direccion URL a donde queremos ir
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    protected void gotoURL(String address, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(address);
        dispatcher.forward(request, response);
    }

    @Override
    public void init() {
        ServletConfig config = getServletConfig();
        ServletContext context = config.getServletContext();
        frontPage = context.getInitParameter("frontPage");
        ticketSearchForm = context.getInitParameter("ticketSearchForm");
        ticketSearch = context.getInitParameter("ticketSearch");
        ticketSelectionSearch = context.getInitParameter("ticketSelectionSearch");
        ticketSelectionForm = context.getInitParameter("ticketSelectionForm");
        ticketSitForm = context.getInitParameter("ticketSitForm");
        ticketSelectionInfo = context.getInitParameter("ticketSelectionInfo");
        ticketCheckoutForm = context.getInitParameter("ticketCheckoutForm");
        ticketCheckoutError = context.getInitParameter("ticketCheckoutError");
        ticketCheckoutOK = context.getInitParameter("ticketCheckoutOK");
        ticketOpSearch = context.getInitParameter("ticketOpSearch");
        ticketOpSearchList = context.getInitParameter("ticketOpSearchList");
        ticketOpSearchError = context.getInitParameter("ticketOpSearchError");
        ticketOpCancelOK = context.getInitParameter("ticketOpCancelOK");
        ticketOpChangeOK = context.getInitParameter("ticketOpChangeOK");
        clubBusCreateForm = context.getInitParameter("clubBusCreateForm");
        clubBusCreateOK = context.getInitParameter("clubBusCreateOK");
        ticketOpMenu = context.getInitParameter("ticketOpMenu");
        ticketOpInfo = context.getInitParameter("ticketOpInfo");
        ticketOpCancel = context.getInitParameter("ticketOpCancel");
        ticketOpChange = context.getInitParameter("ticketOpChange");
        errorForm = context.getInitParameter("errorForm");
        exceptionError = context.getInitParameter("exceptionError");
        frontController = context.getInitParameter("frontController");
        ticketSearchServlet = context.getInitParameter("ticketSearchServlet");
        ticketSelectionServlet = context.getInitParameter("ticketSelectionServlet");
        ticketSelectionFormServlet = context.getInitParameter("ticketSelectionFormServlet");
        ticketSitServlet = context.getInitParameter("ticketSitServlet");
        ticketCheckoutServlet = context.getInitParameter("ticketCheckoutServlet");
        ticketOpSearchServlet = context.getInitParameter("ticketOpSearchServlet");
        ticketOpSearchListServlet = context.getInitParameter("ticketOpSearchListServlet");
        ticketOpCancelServlet = context.getInitParameter("ticketOpCancelServlet");
        ticketOpChangeSelectionServlet = context.getInitParameter("ticketOpChangeSelectionServlet");
        ticketOpChangeServlet = context.getInitParameter("ticketOpChangeServlet");
        persistenceMechanism = context.getInitParameter("persistenceMechanism");
        //adminConfigFile = context.getInitParameter("adminConfigFile");
        emailConfigFile = context.getInitParameter("emailConfigFile");
        xssPolicyConfigFile = context.getInitParameter("xssPolicyConfigFile");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
   
    /**
     * Valida que la cadena contiene campos permitidos para un nombre,
     * apellidos... solo caracteres alfabeticos y con acentos
     * @param data datos a validar
     * @param minAcceptable tamanyo minimo de la cadena
     * @param maxAcceptable tamanyo maximo de la cadena
     * @return true si es valida, false en caso contrario
     */
    protected boolean validateName(String data, int minAcceptable, int maxAcceptable) {
        boolean isGoodLength = data!=null && checkLength(data, minAcceptable, maxAcceptable);
        String namePattern = "([a-zA-ZñÑáéíóúÁÉÍÓÚç ])+";
        return isGoodLength && data.matches(namePattern);
    }
    
    /**
     * Valida que la cadena contiene solo caracteres alfabeticos y numericos
     * @param data datos a validar
     * @param minAcceptable tamanyo minimo de la cadena
     * @param maxAcceptable tamanyo maximo de la cadena
     * @return true si es valida, false en caso contrario
     */
    protected boolean validateAlphaNumeric(String data, int minAcceptable, int maxAcceptable) {
        boolean isGoodLength = data!=null && checkLength(data, minAcceptable, maxAcceptable);
        String namePattern = "([0-9a-zA-Z])+";
        return isGoodLength && data.matches(namePattern);
    }

    protected boolean validateDNI(String dni) {
        String namePattern = "[0-9]+[A-Z]";
        return dni.length() == 9 && dni.matches(namePattern);
    }
    
    /**
     * Valida que la cadena contiene solo caracteres numericos
     * @param data datos a validar
     * @param minAcceptable tamanyo minimo de la cadena
     * @param maxAcceptable tamanyo maximo de la cadena
     * @return true si es valida, false en caso contrario
     */
    protected boolean validateNumeric(String data, int minAcceptable, int maxAcceptable) {
        boolean isGoodLength = data!=null && checkLength(data, minAcceptable, maxAcceptable);
        String namePattern = "([0-9])+";
        return isGoodLength && data.matches(namePattern);
    }
    
    /**
     * Valida que la cadena contiene solo caracteres validos para un email
     * @param data datos a validar
     * @return true si es valida, false en caso contrario
     */
    protected boolean validateEmail(String data) {
        String emailPattern = "\\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}\\b";
        Pattern p = Pattern.compile(emailPattern,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(data);
        return (data!=null && m.matches());
    }
    
    /**
     * Valida que la cadena contiene solo caracteres alfabeticos, acentuados
     * y numericos y algunos caracteres especiales. Direcciones, descripciones...
     * @param data datos a validar
     * @param minAcceptable tamanyo minimo de la cadena
     * @param maxAcceptable tamanyo maximo de la cadena
     * @return true si es valida, false en caso contrario
     */
    protected boolean validateFreeText(String data, int minAcceptable, int maxAcceptable) {
        boolean isGoodLength = data!=null && checkLength(data, minAcceptable, maxAcceptable);
        String namePattern = "([0-9a-zA-ZñÑáéíóúÁÉÍÓÚ._,;:()/\\?\\!\\¿\\¡ \\-])*";
        return isGoodLength && data.matches(namePattern);
    }
    
    /**
     * Valida una fecha de nacimiento dada por:
     * @param day dia
     * @param month mes
     * @param year anho
     * @return true si es valida, false en caso contrario
     */
    protected boolean validateDate(String year, String month, String day) {
        if (checkLength(day, 1, 2) && checkLength(month, 1, 2) && checkLength(year, 4, 4)) {
            if (day.matches("([0-9])*") && month.matches("([0-9])*") && year.matches("([0-9])*")) {
            int dayInt = Integer.parseInt(day);
            int monthInt = Integer.parseInt(month);
            int yearInt = Integer.parseInt(year);
            return ( (dayInt <= 31) && (dayInt > 0) && (monthInt <= 12) && (monthInt > 0)
                    && (yearInt <= 2200) && (yearInt >= 1900) );
            }
        }
        return false;
    }

    protected boolean checkLength(String data, int minAcceptable, int maxAcceptable) {
        if(data == null)
            return false;
        int length = data.length();
        boolean isGoodLength = length >= minAcceptable && length <= maxAcceptable;
        return isGoodLength;
    }
    
    /**
     * Comprueba que el String esta libre de XSS empleando las librerias ESAPI
     * @param data string a securizar
     * @return true si es seguro, false en caso contrario
     */
    protected boolean isStringXSSSecured (String data) {
        try {
            Policy policy = Policy.getInstance(this.getServletContext().getRealPath(xssPolicyConfigFile));
            AntiSamy validator = new AntiSamy();
            CleanResults cleanResults = validator.scan(ESAPI.encoder().canonicalize(data), policy);
            if (cleanResults.getNumberOfErrors() == 0){
                return true;
            }
        } catch (ScanException ex) {
            Logger.getLogger(BasicUtilitiesServlet.class.getName()).error(
                    "Error escaneando la politica XSS", ex);
        } catch (PolicyException ex) {
            Logger.getLogger(BasicUtilitiesServlet.class.getName()).error(
                    "Error aplicando la politica XSS", ex);
        } 
        return false;       
    }
}
