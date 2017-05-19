package com.herokuapp.polimiboardgamemanager.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import com.herokuapp.polimiboardgamemanager.filter.AuthenticationFilter;
import com.herokuapp.polimiboardgamemanager.model.BoardGame;
import com.herokuapp.polimiboardgamemanager.model.User;

/**
 * Singleton DAO class to access and manage a board game
 * @author Luca Luciano Costanzo
 *
 */
public class BoardGameDao {
    
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
     * Gets a board game by its id
     * @param id of the board game
     * @return BoardGame object corresponding to that id
     */
    public BoardGame getBoardGame(long id) {
        try {
            BoardGame board = MyEntityManager.getInstance().getEm().find(BoardGame.class, id);
            return board;
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Get: BoardGame with " + id +  " not found");
        }
    }
    
    /**
     * Inserts a new board game
     * @param board BoardGame object to insert
     */
    public long createBoardGame(BoardGame board, String authorizationBearer) throws Exception  {
        try {
            String token = authorizationBearer.substring("Bearer".length()).trim();
            String authenticatedSubject = AuthenticationFilter.validateToken(token);
            long authenticatedUserId = Long.parseLong(authenticatedSubject.split(UserDao.SUBJECT_ID_SEPARATOR)[0]);
            
            // Verify if the authenticated user is a power user
            if (! UserDao.getInstance().findById(authenticatedUserId).isPowerUser())
                throw new SecurityException("User unauthorized");
            
            MyEntityManager.getInstance().persistEntity(board);
            
            return board.getId();
        } catch (Exception e) {
            throw new SecurityException("User unauthorized");
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
    public void updateBoardGame(BoardGame board) {
        
    }
    
    /**
     * Deletes a board game
     * @param board the BoardGame to delete
     */
    public void deleteBoardGame(BoardGame board) {
        
    }
    
    /**
     * Deletes a board game given its id
     * @param id the id of the board game to delete
     */
    public void deleteBoardGame(long id) {
        
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
