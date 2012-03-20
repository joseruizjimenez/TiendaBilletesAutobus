package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * Servlet para gestionar la Excepciones.
 * Las logueara y redirige la peticion a la pagina de error 500
 */
@WebServlet(name="ManageExceptionServlet", urlPatterns={"/Ooops"})
public class ManageExceptionServlet extends BasicUtilitiesServlet {
    Logger logger = Logger.getLogger(ManageExceptionServlet.class.getName());
   
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Exception exception =
                (Exception) request.getAttribute("javax.servlet.error.exception");
        String exceptionSource =
                (String) request.getAttribute("javax.servlet.error.request_uri");
        
        logger.error("Excepcion en "+exceptionSource,exception);
        gotoURL(exceptionError,request,response);      
    }

}