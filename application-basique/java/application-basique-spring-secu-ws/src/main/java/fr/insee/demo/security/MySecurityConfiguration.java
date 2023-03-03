package fr.insee.demo.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class MySecurityConfiguration {

  public static String OIDC_CLAIM_ROLE = "realm_access.roles";

  @Bean
  protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // Cas d'un webservice :
    // - ne pas gerer les sessions
    // - pas de csrf
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    http.csrf().disable();
    // Descriptions des regles d'accès par path
    http.authorizeHttpRequests(
        requests ->
            requests
                // Ignorer le path du swagger
                .requestMatchers(HttpMethod.GET, "/swagger-ui/**", "/v3/api-docs/**")
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/", "/healthcheck")
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/private")
                .authenticated()
                .requestMatchers(HttpMethod.GET, "/admin")
                .hasRole("admin")
                .requestMatchers(HttpMethod.OPTIONS)
                .permitAll()
                .anyRequest()
                .denyAll());
    // Moyens d'authentification : Basic + oAuth2 bearer
    http.httpBasic();
    http.oauth2ResourceServer(
        oauth2 ->
            oauth2.jwt(
                jwtConfigurer -> {
                  jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter());
                }));
    return http.build();
  }

  // CONFIGURATION oAuth
  // -> Customization du nom
  // -> Customization pour récupérer les rôles Keycloak

  JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter());
    jwtAuthenticationConverter.setPrincipalClaimName("preferred_username");
    return jwtAuthenticationConverter;
  }

  @SuppressWarnings("unchecked")
  Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter() {
    return source -> {
      String[] claimPath = OIDC_CLAIM_ROLE.split("\\.");
      Map<String, Object> claims = source.getClaims();
      try {

        for (int i = 0; i < claimPath.length - 1; i++) {
          claims = (Map<String, Object>) claims.get(claimPath[i]);
        }

        List<String> roles =
            (List<String>) claims.getOrDefault(claimPath[claimPath.length - 1], new ArrayList<>());
        return roles.stream()
            .map(
                s ->
                    new GrantedAuthority() {
                      @Override
                      public String getAuthority() {
                        return "ROLE_" + s;
                      }

                      @Override
                      public String toString() {
                        return getAuthority();
                      }
                    })
            .collect(Collectors.toList());
      } catch (ClassCastException e) {
        // role path not correctly found, assume that no role for this user
        return new ArrayList<>();
      }
    };
  }

  // CONFIGURATION BASIC
  // Ou vérifier les credentials passés en basic ?
  // ici : users de test en mémoire

  @Bean
  public UserDetailsService userDetailsService() {
    UserDetails user = User.withUsername("user").password("{noop}password").roles("").build();
    UserDetails admin =
        User.withUsername("admin").password("{noop}password").roles("ADMIN").build();
    return new InMemoryUserDetailsManager(user, admin);
  }
}
