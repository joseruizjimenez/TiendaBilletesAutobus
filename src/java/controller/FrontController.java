package controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.BilleteVendido;
import model.Factura;
import model.Servicio;
import org.apache.log4j.Logger;
import persistence.billeteVendido.BilleteVendidoDAO;
import persistence.factura.FacturaDAO;
import persistence.ruta.RutaDAO;
import persistence.servicio.ServicioDAO;

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
            FacturaDAO facturaDAO = (FacturaDAO) context.getAttribute("facturaDAO");
            ServicioDAO servicioDAO = (ServicioDAO) context.getAttribute("servicioDAO");
            RutaDAO rutaDAO = (RutaDAO) context.getAttribute("rutaDAO");
            BilleteVendido billeteGestion = billeteVendidoDAO.
                    readBilleteVendido(idBilleteGestion);
            Servicio servicioGestion = servicioDAO.readServicio(
                    billeteGestion.getServicio().getIdAsString());
            servicioGestion.setRuta(rutaDAO.readRuta(
                servicioGestion.getRuta().getIdAsString()));
            billeteGestion.setServicio(servicioGestion);
            billeteGestion.setFactura(facturaDAO.readFactura(
                    billeteGestion.getFactura().getIdAsString()));
            session.setAttribute("billeteGestion", billeteGestion);
            gotoURL(ticketOpMenu, request, response);
        } else if(to.equals("informacion") &&
                session.getAttribute("billeteGestion") != null) {
            gotoURL(ticketOpInfo, request, response);
        } else if(to.equals("emailFactura") &&
                session.getAttribute("billeteGestion") != null) {
            BilleteVendido billeteG = (BilleteVendido) session.getAttribute("billeteGestion");
            enviarEmailFactura(billeteG.getFactura(), request, response);
        } else if(to.equals("cancelMenu") &&
                session.getAttribute("billeteGestion") != null) {
            gotoURL(ticketOpCancel, request, response);
        } else if(to.equals("cancelacion") &&
                session.getAttribute("billeteGestion") != null) {
            gotoNamedResource(ticketOpCancelServlet, request, response);
        } else if(to.equals("cambioCheck") &&
                session.getAttribute("billeteGestion") != null) {
            gotoURL(ticketSearchForm, request, response);
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

    private void enviarEmailFactura(Factura factura, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        ServletContext context = session.getServletContext();
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
        bill.append(factura.getMvl());
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
            request.setAttribute("msg", "El email con la factura ha sido enviado");
        } catch (IOException ex) {
            request.setAttribute("msg", "Error enviando el email");
        } catch (AddressException ex) {
            request.setAttribute("msg", "Error enviando el email");
        } catch (MessagingException ex) {
            request.setAttribute("msg", "Error enviando el email");
        } finally {
            gotoURL(ticketOpMenu, request, response);
        }
    }
}
