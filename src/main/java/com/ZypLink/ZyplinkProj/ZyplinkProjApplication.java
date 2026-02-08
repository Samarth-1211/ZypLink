package com.ZypLink.ZyplinkProj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ZyplinkProjApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZyplinkProjApplication.class, args);
	}

}
