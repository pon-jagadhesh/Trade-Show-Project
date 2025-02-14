package com.jagger.hello;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jagger.utils.ApiAction;
import com.jagger.utils.ApiParam;
import com.jagger.utils.ApiParamGroup;

@WebServlet("/myServlet")
public class MyServlet extends HttpServlet {

    @ApiAction(actionName = "get")
    protected void handleAction1(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("This is Action 1");
    }

    @ApiAction(actionName = "get")
    protected void handleAction2(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("This is Action 2");
    }

    @Override
    @ApiParamGroup(
        value = {
            @ApiParam(name = "action", type = "String"),
        }
    )
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");

        String action = request.getParameter("name");
        // String action2 = request.getParameter("age");
        // System.out.println(action + " " + action2);
        // response.getWriter().write("name=" + action + "age=" + action2);


        try {
            if ("action1".equals(action)) {
                handleAction1(request, response);
            } else if ("action2".equals(action)) {
                handleAction2(request, response);
            } else {
                response.getWriter().write("Invalid action");
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while processing the request");
        }
    }
}