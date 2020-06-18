package fr.insee.demo.configurations;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@KeycloakConfiguration
public class KeycloakConfigurationAdapter extends KeycloakWebSecurityConfigurerAdapter {

	/**
	 * Defines the session authentication strategy.
	 */
	@Bean
	@Override
	protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
		return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
	}

	/**
	 * Registers the KeycloakAuthenticationProvider with the authentication manager.
	 */
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		// Transfert dans role uniquement des roles sous la forme ROLE_*
		// "hasRole" ne connaîtra que ces ROLE_*, "hasAuthority" connaîtra tous les
		// roles
		// auth.authenticationProvider(keycloakAuthenticationProvider());

		// Pour transformer tous les roles existant en ROLE_roleexistant (sauf s'ils
		// sont déjà sous la
		// forme ROLE_*)
		// Ces role seront correctement résolus par isUserInRole
		KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
		keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
		auth.authenticationProvider(keycloakAuthenticationProvider);
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
		http
				// delegate logout endpoint to spring security
				.logout().addLogoutHandler(keycloakLogoutHandler()).logoutUrl("/logout").logoutSuccessUrl("/").and()
				// regles de securites
				// tout en https
				//.requiresChannel().anyRequest().requiresSecure().and()
				// espace public
				.authorizeRequests().antMatchers(HttpMethod.OPTIONS).permitAll()
				.antMatchers("error", "/logout", "/", "/accueil", "/css/**").permitAll()
				// espace privé
				.antMatchers("/private").authenticated().antMatchers("/admin").hasRole("utilisateur_oidc")

				// dans le doute le reste est interdit
				.anyRequest().denyAll();
	}
}
