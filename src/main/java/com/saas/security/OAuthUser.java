
package com.saas.security;

import java.io.Serializable;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Component
public class OAuthUser implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String authority;
	
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
	
	public void setOAuthUser(Principal principal) {
	    this.principal = principal;
	    init();
	}
	
	public Principal getPrincipal() {
	    return principal;
	}

	private void init() {
	    if (principal != null) {
	        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) principal;
	        if (oAuth2Authentication != null) {
	            for (GrantedAuthority ga : oAuth2Authentication.getAuthorities()) {
	                setAuthority(ga.getAuthority());
	            }
	            setClientId(oAuth2Authentication.getOAuth2Request().getClientId());
	            setGrantType(oAuth2Authentication.getOAuth2Request().getGrantType());
	            setAuthenticated(oAuth2Authentication.getUserAuthentication().isAuthenticated());
	
	            OAuth2AuthenticationDetails oAuth2AuthenticationDetails = (OAuth2AuthenticationDetails) oAuth2Authentication
	                    .getDetails();
	            if (oAuth2AuthenticationDetails != null) {
	                setSessionId(oAuth2AuthenticationDetails.getSessionId());
	                setTokenType(oAuth2AuthenticationDetails.getTokenType());
	
	            // This is what you will be looking for 
	                setAccessToken(oAuth2AuthenticationDetails.getTokenValue());
	            }
	
	    // This detail is more related to Logged-in User
	            UsernamePasswordAuthenticationToken userAuthenticationToken = (UsernamePasswordAuthenticationToken) oAuth2Authentication.getUserAuthentication();
	            if (userAuthenticationToken != null) {
	                LinkedHashMap<String, Object> detailMap = (LinkedHashMap<String, Object>) userAuthenticationToken.getDetails();
	                if (detailMap != null) {
	                    for (Map.Entry<String, Object> mapEntry : detailMap.entrySet()) {
	                        //System.out.println("#### detail Key = " + mapEntry.getKey());
	                        //System.out.println("#### detail Value = " + mapEntry.getValue());
	                        getUserDetail().put(mapEntry.getKey(), mapEntry.getValue());
	                    }
	
	                }
	
	            }
	
	        }
	
	    }
	}
	
	public String getAuthority() {
	    return authority;
	}
	
	public void setAuthority(String authority) {
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


