package com.herokuapp.polimiboardgamemanager.dao;

import java.util.ArrayList;
import java.util.List;

import com.herokuapp.polimiboardgamemanager.filter.AuthenticationFilter;
import com.herokuapp.polimiboardgamemanager.model.Play;

/**
 * The Class PlayDao.
 */
public class PlayDao {
    
    /** The Constant USER_UNAUTHORIZED_MSG. */
    private static final String USER_UNAUTHORIZED_MSG = "User unauthorized to do this operation!";
    
    /** The Constant PLAY_ID_EXISTS_MSG. */
    private static final String PLAY_ID_EXISTS_MSG = "A play with desired id already exists!";
    
    /** The instance. */
    private static PlayDao instance = null;
        
    /**
     * Gets the instance of PlayDao.
     *
     * @return instance of PlayDao
     */
    public static PlayDao getInstance() {
        if (instance == null)
            instance = new PlayDao();

        return instance;
    }

    /**
     * Instantiates a new play dao.
     */
    private PlayDao() {
    }
    
    /**
     * Creates the play.
     *
     * @param play the play
     * @param authorizationBearer the authorization bearer
     * @return the id of the play created
     * @throws Exception the exception
     */
    public long createPlay(Play play, String authorizationBearer) throws Exception {
        try {
        	long authenticatedId = AuthenticationFilter.getAuthIdFromBearer(authorizationBearer);
            
            // Verify if the id of authenticated user corresponds to the id of the user creator of play
            if (authenticatedId != play.getUserCreator().getId())
                throw new SecurityException(USER_UNAUTHORIZED_MSG);
            
            Long id = play.getId();
            
            if (id != null && findById(id) != null) // if already exists a play with specified id
            	throw new IllegalArgumentException(PLAY_ID_EXISTS_MSG);
            
            return MyEntityManager.getInstance().mergeEntity(play).getId();
        } catch (Exception e) {
            throw new SecurityException(USER_UNAUTHORIZED_MSG, e);
        }        
    }  
    
    /**
     * Update play.
     *
     * @param id the id of the play to update
     * @param play the play with fields updated
     * @param authorizationBearer the authorization bearer
     * @throws Exception the exception
     */
    public void updatePlay(long id, Play play, String authorizationBearer) throws Exception {
        try {
        	long authenticatedId = AuthenticationFilter.getAuthIdFromBearer(authorizationBearer);
            
            // Verify if the id of authenticated user corresponds to the id of the user creator of play
            if (authenticatedId != play.getUserCreator().getId())
                throw new SecurityException(USER_UNAUTHORIZED_MSG);
            
            if (id != play.getId())
            	throw new IllegalArgumentException("Wrong id of play!");
            
            MyEntityManager.getInstance().mergeEntity(play);
        } catch (Exception e) {
            throw new SecurityException(USER_UNAUTHORIZED_MSG, e);
        }        
    }      
        
    /**
     * Removes the play.
     *
     * @param id the id of the play to remove
     * @param authorizationBearer the authorization bearer
     * @throws Exception the exception
     */
    public void removePlay(long id, String authorizationBearer) throws Exception {     
        try {
            long authenticatedId = AuthenticationFilter.getAuthIdFromBearer(authorizationBearer);
            
            Play play = findById(id);
            if (play == null)
            	throw new IllegalArgumentException("The play to remove doesn't exist!");
            
            // Verify if the id of authenticated user corresponds to the id of the user creator of play
            if (authenticatedId != play.getUserCreator().getId())
                throw new SecurityException(USER_UNAUTHORIZED_MSG);
            
            MyEntityManager.getInstance().removeEntity(Play.class, id);
        } catch (Exception e) {
            throw new SecurityException(USER_UNAUTHORIZED_MSG, e);
        }
    }
    
    /**
     * Find by id.
     *
     * @param id the id of the play to find
     * @return the play found
     */
    public Play findById(long id) {
        return MyEntityManager.getInstance().findEntity(Play.class, id);
    }
    
    /**
     * Find plays by user.
     *
     * @param userCreatorId the user creator id
     * @param filtersString the list of filters string
     * @param ordersString the list of orders string
     * @return the list of plays
     * @throws Exception the exception
     */
    public List<Play> findPlaysByUser(long userCreatorId, List<String> filtersString, List<String> ordersString) throws Exception {
        if (filtersString == null)
        	filtersString = new ArrayList<>();
        
        filtersString.add("userCreator_id@"+userCreatorId);
    	
    	return MyEntityManager.getInstance().findAllEntities(Play.class,
       		 filtersString, ordersString, Play.FilterBy.class, Play.OrderBy.class);
   }   
}
