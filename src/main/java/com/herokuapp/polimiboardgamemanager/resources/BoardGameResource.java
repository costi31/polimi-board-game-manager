package com.herokuapp.polimiboardgamemanager.resources;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import com.herokuapp.polimiboardgamemanager.dao.BoardGameDao;
import com.herokuapp.polimiboardgamemanager.model.BoardGame;


public class BoardGameResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    int id;
    public BoardGameResource(UriInfo uriInfo, Request request, int id) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.id = id;
    }

    //Application integration
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public BoardGame getBoardGame() {
        /*BoardGame board = BoardGameDao.instance.getModel().get(id);
        if(board==null)
                throw new RuntimeException("Get: BoardGame with " + id +  " not found");*/
        
        EntityManagerFactory emf = Persistence
                .createEntityManagerFactory("BoardGameManagerPU");
        EntityManager em = emf.createEntityManager();
        
        BoardGame board = null;
        
        try {
            board = em.find(BoardGame.class, id);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Get: BoardGame with " + id +  " not found");
        }
 
        return board;
    }

    // for the browser
    @GET
    @Produces(MediaType.TEXT_XML)
    public BoardGame getTodoHTML() {
        BoardGame board = BoardGameDao.instance.getModel().get(id);
        if(board==null)
            throw new RuntimeException("Get: BoardGame with " + id +  " not found");
        return board;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public Response putTodo(JAXBElement<BoardGame> board) {
        BoardGame c = board.getValue();
        return putAndGetResponse(c);
    }

    @DELETE
    public void deleteTodo() {
        BoardGame c = BoardGameDao.instance.getModel().remove(id);
        if(c==null)
            throw new RuntimeException("Delete: BoardGame with " + id +  " not found");
    }

    private Response putAndGetResponse(BoardGame board) {
        Response res;
        if(BoardGameDao.instance.getModel().containsKey(board.getId())) {
                res = Response.noContent().build();
        } else {
                res = Response.created(uriInfo.getAbsolutePath()).build();
        }
        //BoardGameDao.instance.getModel().put(board.getId(), board);
        return res;
    }
}
