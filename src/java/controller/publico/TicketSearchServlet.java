package controller.publico;

import controller.BasicUtilitiesServlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;
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
 * 
 * Llega de: TicketSearchForm.jsp
 * Va a: TicketSearch.jsp (sin fecha), TicketSelectionSearch.jsp (con fecha)
 * 
 * Espera recibir en la consulta:
 *     - numBilletes: el numero de billetes que se desean
 *     - origen: origen del servicio
 *     - destino: destino del servicio
 *     - modo: 'i'/'iv' (ida / ida y vuelta)
 *     - day, month, year
 *     - dayVuelta, monthVuelta, yearVuelta (opcional)
 *     - clubBus (opcional), nif (opcional)
 * Devuelve:
 *     - cookie 'numBilletes'
 *     - idServiciosIda (atributo del request): IDs de los servicios encontrados
 *     - idServiciosVuelta (atributo del request): IDs de los servicios encontrados
 *          Será null si no se ha solicitado vuelta.
 *     - msg si no se ha rellenado ningun campo
 */
@WebServlet(name="TicketSearchServlet")
public class TicketSearchServlet extends BasicUtilitiesServlet {
    Logger logger = Logger.getLogger(TicketSearchServlet.class.getName());

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ArrayList<String> idServiciosEncontradosIda =
            generateIdaServiceIdListFromRequest(request);
        ArrayList<String> idServiciosEncontradosVuelta =
            generateVueltaServiceIdListFromRequest(request);
        Cookie numBilletesSeleccionados = new Cookie("numBilletes",
                request.getParameter("numBilletes"));
        response.addCookie(numBilletesSeleccionados);
        if(idServiciosEncontradosIda.isEmpty()) {
            request.setAttribute("msg", "No se han encontrado resultados!");
        } else {
            request.setAttribute("msg", "Se han encontrado: " +
                    idServiciosEncontradosIda.size() + " servicios de ida");
        }
        request.setAttribute("idServiciosIda", idServiciosEncontradosIda);
        request.setAttribute("idServiciosVuelta", idServiciosEncontradosVuelta);
        String day = request.getParameter("day");
        String month = request.getParameter("month");
        String year = request.getParameter("year");
        if((day != null && !"-".equals(day)) &&
                (month != null && !"-".equals(month)) &&
                (year != null && !"-".equals(year))) {
            gotoURL(ticketSelectionSearch, request, response);
        } else {
            gotoURL(ticketSearch, request, response);
        }
    }
    
    /**
     * Genera un listado resultado de la busqueda empleando los parametros de request
     */
    private ArrayList<String> generateIdaServiceIdListFromRequest(
            HttpServletRequest request) {
        HttpSession session = request.getSession();
        ServletContext context = session.getServletContext();
        HashMap<UUID,Servicio> servicios = (HashMap<UUID,Servicio>)
            context.getAttribute("servicios");
        ArrayList<String> idServiciosEncontrados = new ArrayList<String>();
        String day = request.getParameter("day");
        String month = request.getParameter("month");
        String year = request.getParameter("year");
        String origen = request.getParameter("origen");
        String destino = request.getParameter("destino");
        for(UUID idUUID : servicios.keySet()) {
            String id = idUUID.toString();
            Servicio candidato = servicios.get(idUUID);
            
            if(candidato.getRuta().getOrigen().equalsIgnoreCase(origen)
                    && candidato.getRuta().getDestino().equalsIgnoreCase(destino)) {
                if((day != null && !"-".equals(day)) &&
                    (month != null && !"-".equals(month)) &&
                    (year != null && !"-".equals(year))) {
                    Calendar fechaSolicitada = Calendar.getInstance();
                    fechaSolicitada.clear();
                    fechaSolicitada.set(Integer.parseInt(year), 
                            Integer.parseInt(month)-1, Integer.parseInt(day));
                    int diaSemana = fechaSolicitada.get(Calendar.DAY_OF_WEEK);
                    String inicialDia = null;
                    switch(diaSemana) {
                        case Calendar.SUNDAY: inicialDia = "D"; break;
                        case Calendar.MONDAY: inicialDia = "L"; break;
                        case Calendar.TUESDAY: inicialDia = "M"; break;
                        case Calendar.WEDNESDAY: inicialDia = "X"; break;
                        case Calendar.THURSDAY: inicialDia = "J"; break;
                        case Calendar.FRIDAY: inicialDia = "V"; break;
                        case Calendar.SATURDAY: inicialDia = "S"; break;
                    }
                    if(candidato.getDiasSemana().contains(inicialDia)) {
                        idServiciosEncontrados.add(id);
                    }
                } else {
                    idServiciosEncontrados.add(id);
                }
            }
        }       
        return idServiciosEncontrados;
    }

    private ArrayList<String> generateVueltaServiceIdListFromRequest(
            HttpServletRequest request) {
        String modo = request.getParameter("modo");
        if(modo != null && !"iv".equalsIgnoreCase(modo)) {
            return null;
        }        
        HttpSession session = request.getSession();
        ServletContext context = session.getServletContext();
        HashMap<UUID,Servicio> servicios = (HashMap<UUID,Servicio>)
            context.getAttribute("servicios");
        ArrayList<String> idServiciosEncontrados = new ArrayList<String>();
        String day = request.getParameter("dayVuelta");
        String month = request.getParameter("monthVuelta");
        String year = request.getParameter("yearVuelta");
        // en la vuelta el origen es el destino y a la inversa...
        String origen = request.getParameter("destino");
        String destino = request.getParameter("origen");
        for(UUID idUUID : servicios.keySet()) {
            String id = idUUID.toString();
            Servicio candidato = servicios.get(idUUID);
            if(candidato.getRuta().getOrigen().equalsIgnoreCase(origen)
                    && candidato.getRuta().getDestino().equalsIgnoreCase(destino)) {
                if((day != null && !"-".equals(day)) &&
                    (month != null && !"-".equals(month)) &&
                    (year != null && !"-".equals(year))) {
                    Calendar fechaSolicitada = Calendar.getInstance();
                    fechaSolicitada.clear();
                    fechaSolicitada.set(Integer.parseInt(year), 
                            Integer.parseInt(month)-1, Integer.parseInt(day));
                    int diaSemana = fechaSolicitada.get(Calendar.DAY_OF_WEEK);
                    String inicialDia = null;
                    switch(diaSemana) {
                        case Calendar.SUNDAY: inicialDia = "D"; break;
                        case Calendar.MONDAY: inicialDia = "L"; break;
                        case Calendar.TUESDAY: inicialDia = "M"; break;
                        case Calendar.WEDNESDAY: inicialDia = "X"; break;
                        case Calendar.THURSDAY: inicialDia = "J"; break;
                        case Calendar.FRIDAY: inicialDia = "V"; break;
                        case Calendar.SATURDAY: inicialDia = "S"; break;
                    }
                    if(candidato.getDiasSemana().contains(inicialDia)) {
                        idServiciosEncontrados.add(id);
                    }
                } else {
                    idServiciosEncontrados.add(id);
                }
            }
        }
        
        return idServiciosEncontrados;
    }

}
