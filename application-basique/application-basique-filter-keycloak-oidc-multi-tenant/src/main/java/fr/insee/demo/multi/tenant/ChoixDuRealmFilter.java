package fr.insee.demo.multi.tenant;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ChoixDuRealmFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    // do nothing
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest myRequest = (HttpServletRequest) request;

    if (myRequest.getRequestURI().startsWith("/error")
        || myRequest.getRequestURI().startsWith("/accueil")) {
      chain.doFilter(request, response);
      return;
    }

    Cookie cookie = null;
    for (Cookie c : myRequest.getCookies()) {
      if (c.getName().equals("realm")) {
        cookie = c;
        break;
      }
    }

    if (cookie == null && !myRequest.getRequestURI().startsWith("/choixRealm")) {
      HttpServletResponse httpResponse = (HttpServletResponse) response;
      httpResponse.sendRedirect("/choixRealm");
      return;
    }

    if (myRequest.getRequestURI().startsWith("/choixRealm")
        && myRequest.getParameterMap().containsKey("realm")) {
      HttpServletResponse httpResponse = (HttpServletResponse) response;
      Cookie cook = new Cookie("realm", myRequest.getParameter("realm"));
      httpResponse.addCookie(cook);
      httpResponse.sendRedirect("/accueil");
      return;
    }
    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {
    // do nothing
  }

}
