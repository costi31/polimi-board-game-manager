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

import com.herokuapp.polimiboardgamemanager.dao.BoardGameDao;
import com.herokuapp.polimiboardgamemanager.model.BoardGame;

// Will map the resource to the URL todos
@Path("/boardgames")
public class BoardGamesResource {

    // Allows to insert contextual objects into the class,
    // e.g. ServletContext, Request, Response, UriInfo
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    // Return the list of todos to the user in the browser
    @GET
    @Produces(MediaType.TEXT_HTML)
    public List<BoardGame> getTodosBrowser() {
            List<BoardGame> boards = new ArrayList<BoardGame>();
            boards.addAll(BoardGameDao.instance.getModel().values());
            return boards;
    }

    // Return the list of todos for applications
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public List<BoardGame> getTodos() {
            List<BoardGame> todos = new ArrayList<BoardGame>();
            todos.addAll(BoardGameDao.instance.getModel().values());
            return todos;
    }

    // retuns the number of boards
    // Use http://localhost:8080/com.vogella.jersey.todo/rest/todos/count
    // to get the total number of records
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
    public void newTodo(@FormParam("id") int id,
                        @FormParam("name") String name,
                        @FormParam("designers") String designers,
                        @FormParam("cover") String cover,
                        @Context HttpServletResponse servletResponse) throws IOException {
        BoardGame board = new BoardGame(id, name, designers, cover);
        BoardGameDao.instance.getModel().put(id, board);
        servletResponse.sendRedirect("../create_todo.html");
    }

    // Defines that the next path parameter after todos is
    // treated as a parameter and passed to the TodoResources
    // Allows to type http://localhost:8080/com.vogella.jersey.todo/rest/todos/1
    // 1 will be treaded as parameter todo and passed to TodoResource
    @Path("{todo}")
    public BoardGameResource getTodo(@PathParam("todo") int id) {
        return new BoardGameResource(uriInfo, request, id);
    }

}