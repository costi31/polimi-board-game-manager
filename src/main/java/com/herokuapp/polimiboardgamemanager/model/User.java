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
 * Entity that represents a user.
 *
 * @author Luca Luciano Costanzo
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
    
    /**
     * The Enum OrderBy.
     */
    public enum OrderBy {
        
        /** The full name. */
        fullName, 
		/** The id. */
		id
    }
    
    /**
     * The Enum FilterBy.
     */
    public enum FilterBy {
    	
	    /** The full name. */
	    fullName, 
	    /** The power user. */
	    powerUser
    }

    /** The Constant COUNT_ALL. */
    public static final String COUNT_ALL = "User.countAll";
    
    /** The Constant FIND_BY_LOGIN_PASSWORD. */
    public static final String FIND_BY_LOGIN_PASSWORD = "User.findByLoginAndPassword";
    
    /** The Constant FIND_BY_USERNAME. */
    public static final String FIND_BY_USERNAME = "User.findByUsername";

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4504381318096463953L;
    
    /** The Constant BASE_URL. */
    private static final String BASE_URL = CommonConstants.API_URI + "users/"; 
    
    // ======================================
    // =             Attributes             =
    // ======================================
    
    /** The id. */
    @Id
    @GenericGenerator(name="assigned_identity_generator", strategy="com.herokuapp.polimiboardgamemanager.model.AssignedIdentityGenerator")
    @GeneratedValue(generator="assigned_identity_generator", strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique=true, insertable=true)
    private Long id;
    
    /** The full name. */
    @Column(name = "fullName")
    private String fullName;
    
    /** The username. */
    @XmlTransient
    @Column(name = "username")
    private String username;
    
    /** The password. */
    @XmlTransient
    @Column(name = "password")
    private String password;
    
    /** The power user. */
    @Column(name = "power_user")
    private boolean powerUser;
    
    /** The plays. */
    @XmlTransient
    @OneToMany(mappedBy="userCreator")
    private List<Play> plays;
    

    /**
     * Instantiates a new user.
     */
    public User() {
    	super();
    }
    
    /**
     * Instantiates a new user.
     *
     * @param fullName the full name
     * @param username the username
     * @param password the password
     * @param powerUser the power user
     */
    public User(String fullName, String username, String password, boolean powerUser) {
        this(null, fullName, username, password, powerUser);
    }
    
    /**
     * Instantiates a new user.
     *
     * @param id the id
     * @param fullName the full name
     * @param username the username
     * @param password the password
     * @param powerUser the power user
     */
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

    /**
     * Digest password.
     */
    @PrePersist
    private void digestPassword() {
        password = PasswordUtils.digestPassword(password);
    }

    // ======================================
    // =          Getters & Setters         =
    // ======================================    
    
    /**
     * Gets the id.
     *
     * @return the id
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the full name.
     *
     * @return the name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the full name.
     *
     * @param fullName the new full name
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Gets the username.
     *
     * @return the login
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param login the login to set
     */
    public void setUsername(String login) {
        this.username = login;
    }

    /**
     * Gets the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Checks if is power user.
     *
     * @return the powerUser
     */
    public boolean isPowerUser() {
        return powerUser;
    }

    /**
     * Sets the power user.
     *
     * @param powerUser the powerUser to set
     */
    public void setPowerUser(boolean powerUser) {
        this.powerUser = powerUser;
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
        links.put("plays", Link.fromUri(BASE_URL+String.valueOf(id)+"/plays").rel("plays").build());
        
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
        User user = (User) o;
        return Objects.equals(id, user.id);
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
        return "User{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }    
}
