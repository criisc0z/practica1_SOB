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
    

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
