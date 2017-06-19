
package com.saas.controller;

import java.security.Principal;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import com.saas.controller.Greeting;
import com.saas.security.KeycloakOAuthUser;


@RestController
public class ApiRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiRestController.class);

    @Autowired
    KeycloakOAuthUser oAuthUser;
    
    private static final String template = "Hello, %s!";

    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
    	
        return new Greeting(counter.incrementAndGet(),
                            String.format(template, name));
    }
    
    @RequestMapping("/greeting/help")
    public String help(HttpSession httpSession, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
   
    	return String.format("Usage: http://%s:%d/greeting[/profile]?<name=value>\n", httpRequest.getServerName(),  httpRequest.getServerPort());
    }
    
    //In case if you want to see Profile of user then you this 
    @RequestMapping(value = "/greeting/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public KeycloakOAuthUser user(Principal principal) {
        oAuthUser.setPrincipal(principal);

        return oAuthUser;
    }


}
