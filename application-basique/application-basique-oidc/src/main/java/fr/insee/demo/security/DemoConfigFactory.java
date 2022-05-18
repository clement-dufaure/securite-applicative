package fr.insee.demo.security;

import org.pac4j.core.authorization.authorizer.CsrfAuthorizer;
import org.pac4j.core.authorization.authorizer.IsAuthenticatedAuthorizer;
import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;
import org.pac4j.core.http.url.DefaultUrlResolver;
import org.pac4j.core.matching.matcher.PathMatcher;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;

public class DemoConfigFactory implements ConfigFactory {


    private String configurationEndpoint = "http://localhost:8180/auth/realms/test/.well-known/openid-configuration";
    public String clientId = "localhost-java";
    public String clientSecret = "0756132b-0e7a-425c-a0e3-7d5946fd372c";

    @Override
    public Config build(final Object... parameters) {

        OidcConfiguration oidcConfig = new OidcConfiguration();
        oidcConfig.setClientId(clientId);
        oidcConfig.setSecret(clientSecret);
        oidcConfig.setDiscoveryURI(configurationEndpoint);
        OidcClient oidcClient = new OidcClient(oidcConfig);

        // accept relative urls in callback definition
        // WARN : the explicit tomcat context is required
//        oidcClient.setCallbackUrl("demo/callback");
//        DefaultUrlResolver resolver = new DefaultUrlResolver();
//        resolver.setCompleteRelativeUrl(true);

        oidcClient.setCallbackUrl("/callback");
        RelativeUrlResolver resolver = new RelativeUrlResolver();
        resolver.setCompleteRelativeUrl(true);
        oidcClient.setUrlResolver(resolver);

        Config config = new Config(oidcClient);
        config.addAuthorizer("admin", new RequireAnyRoleAuthorizer("ROLE_ADMIN"));
        config.addAuthorizer("mustBeAuthent",new IsAuthenticatedAuthorizer());
        config.addAuthorizer("csrfToken",new CsrfAuthorizer());

        config.addMatcher("excludedPath", new PathMatcher().excludeRegex("^\\/(accueil)?$"));

        config.setLogoutLogic(new RelativeLogoutLogic());

        return config;
    }

}
