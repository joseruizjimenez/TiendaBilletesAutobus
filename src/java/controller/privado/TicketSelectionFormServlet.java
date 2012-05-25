package controller.privado;

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
import model.BilleteVendido;
import model.Servicio;
import org.apache.log4j.Logger;
import persistence.billeteVendido.BilleteVendidoDAO;

/**
 * Recibe datos personales del viajero de cada billete y va componiendo la reserva
 * 
 * Llega de: TicketSelectionForm.jsp
 * Va a: TicketSitForm.jsp
 * 
 * Espera recibir en la consulta:
 *     - idServicioIda (cookie)
 *     - idServicioVuelta (cookie) opcional
 *     - numBilletes (cookie)
 *     - nombre1, apellidos1, dni1 (parametros)
 *     - nombreVuelta1, apellidosVuelta2, dniVuelta1 (parametros) opcional
 *     - lo mismo para mas billetes: nombre2, apellidos2 etc (opcional)
 * Devuelve:
 *     - billetesReservados (sesion) seran vendidos rellenando: factura y localizador
 *     - asientosReservadosIda (atributo) para pintar el autobus del servicio de ida
 *     - asientosReservadosVuelta (atributo) para pintar el servicio de vuelta
 *     - msg si no se ha rellenado ningun campo
 * Elimina:
 *     - idServicioIda, idServicioVuelta y numBilletes (cookies)
 */
@WebServlet(name="TicketSelectionFormServlet")
public class TicketSelectionFormServlet extends BasicUtilitiesServlet {
    Logger logger = Logger.getLogger(TicketSelectionFormServlet.class.getName());
   
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        ServletContext context = session.getServletContext();
        HashMap<String, Servicio> servicios = (HashMap<String, Servicio>)
                context.getAttribute("servicios");
        Cookie[] cookies = request.getCookies();
        String idServicioIda = getIdServicioIdaFromCookies(cookies);
        String idServicioVuelta = getIdServicioVueltaFromCookies(cookies);
        int numBilletes = getNumBilletesFromCookies(cookies);
        if (numBilletes <= 0) {
            gotoURL(frontPage, request, response);
        } else {
            ArrayList<BilleteVendido> billetesReservados = new ArrayList<BilleteVendido>();
            for (int i = 1; i <= numBilletes; i++) {
                //billete ida
                String nombre = request.getParameter("nombre"
                        + Integer.toString(i));
                String apellidos = request.getParameter("apellidos"
                        + Integer.toString(i));
                String dni = request.getParameter("dni"
                        + Integer.toString(i));
                if (nombre == null || "".equals(nombre)
                        || apellidos == null || "".equals(apellidos)
                        || dni == null || "".equals(dni)) {
                    request.setAttribute("msg", "Complete todos los campos requeridos");
                    numBilletes = -1; //condicion de parada del bucle
                    gotoURL(ticketSelectionForm, request, response);
                } else if (!validateName(nombre, 2, 50)
                        || !validateName(apellidos, 1, 100)
                        || !validateDNI(dni)) {
                    request.setAttribute("msg", "El campo contiene caracteres no permitidos");
                    numBilletes = -1; //condicion de parada del bucle
                    gotoURL(ticketSelectionForm, request, response);
                } else {
                    BilleteVendido billeteReservadoIda = new BilleteVendido();
                    billeteReservadoIda.setServicio(servicios.get(
                            idServicioIda));
                    billeteReservadoIda.setNombreViajero(nombre + " " + apellidos);
                    billeteReservadoIda.setDniViajero(dni);
                    billetesReservados.add(billeteReservadoIda);

                    //billete vuelta
                    if (idServicioVuelta != null) {
                        String nombreVuelta = request.getParameter("nombreVuelta"
                                + Integer.toString(i));
                        String apellidosVuelta = request.getParameter("apellidosVuelta"
                                + Integer.toString(i));
                        String dniVuelta = request.getParameter("dniVuelta"
                                + Integer.toString(i));
                        if (nombreVuelta == null || "".equals(nombreVuelta)
                                || apellidosVuelta == null || "".equals(apellidosVuelta)
                                || dniVuelta == null || "".equals(dniVuelta)) {
                            request.setAttribute("msg", "Complete todos los campos requeridos");
                            numBilletes = -1; //condicion de parada del bucle
                            gotoURL(ticketSelectionForm, request, response);
                        } else if (!validateName(nombreVuelta, 2, 50)
                                || !validateName(apellidosVuelta, 1, 100)
                                || !validateDNI(dniVuelta)) {
                            request.setAttribute("msg", "El campo contiene caracteres no permitidos");
                            numBilletes = -1; //condicion de parada del bucle
                            gotoURL(ticketSelectionForm, request, response);
                        } else {
                            BilleteVendido billeteReservadoVuelta = new BilleteVendido();
                            billeteReservadoVuelta.setServicio(servicios.get(
                                    idServicioVuelta));
                            billeteReservadoVuelta.setNombreViajero(nombreVuelta
                                    + " " + apellidosVuelta);
                            billeteReservadoVuelta.setDniViajero(dniVuelta);
                            billetesReservados.add(billeteReservadoVuelta);
                        }
                    }
                }
                if(numBilletes == -1) {
                    session.setAttribute("billetesReservados", billetesReservados);
                    response.addCookie(new Cookie("numBilletes", ""));
                    response.addCookie(new Cookie("idServicioIda", ""));
                    response.addCookie(new Cookie("idServicioVuelta", ""));
                    
                    BilleteVendidoDAO billeteVendidoDAO =
                            (BilleteVendidoDAO) context.getAttribute("billeteVendidoDAO");
                    ArrayList<BilleteVendido> billetesVendidos = (ArrayList<BilleteVendido>)
                            billeteVendidoDAO.listBilleteVendido("", "", "");
                    ArrayList<Integer> asientosReservadosIda = new ArrayList<Integer>();
                    for(BilleteVendido billete : billetesVendidos) {
                        if (billete.getServicio().getIdAsString().equals(idServicioIda)) {
                            asientosReservadosIda.add(Integer.valueOf(billete.getNumAsiento()));
                        }
                    }
                    request.setAttribute("asientosReservadosIda", asientosReservadosIda);
                    if(idServicioVuelta != null) {
                        ArrayList<Integer> asientosReservadosVuelta = new ArrayList<Integer>();
                        for (BilleteVendido billete : billetesVendidos) {
                            if (billete.getServicio().getIdAsString().equals(idServicioVuelta)) {
                                asientosReservadosVuelta.add(Integer.valueOf(billete.getNumAsiento()));
                            }
                        }
                        request.setAttribute("asientosReservadosVuelta", asientosReservadosVuelta);
                    }
                    
                    gotoURL(ticketSitForm, request, response);
                }
            }
        }
    }

    private int getNumBilletesFromCookies(Cookie[] cookies) {
        if(cookies != null) {
            for(int i=0; i<cookies.length; i++) {
                if("numBilletes".equals(cookies[i].getName()) &&
                        !"".equals(cookies[i].getValue()))
                    return Integer.parseInt(cookies[i].getValue());
            }
        }
        return 0;
    }
    
    private String getIdServicioIdaFromCookies(Cookie[] cookies) {
        if(cookies != null) {
            for(int i=0; i<cookies.length; i++) {
                if("idServicioIda".equals(cookies[i].getName()) &&
                        !"".equals(cookies[i].getValue()))
                    return cookies[i].getValue();
            }
        }
        return null;
    }
    
    private String getIdServicioVueltaFromCookies(Cookie[] cookies) {
        if(cookies != null) {
            for(int i=0; i<cookies.length; i++) {
                if("idServicioVuelta".equals(cookies[i].getName()) &&
                        !"".equals(cookies[i].getValue()))
                    return cookies[i].getValue();
            }
        }
        return null;
    }
   

}

