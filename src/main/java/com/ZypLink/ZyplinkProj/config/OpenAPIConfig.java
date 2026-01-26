package com.ZypLink.ZyplinkProj.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "ZypLink API",
        version = "1.0",
        description = "URL Shortener Service APIs",
        contact = @Contact(
            name = "Samarth Sharma",
            email = "samarthsharma1211@gmail.com"
        )
    )
)
public class OpenAPIConfig {

   
}