package controller.publico;

import controller.BasicUtilitiesServlet;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * Si el servicio elegido es valido, lo adjunta como cookie
 * 
 * Llega de: TicketSelectionSearch.jsp
 * Va a: TicketSelectionForm.jsp
 * 
 * Espera recibir en la consulta:
 *     - idIda: id del billete seleccionado para la ida
 *     - idVuelta: (opcional) id del billete seleccionado para la vuelta
 * Devuelve:
 *     - idServicioIda (cookie y request): id servicio seleccionado
 *     - idServicioVuelta (cookie y request): id servicio seleccionado
 *     - numBilletes (request) para el jsp
 *          No existe (será null) si no se ha solicitado vuelta.
 */
@WebServlet(name="TicketSelectionServlet")
public class TicketSelectionServlet extends BasicUtilitiesServlet {
    Logger logger = Logger.getLogger(TicketSelectionServlet.class.getName());
   
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Cookie idServicioIda = new Cookie("idServicioIda",
                request.getParameter("idIda"));
        response.addCookie(idServicioIda);
        String idServicioVuelta = request.getParameter("idVuelta");
        if(idServicioVuelta == null) {
            idServicioVuelta = "";
        }
        response.addCookie(new Cookie("idServicioVuelta", idServicioVuelta));
        request.setAttribute("idServicioIda", idServicioIda.getValue());
        request.setAttribute("idServicioVuelta", idServicioVuelta);
        Cookie[] cookies = request.getCookies();
        for (int i = 0; i < cookies.length; i++) {
            if ("numBilletes".equals(cookies[i].getName())
                    && !"".equals(cookies[i].getValue())) {
                request.setAttribute("numBilletes", cookies[i].getValue());
            }
        }
        
        gotoURL(ticketSelectionForm, request, response);
    }

}

