
package com.saas.controller;

import java.security.Principal;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import com.saas.controller.Greeting;
import com.saas.security.OAuthUser;



@RestController
public class ApiRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiRestController.class);

    @Value("${oauth2-demo.greetings}")
    private String greetings;
    
    //@Autowired
    OAuthUser oAuthUser;
    
    private static final String template = "Hello, %s!";

    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
    	
       // System.out.println(String.format("Auth: %s %s", oAuthUser.getTokenType(), oAuthUser.getAccessToken()));

        
        return new Greeting(counter.incrementAndGet(),
                            String.format(template, name));
    }
    
 // In case if you want to see Profile of user then you this 
    @RequestMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public OAuthUser user(Principal principal) {
        oAuthUser.setOAuthUser(principal);

        // System.out.println("#### Inside user() - oAuthUser.toString() = " + oAuthUser.toString());

        return oAuthUser;
    }


}
