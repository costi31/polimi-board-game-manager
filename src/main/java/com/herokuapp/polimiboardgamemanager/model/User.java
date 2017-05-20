package com.herokuapp.polimiboardgamemanager.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.herokuapp.polimiboardgamemanager.util.PasswordUtils;


/**
 * Entity that represents an user
 * @author Luca Luciano Costanzo
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "user")
@NamedQueries({
    @NamedQuery(name = User.FIND_BY_LOGIN_PASSWORD, query = "SELECT u FROM User u WHERE u.username = :username AND u.password = :password"),
    @NamedQuery(name = User.FIND_BY_USERNAME, query = "SELECT u FROM User u WHERE u.username = :username"),
    @NamedQuery(name = User.COUNT_ALL, query = "SELECT COUNT(u) FROM User u")
})
public class User implements Serializable {
    
    // ======================================
    // =             Constants              =
    // ======================================
    
    public enum OrderBy {
        fullName, id
    }
    
    public enum OrderType {
        ASC, DESC
    }

    public static final String COUNT_ALL = "User.countAll";
    public static final String FIND_BY_LOGIN_PASSWORD = "User.findByLoginAndPassword";
    public static final String FIND_BY_USERNAME = "User.findByUsername";

    @Transient
    private static final long serialVersionUID = -4504381318096463953L;
    
    @Transient
    private static final String BASE_URL = "https://polimi-board-game-manager.herokuapp.com/users/"; 
    
    // ======================================
    // =             Attributes             =
    // ======================================
    
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    
    @Column(name = "fullName")
    private String fullName;
    
    @XmlTransient
    @Column(name = "username")
    private String username;
    
    @XmlTransient
    @Column(name = "password")
    private String password;
    
    @Column(name = "power_user")
    private boolean powerUser;
    
    @XmlTransient
    @OneToMany(mappedBy="userCreator")
    private List<Play> plays;
    

    public User() {
    }
    
    public User(String fullName, String username, String password, boolean powerUser) {
        super();
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.powerUser = powerUser;
    }    
    
    // ======================================
    // =         Lifecycle methods          =
    // ======================================

    @PrePersist
    private void digestPassword() {
        password = PasswordUtils.digestPassword(password);
    }

    // ======================================
    // =          Getters & Setters         =
    // ======================================    
    
    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * @param name the name to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * @return the login
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param login the login to set
     */
    public void setUsername(String login) {
        this.username = login;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the powerUser
     */
    public boolean isPowerUser() {
        return powerUser;
    }

    /**
     * @param powerUser the powerUser to set
     */
    public void setPowerUser(boolean powerUser) {
        this.powerUser = powerUser;
    }
    
    /**
     * @return the plays
     */
    public List<Play> getPlays() {
        return plays;
    }
    
    
    @XmlElement(name = "link")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    public List<Link> getLinks()
    {
        List<Link> links = new ArrayList<Link>();
        links.add(Link.fromUri(BASE_URL+String.valueOf(id)).rel("self").build());
        links.add(Link.fromUri(BASE_URL).rel("parent").build());
        links.add(Link.fromUri(BASE_URL+String.valueOf(id)+"/plays").rel("plays").build());
        
        return links;
    }

    // ======================================
    // =   Methods hash, equals, toString   =
    // ======================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }    
}