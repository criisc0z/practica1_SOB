/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import authn.Secured;
import model.entities.Customer;
import model.entities.Prompt;
import model.entities.Tool;

@Stateless
@Path("prompt")
public class PromptFacadeREST extends AbstractFacade<Prompt> {

    @PersistenceContext(unitName = "Homework1PU")
    private EntityManager em;

    public PromptFacadeREST() {
        super(Prompt.class);
    }
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    //GET /rest/api/v1/prompt?author=${author}&tag=${tag}
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Object> findPrompts(@QueryParam("author") String author,
                                  @QueryParam("tag") String tag){
        
        String jpql = "SELECT p FROM Prompt p";
        if(tag != null && !tag.isEmpty()){
            jpql += " JOIN p.tags e";
        }
        jpql += " WHERE p.id > 0";
        if (author != null && !author.isEmpty()) {
            jpql += " AND p.author.username = :author";
        }
        if(tag != null && !tag.isEmpty()){
            jpql += " AND e = :tag";
        }
        jpql += " ORDER BY p.titol";
        TypedQuery<Prompt> query = em.createQuery(jpql, Prompt.class);
        if (author != null && !author.isEmpty()){ query.setParameter("author", author);}
        if (tag != null && !tag.isEmpty()){query.setParameter("tag", tag);}
    
        List<Object> result = new ArrayList<>();
        for (Prompt p : query.getResultList()) {
            if (p.isPrivat()) {
                result.add(resumen(p));      // privado: solo id, título, author y fecha
            } else {
                result.add(completo(p));     // público: todo
            }
        }
        return result;
    }
    
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findPrompt(@PathParam("id") Long id, @Context SecurityContext sc) {
        Prompt p = super.find(id);
        if (p == null) {
            return Response.status(Response.Status.NOT_FOUND).build();       // 404
        }
        // Si es privado, hay que haber hecho login
        if (p.isPrivat() && sc.getUserPrincipal() == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();    // 401
        }
        return Response.ok(completo(p)).build();
    }
        // 3. POST /rest/api/v1/prompt
    @POST
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPrompt(Prompt prompt) {

        // Título y plantilla obligatorios
        if (prompt == null || prompt.getTitol() == null || prompt.getTitol().isEmpty()
                || prompt.getTemplate() == null || prompt.getTemplate().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();     // 400
        }

        // El autor tiene que existir
        if (prompt.getAutor() == null || prompt.getAutor().getId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();     // 400
        }
        Customer author = em.find(Customer.class, prompt.getAutor().getId());
        if (author == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();     // 400
        }

        // Todas las herramientas indicadas tienen que existir
        List<Tool> tools = new ArrayList<>();
        if (prompt.getTools() != null) {
            for (Tool t : prompt.getTools()) {
                if (t == null || t.getId() == null) {
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
                Tool real = em.find(Tool.class, t.getId());
                if (real == null) {
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
                tools.add(real);
            }
        }

        // Guardamos
        prompt.setAutor(author);
        prompt.setTools(tools);
        if (prompt.getEtiquetes() == null) {
            prompt.setEtiquetes(new ArrayList<String>());
        }
        super.create(prompt);

        // 201 Created con el id nuevo
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("id", prompt.getId());
        return Response.status(Response.Status.CREATED).entity(body).build();
    }
    
     // Versión reducida (prompts privados)
    private Map<String, Object> resumen(Prompt p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", p.getId());
        m.put("titol", p.getTitol());
        m.put("author", p.getAuthor().getUsername());
        m.put("dataCreacio", p.getDataCreacio());
        return m;
    }

    // Versión completa
    private Map<String, Object> completo(Prompt p) {
        Map<String, Object> m = resumen(p);
        m.put("description", p.getDescription());
        m.put("template", p.getTemplate());
        m.put("tags", new ArrayList<>(p.getTags()));
        List<Long> toolIds = new ArrayList<>();
        for (Tool t : p.getTools()) {
            toolIds.add(t.getId());
        }
        m.put("tools", toolIds);
        m.put("privat", p.isPrivat());
        return m;
    }
    
}
