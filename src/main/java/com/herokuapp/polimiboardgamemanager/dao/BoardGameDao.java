package com.herokuapp.polimiboardgamemanager.dao;

import java.util.HashMap;
import java.util.Map;

import com.herokuapp.polimiboardgamemanager.model.BoardGame;

public enum BoardGameDao {
    instance;

    private Map<String, BoardGame> contentProvider = new HashMap<String, BoardGame>();

    private BoardGameDao() {

        BoardGame todo = new BoardGame("1", "Learn REST");
        todo.setDescription("Read http://www.vogella.com/tutorials/REST/article.html");
        contentProvider.put("1", todo);
        todo = new BoardGame("2", "Do something");
        todo.setDescription("Read complete http://www.vogella.com");
        contentProvider.put("2", todo);

    }
    
    public Map<String, BoardGame> getModel(){
            return contentProvider;
    }

}