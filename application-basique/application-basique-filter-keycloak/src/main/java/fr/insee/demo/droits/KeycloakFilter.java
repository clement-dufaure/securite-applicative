package fr.insee.demo.droits;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.keycloak.adapters.servlet.KeycloakOIDCFilter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class KeycloakFilter implements Filter {

    KeycloakOIDCFilter keycloakFilter = new KeycloakOIDCFilter();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        keycloakFilter.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        keycloakFilter.doFilter(request, response, chain);
    }

}