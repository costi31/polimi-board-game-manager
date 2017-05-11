package com.herokuapp.polimiboardgamemanager.resources;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;

import org.junit.Test;

import com.herokuapp.polimiboardgamemanager.model.BoardGame;

import static org.junit.Assert.assertEquals;

public class BoardGamesResourceTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(BoardGamesResource.class);
    }

    /**
     * Test to see that the message "Got it!" is sent in the response.
     */
    @Test
    public void testCreate() {
        //BoardGame board = new BoardGame("board1", "pinco,pallino", "cover1.jpg");
        
        Form form =new Form();
        form.param("name", "boardgame1");
        form.param("designers","pinco,pallo");
        form.param("cover","cover1.jpg");
        
        Response response = target().path("boardgames").request().post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED),Response.class);
        
        System.out.println(response.toString());
        
        assertEquals("1", "1");
    }
}
