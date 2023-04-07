package fr.insee.demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.matching.matcher.csrf.DefaultCsrfTokenGenerator;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.jee.context.JEEContext;
import org.pac4j.jee.context.session.JEESessionStore;
import org.pac4j.oidc.profile.OidcProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

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
		
		WebContext context = new JEEContext(request, response);
		ProfileManager manager =
				  new ProfileManager(context, JEESessionStore.INSTANCE);
		Optional<UserProfile> profile = manager.getProfile();
		OidcProfile oidcProfile = (OidcProfile) profile.get();
		
		Map h = profile.get().getAttributes();
		
		model.addAttribute("nom", h.get("name"));
		model.addAttribute("mail", h.get("email"));
		model.addAttribute("roles", oidcProfile.getRoles());
		model.addAttribute("urlAccount", urlAccount);
		
		var gen = new DefaultCsrfTokenGenerator();
		var csrfToken = gen.get(context, JEESessionStore.INSTANCE);
		model.addAttribute("_csrf_token_name", Pac4jConstants.CSRF_TOKEN);
		model.addAttribute("_csrf_token", csrfToken);
		
		/* Envoi d'une requête au ws */
		
		OkHttpClient client = new OkHttpClient();
		
		Request req = new Request.Builder().url(webServiceUrl).get()
				  .header("Authorization",
							 oidcProfile.getAccessToken().toAuthorizationHeader())
				  .build();
		
		Response rep = client.newCall(req).execute();
		model.addAttribute("reponseWS", rep.body().string());
		
		return "private";
	}
	
}
