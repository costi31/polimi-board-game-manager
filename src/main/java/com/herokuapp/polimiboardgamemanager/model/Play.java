package com.herokuapp.polimiboardgamemanager.model;

import java.io.Serializable;
import java.sql.Time;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.annotations.GenericGenerator;


/**
 * Entity that represents a "play" of a boardgame, started by an user
 * @author Luca Luciano Costanzo
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "play")
@NamedQueries({
    @NamedQuery(name = Play.FIND_BY_USER, query = "SELECT p FROM Play p WHERE p.userCreator = :userCreator"),
    @NamedQuery(name = Play.COUNT_ALL, query = "SELECT COUNT(p) FROM Play p")
})
public class Play implements Identifiable<Long>, Serializable {
    
    // ======================================
    // =             Constants              =
    // ======================================
    
    public enum OrderBy {
        id, boardGame, date, completed, timeToComplete
    }
    
    public enum OrderType {
        ASC, DESC
    }

    public static final String COUNT_ALL = "Play.countAll";
    public static final String FIND_BY_USER = "Play.findByUser";

    private static final long serialVersionUID = -2891773437428236416L;
    
    private static final String BASE_URL = "https://polimi-board-game-manager.herokuapp.com/users/"; 
    
    // ======================================
    // =             Attributes             =
    // ======================================
    
    @Id
    @GenericGenerator(name="assigned_identity_generator", strategy="com.herokuapp.polimiboardgamemanager.model.AssignedIdentityGenerator")
    @GeneratedValue(generator="assigned_identity_generator", strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique=true, insertable=true)
    private Long id;
    
    @ManyToOne(optional=false) 
    @JoinColumn(name="userCreatorId", nullable=false, updatable=false)
    private User userCreator;
    
    @ManyToOne(optional=false) 
    @JoinColumn(name="boardGameId", nullable=false, updatable=false)
    private BoardGame boardGame;
    
    @Column(name="date")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar date;
    
    @Column(name="timeToComplete")
    private Time timeToComplete;
    
    @Column(name="completed")
    private boolean completed;
    
    @ManyToOne
    @JoinColumn(name="userWinnerId")
    private User userWinner;   
    
    @Column(name="playersInvolved")
    private int playersInvolved;

    public Play() {
        super();
    }
    
    public Play(Long id, User userCreator, BoardGame boardGame, Calendar date) {
    	this(id, userCreator, boardGame, date, 1, false, null, null);
    }
    
    public Play(User userCreator, BoardGame boardGame, Calendar date) {
    	this(null, userCreator, boardGame, date, 1, false, null, null);
    }
    
    public Play(User userCreator, BoardGame boardGame, Calendar date, int playersInvolved) {
    	this(null, userCreator, boardGame, date, playersInvolved, false, null, null);
    }
    
    public Play(User userCreator, BoardGame boardGame, Calendar date, int playersInvolved, boolean completed) {
    	this(null, userCreator, boardGame, date, playersInvolved, completed, null, null);
    }
    
    public Play(User userCreator, BoardGame boardGame, Calendar date, int playersInvolved, boolean completed, Time timeToComplete) {
    	this(null, userCreator, boardGame, date, playersInvolved, completed, timeToComplete, null);
    }
    
    public Play(User userCreator, BoardGame boardGame, Calendar date, int playersInvolved, boolean completed, Time timeToComplete, User userWinner) {
    	this(null, userCreator, boardGame, date, playersInvolved, completed, timeToComplete, null);
    }
    
    public Play(Long id, User userCreator, BoardGame boardGame, Calendar date, int playersInvolved, boolean completed, Time timeToComplete, User userWinner) {
        super();
        if (id != null)
        	this.id = id;
        this.userCreator = userCreator;
        this.boardGame = boardGame;
        this.date = date;
        this.playersInvolved = playersInvolved;
        this.completed = completed;
        this.timeToComplete = timeToComplete;
        this.userWinner = userWinner;
    }    
    
    // ======================================
    // =          Getters & Setters         =
    // ======================================    
    
    /**
     * @return the id
     */
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
     * @return the userCreator
     */
    public User getUserCreator() {
        return userCreator;
    }

    /**
     * @param userCreator the userCreator to set
     */
    public void setUserCreator(User userCreator) {
        this.userCreator = userCreator;
    }

    /**
     * @return the boardGame
     */
    public BoardGame getBoardGame() {
        return boardGame;
    }

    /**
     * @param boardGame the boardGame to set
     */
    public void setBoardGame(BoardGame boardGame) {
        this.boardGame = boardGame;
    }

    /**
     * @return the date
     */
    public Calendar getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Calendar date) {
        this.date = date;
    }

    /**
     * @return the timeToComplete
     */
    public Time getTimeToComplete() {
        return timeToComplete;
    }

    /**
     * @param timeToComplete the timeToComplete to set
     */
    public void setTimeToComplete(Time timeToComplete) {
        this.timeToComplete = timeToComplete;
    }

    /**
     * @return the completed
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * @param completed the completed to set
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    /**
     * @return the userWinner
     */
    public User getUserWinner() {
        return userWinner;
    }

    /**
     * @param userWinner the userWinner to set
     */
    public void setUserWinner(User userWinner) {
        this.userWinner = userWinner;
    }

    /**
     * @return the playersInvolved
     */
    public int getPlayersInvolved() {
        return playersInvolved;
    }

    /**
     * @param playersInvolved the playersInvolved to set
     */
    public void setPlayersInvolved(int playersInvolved) {
        this.playersInvolved = playersInvolved;
    }

    public Map<String, Link> getLinks() {
        String baseUrl = BASE_URL+userCreator.getId()+"/plays/";
        Map<String, Link> links = new HashMap<>();
        links.put("self", Link.fromUri(baseUrl+String.valueOf(id)).rel("self").build());
        links.put("parent", Link.fromUri(baseUrl).rel("parent").build());
        
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
        Play play = (Play) o;
        return Objects.equals(id, play.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Play{" +
                "id='" + id + "'" +
                ", userCreatorId='" + userCreator.getId() + "'" +
                " boardGameId='" + boardGame.getId() + "'" +
                '}';
    }    
}
