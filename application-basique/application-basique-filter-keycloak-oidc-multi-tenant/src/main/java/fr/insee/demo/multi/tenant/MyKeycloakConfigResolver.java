package fr.insee.demo.multi.tenant;

import java.io.InputStream;
import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.spi.HttpFacade.Request;

public class MyKeycloakConfigResolver implements KeycloakConfigResolver {

  @Override
  public KeycloakDeployment resolve(Request facade) {
    if (facade.getCookie("realm") != null) {
      String realmDemande = facade.getCookie("realm").getValue();
      InputStream is;
      if (realmDemande.equals("agents-insee-interne")) {
        is = getClass().getClassLoader().getResourceAsStream("keycloak-agent.json");
      } else {
        is = getClass().getClassLoader().getResourceAsStream("keycloak.json");
      }
      return KeycloakDeploymentBuilder.build(is);
    } else {
      return null;
    }
  }

}
