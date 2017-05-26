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
import javax.ws.rs.QueryParam;
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

@Path("/boardgames")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
public class BoardGamesResource {

    // ======================================
    // =          Injection Points          =
    // ======================================
    
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    
    // ======================================
    // =          POST requests             =
    // ======================================    
    
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
    
    // ======================================
    // =          GET requests              =
    // ======================================

    @GET
    public Response findAllBoardGames(@QueryParam("filter") final List<String> filter,
    								  @QueryParam("order") final List<String> order) {

        try {
            GenericEntity<List<BoardGame>> list = new GenericEntity<List<BoardGame>>(
            		BoardGameDao.getInstance().findAllBoardGames(filter, order)){};
            return Response.ok(list).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    // returns the number of board games
    @GET
    @Path("/count")
    @Produces({MediaType.TEXT_PLAIN, MediaType.TEXT_XML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public long getCount() {
        return BoardGameDao.getInstance().getBoardGamesCount();
    }
    
    // ======================================
    // =     Single board game requests     =
    // ======================================       

    @Path("/{id}")
    public BoardGameResource getBoard(@PathParam("id") Long id) {
        return new BoardGameResource(uriInfo, request, id);
    }

}