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
import com.herokuapp.polimiboardgamemanager.util.InputValidator;

/**
 * The Class BoardGamesResource.
 */
@Path("/boardgames")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
public class BoardGamesResource {

    // ======================================
    // =          Injection Points          =
    // ======================================
    
    /** The uri info. */
    @Context
    UriInfo uriInfo;
    
    /** The request. */
    @Context
    Request request;
    
    // ======================================
    // =          POST requests             =
    // ======================================    
    
    /**
     * New board game form.
     *
     * @param name the name
     * @param designers the designers
     * @param cover the cover
     * @param authorizationBearer the authorization bearer
     * @return the response
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @POST
    @Secured
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response newBoardGameForm(@FormParam("name") String name,
                                     @FormParam("designers") String designers,
                                     @FormParam("cover") String cover,
                                     @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationBearer)
                                    throws IOException {
    	
    	if (! InputValidator.isValidGenericInput(name) || 
    		! InputValidator.isValidGenericInput(designers) ||
    		! InputValidator.isValidUrl(cover) ) {
    		
    		String response = InputValidator.INVALID_INPUT_MSG + "\n" + 
    						  "The name must match this regex of allowed characters: " +
    						  InputValidator.GENERIC_INPUT_ALLOWED_CHARACTERS + "\n" +
    						  "The designers must match this regex of allowed characters: " +
    						  InputValidator.GENERIC_INPUT_ALLOWED_CHARACTERS + "\n" +
    						  "The url must be valid.";
    		
    		return Response.status(Response.Status.BAD_REQUEST).type(MediaType.TEXT_XML).entity(response).build();
    	}  
        
        try {
            long id = BoardGameDao.getInstance().createBoardGame(name, designers, cover, authorizationBearer);
            return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(id)).build()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
    
    /**
     * New board game app.
     *
     * @param board the board
     * @param authorizationBearer the authorization bearer
     * @return the response
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @POST
    @Secured
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response newBoardGameApp(BoardGame board,
                                    @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationBearer)
                                   throws IOException {
    	
    	if (! InputValidator.isValidGenericInput(board.getName()) || 
    		! InputValidator.isValidGenericInput(board.getDesigners()) ||
    		! InputValidator.isValidUrl(board.getCover()) ) {
    		
    		String response = InputValidator.INVALID_INPUT_MSG + "\n" + 
    						  "The name must match this regex of allowed characters: " +
    						  InputValidator.GENERIC_INPUT_ALLOWED_CHARACTERS + "\n" +
    						  "The designers must match this regex of allowed characters: " +
    						  InputValidator.GENERIC_INPUT_ALLOWED_CHARACTERS + "\n" +
    						  "The url must be valid.";
    		
    		return Response.status(Response.Status.BAD_REQUEST).type(MediaType.TEXT_XML).entity(response).build();
    	}      	
        
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

    /**
     * It receives the GET requests sent to "/boardgames" and returns
     * all the board games existing in the database, sorted and filtered
     * with the desired criteria.
     * @param filter is a list of couples "filterName@filterValue", where filterName can be
     *        only one of
     *        {@link com.herokuapp.polimiboardgamemanager.model.BoardGame#FilterBy BoardGame.FilterBy}
     *        and filterValue is the string representation of the desired value for the user
     *        attribute to filter 
     * @param order is a list of couples "orderBy@orderMode", where orderBy can be
     *        only one of
     *        {@link com.herokuapp.polimiboardgamemanager.model.BoardGame#OrderBy BoardGame.OrderBy}
     *        and orderMode can be only one of
     *        {@link com.herokuapp.polimiboardgamemanager.model.MyEntityManager#OrderMode MyEntityManager.OrderMode}
     * @return <b>200 OK</b> if there are no errors in the parameters,<br />
     *         <b>400 Bad request</b> if the parameters are not correct.
     */
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

    /**
     * Gets the count of existing board games.
     *
     * @return the count
     */
    @GET
    @Path("/count")
    @Produces({MediaType.TEXT_PLAIN, MediaType.TEXT_XML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public long getCount() {
        return BoardGameDao.getInstance().getBoardGamesCount();
    }
    
    // ======================================
    // =     Single board game requests     =
    // ======================================       

    /**
     * Gets the board.
     *
     * @param id the id
     * @return the board
     */
    @Path("/{id}")
    public BoardGameResource getBoard(@PathParam("id") Long id) {
        return new BoardGameResource(uriInfo, request, id);
    }

}