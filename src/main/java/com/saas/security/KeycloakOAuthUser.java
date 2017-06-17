
package com.saas.security;

import java.io.Serializable;
import java.security.Principal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Component
public class KeycloakOAuthUser implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Collection<GrantedAuthority> authority;
	
	@JsonIgnore
	private String clientId;
	
	@JsonIgnore
	private String grantType;
	private boolean isAuthenticated;
	private Map<String, Object> userDetail = new LinkedHashMap<String, Object>();
	
	@JsonIgnore
	private String sessionId;
	
	@JsonIgnore
	private String tokenType;
	
	@JsonIgnore
	private String accessToken;
	
	@JsonIgnore
	private Principal principal;
	
	public void setPrincipal(Principal principal) {
	    this.principal = principal;
	    init();
	}
	
	public Principal getPrincipal() {
	    return principal;
	}

	@SuppressWarnings({ "unused", "unchecked" })
	private void init() {
	    if (principal != null) {
	    	KeycloakAuthenticationToken authToken = (KeycloakAuthenticationToken) principal;
	    	KeycloakPrincipal kcPrincipal =  (KeycloakPrincipal) authToken.getPrincipal();
	    	
	        KeycloakSecurityContext ctxt = kcPrincipal.getKeycloakSecurityContext();
	        AccessToken token = (AccessToken) ctxt.getToken();
	        
	        setAuthority(authToken.getAuthorities());
	        
            setClientId(token.getIssuedFor());
	        //setGrantType(oAuth2Authentication.getOAuth2Request().getGrantType());
	        setAuthenticated(authToken.isAuthenticated());
	
            setTokenType(token.getType());
            setAccessToken(ctxt.getTokenString());
            setSessionId(token.getClientSession());
	            
	            
            // This detail is more related to Logged-in User
            getUserDetail().put("family Name",  token.getFamilyName());
            getUserDetail().put("name",  token.getName());
            getUserDetail().put("email",  token.getEmail());
            getUserDetail().put("preferred_username",  token.getPreferredUsername());
            getUserDetail().put("subject_id",  token.getSubject());
 	
	    }
	}
	
	public Collection<GrantedAuthority> getAuthority() {
	    return authority;
	}
	
	public void setAuthority(Collection<GrantedAuthority> authority) {
	    this.authority = authority;
	}
	
	public String getClientId() {
	    return clientId;
	}
	
	public void setClientId(String clientId) {
	    this.clientId = clientId;
	}
	
	public String getGrantType() {
	    return grantType;
	}
	
	public void setGrantType(String grantType) {
	    this.grantType = grantType;
	}
	
	public boolean isAuthenticated() {
	    return isAuthenticated;
	}
	
	public void setAuthenticated(boolean isAuthenticated) {
	    this.isAuthenticated = isAuthenticated;
	}
	
	public Map<String, Object> getUserDetail() {
	    return userDetail;
	}
	
	public void setUserDetail(Map<String, Object> userDetail) {
	    this.userDetail = userDetail;
	}
	
	public String getSessionId() {
	    return sessionId;
	}
	
	public void setSessionId(String sessionId) {
	    this.sessionId = sessionId;
	}
	
	public String getTokenType() {
	    return tokenType;
	}
	
	public void setTokenType(String tokenType) {
	    this.tokenType = tokenType;
	}
	
	public String getAccessToken() {
	    return accessToken;
	}
	
	public void setAccessToken(String accessToken) {
	    this.accessToken = accessToken;
	}
	
	@Override
	public String toString() {
	    return "OAuthUser [clientId=" + clientId + ", grantType=" + grantType + ", isAuthenticated=" + isAuthenticated
	            + ", userDetail=" + userDetail + ", sessionId=" + sessionId + ", tokenType="
	            + tokenType + ", accessToken= " + accessToken + " ]";
	}

}


