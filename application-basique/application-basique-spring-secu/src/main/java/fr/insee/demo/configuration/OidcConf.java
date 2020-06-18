package fr.insee.demo.configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class OidcConf extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // super.configure(http);
        // http.authorizeRequests(
        // exchanges -> exchanges.antMatchers("/private", "/admin" ).authenticated())
        // .oauth2Login();
        http.authorizeRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated()).oauth2Login();
    }
}