package com.herokuapp.polimiboardgamemanager.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.*;
import javax.ws.rs.core.Link;
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
@Entity
@Table(name = "user")
@NamedQueries({
    @NamedQuery(name = User.FIND_ALL_NAME_DESC, query = "SELECT u FROM User u ORDER BY u.fullName DESC"),
    @NamedQuery(name = User.FIND_ALL_NAME_ASC, query = "SELECT u FROM User u ORDER BY u.fullName ASC"),
    @NamedQuery(name = User.FIND_BY_LOGIN_PASSWORD, query = "SELECT u FROM User u WHERE u.username = :username AND u.password = :password"),
    @NamedQuery(name = User.COUNT_ALL, query = "SELECT COUNT(u) FROM User u")
})
public class User implements Serializable {
    
    // ======================================
    // =             Constants              =
    // ======================================

    public static final String FIND_ALL_NAME_DESC = "User.findAllNameDesc";
    public static final String FIND_ALL_NAME_ASC = "User.findAllNameAsc";
    public static final String COUNT_ALL = "User.countAll";
    public static final String FIND_BY_LOGIN_PASSWORD = "User.findByLoginAndPassword";    

    @Transient
    private static final long serialVersionUID = -4504381318096463953L;
    
    @Transient
    private static final String BASE_URL = "https://polimi-board-game-manager.herokuapp.com/users/"; 
    
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    
    @Column(name = "fullName")
    private String fullName;
    
    @Column(name = "username")
    private String username;
    
    @Column(name = "password")
    private String password;
    
    @Column(name = "power_user")
    private boolean powerUser;

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
    @XmlTransient
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
    @XmlTransient
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
    @XmlTransient
    public boolean isPowerUser() {
        return powerUser;
    }

    /**
     * @param powerUser the powerUser to set
     */
    public void setPowerUser(boolean powerUser) {
        this.powerUser = powerUser;
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
