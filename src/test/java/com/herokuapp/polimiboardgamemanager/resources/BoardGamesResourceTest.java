package com.herokuapp.polimiboardgamemanager.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import com.herokuapp.polimiboardgamemanager.model.BoardGame;

public class BoardGamesResourceTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(BoardGamesResource.class);
    }


    @Test
    public void testGetAllBoardGames() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("testGetAllBoardGames");
        List<BoardGame> allBoards = target("/boardgames").request(MediaType.APPLICATION_XML).get(new GenericType<List<BoardGame>>() {});
        
        
        System.out.println(allBoards.get(0).toString());
        
        for (BoardGame board: allBoards) {
        
            long id = board.getId();
            String name = board.getName();
            
            System.out.println("ID: "+id+"; name: "+name);
        
        }

        assertNotNull(allBoards);
    }
    
    @Test
    public void testGetBoardGame4() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("testGetBoardGame4");
        final BoardGame board = target().path("boardgames/4").request().get(BoardGame.class);
        
        long id = board.getId();
        String name = board.getName();
        
        System.out.println("ID: "+id+"; name: "+name);
        
        assertEquals(4, id);
        assertEquals("boardgame1", name);
    }
    
    @Test
    public void testGetBoardGame4Json() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("testGetBoardGame4Json");        
        final String boardJson = target().path("boardgames/4").request(MediaType.APPLICATION_JSON).get(String.class);
        
        System.out.println(boardJson);
        
        assertEquals(1, 1);
    }    
    
    @Test
    public void testBoardGameLinks() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("testGetBoardGameLinks");        
        final BoardGame board = target().path("boardgames/4").request().get(BoardGame.class);
            
        List<Link> links = board.getLinks();

        for (Link link: links) {
            System.out.printf(
                "link relation uri=%s, rel=%s \n",
                link.getUri(), link.getRel());
            
            if (link.getRel().equals("self")) {
                assertEquals("https://polimi-board-game-manager.herokuapp.com/boardgames/4",
                             link.getUri().toString());
            } else if (link.getRel().equals("parent")) {
                assertEquals("https://polimi-board-game-manager.herokuapp.com/boardgames/",
                             link.getUri().toString());
            }
        }
    }
    
    @Test
    public void testBoardGameCount() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("testBoardGameCount");        
        final long count = target("/boardgames").path("count").request().get(Long.class);
        
        System.out.println("boardgames count: "+String.valueOf(count));
            
        assertTrue(count >= 0);
    }    
}
