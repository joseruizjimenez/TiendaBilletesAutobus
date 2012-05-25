package controller.privado;

import controller.BasicUtilitiesServlet;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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
import persistence.servicio.ServicioDAO;

/**
 * Cambia un billete adquirido por el seleccionado
 * 
 * Llega de: TicketSelectionInfo.jsp
 *           TicketOpMenu.jsp (si ya hay un billeteRerservado)
 * Va a: TicketOpChangeOK.jsp
 * 
 * Espera recibir en la consulta:
 *     - billeteGestion (session)
 *     - billetesReservados (session)
 * Devuelve:
 * 
 */
@WebServlet(name="TicketOpCchangeServlet")
public class TicketOpChangeServlet extends BasicUtilitiesServlet {
    Logger logger = Logger.getLogger(TicketOpChangeServlet.class.getName());
   
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
        
        
        // COMPRA
        ArrayList<BilleteVendido> billetesReservados =
                (ArrayList<BilleteVendido>) context.getAttribute("billetesRerservados");
        if(billetesReservados == null || billetesReservados.isEmpty()) {
            gotoURL(frontPage, request, response);
        } else {
            BilleteVendido billeteNuevo = billetesReservados.get(0);
            Factura facturaVieja = billete.getFactura();
            String NIF = facturaVieja.getDNI();
            String nombreComprador = facturaVieja.getNombreComprador();
            String DNI = facturaVieja.getDNI();
            String email = facturaVieja.getEmail();
            String mvl = facturaVieja.getMvl();
            String numTarjeta = facturaVieja.getNumTarjeta();
            String calle = facturaVieja.getCalle();
            String poblacion = facturaVieja.getPoblacion();
            String provincia = facturaVieja.getProvincia();
            String codigoPostal = facturaVieja.getCodPostal();
            BigDecimal totalFactura = billeteNuevo.getServicio().getPrecio();
            Factura facturaNueva = new Factura(NIF, nombreComprador, DNI, email,
                    mvl, numTarjeta, calle, poblacion, provincia,
                    codigoPostal, totalFactura);
            facturaDAO.createFactura(facturaNueva);
            billeteNuevo.setFactura(facturaNueva);
            billeteVendidoDAO.createBilleteVendido(billeteNuevo);
            String servicioNuevoId = billeteNuevo.getServicio().getIdAsString();
            Servicio servicioNuevoActualizado = servicioDAO.readServicio(servicioNuevoId);
            servicioNuevoActualizado.setPlazasOcupadas(
                    servicioNuevoActualizado.getPlazasOcupadas() + 1);
            servicioDAO.deleteServicio(servicioNuevoId);
            servicioDAO.createServicio(servicioNuevoActualizado);
            servicios.put(UUID.fromString(servicioNuevoId), servicioNuevoActualizado);
            context.setAttribute("servicios", servicios);
            request.setAttribute("factura", facturaNueva);
                    
            StringBuilder bill = new StringBuilder("");
            bill.append("Factura de compra: ");
            bill.append(facturaNueva.getIdAsString());
            bill.append("\n");
            bill.append("Nombre: ");
            bill.append(facturaNueva.getNombreComprador());
            bill.append("\n");
            bill.append("DNI: ");
            bill.append(facturaNueva.getDNI());
            bill.append("\n");
            bill.append("CIF: ");
            bill.append(facturaNueva.getCIF());
            bill.append("\n");
            bill.append("Numero de Tarjeta: ");
            bill.append(facturaNueva.getNumTarjeta());
            bill.append("\n");
            bill.append("Numero movil: ");
            bill.append(facturaNueva.getNumTarjeta());
            bill.append("\n");
            bill.append("Calle: ");
            bill.append(facturaNueva.getCalle());
            bill.append("\n");
            bill.append("Poblacion: ");
            bill.append(facturaNueva.getPoblacion());
            bill.append("\n");
            bill.append("Provincia: ");
            bill.append(facturaNueva.getProvincia());
            bill.append("\n");
            bill.append("Cod. Postal: ");
            bill.append(facturaNueva.getCodPostal());
            bill.append("\n\n");
            bill.append("Total Pago: ");
            bill.append(facturaNueva.getTotalAsFormattedString());
            InputStream is = context.getResourceAsStream(emailConfigFile);
            Properties emailConfig = new Properties();
            try {
                emailConfig.load(is);
                Session mailSession = Session.getDefaultInstance(emailConfig);
                mailSession.setDebug(true);
                Message mail = new MimeMessage(mailSession);
                mail.setSentDate(new java.util.Date(System.currentTimeMillis()));
                mail.addHeader("Content-Type", "text/html");
                mail.setFrom(new InternetAddress(emailConfig.getProperty("mail.smtp.user")));
                mail.setRecipient(Message.RecipientType.TO,
                        new InternetAddress(facturaNueva.getEmail()));

                mail.setSubject("Factura de compra de I Love Bus");
                mail.setText("Ha realizado con exito su compra, esta es su factura:\n" + bill);

                Transport transport = mailSession.getTransport("smtp");
                transport.connect(emailConfig.getProperty("mail.smtp.user"),
                        emailConfig.getProperty("password"));
                transport.sendMessage(mail, mail.getAllRecipients());
                transport.close();
                request.setAttribute("info", "El email con la factura ha sido enviado");
            } catch (IOException ex) {
                request.setAttribute("info", "Error enviando el email");
                logger.error("Error cargando los parametros del fichero de email", ex);
            } catch (AddressException ex) {
                request.setAttribute("info", "Error enviando el email");
                logger.warn("Error enviando email", ex);
            } catch (MessagingException ex) {
                request.setAttribute("info", "Error enviando el email");
                logger.warn("Error enviando email", ex);
            } finally {
                gotoURL(ticketOpChangeOK, request, response);
            }
        }
        
    }

}
