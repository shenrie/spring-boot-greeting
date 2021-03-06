package com.saas.security;


import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.keycloak.adapters.springsecurity.client.KeycloakClientRequestFactory;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackageClasses = KeycloakSecurityComponents.class)
public class ResourceSecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    /**
     * Registers the KeycloakAuthenticationProvider with the authentication manager.
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(keycloakAuthenticationProvider());
    }

    @Autowired
    public KeycloakClientRequestFactory keycloakClientRequestFactory;
       
    @Bean()
    @Scope("prototype")
    public KeycloakRestTemplate keycloakRestTemplate() {
    	return new KeycloakRestTemplate(keycloakClientRequestFactory); 
    }
        
    /**
     * Defines the session authentication strategy.
     */
    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        super.configure(http);
        
        http.csrf().disable();
        
        http
        	.authorizeRequests()
        	.antMatchers("/index.html", "/sso/login/**", "/logout").permitAll()
        	.antMatchers("/greeting/help").permitAll()
        	//Spring Security REQUIRES role to be name ROLE_PROFILE in Keycloak!!!!
        	.antMatchers("/greeting/profile*").hasRole("PROFILE")  
        	.anyRequest().authenticated()
        	.and().exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());

    }
    
    
}
