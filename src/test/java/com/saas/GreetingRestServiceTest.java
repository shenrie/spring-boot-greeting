package com.saas;
 
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.saas.security.AuthTokenInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootConfiguration
@ComponentScan(basePackages = { "com.saas" })
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.DEFINED_PORT)
public class GreetingRestServiceTest {
 
    protected int port = 8091;   //specified in test application.yaml
    
    @Value("${greeting.host}")
    private String greetingHost;
       
    private String authServerUri;
    private String passwordGrant;
      
    private String restServiceUri;
    

    @Autowired
    private Environment env;
    
    @Before
    public void classSetup() {
    	
    	authServerUri = env.getProperty("security.oauth2.client.accessTokenUri");
    	passwordGrant = String.format("grant_type=password&client_id=%s&username=%s&password=%s", 
    			env.getProperty("security.oauth2.client.clientId"),
    			env.getProperty("security.user.name"),
    			env.getProperty("security.user.password"));
    	
    	restServiceUri = greetingHost + ":" + port;
    }

    /*
     * Prepare HTTP Headers.
     */
    private HttpHeaders getHeaders(){
    	HttpHeaders headers = new HttpHeaders();
    	headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    	return headers;
    }
    
    private HttpHeaders getPostHeaders() {
    	HttpHeaders headers = getHeaders();
    	headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    	return headers;
    }
    
    private HttpHeaders getBearerHeaders(String token) {
    	HttpHeaders headers = getHeaders();
    	headers.add("Authorization", "Bearer " + token);
    	return headers;
    }
 
     
    
    /*
     * Send a POST request [on /oauth/token] to get an access-token, which will then be send with each request.
     */
    @SuppressWarnings({ "unchecked"})
	private AuthTokenInfo sendTokenRequest(){
        RestTemplate restTemplate = new RestTemplate(); 
        
        HttpEntity<String> request = new HttpEntity<String>(passwordGrant, getPostHeaders());
        ResponseEntity<Object> response = restTemplate.exchange(authServerUri, HttpMethod.POST, request, Object.class);
        LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>)response.getBody();
        AuthTokenInfo tokenInfo = null;
        
        if(map!=null){
        	tokenInfo = new AuthTokenInfo();
        	tokenInfo.setAccess_token((String)map.get("access_token"));
        	tokenInfo.setToken_type((String)map.get("token_type"));
        	tokenInfo.setRefresh_token((String)map.get("refresh_token"));
        	tokenInfo.setExpires_in((Integer)map.get("expires_in"));
        	tokenInfo.setScope((String)map.get("scope"));
        }else{
            System.out.println("No user exist----------");
            
        }
        return tokenInfo;
    }
    
    /*
     * Set Get without Auth
     */
    @Test(expected=HttpClientErrorException.class)
    public void testGreetingNoAuth(){
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request = new HttpEntity<String>(getHeaders());
        restTemplate.exchange(restServiceUri+"/greeting", HttpMethod.GET, request, Object.class);
        
    }
 
    /*
     * Send a GET Help request without auth
     */
    @Test
    public void testHelp(){
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request = new HttpEntity<String>(getHeaders());
        ResponseEntity<String> response = restTemplate.exchange(restServiceUri+"/greeting/help", HttpMethod.GET, request, String.class);
        
        String resp=response.getBody();
        assertTrue(resp.startsWith("Usage: http://127.0.0.1:8091"));
    }

     
    /*
     * Send a GET request with auth
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testDefaultGreeting(){
    	AuthTokenInfo tokenInfo = sendTokenRequest();
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request = new HttpEntity<String>(getBearerHeaders(tokenInfo.getAccess_token()));
        ResponseEntity<Object> response = restTemplate.exchange(restServiceUri+"/greeting", HttpMethod.GET, request, Object.class);
        LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>)response.getBody();

        assertNotNull(responseMap);
        assertTrue(responseMap.containsKey("id"));
        
        String content = (String)responseMap.get("content");
        assertEquals("Hello, World!",content);
        
    }

  
    /*
     * Send a GET request with auth
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testBobGreeting(){
    	AuthTokenInfo tokenInfo = sendTokenRequest();
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request = new HttpEntity<String>(getBearerHeaders(tokenInfo.getAccess_token()));
        ResponseEntity<Object> response = restTemplate.exchange(restServiceUri+"/greeting?name=Bob", HttpMethod.GET, request, Object.class);
        LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>)response.getBody();

        assertNotNull(responseMap);
        assertTrue(responseMap.containsKey("id"));
        
        String content = (String)responseMap.get("content");
        assertEquals("Hello, Bob!",content);
        
    }

    /*
     * Send a GET request with auth
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testProfile(){
    	AuthTokenInfo tokenInfo = sendTokenRequest();
    	
    	System.out.println("Token Auth: " + tokenInfo);
    	
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request = new HttpEntity<String>(getBearerHeaders(tokenInfo.getAccess_token()));
        ResponseEntity<Object> response = restTemplate.exchange(restServiceUri+"/greeting/profile", HttpMethod.GET, request, Object.class);
        LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>)response.getBody();

        assertNotNull(responseMap);
        boolean auth = (Boolean)responseMap.get("authenticated");
        assertTrue(auth);
        
		Map<String, Object> user = (Map<String, Object>)responseMap.get("userDetail");
        String uname = (String)user.get("preferred_username");
        assertEquals("test",uname);

        String name = (String)user.get("name");
        assertEquals("Test Test",name);
        
        String email = (String)user.get("email");
        assertEquals("test@demo.com",email);
    }

}