<%-- 
    Document   : listBooks
    Created on : Feb 28, 2023, 11:10:00 AM
    Author     : user
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

       <h3 class="w-100 d-flex justify-content-center mt-5">Список обуви</h3>
       <div class="w-100 p-3 d-flex justify-content-center">
           <c:forEach var="product" items="${listProducts}">
            <div class="card m-2" style="width: 13rem;">
                <a class="text-decoration-none" href="product?id=${product.id}"><img src="insertFile/${product.cover.url}" width="200" height="200"/></a>
                
             </div>
           </c:forEach>
       </div>