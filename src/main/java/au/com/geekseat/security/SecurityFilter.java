package au.com.geekseat.security;

import au.com.geekseat.helper.Utility;
import io.smallrye.jwt.auth.principal.DefaultJWTCallerPrincipal;
import org.jose4j.jwt.JwtClaims;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static javax.ws.rs.Priorities.AUTHENTICATION;
import static javax.ws.rs.core.Response.Status.*;

@Provider
@Priority(AUTHENTICATION)
public class SecurityFilter implements ContainerRequestFilter {
    @Context
    UriInfo uriInfo;

    public final static String AUTHORIZATION = "Authorization";

    @Override
    public void filter(ContainerRequestContext context) {

        if (!context.getUriInfo().getPath().equals("/auth/login")) {
            String authorizationHeader = context.getHeaderString(AUTHORIZATION);
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                try {
                    String token = authorizationHeader.substring("Bearer ".length());
                    String json = new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]), StandardCharsets.UTF_8);

                    DefaultJWTCallerPrincipal defaultJWTCallerPrincipal = new DefaultJWTCallerPrincipal(JwtClaims.parse(json));
                    Principal principal = Utility.gson.fromJson(defaultJWTCallerPrincipal.getClaim("principal").toString(), Principal.class);

                    SecurityContext securityContext = getSecurityContext(principal);
                    context.setSecurityContext(securityContext);
                } catch (Exception exception) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error_message", exception.getMessage());
                    context.abortWith(Response.status(FORBIDDEN).entity(error).build());
                }
            }
        }
    }

    private SecurityContext getSecurityContext(Principal principal) {
        return new SecurityContext() {
            @Override
            public java.security.Principal getUserPrincipal() {
                return principal;
            }

            @Override
            public boolean isUserInRole(String s) {
                Set<String> roles = principal.getRoles();
                return roles.contains(s);
            }

            @Override
            public boolean isSecure() {
                return uriInfo.getAbsolutePath().toString().startsWith("https");
            }

            @Override
            public String getAuthenticationScheme() {
                return "Token-Based-Auth-Scheme";
            }
        };
    }
}