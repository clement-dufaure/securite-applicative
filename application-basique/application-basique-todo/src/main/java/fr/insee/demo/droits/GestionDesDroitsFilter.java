package fr.insee.demo.droits;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GestionDesDroitsFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    // do nothing
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    // Attention: l'URI contient le chemin de contexte
    String uri = httpRequest.getRequestURI();
    String contextPath = httpRequest.getContextPath();

    if (uri.startsWith(contextPath + "/admin")) {
      // Verifier droits
      // Par exemple autoriser uniquement les utilisateur_oidc
      // httpResponse.sendError(403, "C'est interdit !");
    }

    chain.doFilter(httpRequest, httpResponse);

  }

  @Override
  public void destroy() {
    // do nothing
  }

}
