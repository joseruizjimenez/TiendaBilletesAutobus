package controller.public;

import controller.BasicUtilitiesServlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Servicio;
import org.apache.log4j.Logger;

/**
 * Busca los servicios ofertados con los datos suministrados
 */
@WebServlet(name="TicketSearchServlet")
public class TicketSearchServlet extends BasicUtilitiesServlet {
    Logger logger = Logger.getLogger(TicketSearchServlet.class.getName());
   
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ArrayList<String> idServiciosEncontrados =
            generateServiceIdListFromRequest(request);
        Cookie numBilletesSeleccionados = new Cookie("numBilletes",
                request.getParameter("numBilletes"));
        response.addCookie(numBilletesSeleccionados);
        if(idServiciosEncontrados.isEmpty()) {
            request.setAttribute("msg", "No se han encontrado resultados!");
        } else {
            request.setAttribute("msg", "Se han encontrado: " +
                    idServiciosEncontrados.size() + " servicios:");
        }
        request.setAttribute("idServicios", idServiciosEncontrados);
        String day = request.getParameter("day");
        String month = request.getParameter("month");
        String year = request.getParameter("year");
        if((day != null && day != "") &&
                (month != null && month != "") &&
                (year != null && year != "")) {
            gotoURL(ticketSelectionSearch, request, response);
        } else {
            gotoURL(ticketSearch, request, response);
        }
    }
    
    /**
     * Genera un listado resultado de la busqueda empleando los parametros de request
     * @param request con el formulario
     * @return recordList generada
     */
    private ArrayList generateServiceIdListFromRequest(HttpServletRequest request) {
        HttpSession session = request.getSession();
        ServletContext context = session.getServletContext();
        HashMap<String,Servicio> servicios = (HashMap<String,Servicio>)
            context.getAttribute("servicios");
        ArrayList<String> idServiciosEncontrados = new ArrayList<String>();
        //TODO SACAR TODOS LOS ATRIBUTOS DE LA BUSQUEDA, ¿VALIDAR?
        // RECORRER SERVICIOS, SI COINCIDEN TODOS LOS CAMPOS -> ADD ID
        
        return idServiciosEncontrados;
    }

}
