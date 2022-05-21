package fr.insee.demo.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class MySecurityConfigurationAdapter
		  extends WebSecurityConfigurerAdapter {
	
	// Conf générale ici :  "http://localhost:8180/auth/realms/test/.well-known/openid-configuration";
	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.sessionManagement()
				  .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.csrf().disable();
		http.httpBasic();
		http.authorizeRequests(
							 authz -> authz.antMatchers(HttpMethod.GET, "/private")
										.authenticated().antMatchers(HttpMethod.GET,
												  "/admin")
										.hasRole("admin").antMatchers(HttpMethod.OPTIONS)
										.permitAll().antMatchers("error", "/",
												  "/accueil")
										.permitAll().anyRequest().permitAll())
				  .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> {
					  jwtConfigurer.jwtAuthenticationConverter(
								 jwtAuthenticationConverter());
				  }));
	}
	
	// Customization pour récupérer les rôles Keycloak
	
	JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtAuthenticationConverter jwtAuthenticationConverter =
				  new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
				  jwtGrantedAuthoritiesConverter());
		jwtAuthenticationConverter.setPrincipalClaimName("preferred_username");
		return jwtAuthenticationConverter;
	}
	
	Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter() {
		return new Converter<Jwt, Collection<GrantedAuthority>>() {
			@Override
			@SuppressWarnings({"unchecked", "serial"})
			public Collection<GrantedAuthority> convert(Jwt source) {
				return ((Map<String, List<String>>) source.getClaim(
						  "realm_access")).get("roles").stream()
						  .map(s -> new GrantedAuthority() {
							  @Override
							  public String getAuthority() {
								  return "ROLE_" + s;
							  }
							  
							  @Override
							  public String toString() {
								  return getAuthority();
							  }
						  }).collect(Collectors.toList());
			}
		};
	}
	
	// User de test en basic pour voir un accès concurrent basic/bearer
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth)
			  throws Exception {
		auth.inMemoryAuthentication().withUser("user").password("{noop}password")
				  .roles("offline_access");
	}
	
}
