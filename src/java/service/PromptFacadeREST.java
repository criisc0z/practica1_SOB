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
                                  @QueryParam("tag") List<String> tag){
        
        String jpql = "SELECT p FROM Prompt p";
        if(tag != null && !tag.isEmpty()){
            jpql += " JOIN p.etiquetes e";
        }
        jpql += " WHERE p.id > 0";
        if (author != null && !author.isEmpty()) {
            jpql += " AND p.autor.username = :author";
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
                result.add(resumen(p));      // privado: solo id, título, autor y fecha
            } else {
                result.add(completo(p));     // público: todo
            }
        }
        return result;
    }
    
     // Versión reducida (prompts privados)
    private Map<String, Object> resumen(Prompt p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", p.getId());
        m.put("titol", p.getTitol());
        m.put("autor", p.getAutor().getUsername());
        m.put("dataCreacio", p.getDataCreacio());
        return m;
    }

    // Versión completa
    private Map<String, Object> completo(Prompt p) {
        Map<String, Object> m = resumen(p);
        m.put("description", p.getDescription());
        m.put("template", p.getTemplate());
        m.put("etiquetes", new ArrayList<>(p.getEtiquetes()));
        List<Long> toolIds = new ArrayList<>();
        for (Tool t : p.getTools()) {
            toolIds.add(t.getId());
        }
        m.put("tools", toolIds);
        m.put("privat", p.isPrivat());
        return m;
    }
    
}
