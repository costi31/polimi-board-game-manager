package com.herokuapp.polimiboardgamemanager.dao;

import com.herokuapp.polimiboardgamemanager.model.Play;

public class PlayDao {
    
    private static PlayDao instance = null;
    
    /**
     * Separator between user id and username in the subject field of the token
     */
    public static final String SUBJECT_ID_SEPARATOR = "@";    
    
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
    
    public Play findById(long id) {
        return (Play) MyEntityManager.getInstance().findEntity(Play.class, id);
    }
}
