package com.herokuapp.polimiboardgamemanager.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.Calendar;
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
import com.herokuapp.polimiboardgamemanager.model.Play;
import com.herokuapp.polimiboardgamemanager.model.User;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PlayResourceTest extends JerseyTest {
	
	private static final long USER_CREATOR_ID = 1;
	private static final String USER_CREATOR_USERNAME = "albert";
	private static final long BOARDGAME_ID = 4;
	private static final Calendar PLAY_DATE = Calendar.getInstance();
	
    private static final String TARGET = "/users/" + USER_CREATOR_ID + "/plays/";

    @Override
    protected Application configure() {
        return new ResourceConfig(PlayResource.class, UserResource.class, BoardGamesResource.class, BoardGameResource.class);
    }
    
    @Test
    public void t1_createPlayFail() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("t1_createBoardGameFail");
    	
        Response loginResponse = login("bob", "bob");
        String authorizationBearer = loginResponse.getHeaderString(HttpHeaders.AUTHORIZATION);
        
        System.out.println("Authorization: "+authorizationBearer);
        
        User userCreator = target("/users/"+USER_CREATOR_ID).request().get(User.class);
        BoardGame board = target("/boardgames/"+BOARDGAME_ID).request().get(BoardGame.class);
        
        Play play = new Play(userCreator, board, PLAY_DATE);
        Response response = target(TARGET).request().
        		header(HttpHeaders.AUTHORIZATION, authorizationBearer).
        		post(Entity.entity(play, MediaType.APPLICATION_JSON_TYPE));
        
        // The creation should fail because the authenticated user bob is not the creator of the play
        assertEquals(Response.Status.UNAUTHORIZED, Response.Status.fromStatusCode(response.getStatus()));
    }    
    
    @Test
    public void t2_createPlaySuccess() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("t2_createPlaySuccess");
    	
        Response loginResponse = login(USER_CREATOR_USERNAME, USER_CREATOR_USERNAME);
        String authorizationBearer = loginResponse.getHeaderString(HttpHeaders.AUTHORIZATION);
        
        System.out.println("Authorization: "+authorizationBearer);
        
        User userCreator = target("/users/"+USER_CREATOR_ID).request().get(User.class);
        BoardGame board = target("/boardgames/"+BOARDGAME_ID).request().get(BoardGame.class);
        
        Play play = new Play(userCreator, board, PLAY_DATE);
        Response response = target(TARGET).request().
        		header(HttpHeaders.AUTHORIZATION, authorizationBearer).
        		post(Entity.entity(play, MediaType.APPLICATION_JSON_TYPE));
        
        // The creation should fail because the authenticated user bob is not the creator of the play
        assertEquals(Response.Status.UNAUTHORIZED, Response.Status.fromStatusCode(response.getStatus()));
    }     


    @Test
    public void t3_findAllBoardGames() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("t3_findAllBoardGames");
        List<Play> allPlays = getAllPlays();
        
        
        System.out.println(allBoards.get(0).toString());
        
        for (BoardGame board: allBoards) {
        
            long id = board.getId();
            String name = board.getName();
            
            System.out.println("ID: "+id+"; name: "+name);
        
        }

        assertNotNull(allBoards);
    }
    
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
    
    @Test
    public void t4_boardGamesCount() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("t4_boardGamesCount");        
        final long count = target("/boardgames").path("count").request().get(Long.class);
        
        System.out.println("boardgames count: "+String.valueOf(count));
            
        assertTrue(count >= 0);
    }  
    

    
    @Test
    public void t6_removeBoardGameFail() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("t6_removeBoardGameFail");        	
    	
        Response loginResponse = login("bob", "bob");
        String authorizationBearer = loginResponse.getHeaderString(HttpHeaders.AUTHORIZATION);
        
        // To get the id of the new user created before I have to scan the full name of the users
        List<BoardGame> allPlays = getAllPlays();
        long id = 0;
        for (Play pl: allPlays)
            if (bg.getName().equals(NEW_NAME))
                id = bg.getId();
        
        System.out.println("Board game to delete: "+id);
        
        System.out.println("Authorization: "+authorizationBearer);
        
        Response response = target(TARGET).path("/"+id).request(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION,  authorizationBearer).delete();
        
        // The removal should fail because bob is not a power user
        
        assertEquals(Response.Status.UNAUTHORIZED, Response.Status.fromStatusCode(response.getStatus()));
    }     
    
    @Test
    public void t7_removeBoardGame() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("t7_removeBoardGame");        	
    	
        Response loginResponse = login(ADMIN_USERNAME, ADMIN_USERNAME);
        String authorizationBearer = loginResponse.getHeaderString(HttpHeaders.AUTHORIZATION);
        
        // To get the id of the new user created before I have to scan the full name of the users
        List<BoardGame> allBoards = getAllBoards();
        long id = 0;
        for (BoardGame bg: allBoards)
            if (bg.getName().equals(NEW_NAME))
                id = bg.getId();
        
        System.out.println("Board game to delete: "+id);
        
        System.out.println("Authorization: "+authorizationBearer);
        
        Response response = target(TARGET).path("/"+id).request(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION,  authorizationBearer).delete();
        
        System.out.println("Response link: "+response.getLink("parent"));
        
        assertEquals(Response.Status.NO_CONTENT, Response.Status.fromStatusCode(response.getStatus()));
    }    
    
    
    private Response login(String username, String password) {
        Form form = new Form();
        // Here I assume that there is a test user with username=bob and password=bob
        form.param("username", username);
        form.param("password", password);
        return target("users/login").request().post(Entity.form(form));
    }   
    
    private List<Play> getAllPlays() {
    	return target(TARGET).request(MediaType.APPLICATION_JSON_TYPE).get(new GenericType<List<Play>>() {});
    }
}