package com.herokuapp.polimiboardgamemanager.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.herokuapp.polimiboardgamemanager.model.User;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserResourceTest extends JerseyTest {
    
    private static final String TARGET = "/users";
    
    private static final String NEW_USERNAME = "cody";
    private static final String NEW_FULLNAME = "Cody Test";

    @Override
    protected Application configure() {
        return new ResourceConfig(UserResource.class);
    }


    @Test
    public void t1_getAllUsers() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("t1_getAllUsers");
        List<User> allUsers = getAllUsers();
        
        System.out.println(allUsers.get(0).toString());
        
        for (User user: allUsers) {
        
            long id = user.getId();
            String fullName = user.getFullName();
            
            System.out.println("ID: "+id+"; fullName: "+fullName);
        
        }

        assertNotNull(allUsers);
    }
    
    @Test
    public void t2_getUsersCount() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("t2_getUsersCount");
        long count = target(TARGET).path("/count").request(MediaType.TEXT_PLAIN).get(Long.class);
        System.out.println("Total existing users: "+String.valueOf(count));
        assertTrue(count > 0);
    }
    
    @Test
    public void t3_createUser() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("t3_createUser");
               
        Form form = new Form();
        // Here I assume that there is a test user with username=bob and password=bob
        form.param("fullName", NEW_FULLNAME);
        form.param("username", NEW_USERNAME);
        form.param("password", NEW_USERNAME);
        Response response = target(TARGET).request().post(Entity.form(form));
        
        URI location = response.getLocation();
        System.out.print(location);

        assertEquals(Response.Status.CREATED, Response.Status.fromStatusCode(response.getStatus()));
    }
       
    @Test
    public void t4_loginFail() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("t4_loginFail");
        
        Response response = login(NEW_USERNAME, "abc");
        
        assertEquals(Response.Status.UNAUTHORIZED, Response.Status.fromStatusCode(response.getStatus()));
    }
    
    @Test
    public void t5_loginSuccess() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("t5_loginSuccess");

        Response response = login(NEW_USERNAME, NEW_USERNAME);
        
        String authenticationBearer = response.getHeaderString(HttpHeaders.AUTHORIZATION);
        
        System.out.println(authenticationBearer);
        
        assertEquals(Response.Status.OK, Response.Status.fromStatusCode(response.getStatus()));
    }    
    
    @Test
    public void t6_getUser() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("t6_getUser");
        
        User bob = target(TARGET).path("/114").request().get(User.class);
        
        System.out.println(bob);
        
        assertNotNull(bob);
    }
    
    @Test
    public void t7_updateUser() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("t7_updateUser");
        
        Response loginResponse = login(NEW_USERNAME, NEW_USERNAME);
        String authorizationBearer = loginResponse.getHeaderString(HttpHeaders.AUTHORIZATION);
        
        // To get the id of the new user created before I have to scan the full name of the users
        List<User> allUsers = getAllUsers();
        long id = 0;
        for (User us: allUsers)
            if (us.getFullName().equals(NEW_FULLNAME))
                id = us.getId();
        
        System.out.println("User to update: "+id);
        
        System.out.println("Authorization: "+authorizationBearer);
        
        Form form = new Form();
        form.param("fullName", NEW_FULLNAME+"2");
        
        Response response = target(TARGET).path("/"+id).request().
                header(HttpHeaders.AUTHORIZATION, authorizationBearer).put(Entity.form(form));
        
        User updatedUser = target(TARGET).path("/"+id).request().get(User.class);
        System.out.println("User updated: ");
        System.out.println(updatedUser);
        
        assertEquals(Response.Status.NO_CONTENT, Response.Status.fromStatusCode(response.getStatus()));       
    }
    
    @Test
    public void t8_removeUser() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("t8_removeUser");
        
        Response loginResponse = login(NEW_USERNAME, NEW_USERNAME);
        String authorizationBearer = loginResponse.getHeaderString(HttpHeaders.AUTHORIZATION);
        
        // To get the id of the new user created before I have to scan the full name of the users
        List<User> allUsers = getAllUsers();
        long id = 0;
        for (User us: allUsers)
            if (us.getFullName().equals(NEW_FULLNAME+"2"))
                id = us.getId();
        
        System.out.println("User to delete: "+id);
        
        System.out.println("Authentication: "+authorizationBearer);
        
        Response response = target(TARGET).path("/"+id).request(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION,  authorizationBearer).delete();
        
        System.out.println("Response link: "+response.getLink("parent"));
        
        assertEquals(Response.Status.NO_CONTENT, Response.Status.fromStatusCode(response.getStatus()));
    }
    
//    @Test
//    public void testGetBoardGame4() {
//        System.out.println("----------------------------------------------------------------");
//        System.out.println("testGetBoardGame4");
//        final BoardGame board = target().path("boardgames/4").request().get(BoardGame.class);
//        
//        long id = board.getId();
//        String name = board.getName();
//        
//        System.out.println("ID: "+id+"; name: "+name);
//        
//        assertEquals(4, id);
//        assertEquals("boardgame1", name);
//    }
    
//    @Test
//    public void testGetBoardGame4Json() {
//        System.out.println("----------------------------------------------------------------");
//        System.out.println("testGetBoardGame4Json");        
//        final String boardJson = target().path("boardgames/4").request(MediaType.APPLICATION_JSON).get(String.class);
//        
//        System.out.println(boardJson);
//        
//        assertEquals(1, 1);
//    }    
    
//    @Test
//    public void testBoardGameLinks() {
//        System.out.println("----------------------------------------------------------------");
//        System.out.println("testGetBoardGameLinks");        
//        final BoardGame board = target().path("boardgames/4").request().get(BoardGame.class);
//            
//        List<Link> links = board.getLinks();
//
//        for (Link link: links) {
//            System.out.printf(
//                "link relation uri=%s, rel=%s \n",
//                link.getUri(), link.getRel());
//            
//            if (link.getRel().equals("self")) {
//                assertEquals("https://polimi-board-game-manager.herokuapp.com/boardgames/4",
//                             link.getUri().toString());
//            } else if (link.getRel().equals("parent")) {
//                assertEquals("https://polimi-board-game-manager.herokuapp.com/boardgames/",
//                             link.getUri().toString());
//            }
//        }
//    }
    
//    @Test
//    public void testBoardGameCount() {
//        System.out.println("----------------------------------------------------------------");
//        System.out.println("testBoardGameCount");        
//        final long count = target("/boardgames").path("count").request().get(Long.class);
//        
//        System.out.println("boardgames count: "+String.valueOf(count));
//            
//        assertTrue(count >= 0);
//    }    
    
    private Response login(String username, String password) {
        Form form = new Form();
        // Here I assume that there is a test user with username=bob and password=bob
        form.param("username", username);
        form.param("password", password);
        return target(TARGET+"/login").request().post(Entity.form(form));
    }
    
    private List<User> getAllUsers() {
        return target(TARGET).request(MediaType.APPLICATION_JSON).get(new GenericType<List<User>>() {});
    }
}
