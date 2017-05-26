package com.herokuapp.polimiboardgamemanager.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.security.sasl.AuthorizeCallback;

import com.herokuapp.polimiboardgamemanager.filter.AuthenticationFilter;
import com.herokuapp.polimiboardgamemanager.model.BoardGame;
import com.herokuapp.polimiboardgamemanager.model.Play;

/**
 * Singleton DAO class to access and manage a board game
 * @author Luca Luciano Costanzo
 *
 */
public class BoardGameDao {
	
    private static final String USER_UNAUTHORIZED_MSG = "User unauthorized to do this operation!";
    private static final String BOARDGAME_ID_EXISTS_MSG = "A board game with desired id already exists!";	
    
    private static BoardGameDao instance = null;
    
    /**
     * Gets the instance of BoardGameDao
     * @return instance of BoardGameDao
     */
    public static BoardGameDao getInstance() {
        if (instance == null)
            instance = new BoardGameDao();

        return instance;
    }

    private BoardGameDao() {
    }
    
    /**
     * Find a board game by its id
     * @param id of the board game
     * @return BoardGame object corresponding to that id
     */
    public BoardGame findById(long id) {
        return (BoardGame) MyEntityManager.getInstance().findEntity(BoardGame.class, id);
    }
    
    /**
     * Inserts a new board game
     * @param board BoardGame object to insert
     */
    public long createBoardGame(BoardGame board, String authorizationBearer) throws Exception  {
        try {
        	long authenticatedUserId = AuthenticationFilter.getAuthIdFromBearer(authorizationBearer);
            
            // Verify if the authenticated user is a power user
            if (! UserDao.getInstance().findById(authenticatedUserId).isPowerUser())
                throw new SecurityException(USER_UNAUTHORIZED_MSG);
            
            Long id = board.getId();
            
            if (id != null && findById(id) != null) // if already exists a board game with specified id
            	throw new IllegalArgumentException(BOARDGAME_ID_EXISTS_MSG);            
            
            board = (BoardGame)MyEntityManager.getInstance().mergeEntity(board);
            return board.getId();
        } catch (Exception e) {
            throw new SecurityException(USER_UNAUTHORIZED_MSG);
        }        
    }
    
    public long createBoardGame(String name, String designers, String cover, String authorizationBearer) throws Exception {
        BoardGame board = new BoardGame(name, designers, cover);
        return createBoardGame(board, authorizationBearer);
    }    
    
    /**
     * Updates a board game
     * @param board the updated version of the desired board game
     */
    public void updateBoardGame(long id, BoardGame board, String authorizationBearer) throws Exception {
        try {
        	long authenticatedUserId = AuthenticationFilter.getAuthIdFromBearer(authorizationBearer);
            
            // Verify if the user is a power user
            if (! UserDao.getInstance().findById(authenticatedUserId).isPowerUser())
                throw new SecurityException(USER_UNAUTHORIZED_MSG);        	
            
            if (id != board.getId())
            	throw new IllegalArgumentException("Wrong id of board game!");
            
            MyEntityManager.getInstance().mergeEntity(board);
        } catch (Exception e) {
            throw new SecurityException(USER_UNAUTHORIZED_MSG);
        }       
    }
    
    public void removeBoardGame(long id, String authorizationBearer) throws Exception {     
        try {
            long authenticatedId = AuthenticationFilter.getAuthIdFromBearer(authorizationBearer);
            
            BoardGame board = findById(id);
            if (board == null)
            	throw new IllegalArgumentException("The board game to remove doesn't exist!");
            
            // Verify if the user is a power user
            if (! UserDao.getInstance().findById(authenticatedId).isPowerUser())
                throw new SecurityException(USER_UNAUTHORIZED_MSG);      
            
            MyEntityManager.getInstance().removeEntity(BoardGame.class, id);
        } catch (Exception e) {
            throw new SecurityException(USER_UNAUTHORIZED_MSG);
        }
    }
    
    /**
     * Gets the count of existing board games
     * @return number of existing board games
     */
    public long getBoardGamesCount() {
        EntityManager em = MyEntityManager.getInstance().getEm();
        long count = (long) em.createQuery("SELECT count(id) FROM BoardGame board").getSingleResult();
        return count;
    }
      
    public List<BoardGame> findAllBoardGames(String orderByString, String orderTypeString) throws Exception {
        EntityManager em = MyEntityManager.getInstance().getEm();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<BoardGame> cq = cb.createQuery(BoardGame.class);
        Root<BoardGame> us = cq.from(BoardGame.class);
        cq.select(us);
        
        // Get the order criteria
        // it throws exception if strings don't correspond to allowed enum values
        BoardGame.OrderBy orderBy = BoardGame.OrderBy.valueOf(orderByString);
        BoardGame.OrderType orderType = BoardGame.OrderType.valueOf(orderTypeString.toUpperCase());
        
        List<Order> orderCriteria = new ArrayList<Order>();
        Expression exp;
        if (orderBy.equals(BoardGame.OrderBy.name)) {
            exp = us.get(orderBy.toString());
            orderCriteria.add(
                              (orderType.equals(BoardGame.OrderType.DESC)) ? cb.desc(exp) : cb.asc(exp)
                             );
        }
        
        exp = us.get(BoardGame.OrderBy.id.toString());
        orderCriteria.add(
                          (orderType.equals(BoardGame.OrderType.DESC)) ? cb.desc(exp) : cb.asc(exp)
                         );
        
        cq.orderBy(orderCriteria);
        
        TypedQuery<BoardGame> q = em.createQuery(cq);
        return q.getResultList();
    }

}
