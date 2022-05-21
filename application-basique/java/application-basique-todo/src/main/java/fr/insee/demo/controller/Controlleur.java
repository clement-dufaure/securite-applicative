package fr.insee.demo.controller;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class Controlleur {
	
	Logger log = LoggerFactory.getLogger(Controlleur.class);
	
	private String webServiceUrl =
			  "http://localhost:8180/auth/realms/test/protocol" +
						 "/openid-connect/userinfo";
	
	private String urlAccount =
			  "http://localhost:8180/auth/realms/formation/account?referrer" +
						 "=client-test-web&referrer_uri=https://localhost:8443" +
						 "/private";
	
	@RequestMapping(value = {"/accueil", "/"})
	public String accueil(Model model) {
		return "accueil";
	}
	
	@RequestMapping(value = {"/private", "/admin"})
	public String espacePrivate(HttpServletRequest request,
										 HttpServletResponse response, Model model)
			  throws IOException {
		
		
		model.addAttribute("secure", request.isSecure());
		
		String modeAuthentification = "";
		if (request.getUserPrincipal() == null) {
			modeAuthentification =
					  "Authentifié par ... pas d'authentification en fait";
		} else {
			modeAuthentification = "Vous êtes bien authentifié";
		}
		
		model.addAttribute("typeAuthentification", modeAuthentification);
		
		model.addAttribute("nom", "anonyme");
		model.addAttribute("mail", "");
		model.addAttribute("roles", "");
		model.addAttribute("urlAccount", urlAccount);
		
		/* Envoi d'une requête au ws */
		
		OkHttpClient client = new OkHttpClient();
		
		Request req = new Request.Builder().url(webServiceUrl).get()
				  .build();
		
		Response rep = client.newCall(req).execute();
		model.addAttribute("reponseWS", rep.body().string());
		
		return "private";
	}
	
}
