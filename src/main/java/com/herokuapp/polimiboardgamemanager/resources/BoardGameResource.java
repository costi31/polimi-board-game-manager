package com.herokuapp.polimiboardgamemanager.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.herokuapp.polimiboardgamemanager.dao.BoardGameDao;
import com.herokuapp.polimiboardgamemanager.filter.Secured;
import com.herokuapp.polimiboardgamemanager.model.BoardGame;
import com.herokuapp.polimiboardgamemanager.util.InputValidator;

@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
public class BoardGameResource {
	
    // ======================================
    // =          Injection Points          =
    // ======================================   
	
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    private long id;
    
    public BoardGameResource(UriInfo uriInfo, Request request, long id) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.id = id;
    }
    
    // ======================================
    // =          GET requests              =
    // ======================================

    @GET
    public Response getBoardGame() {
        BoardGame board = BoardGameDao.getInstance().findById(id);

        if (board == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        return Response.ok(board).links(board.getLinksArray()).build();
    }

    // ======================================
    // =          PUT requests              =
    // ======================================    
    
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Secured
    public Response putBoardGame(BoardGame board,
    							 @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationBearer) {
    	
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
            if (BoardGameDao.getInstance().findById(id) != null) {
            	BoardGameDao.getInstance().updateBoardGame(id, board, authorizationBearer);
                return Response.noContent().build();
            } else {
            	BoardGameDao.getInstance().createBoardGame(board, authorizationBearer);
                return Response.created(uriInfo.getAbsolutePathBuilder().build()).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } 
    }
    
    // ======================================
    // =          DELETE requests           =
    // ======================================
    
    @DELETE
    @Secured
    public Response remove(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationBearer) {
        try {
            BoardGameDao.getInstance().removeBoardGame(id, authorizationBearer);
            return Response.noContent().link(uriInfo.getAbsolutePathBuilder().path("../").build(),
					 						 "parent").build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

}
