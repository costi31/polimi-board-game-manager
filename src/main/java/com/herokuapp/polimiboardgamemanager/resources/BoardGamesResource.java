package com.herokuapp.polimiboardgamemanager.resources;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.herokuapp.polimiboardgamemanager.dao.BoardGameDao;
import com.herokuapp.polimiboardgamemanager.model.BoardGame;

// Will map the resource to the URL boardgames
@Path("/boardgames")
public class BoardGamesResource {

    // Allows to insert contextual objects into the class,
    // e.g. ServletContext, Request, Response, UriInfo
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    
    private EntityManager em = null;

    // Return the list of board games to the user in the browser
    @GET
    @Produces(MediaType.TEXT_XML)
    public List<BoardGame> getBoardsBrowser() {
        List<BoardGame> boards = new ArrayList<BoardGame>();
        boards.addAll(BoardGameDao.instance.getModel().values());
        return boards;
    }

    // Return the list of board games for applications
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public List<BoardGame> getBoards() {
        List<BoardGame> boards = new ArrayList<BoardGame>();
        boards.addAll(BoardGameDao.instance.getModel().values());
        return boards;
    }

    // returns the number of board games
    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String getCount() {
        int count = BoardGameDao.instance.getModel().size();
        return String.valueOf(count);
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void newTodo(@FormParam("name") String name,
                        @FormParam("designers") String designers,
                        @FormParam("cover") String cover,
                        @Context HttpServletResponse servletResponse) throws IOException {
        
        if (em == null) {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.herokuapp.polimiboardgamemanager");
            em = emf.createEntityManager();
        }
 
        em.getTransaction().begin();
        BoardGame board = new BoardGame (name, designers, cover);
        em.persist(board);
        em.getTransaction().commit();
        
        BoardGameDao.instance.getModel().put(board.getId(), board);
        servletResponse.sendRedirect("../create_boardgame.html");
    }
    
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public void newTodoApp(BoardGame b,
                        @Context HttpServletResponse servletResponse) throws IOException {
        
        if (em == null) {
            EntityManagerFactory emf = Persistence
                    .createEntityManagerFactory("BoardGameManagerPU");
            em = emf.createEntityManager();
        }
 
        em.getTransaction().begin();
        BoardGame board = new BoardGame (b.getName(), b.getDesigners(), b.getCover());
        em.persist(board);
        em.getTransaction().commit();
        
        BoardGameDao.instance.getModel().put(board.getId(), board);
        //servletResponse.sendRedirect("../create_boardgame.html");
    }
    

    @Path("{board}")
    public BoardGameResource getTodo(@PathParam("board") int id) {
        return new BoardGameResource(uriInfo, request, id);
    }

}