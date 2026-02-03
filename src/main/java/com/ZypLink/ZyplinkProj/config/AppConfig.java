package com.ZypLink.ZyplinkProj.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// import org.springframework.boot.web.client.RestTemplateBuilder;


@Configuration
public class AppConfig {

    @Bean 
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    // @Bean
    // public RestTemplateBuilder restTemplateBuilder(){
    //     return new RestTemplateBuilder();
    // }

}
