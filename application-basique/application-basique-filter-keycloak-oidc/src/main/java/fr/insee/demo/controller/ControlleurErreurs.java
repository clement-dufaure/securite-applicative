package fr.insee.demo.controller;

import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ControlleurErreurs implements ErrorController {

  private static final String PATH = "/error";

  @RequestMapping(PATH)
  public String error(HttpServletRequest request, Model model) {
    model.addAttribute("status", getStatus(request));
    return "error";
  }

  @Override
  public String getErrorPath() {
    return PATH;
  }

  protected HttpStatus getStatus(HttpServletRequest request) {
    Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
    if (statusCode == null) {
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }
    try {
      return HttpStatus.valueOf(statusCode);
    } catch (Exception ex) {
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }
  }

}
