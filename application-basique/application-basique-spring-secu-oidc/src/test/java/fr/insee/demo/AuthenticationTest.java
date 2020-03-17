package fr.insee.demo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.adapters.springboot.KeycloakBaseSpringBootConfiguration;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import fr.insee.demo.configurations.KeycloakConfigurationAdapter;
import fr.insee.demo.controller.Controlleur;

// Runner Spring si Junit 4 (lance un serveur pour les tests)
// @RunWith(SpringRunner.class)
//Runner Spring si Junit 5 (lance un serveur pour les tests)
@ExtendWith(SpringExtension.class)

// Prepare la conf MVC de la classe en paramètre
@WebMvcTest(Controlleur.class)

// Specifie tous les beans a charger
@ContextConfiguration(classes = { Controlleur.class, KeycloakConfigurationAdapter.class,
		KeycloakBaseSpringBootConfiguration.class, KeycloakSpringBootProperties.class })
public class AuthenticationTest {

	@Autowired
	private MockMvc mvc;

	@Test
	public void redirectionEnHttps() throws Exception {
		mvc.perform(get("/").secure(false)).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrlPattern("https://**")).andReturn();
	}

	@Test
	public void recupererPageAuthentifieeEnEtantAnonyme() throws Exception {
		mvc.perform(get("/private").secure(true)).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/sso/login")).andReturn();
		;
	}

	@Test
	@WithMockUser(username = "Alfred Robichu")
	public void recupererPageAuthentifieeEnEtantAuthentifie() throws Exception {
		mvc.perform(get("/private").secure(true)).andExpect(status().isOk())
				.andExpect(model().attribute("nom", "Alfred Robichu")).andReturn();
	}

	@Test
	@WithMockUser(username = "Alfred Robichu")
	public void recupererPageAdminEnEtantPasAdmin() throws Exception {
		mvc.perform(get("/admin").secure(true)).andExpect(status().isForbidden()).andReturn();
	}

	@Test
	@WithMockUser(username = "Alfred Robichu", roles = { "utilisateur_oidc" })
	public void recupererPageAdminEnEtantAdmin() throws Exception {
		mvc.perform(get("/admin").secure(true)).andExpect(status().isOk())
				.andExpect(model().attribute("nom", "Alfred Robichu")).andReturn();
	}

}
