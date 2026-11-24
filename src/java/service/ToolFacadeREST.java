/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
import model.entities.Tool;
import authn.Secured;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.core.Response;

/**
 *
 * @author Asus
 */
@Stateless
@Path("tool")
public abstract class ToolFacadeREST extends AbstractFacade<Tool>{
    @PersistenceContext(unitName = "Homework1PU")
    private EntityManager em;
    private Object Json;

    public ToolFacadeREST() {
        super(Tool.class);
    }
    
    //1. GET /rest/api/v1/tool?author=${author}&language=${language}
    // Llista totes les eines disponibles, ordenades pel seu nom.
    @GET
    @Override
    @Produces(MediaType.APPLICATION_JSON)
    public List<Tool> findAll(){
        List<Tool> llista = (List<Tool>) super.findAll().stream().sorted();
        
    }
    
    
    
}
