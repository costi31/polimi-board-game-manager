package com.herokuapp.polimiboardgamemanager.model;

import javax.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
 
@XmlRootElement
@Entity
@Table(name = "boardgame")
public class BoardGame implements Serializable {
    
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
        this.setName(name);
        this.setDesigners(designers);
        this.setCover(cover);
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

}