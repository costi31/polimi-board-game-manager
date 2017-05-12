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
    long id;
    public BoardGameResource(UriInfo uriInfo, Request request, long id) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.id = id;
    }

    //Application integration
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public BoardGame getBoardGame() {
        return BoardGameDao.getInstance().getBoardGame(id);
    }

    // for the browser
    @GET
    @Produces(MediaType.TEXT_XML)
    public BoardGame getBoardGameHTML() {
        return BoardGameDao.getInstance().getBoardGame(id);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public Response putTodo(JAXBElement<BoardGame> board) {
        BoardGame c = board.getValue();
        return putAndGetResponse(c);
    }

    @DELETE
    public void deleteBoardGame() {
        BoardGameDao.getInstance().deleteBoardGame(id);
    }

    private Response putAndGetResponse(BoardGame board) {
        Response res;
        if(BoardGameDao.getInstance().getBoardGame(board.getId()) != null) {
                res = Response.noContent().build();
        } else {
                res = Response.created(uriInfo.getAbsolutePath()).build();
                BoardGameDao.getInstance().insertBoardGame(board.getName(), board.getDesigners(), board.getCover());
        }

        return res;
    }
}
