package controller.privado;

import controller.BasicUtilitiesServlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.BilleteVendido;
import model.Factura;
import model.Servicio;
import org.apache.log4j.Logger;
import persistence.billeteVendido.BilleteVendidoDAO;
import persistence.factura.FacturaDAO;

/**
 * Realiza la busqueda del billete a gestionar
 * 
 * Llega de: TicketOpSearch.jsp
 * Va a: TicketOpMenu.jsp (exito y solo una coincidencia)
 *       TicketOpSerachList.jsp (exito y varias coincidencias))
 *       TicketOpSerachError.jsp (no se encuentra ninguna coincidencia)
 * 
 * Espera recibir en la consulta:
 *     - localizador o numTarjeta (request)
 *     - dni (request)
 *     - fecha (request) NO SE USA
 * Devuelve:
 *     - billetesGestion (request) solo por num tarjeta y varias coincidencias
 *     - billeteGestion (session)
 *     - msg si no se ha rellenado ningun campo
 */
@WebServlet(name="TicketOpSearchServlet")
public class TicketOpSearchServlet extends BasicUtilitiesServlet {
    Logger logger = Logger.getLogger(TicketOpSearchServlet.class.getName());
   
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        ServletContext context = session.getServletContext();
        String localizador = request.getParameter("localizador");
        String dni = request.getParameter("dni");
        FacturaDAO facturaDAO = (FacturaDAO) context.getAttribute("facturaDAO");
        HashMap<UUID, Servicio> servicios =
                        (HashMap<UUID, Servicio>) context.getAttribute("servicios");
        BilleteVendidoDAO billeteVendidoDAO =
                (BilleteVendidoDAO) context.getAttribute("billeteVendidoDAO");
        if(localizador != null && !"".equals(localizador)) {
            if (validateAlphaNumeric(localizador, 7, 7)) {
                ArrayList<BilleteVendido> billetesGestionBusqueda = (ArrayList<BilleteVendido>)
                    billeteVendidoDAO.listBilleteVendido("", "", "");
                boolean exito = false;
                for (BilleteVendido encontrado : billetesGestionBusqueda) {
                    if(encontrado.getLocalizador().equalsIgnoreCase(localizador) &&
                            encontrado.getDniViajero().equalsIgnoreCase(dni)) {
                        encontrado.setFactura(facturaDAO.readFactura(
                            encontrado.getFactura().getIdAsString()));
                        encontrado.setServicio(servicios.get(encontrado.getServicio().getId()));
                        session.setAttribute("billeteGestion", encontrado);
                        exito = true;
                        break;
                    }
                }
                if(!exito) {
                    request.setAttribute("msg", "No se han encontrado coincidencias!");
                    gotoURL(ticketOpSearch, request, response);
                } else {

                    gotoURL(ticketOpMenu, request, response);
                }
            } else {
                request.setAttribute("msg", "Formato de localizador erroneo!");
                gotoURL(ticketOpSearch, request, response);
            }
        } else {
            String numTarjeta = request.getParameter("numTarjeta");
            if(numTarjeta != null && !"".equals(numTarjeta)) {
                if (validateNumeric(numTarjeta, 16, 16)) {
                    ArrayList<Factura> facturas = facturaDAO.listFactura(dni, numTarjeta);
                    ArrayList<BilleteVendido> billetesVendidos = (ArrayList<BilleteVendido>)
                            billeteVendidoDAO.listBilleteVendido("", "", "");
                    ArrayList<BilleteVendido> billetesGestion = new ArrayList<BilleteVendido>();
                    for (Factura factura : facturas) {
                        String facturaId = factura.getIdAsString();
                        for(BilleteVendido billete : billetesVendidos) {
                            if (billete.getFactura().getIdAsString().equals(facturaId)) {
                                billete.setFactura(factura);
                                Servicio servicio = servicios.get(billete.getServicio().getId());
                                billete.setServicio(servicio);
                                billetesGestion.add(billete);
                            }
                        }
                    }
                    if (billetesGestion.isEmpty()) {
                        request.setAttribute("msg", "No se han encontrado coincidencias!");
                        gotoURL(ticketOpSearch, request, response);
                    } else {
                        if (billetesGestion.size() == 1) {
                            session.setAttribute("billeteGestion", billetesGestion.get(0));
                            gotoURL(ticketOpMenu, request, response);
                        } else {
                            request.setAttribute("billetesGestion", billetesGestion);
                            gotoURL(ticketOpSearchList, request, response);
                        }
                    }
                } else {
                    request.setAttribute("msg", "Formato del num Tarjeta erroneo!");
                    gotoURL(ticketOpSearch, request, response);
                }
            } else {
                request.setAttribute("msg", "Rellene algún campo de busqueda");
                gotoURL(ticketOpSearch, request, response);
            }
        }
    }

}
