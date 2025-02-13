package com.jagger.utils;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.servlet.annotation.WebServlet;


import org.json.JSONArray;
import org.json.JSONObject;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

public class ServletScanner {

	public static JSONArray scanServlets() {
	    JSONArray endpoints = new JSONArray();

	    // Scan the entire root package 
	    Reflections reflections = new Reflections("com", Scanners.TypesAnnotated);

	    Set<Class<?>> servlets = reflections.getTypesAnnotatedWith(WebServlet.class);

	    for (Class<?> servlet : servlets) {
	        WebServlet annotation = servlet.getAnnotation(WebServlet.class);
	        if (annotation != null) {
	            JSONObject endpoint = new JSONObject();
	            endpoint.put("name", servlet.getSimpleName());
	            endpoint.put("url", annotation.value()[0]); // First URL pattern
	            endpoint.put("methods", getHttpMethods(servlet));
	            endpoints.put(endpoint);
	        }
	    }
	    return endpoints;
	}
    private static List<String> getHttpMethods(Class<?> servlet) {
        List<String> methods = new ArrayList<>();
        
        // Get all declared methods in the servlet class
        Method[] declaredMethods = servlet.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (method.getName().equals("doGet")) {
                methods.add("GET");
            }
            if (method.getName().equals("doPost")) {
                methods.add("POST");
            }
            if (method.getName().equals("doPut")) {
                methods.add("PUT");
            }
            if (method.getName().equals("doDelete")) {
                methods.add("DELETE");
            }
        }
        return methods;
    }
}
