package com.herokuapp.polimiboardgamemanager.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
//    @NamedQuery(name = Play.FIND_BY_LOGIN_PASSWORD, query = "SELECT u FROM User u WHERE u.username = :username AND u.password = :password"),
    @NamedQuery(name = Play.FIND_BY_USER, query = "SELECT p FROM Play p WHERE p.userCreator = :userCreator"),
    @NamedQuery(name = Play.COUNT_ALL, query = "SELECT COUNT(p) FROM Play p")
})
public class Play implements Serializable {
    
    // ======================================
    // =             Constants              =
    // ======================================
    
    public enum OrderBy {
        fullName, id
    }
    
    public enum OrderType {
        ASC, DESC
    }

    public static final String COUNT_ALL = "Play.countAll";
//    public static final String FIND_BY_LOGIN_PASSWORD = "User.findByLoginAndPassword";
    public static final String FIND_BY_USER = "Play.findByUser";

    private static final long serialVersionUID = -2891773437428236416L;
    
    private static final String BASE_URL = "https://polimi-board-game-manager.herokuapp.com/users/"; 
    
    // ======================================
    // =             Attributes             =
    // ======================================
    
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    
    @ManyToOne(optional=false) 
    @JoinColumn(name="userCreatorId", nullable=false, updatable=false)
    private User userCreator;
    
    @ManyToOne(optional=false) 
    @JoinColumn(name="boardGameId", nullable=false, updatable=false)
    private BoardGame boardGame;
    
    @Column(name="date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    
    @Column(name="timeToComplete")
    @Temporal(TemporalType.TIME)
    private Date timeToComplete;
    
    @Column(name="completed")
    private boolean completed;
    
    @ManyToOne
    @JoinColumn(name="userWinnerId")
    private User userWinner;   
    
    @Column(name="playersInvolved")
    private int playersInvolved;

    public Play() {
    }
    
    public Play(User userCreator, BoardGame boardGame) {
        this(userCreator, boardGame, 1);
    }
    
    public Play(User userCreator, BoardGame boardGame, int playersInvolved) {
        super();
        this.userCreator = userCreator;
        this.boardGame = boardGame;
        this.date = new Date();
        this.playersInvolved = playersInvolved;
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
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the timeToComplete
     */
    public Date getTimeToComplete() {
        return timeToComplete;
    }

    /**
     * @param timeToComplete the timeToComplete to set
     */
    public void setTimeToComplete(Date timeToComplete) {
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

    @XmlElement(name = "link")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    public List<Link> getLinks()
    {
        String baseUrl = BASE_URL+userCreator.getId()+"/plays/";
        List<Link> links = new ArrayList<Link>();
        links.add(Link.fromUri(baseUrl+String.valueOf(id)).rel("self").build());
        links.add(Link.fromUri(baseUrl).rel("parent").build());
        
        return links;
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
