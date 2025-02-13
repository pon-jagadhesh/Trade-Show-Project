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
    List<String> typeList = new ArrayList<>();
    List<String> nameList = new ArrayList<>();

    public JSONArray scanServlets() {
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

                // Clear lists before processing
                nameList.clear();
                typeList.clear();
                getParams(servlet);

                // Add parameters to the endpoint
                if (!nameList.isEmpty()) {
                    endpoint.put("dataName", nameList);
                }
                if (!typeList.isEmpty()) {
                    endpoint.put("dataType", typeList);
                }

                endpoints.put(endpoint);
            }
        }
        return endpoints;
    }

    private List<String> getHttpMethods(Class<?> servlet) {
        List<String> methods = new ArrayList<>();
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
            if (method.isAnnotationPresent(ApiAction.class)) {
                ApiAction apiAction = method.getAnnotation(ApiAction.class);
                methods.add(apiAction.actionName());
            }
        }
        return methods;
    }

    private void getParams(Class<?> servlet) {
        Method[] declaredMethods = servlet.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (method.isAnnotationPresent(ApiParamGroup.class)) {
                ApiParamGroup apiParamGroup = method.getAnnotation(ApiParamGroup.class);
                ApiParam[] entries = apiParamGroup.getApiParam(); // Use value() to get ApiParam array

                for (ApiParam entry : entries) {
                    System.out.println("Key: " + entry.name() + ", Value: " + entry.type());
                    nameList.add(entry.name());
                    typeList.add(entry.type());
                }
            } else if (method.isAnnotationPresent(ApiParam.class)) {
                ApiParam apiParam = method.getAnnotation(ApiParam.class);
                System.out.println("Key: " + apiParam.name() + ", Value: " + apiParam.type());
                nameList.add(apiParam.name());
                typeList.add(apiParam.type());
            }
        }
    }

    public static void main(String[] args) {
        ServletScanner scanner = new ServletScanner();
        System.out.println(scanner.scanServlets());
    }
}