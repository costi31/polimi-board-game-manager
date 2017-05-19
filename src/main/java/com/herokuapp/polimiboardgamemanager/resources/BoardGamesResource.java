package com.herokuapp.polimiboardgamemanager.resources;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.herokuapp.polimiboardgamemanager.dao.BoardGameDao;
import com.herokuapp.polimiboardgamemanager.filter.Secured;
import com.herokuapp.polimiboardgamemanager.model.BoardGame;

// Will map the resource to the URL boardgames
@Path("/boardgames")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
public class BoardGamesResource {

    // Allows to insert contextual objects into the class,
    // e.g. ServletContext, Request, Response, UriInfo
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    // Return the list of board games for applications
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response getBoards() {
        GenericEntity<List<BoardGame>> list = new GenericEntity<List<BoardGame>>(BoardGameDao.getInstance().getAllBoardGames()){};
        return Response.ok(list).build();
    }
    
    // Return the list of board games to the user in the browser
    @GET
    @Produces(MediaType.TEXT_XML)
    public Response getBoardsBrowser() {
        GenericEntity<List<BoardGame>> list = new GenericEntity<List<BoardGame>>(BoardGameDao.getInstance().getAllBoardGames()){};
        return Response.ok(list).build();
    }  

    // returns the number of board games
    @GET
    @Path("/count")
    @Produces({MediaType.TEXT_PLAIN, MediaType.TEXT_XML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public long getCount() {
        return BoardGameDao.getInstance().getBoardGamesCount();
    }

    @POST
    @Secured
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response newBoardGameForm(@FormParam("name") String name,
                                     @FormParam("designers") String designers,
                                     @FormParam("cover") String cover,
                                     @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationBearer)
                                    throws IOException {
        
        try {
            long id = BoardGameDao.getInstance().createBoardGame(name, designers, cover, authorizationBearer);
            return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(id)).build()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
    
    @POST
    @Secured
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response newBoardGameApp(BoardGame board,
                                    @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationBearer)
                                   throws IOException {
        
        try {
            long id = BoardGameDao.getInstance().createBoardGame(board, authorizationBearer);
            return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(id)).build()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
    

    @Path("/{board_id}")
    public BoardGameResource getBoard(@PathParam("board_id") Long id) {
        return new BoardGameResource(uriInfo, request, id);
    }

}