
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import entity.Book;
import entity.History;
import entity.Reader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import session.BookFacade;
import session.HistoryFacade;
import session.ReaderFacade;

/**
 *
 * @author user
 */
@WebServlet(name = "HistoryServlet", urlPatterns = {
    "/takeOnBook",
    "/createHistory",
    "/returnBook",
    "/updateHistory",
    "/listHistories",
    "/showStat",
    "/calcStat",
    
})
public class HistoryServlets extends HttpServlet {
    
    @EJB ReaderFacade readerFacade;
    @EJB BookFacade bookFacade;
    @EJB HistoryFacade historyFacade;
    
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
            case "/takeOnBook":
                List<Reader> listReaders = readerFacade.findAll();
                List<Book> listBooks = bookFacade.findAll();
                request.setAttribute("listReaders", listReaders);
                request.setAttribute("listBooks", listBooks);
                request.getRequestDispatcher("/WEB-INF/history/takeOnBook.jsp").forward(request, response);
                break;
            case "/createHistory":
                String bookId = request.getParameter("bookId");
                String readerId = request.getParameter("readerId");
               
                if(bookId == null || bookId.isEmpty() || readerId == null || readerId.isEmpty()){
                    request.setAttribute("info","Обувь или Клиент не выбраны!");
                    request.getRequestDispatcher("/takeOnBook").forward(request, response);
                    break;
                }
               
                Book book = bookFacade.find(Long.parseLong(bookId));
                Reader reader = readerFacade.find(Long.parseLong(readerId));
                if(book.getQuantity()>0){
                    book.setQuantity(book.getQuantity()-1);
                    reader.setBalanse(reader.getBalanse()- book.getPublishedYear());
                    bookFacade.edit(book);                   
                    History history = new History();
                    history.setBook(book);
                    history.setReader(reader);
                    history.setTakeOnBook(new GregorianCalendar().getTime());
                    historyFacade.create(history);
                    readerFacade.edit(reader);
                    request.setAttribute("info","Покупка завершена");
                    
                }else{
                    request.setAttribute("info","Обувь не выдана, все экземпляры проданы");
                    
                }
                request.getRequestDispatcher("/takeOnBook").forward(request, response);
                break;
           
            
            case "/listHistories":
                request.setAttribute("listReaders", readerFacade.findAll());
                request.getRequestDispatcher("/WEB-INF/readers/listReaders.jsp").forward(request, response);
                break;
            case "/showStat":
                LocalDateTime localDateTime = LocalDateTime.now();
                int year = localDateTime.getYear();
                request.setAttribute("year", year);
                request.getRequestDispatcher("/WEB-INF/history/showStat.jsp").forward(request, response);
                break;
            case "/calcStat":
                String selectDay = request.getParameter("selectDay");
                String selectMonth = request.getParameter("selectMonth");
                String selectYear = request.getParameter("selectYear");
                Map<Book,Integer> mapStat = new HashMap<>();
                List<History> listHistoryStat = historyFacade.getListHistory(selectYear,selectMonth,selectDay);
                listBooks = bookFacade.findAll();
                for (int i = 0; i < listBooks.size(); i++) {
                    Book b = listBooks.get(i);
                    int n = 0;
                    for (int j = 0; j < listHistoryStat.size(); j++) {
                        Book readerBook = listHistoryStat.get(j).getBook();
                        if(readerBook.equals(b)){
                            if(mapStat.get(b) != null) n = mapStat.get(b); 
                            mapStat.put(b,n+1);
                        }
                    }
                }
                request.setAttribute("mapStat", mapStat);
                request.setAttribute("selectDay", selectDay);
                request.setAttribute("selectMonth", selectMonth);
                request.setAttribute("selectYear", selectYear);
                if(selectDay.isEmpty() && selectMonth.isEmpty() && !selectYear.isEmpty()){
                        request.setAttribute("period", "За год");
                }else if(selectDay.isEmpty() && !selectMonth.isEmpty() && !selectYear.isEmpty()){
                    request.setAttribute("period", "За месяц");
                }else if(!selectDay.isEmpty() && !selectMonth.isEmpty() && !selectYear.isEmpty()){
                    request.setAttribute("period", "За день");
                }
                request.getRequestDispatcher("/showStat").forward(request, response);
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
