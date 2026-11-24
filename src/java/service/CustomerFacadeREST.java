package service;

import java.util.List;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import model.entities.Customer;
import authn.Secured;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.core.Response;


@Stateless
@Path("customer")
public class CustomerFacadeREST extends AbstractFacade<Customer> {

    @PersistenceContext(unitName = "Homework1PU")
    private EntityManager em;
    private Object Json;

    public CustomerFacadeREST() {
        super(Customer.class);
    }
    
    //1: GET /rest/api/v1/customer
    @GET
    @Override
    @Produces(MediaType.APPLICATION_JSON)
    public List<Customer> findAll(){
        return super.findAll();
    }

    //2: GET /rest/api/v1/customer/{id}
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findCustomer(@PathParam("id") Long id) {
        Customer c = super.find(id);
        if (c == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        StringBuilder links = new StringBuilder();
        Long lastPrompt = lastPromptId(c);
        if (lastPrompt != null) {
            links.append("\"last-prompt-added\":{\"href\":\"/prompt/").append(lastPrompt).append("\"}");
        }
        Long lastTool = lastToolId(c);
        if (lastTool != null) {
            if (links.length() > 0) links.append(",");
            links.append("\"last-tool-added\":{\"href\":\"/tool/").append(lastTool).append("\"}");
        }

        String json = "{\"id\":" + c.getId()
                + ",\"username\":\"" + c.getUsername().replace("\\", "\\\\").replace("\"", "\\\"") + "\""
                + ",\"_links\":{" + links + "}}";
        return Response.ok(json).build();
    }

    private Long lastPromptId(Customer c) {
        List<Long> r = em.createQuery(
                "SELECT p.id FROM Prompt p WHERE p.autor = :a ORDER BY p.id DESC", Long.class)
                .setParameter("a", c).setMaxResults(1).getResultList();
        return r.isEmpty() ? null : r.get(0);
    }

    private Long lastToolId(Customer c) {
        List<Long> r = em.createQuery(
                "SELECT t.id FROM Tool t WHERE t.autor = :a ORDER BY t.id DESC", Long.class)
                .setParameter("a", c).setMaxResults(1).getResultList();
        return r.isEmpty() ? null : r.get(0);
    }
    
    @PUT
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response edit(@PathParam("id") Long id, Customer entity) {
        Customer c = super.find(id);
        if (c == null) {
            return Response.status(Response.Status.NOT_FOUND).build();          // 404
        }
        if (entity == null || entity.getUsername() == null
                || entity.getUsername().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();        // 400
        }
        // username es unique: evitamos el error de BD y devolvemos 409
        List<Customer> same = em.createNamedQuery("Customer.findByUsername", Customer.class)
                .setParameter("username", entity.getUsername())
                .getResultList();
        if (!same.isEmpty() && !same.get(0).getId().equals(id)) {
            return Response.status(Response.Status.CONFLICT).build();           // 409
        }
        c.setUsername(entity.getUsername());   // entidad gestionada: se guarda al acabar la transacción
        return Response.noContent().build();                                    // 204
    }
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
