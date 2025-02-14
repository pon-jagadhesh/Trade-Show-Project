package com.jagger.utils;

import java.lang.annotation.*;
import java.lang.reflect.Method;

@Retention(RetentionPolicy.RUNTIME)  // Available at runtime for processing
@Target(ElementType.METHOD)  // Can be applied to servlet methods
@Repeatable(ResponseParams.class) // Allows multiple @ResponseParam annotations
public @interface ResponseParam {
    
    String responseCode() default "200";  // HTTP Status Code (e.g., "200", "404")

    String description() default "";  // Short description of the response

    String mediaType() default "application/json";  // Default media type

    Class<?> schema() default Void.class;  // Optional response schema

    boolean deprecated() default false;  // If this response is deprecated

    String[] headers() default {};  // Possible headers returned (e.g., "Content-Type: application/json")

    String[] examples() default {};  // Example JSON response

}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
 @interface ResponseParams {
    ResponseParam[] value();
}



class AnnotationGenrator{
	
	
	public void getResponseParam(Method method) {
		 StringBuilder output = new StringBuilder();

	        try {
	                if (method.isAnnotationPresent(ResponseParams.class)) {
	                    ResponseParams responseParams = method.getAnnotation(ResponseParams.class);

	                    output.append("Method: ").append(method.getName()).append("\n");
	                    for (ResponseParam annotation : responseParams.value()) {
	                        output.append("Response Code: ").append(annotation.responseCode()).append("\n")
	                              .append("Description: ").append(annotation.description()).append("\n")
	                              .append("Media Type: ").append(annotation.mediaType()).append("\n")
	                              .append("Schema: ").append(annotation.schema().getSimpleName()).append("\n")
	                              .append("Deprecated: ").append(annotation.deprecated()).append("\n");

	                        if (annotation.headers().length > 0) {
	                            output.append("Headers: ").append(String.join(", ", annotation.headers())).append("\n");
	                        }

	                        if (annotation.examples().length > 0) {
	                            output.append("Examples: ").append(String.join(", ", annotation.examples())).append("\n");
	                        }

	                        output.append("------------------------------\n");
	                    }
	                }
	            
	        } catch (Exception e) {
	            output.append("Error extracting annotations: ").append(e.getMessage());
	        }
	}
}