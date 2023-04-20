/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import entity.Author;
import entity.Book;
import entity.Cover;
import entity.History;
import entity.Reader;
import entity.secure.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import session.AuthorFacade;
import session.BookFacade;
import session.CoverFacade;

/**
 *
 * @author user
 */
@WebServlet(name = "BookServlet", urlPatterns = {
    "/addProduct",
    "/createBook",
    "/listProducts",
    "/product",
    "/plusBook",
     "/takeProd",
    
})
public class ProductServlet extends HttpServlet {
    @EJB AuthorFacade authorFacade;
    @EJB BookFacade bookFacade;
    @EJB CoverFacade coverFacade;
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
        HttpSession session = request.getSession(false);
        if(session == null){
            request.setAttribute("info", "У вас нет прав, авторизуйтесь!");
            request.getRequestDispatcher("/showLogin").forward(request, response);
            return;
        }
        User authUser = (User) session.getAttribute("user");
        if(authUser == null){
            request.setAttribute("info", "У вас нет прав, авторизуйтесь!");
            request.getRequestDispatcher("/showLogin").forward(request, response);
            return;
        }
        
        String path = request.getServletPath();
        switch (path) {
            case "/addProduct":
                if(!authUser.getRoles().contains("MANAGER")){
                    request.setAttribute("info", "У вас нет прав, Вы не менеджер!");
                    request.getRequestDispatcher("/showLogin").forward(request, response);
                    break;
                }
                request.setAttribute("listAuthors", authorFacade.findAll());
                request.setAttribute("listCovers", coverFacade.findAll());
                request.getRequestDispatcher("/WEB-INF/products/addProduct.jsp").forward(request, response);
                break;
            case "/createBook":
                String bookName = request.getParameter("bookName");
                String[] authors = request.getParameterValues("authors");
                String publishedYear = request.getParameter("publishedYear");
                String quantity = request.getParameter("quantity");
                String coverId = request.getParameter("coverId");
                if(bookName.isEmpty() || publishedYear.isEmpty() || quantity.isEmpty()){
                    request.setAttribute("bookName", bookName);
                    request.setAttribute("publishedYear", publishedYear);
                    request.setAttribute("quantity", quantity);
                    request.setAttribute("info", "Заполните все поля.");
                    request.getRequestDispatcher("/addProduct").forward(request, response);
                    break;
                }
                if(authors == null){
                    request.setAttribute("bookName", bookName);
                    request.setAttribute("publishedYear", publishedYear);
                    request.setAttribute("quantity", quantity);
                    request.setAttribute("info", "Вы не выбрали фирму");
                    request.getRequestDispatcher("/addProduct").forward(request, response);
                    break;
                }
                if(coverId == null){
                    request.setAttribute("bookName", bookName);
                    request.setAttribute("publishedYear", publishedYear);
                    request.setAttribute("quantity", quantity);
                    request.setAttribute("info", "Вы не выбрали картинку для обуви");
                    request.getRequestDispatcher("/addProduct").forward(request, response);
                    break;
                }
                List<Author> listAuthors = new ArrayList<>();
                for (int i = 0; i < authors.length; i++) {
                   listAuthors.add(authorFacade.find(Long.parseLong(authors[i])));
                }
                Book book = new Book();
                book.setAuthors(listAuthors);
                book.setBookName(bookName);
                book.setPublishedYear(Integer.parseInt(publishedYear));
                book.setQuantity(Integer.parseInt(quantity));
                Cover cover = coverFacade.find(Long.parseLong(coverId));
                book.setCover(cover);
                bookFacade.create(book);
                for (int i = 0; i < listAuthors.size(); i++) {
                    Author author = listAuthors.get(i);
                    author.getBooks().add(book);
                    authorFacade.edit(author);
                }
                request.setAttribute("info", "Обувь добавлена успешно");
                request.getRequestDispatcher("/index.jsp").forward(request, response);
                break;
            case "/listProducts":
                request.setAttribute("listProducts", bookFacade.findAll());
                request.getRequestDispatcher("/WEB-INF/products/listProducts.jsp").forward(request, response);
                break;
            case "/product":
                String id = request.getParameter("id");
                request.setAttribute("book", bookFacade.find(Long.parseLong(id)));
                request.getRequestDispatcher("/WEB-INF/products/product.jsp").forward(request, response);
                break;
             case "/takeProd":
            
                List<Book> listBooks = bookFacade.findAll();
              
                request.setAttribute("listBooks", listBooks);
                request.getRequestDispatcher("/WEB-INF/products/plusproduct.jsp").forward(request, response);
                break;
            case "/plusBook":
                String bookId = request.getParameter("bookId");
                String readerId = request.getParameter("readerId");
                String quantityby = request.getParameter("quantityby");
                if(bookId == null ){
                    request.setAttribute("info","Обувь выбраны!");
                    request.getRequestDispatcher("/takeOnBook").forward(request, response);
                    break;
                }
               
                Book bookk = bookFacade.find(Long.parseLong(bookId));
                Integer.parseInt(quantityby);
                bookk.setQuantity(bookk.getQuantity()+4);
                    
                bookFacade.edit(bookk);                   
                    
                request.setAttribute("info","Покупка завершена");
               
                request.getRequestDispatcher("/takeOnBook").forward(request, response);
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
