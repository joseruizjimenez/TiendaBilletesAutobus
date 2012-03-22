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
 * Si el servicio elegido es valido, lo adjunta como cookie
 */
@WebServlet(name="TicketSelectionServlet")
public class TicketSelectionServlet extends BasicUtilitiesServlet {
    Logger logger = Logger.getLogger(TicketSelectionServlet.class.getName());
   
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        ServletContext context = session.getServletContext();
        // si llega de ticketSelectionSearch
        if(request.getParameters("billete1") == null) {
            HashMap<String,Servicio> servicios = (HashMap<String,Servicio>)
                context.getAttribute("servicios");
            String idServicio = request.getParameter("idServicio");
            if(!validateFreeText(idServicio,36,36) || !servicios.hasKey(idServicio)) {
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
                        gotoURL(ticketSelectionForm);
                    } else {
                        datosViajeros.add([nombre, apellidos, dni]);
                        //billeteReservado = new BilleteVendido(/*TODO*/);
                        //billetesRerservados.add(billeteReservado);
                    }
                }
                session.setAttribute("datosViajeros", datosViajeros);
                gotoURL(ticketSitForm, request, response);
            }
        }
    }

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

}

