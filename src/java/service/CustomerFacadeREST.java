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
import jakarta.ws.rs.core.Response;

@Stateless
@Path("customer")
public class CustomerFacadeREST extends AbstractFacade<Customer> {

    @PersistenceContext(unitName = "Homework1PU")
    private EntityManager em;

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
    
    
    

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
