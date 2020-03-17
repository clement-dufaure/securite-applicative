package fr.insee.demo.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Controlleur {

	Logger log = LoggerFactory.getLogger(Controlleur.class);

	@Value("${keycloak.auth-server-url}")
	private String authServerUrl;

	@Value("${keycloak.realm}")
	private String realm;

	@RequestMapping(value = { "/accueil", "/" })
	public String accueil() {
		return "accueil";
	}

	@RequestMapping(value = { "/private", "/admin" })
	public String espacePrivate(HttpServletRequest request, Model model) {

		model.addAttribute("secure", request.isSecure());

		String modeAuthentification = "";
		if (request.getUserPrincipal() == null) {
			modeAuthentification = "... pas d'authentification en fait";
		} else {
			modeAuthentification = "OpenIDConnect avec adapter Spring security Keycloak";
		}

		model.addAttribute("typeAuthentification", modeAuthentification);

		model.addAttribute("nom", request.getRemoteUser());

		KeycloakSecurityContext sc = (KeycloakSecurityContext) request
				.getAttribute(KeycloakSecurityContext.class.getName());
		if (sc != null) {
			AccessToken accessToken = sc.getToken();
			String accessTokenString = sc.getTokenString();
			model.addAttribute("mail", accessToken.getEmail());
			model.addAttribute("roles", accessToken.getRealmAccess().getRoles());
			model.addAttribute("urlAccount", authServerUrl + "/realms/" + realm + "/account"
					+ "?referrer=client-test-web&referrer_uri=https://localhost:8443/private");

			/* Envoi d'une requête au ws */

			HttpURLConnection connection = null;
			try { // Create connection
				URL url = new URL(getUserInfoUrl(request));
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Authorization", "Bearer " + accessTokenString);

				// Get Response
				InputStream is = connection.getInputStream();
				BufferedReader rd = new BufferedReader(new InputStreamReader(is));
				StringBuffer response = new StringBuffer();
				String line;
				while ((line = rd.readLine()) != null) {
					response.append(line);
					response.append('\r');
				}
				rd.close();
				model.addAttribute("reponseWS", response.toString());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
			}
		}

		return "private";
	}

	private String getUserInfoUrl(HttpServletRequest req) {
		return authServerUrl + "/realms/" + realm + "/protocol/openid-connect/userinfo";
	}

}
