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
        String to = request.getParameter("to");
        HttpSession session = request.getSession();
        String authenticated = (String) session.getAttribute("authenticated");
        String admin = (String) session.getAttribute("admin");
        if (to == null || (persistenceMechanism != null
                && persistenceMechanism.equals("none"))) {
            gotoURL(errorForm, request, response);
        } else if(to.equals("product")) {
            gotoNamedResource(recordInfoServlet,request,response);
        } else if(to.equals("search")) {
            if(request.getParameter("type")==null && request.getParameter("artist")==null
                    && request.getParameter("recordLabel")==null) {
                gotoURL(recordSearch,request,response);
            } else {
                gotoNamedResource(recordSearchServlet,request,response);
            }
        } else if(to.equals("featured")) {
            gotoURL(featuredRecords,request,response);
        } else if(to.equals("new")) {
            gotoURL(newRecords,request,response);
        } else if(to.equals("login")) {
            gotoURL(userLogin,request,response);
        } else if(to.equals("shoppingcart")) {
            if(request.getParameter("id")==null) {
                gotoURL(shoppingCart,request,response);
            } else {
                gotoNamedResource(shoppingCartServlet,request,response);
            }
        } else if(to.equals("checkout")) {
            if(request.getParameter("action")==null) {
                gotoURL(checkoutLogin,request,response);
            } else {
                gotoNamedResource(checkoutCartServlet,request,response);
            }
        } else if(to.equals("user")) {
            if("false".equals(authenticated)) {
                if(request.getParameter("user")==null) {
                    gotoURL(userLogin,request,response);
                } else {
                    gotoNamedResource(userLoginServlet,request,response);
                }
            } else if("true".equals(authenticated)) {
                String action = request.getParameter("action");
                if(action == null) {
                    gotoURL(userSettings,request,response);
                } else if("delete".equals(action)) {
                    gotoNamedResource(userDeleteServlet,request,response);
                } else if("update".equals(action)) {
                    if(request.getParameter("nick")==null) {
                        gotoURL(userUpdate,request,response);
                    } else {
                        gotoNamedResource(userUpdateServlet,request,response);
                    }
                }
            }
        } else if(to.equals("register") && "false".equals(authenticated)) {
            if(request.getParameter("nick")==null) {
                gotoURL(userForm,request,response);
            } else {
                gotoNamedResource(userCreateServlet,request,response);
            }
        } else if(to.equals("logout")) {
            gotoNamedResource(userLogoutServlet,request,response);
        } else if(to.equals("comment") && "true".equals(authenticated)) {
            String action = request.getParameter("action");
            if("new".equals(action)) {
                gotoNamedResource(commentCreateServlet,request,response);
            } else if("delete".equals(action)) {
                gotoNamedResource(commentDeleteServlet,request,response);
            }
        } else if(to.equals("mailuser")) {
            gotoNamedResource(mailUserServlet,request,response);
        } else if(to.equals("mailsale")) {
            gotoNamedResource(mailSaleServlet,request,response);
        } else if(to.equals("admin")) {
            if("false".equals(admin)) {
                if(request.getParameter("user")==null) {
                    gotoURL(adminLogin,request,response);
                } else {
                    gotoNamedResource(adminLoginServlet,request,response);
                }
            } else if("true".equals(admin)) {
                String action = request.getParameter("action");
                if(action == null) {
                    gotoURL(adminSettings,request,response);
                } else if("newrecord".equals(action)) {
                    if(request.getParameter("name")==null) {
                        gotoURL(adminRecordForm,request,response);
                    } else {
                        gotoNamedResource(recordCreateServlet,request,response);
                    }
                } else if("updaterecord".equals(action)) {
                    if(request.getParameter("name")==null) {
                        request.setAttribute("id",request.getParameter("id"));
                        gotoURL(adminRecordUpdate,request,response);
                    } else {
                        gotoNamedResource(recordUpdateServlet,request,response);
                    }
                } else if("deleterecord".equals(action)) {
                    gotoNamedResource(recordDeleteServlet,request,response);
                } else if("searchuser".equals(action)) {
                    if(request.getParameter("nick")==null) {
                        gotoURL(adminUserSearch,request,response);
                    } else {
                        gotoNamedResource(userSearchServlet,request,response);
                    }
                } else if("searchsale".equals(action)) {
                    if(request.getParameter("id")==null) {
                        gotoURL(adminSalesSearch,request,response);
                    } else {
                        gotoNamedResource(salesSearchServlet,request,response);
                    }
                } else if("logout".equals(action)) {
                    gotoNamedResource(adminLogoutServlet,request,response);
                }
            }      
        } else {
            gotoURL(errorForm, request, response);
        }
    }
}