package fr.insee.demo.security;

import org.pac4j.core.context.WebContextHelper;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.http.url.UrlResolver;
import org.pac4j.jee.context.JEEContext;

/**
 * Default URL resolver: use the provided URL as is or append the server and port for relative URLs.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */

/**
 * Add context path to the transition of relative to absolute url
 */
public class RelativeUrlResolver implements UrlResolver {

    private boolean completeRelativeUrl;

    public RelativeUrlResolver() {}

    public RelativeUrlResolver(final boolean completeRelativeUrl) {
        this.completeRelativeUrl = completeRelativeUrl;
    }

    @Override
    public String compute(final String url, WebContext context) {
        if (this.completeRelativeUrl) {

            final var relativeUrl = url != null
                    && !url.startsWith(HttpConstants.SCHEME_HTTP) && !url.startsWith(HttpConstants.SCHEME_HTTPS);

            if (context != null && relativeUrl) {
                final var sb = new StringBuilder();

                sb.append(context.getScheme()).append("://").append(context.getServerName());

                final var notDefaultHttpPort = WebContextHelper.isHttp(context) &&
                        context.getServerPort() != HttpConstants.DEFAULT_HTTP_PORT;
                final var notDefaultHttpsPort = WebContextHelper.isHttps(context) &&
                        context.getServerPort() != HttpConstants.DEFAULT_HTTPS_PORT;
                if (notDefaultHttpPort || notDefaultHttpsPort) {
                    sb.append(":").append(context.getServerPort());
                }

                // Add context path in url
                String contextPath = ((JEEContext) context).getNativeRequest().getContextPath();
                if(contextPath!=null && !contextPath.isEmpty()){
                    sb.append(contextPath);
                }

                sb.append(url.startsWith("/") ? url : "/" + url);

                return sb.toString();
            }

        }
        return url;
    }

    public boolean isCompleteRelativeUrl() {
        return completeRelativeUrl;
    }

    public void setCompleteRelativeUrl(final boolean completeRelativeUrl) {
        this.completeRelativeUrl = completeRelativeUrl;
    }
}
