package controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

/**
 * Servlet Controlador que recibe todas las peticiones destinadas a un Servlet
 * comprueba a donde van las peticiones y las redirige si cumplen los requisitos
 * @author Jose Ruiz Jimenez
 */
@WebServlet(name="FrontController", urlPatterns={"/go"})
public class FrontController extends BasicUtilitiesServlet {

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(FrontController.class.getName()).error(
                    "No se pudo establecer el encoding", ex);
        }
        HttpSession session = request.getSession();
        String to = request.getParameter("to");
        if (to == null || (persistenceMechanism != null
                && persistenceMechanism.equals("none"))) {
            gotoURL(errorForm, request, response);
        } else if(to.equals("busqueda")) {
            if(request.getParameter("origen") == null ) {
                gotoURL(ticketSearchForm, request, response);
            } else {
                gotoNamedResource(ticketSearchServlet, request, response);
            }            
        } else if(to.equals("seleccion")) {
            gotoNamedResource(ticketSelectionServlet, request, response);            
        } else if(to.equals("asientos")) {
            gotoNamedResource(ticketSitServlet, request, response);
        } else if(to.equals("aceptacion")) {
            gotoURL(ticketSelectionInfo, request, response);
        } else if(to.equals("cancelado")) {
            session.removeAttribute("datosViajeros");
            session.removeAttribute("billetesReservados");
            response.addCookie(new Cookie("numBilletes", ""));
            response.addCookie(new Cookie("idServicio", ""));
            gotoURL(frontPage, request, response);
        } else if(to.equals("aceptado")) {
            gotoURL(ticketCheckoutForm, request, response);
        } else if(to.equals("pagar")) {
            gotoNamedResource(ticketCheckoutServlet, request, response);
        } else if(to.equals("gestion")) {
            gotoURL(ticketOpSearch, request, response);
        } else if(to.equals("buscarBillete")) {
            gotoNamedResource(ticketOpSearchServlet, request, response);
        } else if(to.equals("informacion") &&
                session.getAttribute("billeteGestion") != null) {
            gotoURL(ticketOpInfo, request, response);
        } else if(to.equals("cancelacion")) {
            gotoNamedResource(ticketOpCancelServlet, request, response);
        } else if(to.equals("cambio")) {
            gotoNamedResource(ticketOpChangeSelectionServlet, request, response);
        } else if(to.equals("aplicarCambio")) {
            gotoNamedResource(ticketOpChangeServlet, request, response);
        } else if(to.equals("solicitudClubBus")) {
            gotoURL(clubBusCreateForm, request, response);
        } else if(to.equals("solicitudAplicada")) {
            gotoURL(clubBusCreateOK, request, response);
        } else {
            gotoURL(errorForm, request, response);
        }
    }
}
