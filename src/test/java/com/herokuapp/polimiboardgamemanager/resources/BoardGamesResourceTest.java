package com.herokuapp.polimiboardgamemanager.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.herokuapp.polimiboardgamemanager.model.BoardGame;

/**
 * The Class BoardGamesResourceTest.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BoardGamesResourceTest extends JerseyTest {
	
    /** The Constant TARGET. */
    private static final String TARGET = "/boardgames";
    
    /** The Constant ADMIN_USERNAME. */
    private static final String ADMIN_USERNAME = "albert";
    
    /** The Constant NEW_NAME. */
    private static final String NEW_NAME = "New board game";
    
    /** The Constant NEW_DESIGNERS. */
    private static final String NEW_DESIGNERS = "pinco, pallo";
    
    /** The Constant NEW_COVER. */
    private static final String NEW_COVER = "http://polimi-board-game-manager.herokuapp.com/New_board_game_cover.jpg";

    /** The new board game location. */
    private static URI newBoardGameLocation;
    
    /** The new board game id. */
    private static long newBoardGameId;
    
    /* (non-Javadoc)
     * @see org.glassfish.jersey.test.JerseyTest#configure()
     */
    @Override
    protected Application configure() {
        return new ResourceConfig(UserResource.class, BoardGamesResource.class, BoardGameResource.class);
    }


    /**
     * T 1 find all board games.
     */
    @Test
    public void t1_findAllBoardGames() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("t1_findAllBoardGames");
        List<BoardGame> allBoards = getAllBoards();
        
        
        System.out.println(allBoards.get(0).toString());
        
        for (BoardGame board: allBoards) {
        
            long id = board.getId();
            String name = board.getName();
            
            System.out.println("ID: "+id+"; name: "+name);
        
        }

        assertNotNull(allBoards);
    }
    
    /**
     * T 2 get board game 4.
     */
    @Test
    public void t2_getBoardGame4() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("t2_getBoardGame4");
        final BoardGame board = target().path("boardgames/4").request().get(BoardGame.class);
        
        long id = board.getId();
        String name = board.getName();
        
        System.out.println("ID: "+id+"; name: "+name);
        
        assertEquals(4, id);
        assertEquals("boardgame1", name);
    }
      
    
    /**
     * T 3 get board game links.
     */
    @Test
    public void t3_getBoardGameLinks() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("t3_getBoardGameLinks");        
        final BoardGame board = target().path("boardgames/4").request().get(BoardGame.class);
            
        Collection<Link> links = board.getLinksCollection();

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
    
    /**
     * T 4 board games count.
     */
    @Test
    public void t4_boardGamesCount() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("t4_boardGamesCount");        
        final long count = target("/boardgames").path("count").request().get(Long.class);
        
        System.out.println("boardgames count: "+String.valueOf(count));
            
        assertTrue(count >= 0);
    }  
    
    /**
     * T 5 create board game fail.
     */
    @Test
    public void t5_createBoardGameFail() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("t5_createBoardGameFail");
    	
        Response loginResponse = login("bob", "bob");
        String authorizationBearer = loginResponse.getHeaderString(HttpHeaders.AUTHORIZATION);
        
        System.out.println("Authorization: "+authorizationBearer);
        
        BoardGame board = new BoardGame(NEW_NAME, NEW_DESIGNERS, NEW_COVER);
        Response response = target(TARGET).request().
        		header(HttpHeaders.AUTHORIZATION, authorizationBearer).
        		post(Entity.entity(board, MediaType.APPLICATION_JSON_TYPE));
        
        // The creation should fail because bob is not a power user

        assertEquals(Response.Status.UNAUTHORIZED, Response.Status.fromStatusCode(response.getStatus()));
    }    
    
    /**
     * T 6 create board game post.
     */
    @Test
    public void t6_createBoardGamePost() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("t6_createBoardGamePost");        	
    	
        Response loginResponse = login(ADMIN_USERNAME, ADMIN_USERNAME);
        String authorizationBearer = loginResponse.getHeaderString(HttpHeaders.AUTHORIZATION);
        
        System.out.println("Authorization: "+authorizationBearer);
        
        BoardGame board = new BoardGame(NEW_NAME, NEW_DESIGNERS, NEW_COVER);
        Response response = target(TARGET).request().
        		header(HttpHeaders.AUTHORIZATION, authorizationBearer).
        		post(Entity.entity(board, MediaType.APPLICATION_JSON_TYPE));
        
        newBoardGameLocation = response.getLocation();
        String path = newBoardGameLocation.getPath();
        newBoardGameId = Long.parseLong( path.substring(path.lastIndexOf('/')+1) );

        assertEquals(Response.Status.CREATED, Response.Status.fromStatusCode(response.getStatus()));
    }
    
    /**
     * T 6 remove board game fail.
     */
    @Test
    public void t6_removeBoardGameFail() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("t6_removeBoardGameFail");        	
    	
        Response loginResponse = login("bob", "bob");
        String authorizationBearer = loginResponse.getHeaderString(HttpHeaders.AUTHORIZATION);
               
        System.out.println("Board game to delete: "+newBoardGameId);
        
        System.out.println("Authorization: "+authorizationBearer);
        
        Response response = target(newBoardGameLocation.getPath()).request(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION,  authorizationBearer).delete();
        
        // The removal should fail because bob is not a power user
        
        assertEquals(Response.Status.UNAUTHORIZED, Response.Status.fromStatusCode(response.getStatus()));
    }     
    
    /**
     * T 7 remove board game.
     */
    @Test
    public void t7_removeBoardGame() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("t7_removeBoardGame");        	
    	
        Response loginResponse = login(ADMIN_USERNAME, ADMIN_USERNAME);
        String authorizationBearer = loginResponse.getHeaderString(HttpHeaders.AUTHORIZATION);
                
        System.out.println("Board game to delete: "+newBoardGameId);
        
        System.out.println("Authorization: "+authorizationBearer);
        
        Response response = target(newBoardGameLocation.getPath()).request(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION,  authorizationBearer).delete();
        
        System.out.println("Response link: "+response.getLink("parent"));
        
        assertEquals(Response.Status.NO_CONTENT, Response.Status.fromStatusCode(response.getStatus()));
    }    
    
    
    /**
     * Login.
     *
     * @param username the username
     * @param password the password
     * @return the response
     */
    private Response login(String username, String password) {
        Form form = new Form();
        // Here I assume that there is a test user with username=bob and password=bob
        form.param("username", username);
        form.param("password", password);
        return target("users/login").request().post(Entity.form(form));
    }   
    
    /**
     * Gets the all boards.
     *
     * @return the all boards
     */
    private List<BoardGame> getAllBoards() {
    	return target(TARGET).request(MediaType.APPLICATION_JSON_TYPE).get(new GenericType<List<BoardGame>>() {});
    }
}
