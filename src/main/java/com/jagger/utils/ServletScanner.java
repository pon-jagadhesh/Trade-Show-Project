package com.jagger.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.servlet.annotation.WebServlet;

import org.reflections.Reflections;
import org.json.JSONArray;
import org.json.JSONObject;

public class ServletScanner {
	
    private static final String DB_URL = "jdbc:mysql://localhost:3306/swagger_lite";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "pass@123";
    
    public static void scanAndStoreServlets() {
        try   { 
        	Class.forName("com.mysql.cj.jdbc.Driver");
        	Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Reflections reflections = new Reflections("com.jagger.hello");
            Set<Class<?>> servlets = reflections.getTypesAnnotatedWith(WebServlet.class);

            String insertSQL = "INSERT INTO api_metadata (name, url, methods) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE url=VALUES(url), methods=VALUES(methods)";
            PreparedStatement stmt = conn.prepareStatement(insertSQL);
            System.out.println("Scanning servlets...");
            for (Class<?> servlet : servlets) {
                WebServlet annotation = servlet.getAnnotation(WebServlet.class);
                if (annotation != null) {
                    String name = servlet.getSimpleName();
                    String url = annotation.value()[0]; // First URL pattern
                    String methods = String.join(", ", getHttpMethods(servlet));

                    stmt.setString(1, name);
                    stmt.setString(2, url);
                    stmt.setString(3, methods);
                    stmt.executeUpdate();
                    
                    System.out.println("Inserted: " + name + " | " + url + " | " + methods);
                
                }
            }
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<String> getHttpMethods(Class<?> servlet) {
        List<String> methods = new ArrayList<>();
        
        // Get all declared methods in the servlet class
        java.lang.reflect.Method[] declaredMethods = servlet.getDeclaredMethods();

        for (java.lang.reflect.Method method : declaredMethods) {
            if (method.getName().equals("doGet")) {
                methods.add("GET");
            }
            if (method.getName().equals("doPost")) {
                methods.add("POST");
            }
        }

        return methods;
    }
    
    public static JSONArray loadAPIsFromDatabase() {
        JSONArray endpoints = new JSONArray();
        try {
        	Class.forName("com.mysql.cj.jdbc.Driver");
        	Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT name, url, methods FROM api_metadata");
             ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                JSONObject endpoint = new JSONObject();
                endpoint.put("name", rs.getString("name"));
                endpoint.put("url", rs.getString("url"));
                endpoint.put("methods", rs.getString("methods").split(", "));
                endpoints.put(endpoint);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return endpoints;
    }
}
