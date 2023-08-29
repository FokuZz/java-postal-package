package ru.skyeng.javapostalpackage;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@OpenAPIDefinition
public class PostalPackageApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(PostalPackageApplication.class, args);
    }

}
