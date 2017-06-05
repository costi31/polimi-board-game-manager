package com.herokuapp.polimiboardgamemanager.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
 
/**
 * The Class BoardGame.
 */
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
    
    /**
     * The Enum OrderBy.
     */
	public enum OrderBy {
        
        /** The id. */
        id, 
        /** The name. */
        name, 
        /** The designers. */
        designers, 
        /** The cover. */
        cover
    }
    
    /**
     * The Enum FilterBy.
     */
    public enum FilterBy {
        
        /** The name. */
        name, 
        /** The designers. */
        designers, 
        /** The cover. */
        cover
    }

    /** The Constant FIND_BY_NAME. */
    public static final String FIND_BY_NAME = "BoardGame.findByName";
    
    /** The Constant COUNT_ALL. */
    public static final String COUNT_ALL = "BoardGame.countAll";
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3879736814114073027L;

    /** The Constant BASE_URL. */
    private static final String BASE_URL = CommonConstants.API_URI + "boardgames/"; 
    
    // ======================================
    // =             Attributes             =
    // ======================================
    
    /** The id. */
    @Id
    @GenericGenerator(name="assigned_identity_generator", strategy="com.herokuapp.polimiboardgamemanager.model.AssignedIdentityGenerator")
    @GeneratedValue(generator="assigned_identity_generator", strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique=true, insertable=true)
    private Long id;
    
    /** The name. */
    @Column(name = "name")
    private String name;
    
    /** The designers. */
    @Column(name = "designers")
    private String designers;
    
    /** The cover. */
    @Column(name = "cover")
    private String cover;
    
    /** The plays. */
    @XmlTransient
    @OneToMany(mappedBy="boardGame")
    private List<Play> plays;
    
    
    /**
     * Instantiates a new board game.
     */
    public BoardGame(){
    	super();
    }
       
    /**
     * Instantiates a new board game.
     *
     * @param name the name
     * @param designers the designers
     * @param cover the cover
     */
    public BoardGame(String name, String designers, String cover) {
        this(null, name, designers, cover);
    }
    
    /**
     * Instantiates a new board game.
     *
     * @param id the id
     * @param name the name
     * @param designers the designers
     * @param cover the cover
     */
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
    
    /* (non-Javadoc)
     * @see com.herokuapp.polimiboardgamemanager.model.Identifiable#getId()
     */
    @Override
    public Long getId() {
        return id;
    }
    
    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the designers.
     *
     * @return the designers
     */
    public String getDesigners() {
        return designers;
    }

    /**
     * Sets the designers.
     *
     * @param designers the new designers
     */
    public void setDesigners(String designers) {
        this.designers = designers;
    }

    /**
     * Gets the cover.
     *
     * @return the cover
     */
    public String getCover() {
        return cover;
    }

    /**
     * Sets the cover.
     *
     * @param cover the new cover
     */
    public void setCover(String cover) {
        this.cover = cover;
    }
    
    /**
     * Gets the plays.
     *
     * @return the plays
     */
    public List<Play> getPlays() {
        return plays;
    }    
    
    /**
     * Gets the links.
     *
     * @return the links
     */
    public Map<String, Link> getLinks() {
        Map<String, Link> links = new HashMap<>();
        links.put("self", Link.fromUri(BASE_URL+String.valueOf(id)).rel("self").build());
        links.put("parent", Link.fromUri(BASE_URL).rel("parent").build());
        
        return links;
    }
    
    /**
     * Gets the links collection.
     *
     * @return the links collection
     */
    @XmlElement(name = "link")
    @XmlJavaTypeAdapter(LinkAdapter.class)
    public Collection<Link> getLinksCollection() {
        return getLinks().values();
    }
    
    /**
     * Gets the links array.
     *
     * @return the links array
     */
    public Link[] getLinksArray() {
        Collection<Link> linksCollection = getLinksCollection();
        return linksCollection.toArray(new Link[linksCollection.size()]);
    }    
    

    // ======================================
    // =   Methods hash, equals, toString   =
    // ======================================
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
        	return true;
        if (o == null || getClass() != o.getClass())
        	return false;
        BoardGame board = (BoardGame) o;
        return Objects.equals(id, board.id);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Play{" +
                "id='" + id + "'" +
                ", name='" + name + "'" +
                ", designers='" + designers + "'" +
                ", cover='" + cover + "'" +
                '}';
    }

}