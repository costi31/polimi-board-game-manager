package com.herokuapp.polimiboardgamemanager.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.herokuapp.polimiboardgamemanager.model.BoardGame;

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
    public void insertBoardGame(BoardGame board) {
        MyEntityManager.getInstance().persistEntity(board);
    }
    
    public void insertBoardGame(String name, String designers, String cover) {
        BoardGame board = new BoardGame(name, designers, cover);
        insertBoardGame(board);
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
    
    /**
     * Gets the list of all the existing board games
     * @return list of existing BoardGame(s)
     */
    public List<BoardGame> getAllBoardGames() {
        EntityManager em = MyEntityManager.getInstance().getEm();
        List<BoardGame> allBoards = (List<BoardGame>) em.createQuery("SELECT board FROM BoardGame board").getResultList();
        return allBoards;
    }

}
