package com.herokuapp.polimiboardgamemanager.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.annotations.GenericGenerator;
 
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "boardgame")
@NamedQueries({
    @NamedQuery(name = BoardGame.FIND_BY_NAME, query = "SELECT b FROM BoardGame b WHERE b.name = :name"),
    @NamedQuery(name = BoardGame.COUNT_ALL, query = "SELECT COUNT(b) FROM BoardGame b")
})
public class BoardGame implements Identifiable<Long>, Serializable {
    
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
    
    @Id
    @GenericGenerator(name="assigned_identity_generator", strategy="com.herokuapp.polimiboardgamemanager.model.AssignedIdentityGenerator")
    @GeneratedValue(generator="assigned_identity_generator", strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique=true, insertable=true)
    private Long id;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "designers")
    private String designers;
    
    @Column(name = "cover")
    private String cover;
    
    @XmlTransient
    @OneToMany(mappedBy="boardGame")
    private List<Play> plays;
    
    
    public BoardGame(){
    	super();
    }
       
    public BoardGame(String name, String designers, String cover) {
        this(null, name, designers, cover);
    }
    
    public BoardGame(Long id, String name, String designers, String cover) {
        super();
        if (id != null)
        	this.id = id;
        this.name = name;
        this.designers = designers;
        this.cover = cover;
    }

    // ======================================
    // =          Getters & Setters         =
    // ======================================
    
    @Override
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
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
    
    /**
     * @return the plays
     */
    public List<Play> getPlays() {
        return plays;
    }    
    
    public Map<String, Link> getLinks() {
        Map<String, Link> links = new HashMap<>();
        links.put("self", Link.fromUri(BASE_URL+String.valueOf(id)).rel("self").build());
        links.put("parent", Link.fromUri(BASE_URL).rel("parent").build());
        
        return links;
    }
    
    @XmlElement(name = "link")
    @XmlJavaTypeAdapter(LinkAdapter.class)
    public Collection<Link> getLinksCollection() {
        return getLinks().values();
    }
    
    public Link[] getLinksArray() {
        Collection<Link> linksCollection = getLinksCollection();
        return linksCollection.toArray(new Link[linksCollection.size()]);
    }    

}