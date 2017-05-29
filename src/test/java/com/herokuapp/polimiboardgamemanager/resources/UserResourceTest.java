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
    
    private static URI newUserLocation;
    private static long newUserId;    

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
            System.out.println(user);        
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
        
        newUserLocation = response.getLocation();
        System.out.print(newUserLocation);
        String path = newUserLocation.getPath();
        newUserId = Long.parseLong( path.substring(path.lastIndexOf('/')+1) );

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
        
        User user = target(newUserLocation.getPath()).request().get(User.class);
        
        System.out.println(user);
        
        assertNotNull(user);
    }
    
    @Test
    public void t7_updateUser() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("t7_updateUser");
        
        Response loginResponse = login(NEW_USERNAME, NEW_USERNAME);
        String authorizationBearer = loginResponse.getHeaderString(HttpHeaders.AUTHORIZATION);
                
        System.out.println("User to update: "+newUserId);
        
        System.out.println("Authorization: "+authorizationBearer);
        
        Form form = new Form();
        form.param("fullName", NEW_FULLNAME+"2");
        
        Response response = target(newUserLocation.getPath()).request().
                header(HttpHeaders.AUTHORIZATION, authorizationBearer).put(Entity.form(form));
        
        User updatedUser = target(newUserLocation.getPath()).request().get(User.class);
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
        
        System.out.println("User to delete: "+newUserId);
        
        System.out.println("Authentication: "+authorizationBearer);
        
        Response response = target(newUserLocation.getPath()).request(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION,  authorizationBearer).delete();
        
        System.out.println("Response link: "+response.getLink("parent"));
        
        assertEquals(Response.Status.NO_CONTENT, Response.Status.fromStatusCode(response.getStatus()));
    }  
    
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
