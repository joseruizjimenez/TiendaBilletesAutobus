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
 *     - idServicioIda (cookie): id servicio seleccionado
 *     - idServicioVuelta (cookie): id servicio seleccionado
 *          No existe (será null) si no se ha solicitado vuelta.
 */
@WebServlet(name="TicketSelectionServlet")
public class TicketSelectionServlet extends BasicUtilitiesServlet {
    Logger logger = Logger.getLogger(TicketSelectionServlet.class.getName());
   
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Cookie idServicioIda = new Cookie("idServicioIda",
                request.getParameter("idServicioIda"));
        response.addCookie(idServicioIda);
        String idServicioVuelta = request.getParameter("idServicioVuelta");
        if(idServicioVuelta == null) {
            idServicioVuelta = "";
        }
        response.addCookie(new Cookie("idServicioIda", idServicioVuelta));
        //System.out.println("Hi darling.");
        
        gotoURL(ticketSelectionForm, request, response);
        
        
        /*
        // si llega de ticketSelectionSearch
        if(request.getParameter("billete1") == null) {
            HashMap<String,Servicio> servicios = (HashMap<String,Servicio>)
                context.getAttribute("servicios");
            String idServicio = request.getParameter("idServicio");
            if(!validateFreeText(idServicio,36,36) || !servicios.containsKey(idServicio)) {
                gotoURL(errorForm, request, response);
            } else {
                Cookie idServicioCookie = new Cookie("idServicio", idServicio);
                response.addCookie(idServicioCookie);
                gotoURL(ticketSelectionForm, request, response);
            }
        // si llega de ticketSelectionForm
        } else {
            Cookie[] cookies = request.getCookies();
            int numBilletes = getNumBilletesFromCookies(cookies);
            if (numBilletes <= 0) {
                gotoURL(frontPage, request, response);
            } else {
                ArrayList<String[]> datosViajeros = new ArrayList<String[]>();
                for(int i=1; i <= numBilletes; i++) {
                    //TODO FALTAN EXTRAER ALGUNOS CAMPOS Y PASAR datosViajeros
                    String nombre = request.getParameter("nombre"+ 
                            Integer.toString(i));
                    String apellidos = request.getParameter("apellidos"+ 
                            Integer.toString(i));
                    String dni = request.getParameter("dni"+
                            Integer.toString(i));
                    if(nombre == null || "".equals(nombre) ||
                            apellidos == null || "".equals(apellidos) ||
                            dni == null || "".equals(dni)) {
                        request.setAttribute("msg", "Complete todos los campos requeridos");
                        numBilletes = -1; //condicion de parada del bucle
                        gotoURL(ticketSelectionForm, request, response);
                    } else if(!validateName(nombre, 2, 50) ||
                            !validateName(apellidos, 1, 100) ||
                            !validateDNI(dni)) {
                        request.setAttribute("msg", "El campo contiene caracteres no permitidos");
                        numBilletes = -1; //condicion de parada del bucle
                        gotoURL(ticketSelectionForm, request, response);
                    } else {
                        datosViajeros.add([nombre, apellidos, dni]);
                        //billeteReservado = new BilleteVendido(/*TODO);
                        //billetesRerservados.add(billeteReservado);
                    }
                }
                session.setAttribute("datosViajeros", datosViajeros);
                gotoURL(ticketSitForm, request, response);
            }
        }*/
    }

    /*
    private int getNumBilletesFromCookies(Cookie[] cookies) {
        if(cookies != null) {
            for(int i=0; i<cookies.length; i++) {
                if("numBilletes".equals(cookies[i].getName()) &&
                        !"".equals(cookies[i].getValue()))
                    return cookies[i].getValue().toInteger();
            }
        }
        return 0;
    }
   */

}

