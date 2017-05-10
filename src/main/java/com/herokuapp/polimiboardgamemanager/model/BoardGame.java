package com.herokuapp.polimiboardgamemanager.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BoardGame {
    private int id;
    private String name;
    private String designers;
    private String cover;

    public BoardGame(){

    }
       
    public BoardGame(int id, String name, String designers, String cover) {
        this.id = id;
        this.setName(name);
        this.setDesigners(designers);
        this.setCover(cover);
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
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