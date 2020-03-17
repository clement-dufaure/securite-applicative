package fr.insee.demo.ws.configurations;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@KeycloakConfiguration
public class KeycloakConfigurationAdapter extends KeycloakWebSecurityConfigurerAdapter {

  /**
   * Defines the session authentication strategy.
   */
  @Bean
  @Override
  protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
    // required for bearer-only applications.
    return new NullAuthenticatedSessionStrategy();
  }

  /**
   * Registers the KeycloakAuthenticationProvider with the authentication manager.
   */
  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    // Il faudra utiliser "hasAuthority" et non pas "hasRole"
    auth.authenticationProvider(keycloakAuthenticationProvider());
    // //Si on tient à tout prix à utiliser "hasRole"
    // KeycloakAuthenticationProvider keycloakAuthenticationProvider =
    // keycloakAuthenticationProvider();
    // keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
    // auth.authenticationProvider(keycloakAuthenticationProvider);
  }

  /**
   * Required to handle spring boot configurations
   * 
   * @return
   */
  @Bean
  public KeycloakConfigResolver KeycloakConfigResolver() {
    return new KeycloakSpringBootConfigResolver();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // Configuration générales
    super.configure(http);
    // Configurations personnalisées
    http // use previously declared bean
    .sessionManagement().sessionAuthenticationStrategy(sessionAuthenticationStrategy())
    .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
        // regles de securites
        // tout en https
        .requiresChannel().anyRequest().requiresSecure().and()
        // espace public
        .authorizeRequests().antMatchers(HttpMethod.OPTIONS).permitAll()
        .antMatchers("error", "/", "/accueil").permitAll()
        // espace privé
        .antMatchers("/ressource")
        .hasAuthority("Utilisateurs_FormationFede")

        // dans le doute le reste est interdit
        .anyRequest().denyAll();
  }

}
