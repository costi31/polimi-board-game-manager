package com.herokuapp.polimiboardgamemanager.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
 
@XmlRootElement
@Entity
@Table(name = "boardgame")
public class BoardGame implements Serializable {

    @Transient
    private static final long serialVersionUID = 3879736814114073027L;

    @Transient
    private static final String BASE_URL = "https://polimi-board-game-manager.herokuapp.com/boardgames/"; 
    
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