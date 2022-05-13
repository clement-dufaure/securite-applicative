package fr.insee.demo.utils;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

public class AddParameterForLogoutFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    // do nothing
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
          throws IOException, ServletException {

    // Add url parameter for easy logout redirect uri
    HttpServletRequest wrappedRequest = new HttpServletRequestWrapper((HttpServletRequest) request){
      @Override
      public String getParameter(String name){
        if(name.equals("url")){
          return  RequestUtils.getAbsoluteUrl("/",(HttpServletRequest) request);
        }else{
          return super.getParameter(name);
        }
      }
    };

    chain.doFilter(wrappedRequest, response);

  }

  @Override
  public void destroy() {
    // do nothing
  }

}
