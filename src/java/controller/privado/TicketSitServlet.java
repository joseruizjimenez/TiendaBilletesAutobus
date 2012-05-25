package controller.privado;

import controller.BasicUtilitiesServlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.BilleteVendido;
import org.apache.log4j.Logger;

/**
 * Formaliza la reserva añadiendo num plaza, localizador y otros datos
 * 
 * Llega de: TicketSitForm.jsp
 * Va a: TicketSelectionInfo.jsp
 * 
 * Espera recibir en la consulta:
 *     - billetesRerservados (session) limitado a 20min
 *     - numPlaza1, numPlaza2 etc
 *     - familia1, familia2 etc
 *     - otros1, otros2 etc
 * Devuelve:
 *     - billetesReservados (sesion) actualizados, falta la factura
 *     - msg
 */
@WebServlet(name="TicketSitServlet")
public class TicketSitServlet extends BasicUtilitiesServlet {
    Logger logger = Logger.getLogger(TicketSitServlet.class.getName());
   
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        ServletContext context = session.getServletContext();
        ArrayList<BilleteVendido> billetesReservados =
                (ArrayList<BilleteVendido>) context.getAttribute("billetesRerservados");
        if(billetesReservados == null || billetesReservados.isEmpty()) {
            gotoURL(frontPage, request, response);
        } else {
            String localizador = UUID.randomUUID().toString().substring(0, 7);
            int numBillete = 1;
            for (BilleteVendido billeteReservado : billetesReservados) {
                String numPlaza = request.getParameter("numPlaza"
                        + Integer.toString(numBillete));
                String familia = request.getParameter("familia"
                        + Integer.toString(numBillete));
                String otros = request.getParameter("otros"
                        + Integer.toString(numBillete));
                if (numPlaza == null || "".equals(numPlaza)
                        || familia == null || "".equals(familia)) {
                    request.setAttribute("msg", "Complete todos los campos requeridos");
                    gotoURL(ticketSitForm, request, response);
                    numBillete = -1;
                    break;
                } else {
                    String unionCamposEnLocalizador = numPlaza + localizador
                            + "Familia Numerosa: " + familia + "\n" + otros;
                    billeteReservado.setLocalizador(unionCamposEnLocalizador);
                    billetesReservados.add(numBillete - 1, billeteReservado);
                    numBillete++;
                }
            }
            if (numBillete != -1) {
                session.setAttribute("billetesReservados", billetesReservados);
                gotoURL(ticketSelectionInfo, request, response);
            }
        }
    }

}
