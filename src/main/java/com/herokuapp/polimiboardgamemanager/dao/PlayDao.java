package com.herokuapp.polimiboardgamemanager.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.herokuapp.polimiboardgamemanager.filter.AuthenticationFilter;
import com.herokuapp.polimiboardgamemanager.model.Play;

public class PlayDao {
    
    private static final Logger LOGGER = LogManager.getLogger(PlayDao.class);
    private static final String USER_UNAUTHORIZED_MSG = "User unauthorized to do this operation!";
    private static final String PLAY_ID_EXISTS_MSG = "A play with desired id already exists!";
    
    private static PlayDao instance = null;
        
    /**
     * Gets the instance of BoardGameDao
     * @return instance of BoardGameDao
     */
    public static PlayDao getInstance() {
        if (instance == null)
            instance = new PlayDao();

        return instance;
    }

    private PlayDao() {
    }
    
    public long createPlay(Play play, String authorizationBearer) throws Exception {
        try {
        	long authenticatedId = AuthenticationFilter.getAuthIdFromBearer(authorizationBearer);
            
            // Verify if the id of authenticated user corresponds to the id of the user creator of play
            if (authenticatedId != play.getUserCreator().getId())
                throw new SecurityException(USER_UNAUTHORIZED_MSG);
            
            Long id = play.getId();
            
            if (id != null && findById(id) != null) // if already exists a play with specified id
            	throw new IllegalArgumentException(PLAY_ID_EXISTS_MSG);
            
            play = (Play)MyEntityManager.getInstance().mergeEntity(play);
            return play.getId();
        } catch (Exception e) {
            throw new SecurityException(USER_UNAUTHORIZED_MSG);
        }        
    }  
    
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
            throw new SecurityException(USER_UNAUTHORIZED_MSG);
        }        
    }      
        
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
            throw new SecurityException(USER_UNAUTHORIZED_MSG);
        }
    }
    
    public Play findById(long id) {
        return (Play) MyEntityManager.getInstance().findEntity(Play.class, id);
    }
    
    public List<Play> findPlaysByUser(long userCreatorId, List<String> filtersString, List<String> ordersString) throws Exception {
        if (filtersString == null)
        	filtersString = new ArrayList<>();
        
        filtersString.add("userCreator_id@"+userCreatorId);
    	
    	return MyEntityManager.getInstance().findAllEntities(Play.class,
       		 filtersString, ordersString, Play.FilterBy.class, Play.OrderBy.class);
   }   
}
