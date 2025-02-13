package com.jagger.hello;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jagger.utils.ApiAction;
import com.jagger.utils.ApiParam;

@WebServlet("/myServlet")
public class MyServlet extends HttpServlet {

    @ApiAction(actionName = "get")

    protected void handleAction1(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.getWriter().write("This is Action 1");
    }

    @ApiAction(actionName = "post")
    protected void handleAction2(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.getWriter().write("This is Action 2");
    }

    @Override
    @ApiParam(name = "action",type="String")
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");
        if ("action1".equals(action)) {
            handleAction1(request, response);
        } else if ("action2".equals(action)) {
            handleAction2(request, response);
        } else {
            response.getWriter().write("Invalid action");
        }
    }
}
