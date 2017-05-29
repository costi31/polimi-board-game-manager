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
 * Entity that represents a "play" of a boardgame, created by a user.
 *
 * @author Luca Luciano Costanzo
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
    
    /**
     * The Enum OrderBy.
     */
    public enum OrderBy {
        
        /** The id. */
        id, 
        /** The board game. */
        boardGame, 
        /** The date. */
        date, 
        /** The completed. */
        completed, 
        /** The time to complete. */
        timeToComplete
    }
    
    /**
     * The Enum FilterBy.
     */
    public enum FilterBy {
    	
	    /** The user creator id. */
	    userCreator_id, 
	    /** The board game id. */
	    boardGame_id, 
	    /** The date. */
	    date, 
	    /** The completed. */
	    completed, 
	    /** The time to complete. */
	    timeToComplete
    }

    /** The Constant COUNT_ALL. */
    public static final String COUNT_ALL = "Play.countAll";
    
    /** The Constant FIND_BY_USER. */
    public static final String FIND_BY_USER = "Play.findByUser";

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2891773437428236416L;
    
    /** The Constant BASE_URL. */
    private static final String BASE_URL = "https://polimi-board-game-manager.herokuapp.com/users/"; 
    
    // ======================================
    // =             Attributes             =
    // ======================================
    
    /** The id. */
    @Id
    @GenericGenerator(name="assigned_identity_generator", strategy="com.herokuapp.polimiboardgamemanager.model.AssignedIdentityGenerator")
    @GeneratedValue(generator="assigned_identity_generator", strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique=true, insertable=true)
    private Long id;
    
    /** The user creator. */
    @ManyToOne(optional=false) 
    @JoinColumn(name="userCreatorId", nullable=false, updatable=false)
    private User userCreator;
    
    /** The board game. */
    @ManyToOne(optional=false) 
    @JoinColumn(name="boardGameId", nullable=false, updatable=false)
    private BoardGame boardGame;
    
    /** The date. */
    @Column(name="date")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar date;
    
    /** The time to complete. */
    @Column(name="timeToComplete")
    private Time timeToComplete;
    
    /** The completed. */
    @Column(name="completed")
    private boolean completed;
    
    /** The user winner. */
    @ManyToOne
    @JoinColumn(name="userWinnerId")
    private User userWinner;   
    
    /** The players involved. */
    @Column(name="playersInvolved")
    private int playersInvolved;

    /**
     * Instantiates a new play.
     */
    public Play() {
        super();
    }
    
    /**
     * Instantiates a new play.
     *
     * @param id the id
     * @param userCreator the user creator
     * @param boardGame the board game
     * @param date the date
     */
    public Play(Long id, User userCreator, BoardGame boardGame, Calendar date) {
    	this(id, userCreator, boardGame, date, 1, false, null, null);
    }
    
    /**
     * Instantiates a new play.
     *
     * @param userCreator the user creator
     * @param boardGame the board game
     * @param date the date
     */
    public Play(User userCreator, BoardGame boardGame, Calendar date) {
    	this(null, userCreator, boardGame, date, 1, false, null, null);
    }
    
    /**
     * Instantiates a new play.
     *
     * @param userCreator the user creator
     * @param boardGame the board game
     * @param date the date
     * @param playersInvolved the players involved
     */
    public Play(User userCreator, BoardGame boardGame, Calendar date, int playersInvolved) {
    	this(null, userCreator, boardGame, date, playersInvolved, false, null, null);
    }
    
    /**
     * Instantiates a new play.
     *
     * @param userCreator the user creator
     * @param boardGame the board game
     * @param date the date
     * @param playersInvolved the players involved
     * @param completed the completed
     */
    public Play(User userCreator, BoardGame boardGame, Calendar date, int playersInvolved, boolean completed) {
    	this(null, userCreator, boardGame, date, playersInvolved, completed, null, null);
    }
    
    /**
     * Instantiates a new play.
     *
     * @param userCreator the user creator
     * @param boardGame the board game
     * @param date the date
     * @param playersInvolved the players involved
     * @param completed the completed
     * @param timeToComplete the time to complete
     */
    public Play(User userCreator, BoardGame boardGame, Calendar date, int playersInvolved, boolean completed, Time timeToComplete) {
    	this(null, userCreator, boardGame, date, playersInvolved, completed, timeToComplete, null);
    }
    
    /**
     * Instantiates a new play.
     *
     * @param userCreator the user creator
     * @param boardGame the board game
     * @param date the date
     * @param playersInvolved the players involved
     * @param completed the completed
     * @param timeToComplete the time to complete
     * @param userWinner the user winner
     */
    public Play(User userCreator, BoardGame boardGame, Calendar date, int playersInvolved, boolean completed, Time timeToComplete, User userWinner) {
    	this(null, userCreator, boardGame, date, playersInvolved, completed, timeToComplete, userWinner);
    }
    
    /**
     * Instantiates a new play.
     *
     * @param id the id
     * @param userCreator the user creator
     * @param boardGame the board game
     * @param date the date
     * @param playersInvolved the players involved
     * @param completed the completed
     * @param timeToComplete the time to complete
     * @param userWinner the user winner
     */
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
     * Gets the user creator.
     *
     * @return the userCreator
     */
    public User getUserCreator() {
        return userCreator;
    }

    /**
     * Sets the user creator.
     *
     * @param userCreator the userCreator to set
     */
    public void setUserCreator(User userCreator) {
        this.userCreator = userCreator;
    }

    /**
     * Gets the board game.
     *
     * @return the boardGame
     */
    public BoardGame getBoardGame() {
        return boardGame;
    }

    /**
     * Sets the board game.
     *
     * @param boardGame the boardGame to set
     */
    public void setBoardGame(BoardGame boardGame) {
        this.boardGame = boardGame;
    }

    /**
     * Gets the date.
     *
     * @return the date
     */
    public Calendar getDate() {
        return date;
    }

    /**
     * Sets the date.
     *
     * @param date the date to set
     */
    public void setDate(Calendar date) {
        this.date = date;
    }

    /**
     * Gets the time to complete.
     *
     * @return the timeToComplete
     */
    public Time getTimeToComplete() {
        return timeToComplete;
    }

    /**
     * Sets the time to complete.
     *
     * @param timeToComplete the timeToComplete to set
     */
    public void setTimeToComplete(Time timeToComplete) {
        this.timeToComplete = timeToComplete;
    }

    /**
     * Checks if is completed.
     *
     * @return the completed
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Sets the completed.
     *
     * @param completed the completed to set
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    /**
     * Gets the user winner.
     *
     * @return the userWinner
     */
    public User getUserWinner() {
        return userWinner;
    }

    /**
     * Sets the user winner.
     *
     * @param userWinner the userWinner to set
     */
    public void setUserWinner(User userWinner) {
        this.userWinner = userWinner;
    }

    /**
     * Gets the players involved.
     *
     * @return the playersInvolved
     */
    public int getPlayersInvolved() {
        return playersInvolved;
    }

    /**
     * Sets the players involved.
     *
     * @param playersInvolved the playersInvolved to set
     */
    public void setPlayersInvolved(int playersInvolved) {
        this.playersInvolved = playersInvolved;
    }

    /**
     * Gets the links.
     *
     * @return the links
     */
    public Map<String, Link> getLinks() {
        String baseUrl = BASE_URL+userCreator.getId()+"/plays/";
        Map<String, Link> links = new HashMap<>();
        links.put("self", Link.fromUri(baseUrl+String.valueOf(id)).rel("self").build());
        links.put("parent", Link.fromUri(baseUrl).rel("parent").build());
        
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
        Play play = (Play) o;
        return Objects.equals(id, play.id);
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
                ", userCreatorId='" + userCreator.getId() + "'" +
                ", boardGameId='" + boardGame.getId() + "'" +
                ", date='" + date.toString() + "'" +
                '}';
    }    
}
