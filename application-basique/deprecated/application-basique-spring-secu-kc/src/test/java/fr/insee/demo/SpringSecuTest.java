package fr.insee.demo;

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import fr.insee.demo.configuration.OidcConf;
import fr.insee.demo.controller.Controlleur;

@ExtendWith(SpringExtension.class)
@WebMvcTest(Controlleur.class)
@ContextConfiguration(classes = { Controlleur.class, OidcConf.class, KeycloakBaseSpringBootConfiguration.class,
        KeycloakSpringBootProperties.class })
public class SpringSecuTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @WithMockUser(username = "Alfred Robichu", roles = { "admin" })
    public void recupererPageAdminEnEtantAdmin() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/admin").secure(true)).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "Alfred Robichu", roles = { "pas-admin" })
    public void recupererPageAdminEnEtantPasAdmin() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/admin").secure(true))
                .andExpect(MockMvcResultMatchers.status().isForbidden()).andReturn();
    }

}
