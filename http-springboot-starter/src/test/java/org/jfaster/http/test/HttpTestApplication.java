package org.jfaster.http.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.jfaster.http"})
public class HttpTestApplication {
    public static void main(String[] args) {
        new SpringApplication().run(HttpTestApplication.class);
    }
}
