
package com.saas.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.RequestMethod;
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
    
    @Value("${security.oauth2.client.logoutUri}")
    private String logoutUri;

    
    private static final String template = "Hello, %s!";

    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
    	
    	
    	KeycloakAuthenticationToken authToken = (KeycloakAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    	KeycloakPrincipal kcPrincipal =  (KeycloakPrincipal) authToken.getPrincipal();
    	
        KeycloakSecurityContext ctxt = kcPrincipal.getKeycloakSecurityContext();
        AccessToken token = (AccessToken) ctxt.getToken();
        
        String usersName = token.getName();
        
        return new Greeting(counter.incrementAndGet(),
                            String.format(template, usersName));
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

	@RequestMapping(value="/logout", method = RequestMethod.GET)
	public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null){    
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}
		try {
			response.sendRedirect(logoutUri);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

}
