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
    private Object Json;

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
            jpql += " AND t.author.username = :autor";
        }
        if (language != null && !language.isEmpty()) {
            jpql += " AND t.language = :language";
        }

        // 3. Orden por nombre
        jpql += " ORDER BY t.name";

        // 4. Creamos la consulta y le damos los valores de los filtros
        TypedQuery<Tool> query = em.createQuery(jpql, Tool.class);
        if (author != null && !author.isEmpty()) {
            query.setParameter("autor", author);
        }
        if (language != null && !language.isEmpty()) {
            query.setParameter("language", language);
        }

        // 5. Montamos la respuesta
        List<Object> result = new ArrayList<>();
        for (Tool t : query.getResultList()) {
            if (t.isPrivat()) {
                // Privada: solo id, nombre, autor y lenguaje
                Map<String, Object> resumen = new LinkedHashMap<>();
                resumen.put("id", t.getId());
                resumen.put("name", t.getName());
                resumen.put("autor", t.getAuthor().getUsername());
                resumen.put("language", t.getLanguage());
                result.add(resumen);
            } else {
                // Pública: información completa
                result.add(t);
            }
        }
        return result;
    }
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
}
