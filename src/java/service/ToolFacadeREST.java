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
import model.entities.Customer;
import authn.Secured;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Asus
 */
@Stateless
@Path("tool")
public class ToolFacadeREST extends AbstractFacade<Tool>{
    @PersistenceContext(unitName = "Homework1PU")
    private EntityManager em;

    public ToolFacadeREST() {
        super(Tool.class);
    }
    
    //1. GET /rest/api/v1/tool?author=${author}&language=${language}
    // Llista totes les eines disponibles, ordenades pel seu nom.
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Object> findTools(@QueryParam("author") String author,
                                  @QueryParam("language") String language) {

        // 1. Consulta base: todas las herramientas
        String jpql = "SELECT t FROM Tool t WHERE t.id > 0";

        // 2. Si nos pasan un filtro, añadimos la condición
        if (author != null && !author.isEmpty()) {
            jpql += " AND t.author.username = :author";
        }
        if (language != null && !language.isEmpty()) {
            jpql += " AND t.language = :language";
        }

        // 3. Orden por nombre
        jpql += " ORDER BY t.name";

        // 4. Creamos la consulta y le damos los valores de los filtros
        TypedQuery<Tool> query = em.createQuery(jpql, Tool.class);
        if (author != null && !author.isEmpty()) {
            query.setParameter("author", author);
        }
        if (language != null && !language.isEmpty()) {
            query.setParameter("language", language);
        }

        // 5. Montamos la respuesta
        List<Object> result = new ArrayList<>();
        for (Tool t : query.getResultList()) {
            if (t.isPrivat()) {
                // Privada: solo id, nombre, author y lenguaje
                Map<String, Object> resumen = new LinkedHashMap<>();
                resumen.put("id", t.getId());
                resumen.put("name", t.getName());
                resumen.put("author", t.getAuthor().getUsername());
                resumen.put("language", t.getLanguage());
                result.add(resumen);
            } else {
                // Pública: información completa
                result.add(t);
            }
        }
        return result;
    }
    
    //2.GET /rest/api/v1/tool/{id}
    //Retorna tota la informació corresponent a l'eina identificada per {id}.
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findTool(@PathParam("id") Long id) {
        Tool t=super.find(id);
        if (t==null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if(t.isPrivat()){
            Response r;
            r = findToolP(id);
            return r;
        }else{
            return Response.ok(t).build();
        }
    }
   
    public Response findToolP(@PathParam("id") Long id) {
        Tool t=super.find(id);
        if (t==null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(t).build();
    }
    
    
    //3.POST /rest/api/v1/tool
    @POST
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createTool(Tool t){
        if(t==null || t.getName() == null || t.getName().isEmpty()){
            return Response.status(Response.Status.PRECONDITION_FAILED).build();
        }
        if(t.getCode() == null || t.getCode().isEmpty()){
            return Response.status(Response.Status.PRECONDITION_FAILED).build();
        }
        if(t.getAuthor()==null || t.getAuthor().getId()==null){
            return Response.status(Response.Status.PRECONDITION_FAILED).build();
        }
        Customer author = em.find(Customer.class, t.getAuthor().getId());
        if (author == null){ return Response.status(Response.Status.PRECONDITION_FAILED).build(); }
        
        t.setId(null);
        t.setAuthor(author);
        if(t.getLanguage()==null || t.getLanguage().isEmpty()){
            t.setLanguage("python");
        }
        super.create(t);
        em.flush();
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("id", t.getId());
        return Response.status(Response.Status.CREATED).entity(body).build();
    }
    
    //4.PUT /rest/api/v1/tool/{id}
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("{id}")
    @Secured
    public Response updateTool(@PathParam("id") Long id, Tool newTool){
        Tool t = super.find(id);
        if (t==null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if(newTool==null || newTool.getName()==null || newTool.getName().isEmpty()|| newTool.getCode()==null || newTool.getCode().isEmpty()){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        t.setName(newTool.getName());
        t.setDescription(newTool.getDescription());
        t.setCode(newTool.getCode());
        t.setVersion(newTool.getVersion());
        t.setCategory(newTool.getCategory());
        t.setPrivat(newTool.isPrivat());
        if(newTool.getLanguage()!= null || !newTool.getLanguage().isEmpty()){
            t.setLanguage(newTool.getLanguage());
        }
        return Response.noContent().build();
    }
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
}
