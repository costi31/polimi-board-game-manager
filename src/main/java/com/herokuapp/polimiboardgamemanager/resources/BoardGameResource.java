package com.herokuapp.polimiboardgamemanager.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import com.herokuapp.polimiboardgamemanager.dao.BoardGameDao;
import com.herokuapp.polimiboardgamemanager.model.BoardGame;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("BoardGame")
public class BoardGameResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    String id;
    public BoardGameResource(UriInfo uriInfo, Request request, String id) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.id = id;
    }

    //Application integration
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public BoardGame getBoardGame() {
        BoardGame todo = BoardGameDao.instance.getModel().get(id);
        if(todo==null)
                throw new RuntimeException("Get: Todo with " + id +  " not found");
        return todo;
    }

    // for the browser
    @GET
    @Produces(MediaType.TEXT_XML)
    public BoardGame getTodoHTML() {
        BoardGame todo = BoardGameDao.instance.getModel().get(id);
        if(todo==null)
                throw new RuntimeException("Get: Todo with " + id +  " not found");
        return todo;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public Response putTodo(JAXBElement<BoardGame> todo) {
        BoardGame c = todo.getValue();
        return putAndGetResponse(c);
    }

    @DELETE
    public void deleteTodo() {
        BoardGame c = BoardGameDao.instance.getModel().remove(id);
        if(c==null)
            throw new RuntimeException("Delete: Todo with " + id +  " not found");
    }

    private Response putAndGetResponse(BoardGame todo) {
        Response res;
        if(BoardGameDao.instance.getModel().containsKey(todo.getId())) {
                res = Response.noContent().build();
        } else {
                res = Response.created(uriInfo.getAbsolutePath()).build();
        }
        BoardGameDao.instance.getModel().put(todo.getId(), todo);
        return res;
    }
}
