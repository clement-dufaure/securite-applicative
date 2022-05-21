package fr.insee.demo.controller;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Swagger {
	
	// http://localhost:8180/auth/realms/test/.well-known/openid-configuration
	
	public final String OAUTHSCHEME = "oAuth";
	public final String SCHEMEBASIC = "basic";
	private String authorization_endpoint = "http://localhost:8180/auth" +
			  "/realms/test/protocol/openid-connect/auth";
	private String token_endpoint = "http://localhost:8180/auth/realms/test" +
			  "/protocol/openid-connect/token";
	
	@Bean
	public OpenAPI customOpenAPIBasicAndOIDC() {
		final OpenAPI openapi = createOpenAPI();
		openapi.components(new Components().addSecuritySchemes(SCHEMEBASIC,
							 new SecurityScheme().type(SecurityScheme.Type.HTTP)
										.scheme(SCHEMEBASIC).in(SecurityScheme.In.HEADER)
										.description("Authentification Basic"))
				  .addSecuritySchemes(OAUTHSCHEME,
							 new SecurityScheme().type(SecurityScheme.Type.OAUTH2)
										.in(SecurityScheme.In.HEADER)
										.description("keycloak")
										.flows(new OAuthFlows().authorizationCode(
												  new OAuthFlow().authorizationUrl(
																		authorization_endpoint)
															 .tokenUrl(token_endpoint)
															 .refreshUrl(token_endpoint)))));
		return openapi;
	}
	
	private OpenAPI createOpenAPI() {
		final OpenAPI openapi = new OpenAPI().info(
				  new Info().title("Appli demo").description(""));
		return openapi;
	}
	
	@Bean
	public OperationCustomizer addAuth() {
		return (operation, handlerMethod) -> {
			return operation.addSecurityItem(
								 new SecurityRequirement().addList(SCHEMEBASIC))
					  .addSecurityItem(
								 new SecurityRequirement().addList(OAUTHSCHEME));
		};
	}
}
