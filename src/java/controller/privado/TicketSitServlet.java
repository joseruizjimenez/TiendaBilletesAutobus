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
        ArrayList<BilleteVendido> billetesReservados =
                (ArrayList<BilleteVendido>) session.getAttribute("billetesReservados");
        if(billetesReservados == null || billetesReservados.isEmpty()) {
            gotoURL(frontPage, request, response);
        } else {
            String localizador = UUID.randomUUID().toString().substring(0, 7);
            int numBillete = 1;
            ArrayList<BilleteVendido> billetesReservadosAux = new ArrayList<BilleteVendido>();
            for (; numBillete <= billetesReservados.size();numBillete++) {
                BilleteVendido billeteReservado = billetesReservados.get(numBillete-1);
                String numPlaza = request.getParameter("numPlaza"
                        + Integer.toString(numBillete));
                String familia = request.getParameter("familia"
                        + Integer.toString(numBillete));
                String otros = request.getParameter("otros"
                        + Integer.toString(numBillete));
                if (numPlaza == null || "".equals(numPlaza)
                        || familia == null || "".equals(familia)) {
                    request.setAttribute("msg", "Complete todos los campos requeridos!");
                    request.setAttribute("asientosReservadosIda", new ArrayList<Integer>());
                    request.setAttribute("asientosReservadosVuelta", new ArrayList<Integer>());
                    gotoURL(ticketSitForm, request, response);
                    numBillete = -1;
                    break;
                } else {
                    if(numPlaza.length() == 1)
                        numPlaza = "0" + numPlaza;
                    String unionCamposEnLocalizador = numPlaza + localizador
                            + familia + "\n" + otros;
                    billeteReservado.setLocalizador(unionCamposEnLocalizador);
                    billetesReservadosAux.add(billeteReservado);
                }
            }
            if (numBillete != -1) {
                session.setAttribute("billetesReservados", billetesReservadosAux);
                gotoURL(ticketSelectionInfo, request, response);
            }
        }
    }

}
