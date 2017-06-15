
package com.saas.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@ComponentScan("com.saas")
public class AppMain {
  
    private static final Logger LOGGER = LoggerFactory.getLogger(AppMain.class);

	public static ApplicationContext applicationContext;
        
	public static void main(String [] args) throws Exception {
	  
		applicationContext = SpringApplication.run(AppMain.class, args);
       
		LOGGER.info("Tomcat is running and listening on the configured port(s)");
		
	}
  
}	
