package fr.insee.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
public class Controlleur {
	
	Logger log = LoggerFactory.getLogger(Controlleur.class);
	
	private String webServiceUrl =
			  "http://localhost:8180/auth/realms/test/protocol" +
						 "/openid-connect/userinfo";
	
	@Operation
	@RequestMapping(value = {"/accueil", "/"}, method = RequestMethod.GET)
	public void accueil(HttpServletResponse response) throws IOException {
		PrintWriter responseWriter = response.getWriter();
		responseWriter.write("accueil");
	}
	
	@Operation
	@RequestMapping(value = {"/private", "/admin"}, method = RequestMethod.GET)
	public void espacePrivate(HttpServletRequest request,
									  HttpServletResponse response) throws IOException {
		PrintWriter responseWriter = response.getWriter();
		
		responseWriter.write("secure ?" + request.isSecure() + "\n");
		
		String modeAuthentification = "";
		if (request.getUserPrincipal() == null) {
			modeAuthentification =
					  "Authentifié par ... pas d'authentification en fait";
		} else {
			modeAuthentification = "Vous êtes bien authentifié";
		}
		
		responseWriter.write(modeAuthentification + "\n");
		
		JwtAuthenticationToken token =
				  (JwtAuthenticationToken) request.getUserPrincipal();
		
		responseWriter.write(
				  token.getTokenAttributes().get("preferred_username") + "\n");
		responseWriter.write(token.getAuthorities() + "\n");
		
		/* Envoi d'une requête au ws */
		
		OkHttpClient client = new OkHttpClient();
		
		Request req = new Request.Builder().url(webServiceUrl)
				  .header("Authorization",
							 "Bearer " + token.getToken().getTokenValue()).get()
				  .build();
		
		Response rep = client.newCall(req).execute();
		responseWriter.write(rep.body().string());
	}
	
}
