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
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.annotations.GenericGenerator;

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
public class User implements Identifiable<Long>, Serializable {
    
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

    private static final long serialVersionUID = -4504381318096463953L;
    
    private static final String BASE_URL = "https://polimi-board-game-manager.herokuapp.com/users/"; 
    
    // ======================================
    // =             Attributes             =
    // ======================================
    
    @Id
    @GenericGenerator(name="assigned_identity_generator", strategy="com.herokuapp.polimiboardgamemanager.model.AssignedIdentityGenerator")
    @GeneratedValue(generator="assigned_identity_generator", strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique=true, insertable=true)
    private Long id;
    
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
        this(null, fullName, username, password, powerUser);
    }
    
    public User(Long id, String fullName, String username, String password, boolean powerUser) {
        super();
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.powerUser = powerUser;
        if (id != null)
            this.id = id;
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
    @Override
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
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
    
    public Map<String, Link> getLinks() {
        Map<String, Link> links = new HashMap<>();
        links.put("self", Link.fromUri(BASE_URL+String.valueOf(id)).rel("self").build());
        links.put("parent", Link.fromUri(BASE_URL).rel("parent").build());
        links.put("plays", Link.fromUri(BASE_URL+String.valueOf(id)+"/plays").rel("plays").build());
        
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
