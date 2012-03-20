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
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;

/**
 * Servlet que implementa una serie de metodos utiles si la clase es extendida
 * @author Jose Ruiz Jimenez
 */
public abstract class BasicUtilitiesServlet extends HttpServlet {
      
    //Recursos accesibles sin credenciales
    protected String frontPage = null;
    protected String userLogin = null;
    protected String userForm = null;
    protected String userDeleted = null;
    protected String shoppingCart = null;
    protected String recordInfo = null;
    protected String recordSearch = null;
    protected String checkoutLogin = null;
    protected String checkoutSuccess = null;
    protected String checkoutError = null;
    protected String newRecords = null;
    protected String featuredRecords = null;
    //Recursos accesibles solo con credenciales de cliente logueado
    protected String userUpdate = null;
    protected String userSettings = null;
    //Recursos accesibles solo con credenciales de administrador
    protected String adminLogin = null;
    protected String adminRecordForm = null;
    protected String adminRecordUpdate = null;
    protected String adminSalesSearch = null;
    protected String adminUserSearch = null;
    protected String adminSettings = null;
    //Solicitud incorrecta por parte del cliente o error por excepcion
    protected String errorForm = null;
    protected String exceptionError = null;
    //Servlets de la aplicacion
    protected String frontController = null;
    protected String userLoginServlet = null;
    protected String userLogoutServlet = null;
    protected String userCreateServlet = null;
    protected String userUpdateServlet = null;
    protected String userDeleteServlet = null;
    protected String userSearchServlet = null;
    protected String shoppingCartServlet = null;
    protected String checkoutCartServlet = null;
    protected String adminLoginServlet = null;
    protected String adminLogoutServlet = null;
    protected String recordInfoServlet = null;
    protected String recordCreateServlet = null;
    protected String recordUpdateServlet = null;
    protected String recordDeleteServlet = null;
    protected String recordSearchServlet = null;
    protected String commentCreateServlet = null;
    protected String commentDeleteServlet = null;
    protected String salesSearchServlet = null;
    protected String mailSaleServlet = null;
    protected String mailUserServlet = null;
    //Modelo de persistencia empleado
    protected String persistenceMechanism = null;
    //Fichero con los credenciales del administrador
    protected String adminConfigFile = null;
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
        userLogin = context.getInitParameter("userLogin");
        userForm = context.getInitParameter("userForm");
        userDeleted = context.getInitParameter("userDeleted");
        shoppingCart = context.getInitParameter("shoppingCart");
        recordInfo = context.getInitParameter("recordInfo");
        recordSearch = context.getInitParameter("recordSearch");
        checkoutLogin = context.getInitParameter("checkoutLogin");
        checkoutSuccess = context.getInitParameter("checkoutSuccess");
        checkoutError = context.getInitParameter("checkoutError");
        newRecords = context.getInitParameter("newRecords");
        featuredRecords = context.getInitParameter("featuredRecords");
        userUpdate = context.getInitParameter("userUpdate");
        userSettings = context.getInitParameter("userSettings");
        adminLogin = context.getInitParameter("adminLogin");
        adminRecordForm = context.getInitParameter("adminRecordForm");
        adminRecordUpdate = context.getInitParameter("adminRecordUpdate");
        adminSalesSearch = context.getInitParameter("adminSalesSearch");
        adminUserSearch = context.getInitParameter("adminUserSearch");
        adminSettings = context.getInitParameter("adminSettings");
        errorForm = context.getInitParameter("errorForm");
        exceptionError = context.getInitParameter("exceptionError");
        frontController = context.getInitParameter("frontController");
        userLoginServlet = context.getInitParameter("userLoginServlet");
        userLogoutServlet = context.getInitParameter("userLogoutServlet");
        userCreateServlet = context.getInitParameter("userCreateServlet");
        userUpdateServlet = context.getInitParameter("userUpdateServlet");
        userDeleteServlet = context.getInitParameter("userDeleteServlet");
        userSearchServlet = context.getInitParameter("userSearchServlet");
        shoppingCartServlet = context.getInitParameter("shoppingCartServlet");
        checkoutCartServlet = context.getInitParameter("checkoutCartServlet");
        adminLoginServlet = context.getInitParameter("adminLoginServlet");
        adminLogoutServlet = context.getInitParameter("adminLogoutServlet");
        recordInfoServlet = context.getInitParameter("recordInfoServlet");
        recordCreateServlet = context.getInitParameter("recordCreateServlet");
        recordUpdateServlet = context.getInitParameter("recordUpdateServlet");
        recordDeleteServlet = context.getInitParameter("recordDeleteServlet");
        recordSearchServlet = context.getInitParameter("recordSearchServlet");
        commentCreateServlet = context.getInitParameter("commentCreateServlet");
        commentDeleteServlet = context.getInitParameter("commentDeleteServlet");
        salesSearchServlet = context.getInitParameter("salesSearchServlet");
        mailSaleServlet = context.getInitParameter("mailSaleServlet");
        mailUserServlet = context.getInitParameter("mailUserServlet");
        persistenceMechanism = context.getInitParameter("persistenceMechanism");
        adminConfigFile = context.getInitParameter("adminConfigFile");
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