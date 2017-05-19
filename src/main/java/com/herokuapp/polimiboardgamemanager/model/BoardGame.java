package com.herokuapp.polimiboardgamemanager.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
 
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "boardgame")
@NamedQueries({
    @NamedQuery(name = BoardGame.FIND_BY_NAME, query = "SELECT b FROM BoardGame b WHERE b.name = :name"),
    @NamedQuery(name = BoardGame.COUNT_ALL, query = "SELECT COUNT(b) FROM BoardGame b")
})
public class BoardGame implements Serializable {
    
    // ======================================
    // =             Constants              =
    // ======================================    
    
    public enum OrderBy {
        name, id
    }
    
    public enum OrderType {
        ASC, DESC
    }

    public static final String FIND_BY_NAME = "BoardGame.findByName";
    public static final String COUNT_ALL = "BoardGame.countAll";
    
    private static final long serialVersionUID = 3879736814114073027L;

    private static final String BASE_URL = "https://polimi-board-game-manager.herokuapp.com/boardgames/"; 
    
    // ======================================
    // =             Attributes             =
    // ======================================
    
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "designers")
    private String designers;
    
    @Column(name = "cover")
    private String cover;
    
    public BoardGame(){
    }
       
    public BoardGame(String name, String designers, String cover) {
        super();
        this.name = name;
        this.designers = designers;
        this.cover = cover;
    }

    // ======================================
    // =          Getters & Setters         =
    // ======================================
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesigners() {
        return designers;
    }

    public void setDesigners(String designers) {
        this.designers = designers;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
    
    @XmlElement(name = "link")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    public List<Link> getLinks()
    {
        List<Link> links = new ArrayList<Link>();
        links.add(Link.fromUri(BASE_URL+String.valueOf(id)).rel("self").build());
        links.add(Link.fromUri(BASE_URL).rel("parent").build());
        
        return links;
    }

}