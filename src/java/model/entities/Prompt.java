/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import java.io.Serializable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
//import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlRootElement;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@XmlRootElement
/**
 *
 * @author Critina
 * @author Aroa
 */
public class Prompt implements Serializable{
    private static final long serialVersionUID = 1L;
    @Id
    @SequenceGenerator(name="Prompt_gen", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Prompt_gen") 
    private Long id;
    
    @NotBlank 
    private String titol;
    
    private String description;
   
    @NotBlank
    @Column (length=5000) //per ampliar la mida del string
    private String template; //plantilla
    
    @ManyToOne
    @NotNull
    @JoinColumn(name = "AUTHOR_ID")   // el nombre que realmente tiene la tabla
    private Customer author;
    
    @ElementCollection
    private List<String> etiquetes = new ArrayList<>();

    @ManyToMany
    private List<Tool> tools = new ArrayList<>();
    
    //atribut que marca si un prompt es public o privat
    private boolean privat;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCreacio;
    
    
    
    //getters i setters
    @PrePersist
    void onCreate(){
        dataCreacio= new Date();
    }
    
    public Long getId(){
        return id;
    }
    
    public void setId(Long id){
        this.id=id;
    } 
    
    public String getTitol(){
        return titol;
    }
    
    public void setTitol(String titol){
        this.titol=titol;
    }
    
    public String getDescription(){
        return description;
    }
    
    public void setDescription(String desc){
        this.description=desc;
    }
    
    public String getTemplate(){
        return template;
    }
    
    public void setTemplate(String template){
        this.template=template;
    }
    
    public Customer getAuthor(){
        return author;
    }
    
    public void setAuthor(Customer a){
        this.author=a;
    }
    
    public  List<String> getEtiquetes(){
        return etiquetes;
    }
    
    public void setEtiquetes(List<String> e){
        this.etiquetes=e;
    }
    
     public List<Tool> getTools() {
        return tools;
    }

    public void setTools(List<Tool> tools) {
        this.tools = tools;
    }

    public boolean isPrivat() {
        return privat;
    }

    public void setPrivat(boolean privat) {
        this.privat = privat;
    }

    public Date getDataCreacio() {
        return dataCreacio;
    }

    public void setDataCreacio(Date dataCreacio) {
        this.dataCreacio = dataCreacio;
    }
}
