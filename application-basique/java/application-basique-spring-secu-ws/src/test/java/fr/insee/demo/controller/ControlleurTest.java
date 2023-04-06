package fr.insee.demo.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//Runner Spring si Junit 5 (lance un serveur pour les tests)
@ExtendWith(SpringExtension.class)
// Prepare la conf MVC de la classe en paramètre
@WebMvcTest(Controlleur.class)
public class ControlleurTest {

    @Autowired
    MockMvc mvc;

    @Test
    public void getRessourceAsAnonymousTest() throws Exception {
        mvc.perform(get("/private").secure(true)).andExpect(status().isUnauthorized());
    }


    @Test
    @WithMockUser(username = "Alphonse Robichu", roles = {"user"})
    public void getRessourceAsNonAdminTest() throws Exception {
        mvc.perform(get("/private").secure(true)).andExpect(status().isOk()).andExpect(content().string(containsString("Vous êtes admin : false")))
                .andReturn();
    }

    @Test
    @WithMockUser(username = "Alphonse Robichu", roles = {"admin"})
    public void getRessourceAsAdminTest() throws Exception {
        mvc.perform(get("/private").secure(true)).andExpect(status().isOk()).andExpect(content().string(containsString("Vous êtes admin : true")))
                .andReturn();
    }

}
