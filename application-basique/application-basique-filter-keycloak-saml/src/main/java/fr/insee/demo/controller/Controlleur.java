package fr.insee.demo.controller;

import javax.servlet.http.HttpServletRequest;

import org.keycloak.adapters.saml.SamlPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Controlleur {

	Logger log = LoggerFactory.getLogger(Controlleur.class);

	@RequestMapping(value = { "/accueil", "/" })
	public String accueil() {
		return "accueil";
	}

	@RequestMapping(value = { "/private", "/privatetest" })
	public String espacePrivate(HttpServletRequest request, Model model) {

		model.addAttribute("secure", request.isSecure());

		String modeAuthentification = "";
		if (request.getUserPrincipal() == null) {
			modeAuthentification = "... pas d'authentification en fait";
		} else {
			modeAuthentification = "Saml avec filter Keycloak";
		}

		model.addAttribute("typeAuthentification", modeAuthentification);

		SamlPrincipal token = (SamlPrincipal) request.getUserPrincipal();

		model.addAttribute("uid", token.getFriendlyAttribute("uid"));
		model.addAttribute("idminefi", token.getFriendlyAttribute("inseeIdentifiantMinisteriel"));
		//model.addAttribute("roles", token.getAttributes("Roles"));

		return "private";
	}

}
