package com.herokuapp.polimiboardgamemanager.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.herokuapp.polimiboardgamemanager.filter.AuthenticationFilter;
import com.herokuapp.polimiboardgamemanager.model.BoardGame;

/**
 * Singleton DAO class to access and manage a board game.
 *
 * @author Luca Luciano Costanzo
 */
public class BoardGameDao {
	
    /** The Constant USER_UNAUTHORIZED_MSG. */
    private static final String USER_UNAUTHORIZED_MSG = "User unauthorized to do this operation!";
    
    /** The Constant BOARDGAME_ID_EXISTS_MSG. */
    private static final String BOARDGAME_ID_EXISTS_MSG = "A board game with desired id already exists!";	
    
    /** The instance. */
    private static BoardGameDao instance = null;
    
    /**
     * Gets the instance of BoardGameDao.
     *
     * @return instance of BoardGameDao
     */
    public static BoardGameDao getInstance() {
        if (instance == null)
            instance = new BoardGameDao();

        return instance;
    }

    /**
     * Instantiates a new board game dao.
     */
    private BoardGameDao() {
    }
    
    /**
     * Find a board game by its id.
     *
     * @param id of the board game
     * @return BoardGame object corresponding to that id
     */
    public BoardGame findById(long id) {
        return MyEntityManager.getInstance().findEntity(BoardGame.class, id);
    }
    
    /**
     * Inserts a new board game.
     *
     * @param board BoardGame object to insert
     * @param authorizationBearer the authorization bearer
     * @return the id of board game
     * @throws Exception the exception
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
            
            return MyEntityManager.getInstance().mergeEntity(board).getId();
        } catch (Exception e) {
            throw new SecurityException(USER_UNAUTHORIZED_MSG, e);
        }        
    }
    
    /**
     * Creates the board game.
     *
     * @param name the name
     * @param designers the designers
     * @param cover the cover
     * @param authorizationBearer the authorization bearer
     * @return the id of board game
     * @throws Exception the exception
     */
    public long createBoardGame(String name, String designers, String cover, String authorizationBearer) throws Exception {
        BoardGame board = new BoardGame(name, designers, cover);
        return createBoardGame(board, authorizationBearer);
    }    
    
    /**
     * Updates a board game.
     *
     * @param id the id of board game to update
     * @param board the updated version of the desired board game
     * @param authorizationBearer the authorization bearer
     * @throws Exception the exception
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
            throw new SecurityException(USER_UNAUTHORIZED_MSG, e);
        }       
    }
    
    /**
     * Removes the board game.
     *
     * @param id the id of board game to remove
     * @param authorizationBearer the authorization bearer
     * @throws Exception the exception
     */
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
            throw new SecurityException(USER_UNAUTHORIZED_MSG, e);
        }
    }
    
    /**
     * Gets the count of existing board games.
     *
     * @return number of existing board games
     */
    public long getBoardGamesCount() {
        EntityManager em = MyEntityManager.getInstance().getEm();
        TypedQuery<Long> query = em.createNamedQuery(BoardGame.COUNT_ALL, Long.class);
        return query.getSingleResult();
    }
      
    /**
     * Find all board games.
     *
     * @param filtersString the filters string
     * @param ordersString the orders string
     * @return the list of all board games
     * @throws Exception the exception
     */
    public List<BoardGame> findAllBoardGames(List<String> filtersString, List<String> ordersString) throws Exception {
        return MyEntityManager.getInstance().findAllEntities(BoardGame.class,
       		 filtersString, ordersString, BoardGame.FilterBy.class, BoardGame.OrderBy.class);
   }   

}
