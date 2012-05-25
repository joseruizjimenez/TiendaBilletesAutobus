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
 * Se comunicaria con la plataforma de pago, realmente solo hará el
 * pago ficticio, se recopilan datos y se persiste
 * 
 * Llega de: TicketCheckoutForm.jsp
 * Va a: TicketCheckoutError.jsp y TicketChekoutOK.jsp
 * 
 * Espera recibir en la consulta:
 *     - billetesRerservados (session) limitado a 20min
 *     - CIF, NombreComprador, DNI, email, mvl, numTarjeta, calle, poblacion,
 *     - provincia y codigoPostal (atributos)
 * Devuelve:
 *     - billetesReservados (sesion) actualizados, falta la factura
 *     - factura (request) por si tiene que ser impresa
 *     - msg si no se ha rellenado ningun campo
 */
@WebServlet(name="TicketCheckoutServlet")
public class TicketCheckoutServlet extends BasicUtilitiesServlet {
    Logger logger = Logger.getLogger(TicketCheckoutServlet.class.getName());
   
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
            String NIF = request.getParameter("NIF");
            String nombreComprador = request.getParameter("nombreComprador");
            String DNI = request.getParameter("DNI");
            String email = request.getParameter("email");
            String mvl = request.getParameter("mvl");
            String numTarjeta = request.getParameter("numTarjeta");
            String calle = request.getParameter("calle");
            String poblacion = request.getParameter("poblacion");
            String provincia = request.getParameter("provincia");
            String codigoPostal = request.getParameter("codigoPostal");
            if (NIF == null ||
                    nombreComprador == null || "".equals(nombreComprador) ||
                    DNI == null || "".equals(DNI) ||
                    email == null || "".equals(email) ||
                    mvl == null || "".equals(mvl) ||
                    numTarjeta == null || "".equals(numTarjeta) ||
                    calle == null || "".equals(calle) ||
                    poblacion == null || "".equals(poblacion) ||
                    provincia == null || "".equals(provincia) ||
                    codigoPostal == null || "".equals(codigoPostal)) {
                request.setAttribute("msg", "Complete todos los campos requeridos");
                gotoURL(ticketCheckoutForm, request, response);
            } else if (!validateName(nombreComprador, 2, 300) ||
                    !validateDNI(DNI) ||
                    !validateEmail(email) ||
                    !validateNumeric(mvl, 9, 9) ||
                    !validateNumeric(numTarjeta, 16, 16) ||
                    !validateName(calle, 0, 300) ||
                    !validateName(poblacion, 0, 300) ||
                    !validateName(provincia, 0, 300) ||
                    !validateNumeric(codigoPostal, 5, 5)) {
                request.setAttribute("msg", "El campo contiene caracteres no permitidos");
                gotoURL(ticketCheckoutForm, request, response);
            } else {
                BigDecimal totalFactura = new BigDecimal(0);
                for(BilleteVendido billete : billetesReservados) {
                    totalFactura.add(billete.getServicio().getPrecio());
                }
                Factura factura = new Factura(NIF, nombreComprador, DNI, email,
                        mvl, numTarjeta, calle, poblacion, provincia,
                        codigoPostal, totalFactura);
                
                BilleteVendidoDAO billeteVendidoDAO =
                        (BilleteVendidoDAO) context.getAttribute("billeteVendidoDAO");
                FacturaDAO facturaDAO = (FacturaDAO) context.getAttribute("facturaDAO");
                ServicioDAO servicioDAO = (ServicioDAO) context.getAttribute("servicioDAO");
                HashMap<UUID, Servicio> servicios =
                        (HashMap<UUID, Servicio>) context.getAttribute("servicios");
                facturaDAO.createFactura(factura);
                for(BilleteVendido billete : billetesReservados) {
                    billete.setFactura(factura);
                    billeteVendidoDAO.createBilleteVendido(billete);
                    
                    String servicioId = billete.getServicio().getIdAsString();
                    Servicio servicioActualizado = servicioDAO.readServicio(servicioId);
                    servicioActualizado.setPlazasOcupadas(
                            servicioActualizado.getPlazasOcupadas() + 1);
                    servicioDAO.deleteServicio(servicioId);
                    servicioDAO.createServicio(servicioActualizado);
                    servicios.put(UUID.fromString(servicioId), servicioActualizado);
                }
                context.setAttribute("servicios", servicios);
                request.setAttribute("factura", factura);
                
                StringBuilder bill = new StringBuilder("");
                bill.append("Factura de compra: ");
                bill.append(factura.getIdAsString());
                bill.append("\n");
                bill.append("Nombre: ");
                bill.append(factura.getNombreComprador());
                bill.append("\n");
                bill.append("DNI: ");
                bill.append(factura.getDNI());
                bill.append("\n");
                bill.append("CIF: ");
                bill.append(factura.getCIF());
                bill.append("\n");
                bill.append("Numero de Tarjeta: ");
                bill.append(factura.getNumTarjeta());
                bill.append("\n");
                bill.append("Numero movil: ");
                bill.append(factura.getNumTarjeta());
                bill.append("\n");
                bill.append("Calle: ");
                bill.append(factura.getCalle());
                bill.append("\n");
                bill.append("Poblacion: ");
                bill.append(factura.getPoblacion());
                bill.append("\n");
                bill.append("Provincia: ");
                bill.append(factura.getProvincia());
                bill.append("\n");
                bill.append("Cod. Postal: ");
                bill.append(factura.getCodPostal());
                bill.append("\n\n");
                bill.append("Total Pago: ");
                bill.append(factura.getTotalAsFormattedString());
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
                            new InternetAddress(factura.getEmail()));

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
                    gotoURL(ticketCheckoutOK, request, response);
                }
                
            }
            
        }
    }

}
