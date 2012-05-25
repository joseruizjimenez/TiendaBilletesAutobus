package controller.privado;

import controller.BasicUtilitiesServlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.BilleteVendido;
import model.Servicio;
import org.apache.log4j.Logger;
import persistence.billeteVendido.BilleteVendidoDAO;
import persistence.factura.FacturaDAO;
import persistence.servicio.ServicioDAO;

/**
 * Cancela el billete a gestionar y te saca el menu de gestion
 * 
 * Llega de: TicketSitForm.jsp
 * Va a: TicketSelectionInfo.jsp
 * 
 * Espera recibir en la consulta:
 *     - billeteGestion (session)
 * Devuelve:
 * 
 */
@WebServlet(name="TicketOpCancelServlet")
public class TicketOpCancelServlet extends BasicUtilitiesServlet {
    Logger logger = Logger.getLogger(TicketOpCancelServlet.class.getName());
   
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        ServletContext context = session.getServletContext();
        BilleteVendidoDAO billeteVendidoDAO =
                (BilleteVendidoDAO) context.getAttribute("billeteVendidoDAO");
        FacturaDAO facturaDAO = (FacturaDAO) context.getAttribute("facturaDAO");
        ServicioDAO servicioDAO = (ServicioDAO) context.getAttribute("servicioDAO");
        HashMap<UUID, Servicio> servicios = (HashMap<UUID, Servicio>)
                context.getAttribute("servicios");
        
        BilleteVendido billete = (BilleteVendido) session.getAttribute("billeteGestion");
        UUID servicioId = UUID.fromString(billete.getServicio().getIdAsString());
        Servicio servicioActualizado = servicios.get(servicioId);
        servicioActualizado.setPlazasOcupadas(servicioActualizado.getPlazasOcupadas()-1);
        servicios.put(servicioId, servicioActualizado);
        context.setAttribute("servicios", servicios);
        servicioDAO.deleteServicio(servicioId.toString());
        servicioDAO.createServicio(servicioActualizado);
        servicios.put(servicioId, servicioActualizado);
        //facturaDAO.deleteFactura(billete.getFactura().getIdAsString());
        billeteVendidoDAO.deleteBilleteVendido(billete.getIdAsString());
        
        gotoURL(ticketOpCancelOK, request, response);
    }

}
