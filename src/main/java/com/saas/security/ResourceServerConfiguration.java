package com.saas.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
	 
    private static final String RESOURCE_ID = "greeting";
     
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(RESOURCE_ID).stateless(false);
    }
 
    @Override
    public void configure(HttpSecurity http) throws Exception {
    	http
    	.authorizeRequests().antMatchers("/greeting/help").permitAll()
    	.anyRequest().authenticated()
    	//.antMatchers("/profile/**").access("hasRole('VIEW_PROFILE')")
    	.and().exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
    }
 
}