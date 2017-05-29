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

/**
 * The Class PlayResourceTest.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PlayResourceTest extends JerseyTest {
	
	/** The Constant USER_CREATOR_ID. */
	private static final long USER_CREATOR_ID = 1;
	
	/** The Constant USER_CREATOR_USERNAME. */
	private static final String USER_CREATOR_USERNAME = "albert";
	
	/** The Constant BOARDGAME_ID. */
	private static final long BOARDGAME_ID = 4;
	
	/** The Constant PLAY_DATE. */
	private static final Calendar PLAY_DATE = Calendar.getInstance();
	
    /** The Constant TARGET. */
    private static final String TARGET = "/users/" + USER_CREATOR_ID + "/plays/";
    
    /** The new play location. */
    private static URI newPlayLocation;
    
    /** The new play id. */
    private static long newPlayId;

    /* (non-Javadoc)
     * @see org.glassfish.jersey.test.JerseyTest#configure()
     */
    @Override
    protected Application configure() {
        return new ResourceConfig(PlayResource.class, UserResource.class, BoardGamesResource.class, BoardGameResource.class);
    }
    
    /**
     * T 1 create play fail.
     */
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
    
    /**
     * T 2 create play success.
     */
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
        
        newPlayLocation = response.getLocation();
        String path = newPlayLocation.getPath();
        newPlayId = Long.parseLong( path.substring(path.lastIndexOf('/')+1) );
        
        // The creation should succeed because the owner of the play is authenticated
        assertEquals(Response.Status.CREATED, Response.Status.fromStatusCode(response.getStatus()));
    }     


    /**
     * T 3 find all plays.
     */
    @Test
    public void t3_findAllPlays() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("t3_findAllPlays");
        List<Play> allPlays = getAllPlays();
        
        
        System.out.println(allPlays.get(0).toString());
        
        for (Play play: allPlays) {
        
            System.out.println(play);
        
        }

        assertNotNull(allPlays);
    }
    
    /**
     * T 4 get play.
     */
    @Test
    public void t4_getPlay() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("t4_getPlay");

        Play newPlay = target(newPlayLocation.getPath()).request(MediaType.APPLICATION_JSON_TYPE).get(Play.class);
        
        System.out.println(newPlay);
        
        assertNotNull(newPlay);
    }
    
    /**
     * T 5 remove play fail.
     */
    @Test
    public void t5_removePlayFail() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("t5_removePlayFail");        	
    	
        Response loginResponse = login("bob", "bob");
        String authorizationBearer = loginResponse.getHeaderString(HttpHeaders.AUTHORIZATION);
        
        System.out.println("Play to delete: "+newPlayId);
        
        System.out.println("Authorization: "+authorizationBearer);
        
        Response response = target(newPlayLocation.getPath()).request(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION,  authorizationBearer).delete();
        
        // The removal should fail because bob is not the owner
        
        assertEquals(Response.Status.UNAUTHORIZED, Response.Status.fromStatusCode(response.getStatus()));
    }     
    
    /**
     * T 6 remove play success.
     */
    @Test
    public void t6_removePlaySuccess() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("t6_removePlaySuccess");        	
    	
        Response loginResponse = login(USER_CREATOR_USERNAME, USER_CREATOR_USERNAME);
        String authorizationBearer = loginResponse.getHeaderString(HttpHeaders.AUTHORIZATION);
        
        System.out.println("Play to delete: "+newPlayId);
        
        System.out.println("Authorization: "+authorizationBearer);
        
        Response response = target(newPlayLocation.getPath()).request(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION,  authorizationBearer).delete();
        
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
     * Gets the all plays.
     *
     * @return the all plays
     */
    private List<Play> getAllPlays() {
    	return target(TARGET).request(MediaType.APPLICATION_JSON_TYPE).get(new GenericType<List<Play>>() {});
    }
}
