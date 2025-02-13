package com.jagger.hello;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import com.jagger.utils.ServletScanner;

@WebServlet("/api-list")
public class ServletListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        
        // Scan and return APIs without storing in the database
        JSONArray apis = ServletScanner.scanServlets();
        response.getWriter().write(apis.toString(2)); // Pretty print JSON
    }
}
