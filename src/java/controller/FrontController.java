package controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.BilleteVendido;
import org.apache.log4j.Logger;
import persistence.billeteVendido.BilleteVendidoDAO;

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
        ServletContext context = session.getServletContext();
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
        } else if(to.equals("seleccionForm")) {
            gotoNamedResource(ticketSelectionFormServlet, request, response);     
        } else if(to.equals("asientos")) {
            gotoNamedResource(ticketSitServlet, request, response);
        } else if(to.equals("aceptacion")) {
            gotoURL(ticketSelectionInfo, request, response);
        } else if(to.equals("cancelado")) {
            //session.removeAttribute("datosViajeros");
            session.removeAttribute("billetesReservados");
            session.removeAttribute("billeteGestion");
            response.addCookie(new Cookie("numBilletes", ""));
            response.addCookie(new Cookie("idServicioIda", ""));
            response.addCookie(new Cookie("idServicioVuelta", ""));
            gotoURL(frontPage, request, response);
        } else if(to.equals("aceptado")) {
            gotoURL(ticketCheckoutForm, request, response);
        } else if(to.equals("pagar")) {
            gotoNamedResource(ticketCheckoutServlet, request, response);
        } else if(to.equals("gestion")) {
            gotoURL(ticketOpSearch, request, response);
        } else if(to.equals("buscarBillete")) {
            gotoNamedResource(ticketOpSearchServlet, request, response);
        } else if(to.equals("buscarBilleteList")) {
            String idBilleteGestion = request.getParameter("idBilleteGestion");
            BilleteVendidoDAO billeteVendidoDAO =
                (BilleteVendidoDAO) context.getAttribute("billeteVendidoDAO");
            session.setAttribute("billeteGestion", billeteVendidoDAO.
                    readBilleteVendido(idBilleteGestion));
            gotoNamedResource(ticketOpSearchListServlet, request, response);
        } else if(to.equals("informacion") &&
                session.getAttribute("billeteGestion") != null) {
            gotoURL(ticketOpInfo, request, response);
        } else if(to.equals("cancelacion") &&
                session.getAttribute("billeteGestion") != null) {
            gotoNamedResource(ticketOpCancelServlet, request, response);
        } else if(to.equals("cambioCheck") &&
                session.getAttribute("billeteGestion") != null) {
            ArrayList<BilleteVendido> billetesReservados =
                    (ArrayList<BilleteVendido>) context.getAttribute("billetesRerservados");
            if (billetesReservados == null || billetesReservados.isEmpty()) {
                gotoURL(ticketSearchForm, request, response);
            } else {
                gotoURL(ticketSearchForm, request, response);
                //gotoNamedResource(ticketOpChangeServlet, request, response);
            }
        /*} else if(to.equals("cambio") &&
                session.getAttribute("billeteGestion") != null) {
            gotoNamedResource(ticketOpChangeSelectionServlet, request, response);*/
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
