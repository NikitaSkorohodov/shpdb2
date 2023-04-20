
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import entity.Book;
import entity.Reader;
import entity.secure.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import session.HistoryFacade;
import session.ReaderFacade;
import session.UserFacade;
import tools.PasswordEncrypt;

/**
 *
 * @author user
 */
@WebServlet(name = "ReaderServlet", urlPatterns = {
    "/addReader",
    "/createReader",
    "/listReaders",
    

})
public class ReaderServlets extends HttpServlet {
    
    @EJB private ReaderFacade readerFacade;
    @EJB private HistoryFacade historyFacade;
    @EJB private UserFacade userFacade;
    static enum Role {USER,MANAGER,ADMINISTRATOR};
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();
        switch (path) {
            case "/addReader":
                request.getRequestDispatcher("/WEB-INF/readers/addReader.jsp").forward(request, response);
                break;
            case "/createReader":
                String firstname = request.getParameter("firstname");
                String lastname = request.getParameter("lastname");
                String phone = request.getParameter("phone");
                String login = request.getParameter("login");
                String password = request.getParameter("password");
                String balanse = request.getParameter("balanse");
                Reader reader = new Reader();
                reader.setPhone(phone);
                reader.setBalanse(Integer.parseInt(balanse));
                reader.setFirstname(firstname);
                reader.setLastname(lastname);
                readerFacade.create(reader);
                User user = new User();
                user.setLogin(login);
                PasswordEncrypt pe = new PasswordEncrypt();
                user.setSalt(pe.getSalt());
                password = pe.getProtectedPassword(password, user.getSalt());
                user.setPassword(password);
                user.setReader(reader);
                List<String> roles = new ArrayList<>();
                roles.add(Role.USER.toString());
                user.setRoles(roles);
                userFacade.create(user);
                request.setAttribute("info","Клиент успешно добавлен");
                request.getRequestDispatcher("/index.jsp").forward(request, response);
                break;
            case "/listReaders":
                Map<Reader, List<Book>> mapReaders = new HashMap();
                List<Reader> listReaders = readerFacade.findAll();
                for (int i = 0; i < listReaders.size(); i++) {
                    Reader r = listReaders.get(i);
                    mapReaders.put(r, historyFacade.getReadingBook(r)); 
                }
                request.setAttribute("mapReaders", mapReaders);
                request.getRequestDispatcher("/WEB-INF/readers/listReaders.jsp").forward(request, response);
                break;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
