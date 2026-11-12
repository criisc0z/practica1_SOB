package authn;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Base64;
import java.util.List;
import jakarta.annotation.Priority;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import model.entities.Customer;

@Priority(Priorities.AUTHENTICATION)
@Provider
public class RESTRequestFilter implements ContainerRequestFilter {

    private static final String PREFIX = "Basic ";

    @Context
    private ResourceInfo resourceInfo;

    @PersistenceContext(unitName = "Homework1PU")
    private EntityManager em;

    @Override
    public void filter(ContainerRequestContext ctx) throws IOException {
        Customer user = authenticate(ctx.getHeaderString(HttpHeaders.AUTHORIZATION));

        // Si las credenciales son válidas, guardamos el usuario en el SecurityContext
        if (user != null) {
            final String username = user.getUsername();
            final boolean secure = ctx.getSecurityContext().isSecure();
            ctx.setSecurityContext(new SecurityContext() {
                @Override
                public Principal getUserPrincipal() {
                    return () -> username;
                }
                @Override
                public boolean isUserInRole(String role) {
                    return false;
                }
                @Override
                public boolean isSecure() {
                    return secure;
                }
                @Override
                public String getAuthenticationScheme() {
                    return "Basic";
                }
            });
        }

        // Solo se exige autenticación en los métodos anotados con @Secured
        Method method = resourceInfo.getResourceMethod();
        boolean secured = method != null && method.isAnnotationPresent(Secured.class);

        if (secured && user == null) {
            ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .header(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"SOB\"")
                    .build());
        }
    }

    /** Devuelve el Customer si las credenciales son correctas; null en cualquier otro caso. */
    private Customer authenticate(String header) {
        if (header == null || !header.startsWith(PREFIX)) {
            return null;
        }
        try {
            String decoded = new String(
                    Base64.getDecoder().decode(header.substring(PREFIX.length()).trim()),
                    StandardCharsets.UTF_8);
            int idx = decoded.indexOf(':');
            if (idx < 0) {
                return null;
            }
            String username = decoded.substring(0, idx);
            String password = decoded.substring(idx + 1);

            List<Customer> result = em.createNamedQuery("Customer.findByUsername", Customer.class)
                    .setParameter("username", username)
                    .getResultList();
            if (result.isEmpty()) {
                return null;
            }
            Customer c = result.get(0);
            return c.getPassword().equals(password) ? c : null;
        } catch (IllegalArgumentException e) {   // Base64 mal formado
            return null;
        }
    }
}