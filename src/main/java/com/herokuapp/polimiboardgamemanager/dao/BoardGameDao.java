package com.herokuapp.polimiboardgamemanager.dao;

import java.util.HashMap;
import java.util.Map;

import com.herokuapp.polimiboardgamemanager.model.BoardGame;

public enum BoardGameDao {
    instance;

    private Map<Long, BoardGame> contentProvider = new HashMap<Long, BoardGame>();

    private BoardGameDao() {

        //BoardGame board = new BoardGame("game1", "a,b", "cover.jpg");
        //contentProvider.put(1, board);
        //board = new BoardGame("2", "Do something");
        //board.setDescription("Read complete http://www.vogella.com");
        //contentProvider.put("2", board);

    }
    
    public Map<Long, BoardGame> getModel(){
            return contentProvider;
    }

}