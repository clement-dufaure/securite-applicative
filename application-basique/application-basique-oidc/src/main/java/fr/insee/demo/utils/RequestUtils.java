package fr.insee.demo.utils;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.WebContextHelper;

import javax.servlet.http.HttpServletRequest;

public class RequestUtils {

    private static final String SCHEME_HTTP = "http";
    private static final String SCHEME_HTTPS = "https";
    private static int DEFAULT_HTTP_PORT = 80;
    private static int DEFAULT_HTTPS_PORT = 443;


    public static String getAbsoluteUrl(final String relativeUrl, HttpServletRequest originalRequest) {

        if( originalRequest==null  || relativeUrl == null
                || relativeUrl.startsWith(HttpConstants.SCHEME_HTTP) && !relativeUrl.startsWith(HttpConstants.SCHEME_HTTPS)) {
            // not a relative url
            return relativeUrl;
        }

        final var sb = new StringBuilder();
        sb.append(originalRequest.getScheme()).append("://").append(originalRequest.getServerName());

        final var notDefaultHttpPort = isHttp(originalRequest) &&
                originalRequest.getServerPort() != DEFAULT_HTTP_PORT;
        final var notDefaultHttpsPort =isHttps(originalRequest) &&
                originalRequest.getServerPort() != DEFAULT_HTTPS_PORT;
        if (notDefaultHttpPort || notDefaultHttpsPort) {
            sb.append(":").append(originalRequest.getServerPort());
        }

        String contextPath = originalRequest.getContextPath();
        if(contextPath!=null && !contextPath.isEmpty()){
            sb.append(contextPath);
        }

        sb.append(relativeUrl.startsWith("/") ? relativeUrl : "/" + relativeUrl);

        return sb.toString();
    }

    private static boolean isHttp(HttpServletRequest request){
        return SCHEME_HTTP.equalsIgnoreCase(request.getScheme());
    }

    private static boolean isHttps(HttpServletRequest request){
        return SCHEME_HTTPS.equalsIgnoreCase(request.getScheme());
    }


}
