package controller.publico;

import controller.BasicUtilitiesServlet;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.User;
import org.apache.log4j.Logger;
import persistence.user.UserDAO;
import persistence.user.UserPersistFactory;

/**
 * Crea una cuenta de usuario, anhadiendola a la persistencia
 */
@WebServlet(name="UserCreateServlet")
public class UserCreateServlet extends BasicUtilitiesServlet {
    Logger logger = Logger.getLogger(UserCreateServlet.class.getName());
   
    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (isBothPasswordEqual(request) && isUserFormSanitized(request)) {
            String nick = request.getParameter("nick");
            UserDAO userDAO = UserPersistFactory.getUserDAO(persistenceMechanism);
            if(isNickTaken(userDAO,nick)) {
                request.setAttribute("registerError","Ese nick: "+nick+" ya esta cogido");
            } else {
                User user = generateUserFromRequest(request);
                if(userDAO.createUser(user)) {
                    HttpSession session = request.getSession();
                    request.setAttribute("info","¡Su cuenta ha sido creada con exito!");
                    request.setAttribute("mail","true");
                    session.setAttribute("authenticated","true");
                    session.setAttribute("user",user);
                    logger.info("Creada la cuenta de usuario: "+user.getIdAsString());
                    gotoURL(userSettings,request,response);
                } else {
                    request.setAttribute("registerError","Algo falla en nuestro sistema! Intentalo mas tarde...");
                    logger.warn("Error insertando la cuenta de usuario: "+user.getIdAsString());
                }
            }
        } else {
            gotoURL(userForm,request,response);  
        }
    }
    
    /**
     * Comprueba que ambos password escritos por el usuario coinciden
     * @param request formulario
     * @return true si coinciden, false en caso contrario
     */
    private boolean isBothPasswordEqual(HttpServletRequest request) {
        String passOne = request.getParameter("pass1");
        String passTwo = request.getParameter("pass2");
        boolean equals = (passOne!=null && passTwo!=null) && (passOne.equals(passTwo));
        if(!equals) {
            request.setAttribute("registerError","Ambos password no coinciden!");
        }
        return equals;
    }
    
    /**
     * Comprueba todos los campos del formulario en busca de caracteres
     * no validos. Ademas comprueba que los tamanyos sean  correctos
     * @param request formulario
     * @return true si correcto, false en caso contrario
     */
    private boolean isUserFormSanitized(HttpServletRequest request) {
        String nick = request.getParameter("nick");
        if(!validateAlphaNumeric(nick,1,15)) {
            request.setAttribute("registerError",
                "El nick solo puede contener maximo 15 caracteres alfanumericos");
            return false;
        }
        String pass = request.getParameter("pass1");
        if(!validateAlphaNumeric(pass,1,15)) {
            request.setAttribute("registerError",
                "El password solo puede contener maximo 15 caracteres alfanumericos");
            return false;
        }
        String firstName = request.getParameter("firstName");
        if(!validateName(firstName,1,15)) {
            request.setAttribute("registerError",
                "El nombre no es valido. Maximo 15 caracteres alfabeticos");
            return false;
        }
        String lastName = request.getParameter("lastName");
        if(!validateName(lastName,1,35)) {
            request.setAttribute("registerError",
                "Los apellidos no son validos. Maximo 35 caracteres alfabeticos");
            return false;
        }
        String address = request.getParameter("address");
        if(!validateFreeText(address,1,100)) {
            request.setAttribute("registerError",
                "La direccion no es valida. Maximo 100 caracteres");
            return false;
        }
        String email = request.getParameter("email");
        if(!validateEmail(email)) {
            request.setAttribute("registerError",
                "La direccion de email no es una direccion real valida");
            return false;
        }
        String birthDay = request.getParameter("birthDay").trim();
        String birthMonth = request.getParameter("birthMonth").trim();
        String birthYear = request.getParameter("birthYear").trim();
        if(!validateDate(birthYear,birthMonth,birthDay)) {
            request.setAttribute("registerError",
                "La fecha de nacimiento no es coherente!");
            return false;
        }
        return true;
    }
    
    /**
     * Comprueba si el nick ya esta usado por otro usuario en la persistencia
     * @param userDAO controlador de la persistencia de los usuarios
     * @param nick nick a comprobar
     * @return true si esta usado, false si no lo esta
     */
    private boolean isNickTaken(UserDAO userDAO, String nick) {
        if(nick != null && userDAO.readUserByNick(nick) != null) {
            return true;
        }       
        return false;
    }
    
    /**
     * Genera un usuario a partir de un formulario con parametros validos
     * @param request con el formulario
     * @return user generado
     */
    private User generateUserFromRequest(HttpServletRequest request) {
        String nick = request.getParameter("nick");
        String pass = request.getParameter("pass1");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String address = request.getParameter("address");
        String email = request.getParameter("email");
        String birthDay = request.getParameter("birthDay").trim();
        String birthMonth = request.getParameter("birthMonth").trim();
        String birthYear = request.getParameter("birthYear").trim();
        int day = Integer.parseInt(birthDay);
        int month = Integer.parseInt(birthMonth);
        int year = Integer.parseInt(birthYear);
        
        return new User(nick,pass,firstName,lastName,address,email,year,month,day);
    }

}