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

import com.herokuapp.polimiboardgamemanager.dao.PlayDao;
import com.herokuapp.polimiboardgamemanager.filter.Secured;
import com.herokuapp.polimiboardgamemanager.model.Play;

/**
 * The Class PlayResource.
 */
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
public class PlayResource {
    
    // ======================================
    // =          Injection Points          =
    // ======================================    
    
    /** The uri info. */
    @Context
    UriInfo uriInfo;
    
    /** The request. */
    @Context
    Request request;
    
    /** The id. */
    private long id;
    
    /**
     * Instantiates a new play resource.
     *
     * @param uriInfo the uri info
     * @param request the request
     * @param id the id
     */
    public PlayResource(UriInfo uriInfo, Request request, long id) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.id = id;
    }
    
    // ======================================
    // =          GET requests              =
    // ======================================

    /**
     * Gets the play.
     *
     * @return the play
     */
    //Application integration
    @GET
    public Response getPlay() {
        Play play = PlayDao.getInstance().findById(id);

        if (play == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        return Response.ok(play).links(play.getLinksArray()).build();
    }
    
    // ======================================
    // =          PUT requests              =
    // ======================================    
    
    /**
     * Put play.
     *
     * @param play the play
     * @param authorizationBearer the authorization bearer
     * @return the response
     */
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Secured
    public Response putPlay(Play play,
                            @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationBearer) {
        
        try {
            if (PlayDao.getInstance().findById(id) != null) {
                PlayDao.getInstance().updatePlay(id, play, authorizationBearer);
                return Response.noContent().build();
            } else {
            	PlayDao.getInstance().createPlay(play, authorizationBearer);
                return Response.created(uriInfo.getAbsolutePathBuilder().build()).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }        
    }    
    
    // ======================================
    // =          DELETE requests           =
    // ======================================
    
    /**
     * Removes the.
     *
     * @param authorizationBearer the authorization bearer
     * @return the response
     */
    @DELETE
    @Secured
    public Response remove(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationBearer) {
        try {
            PlayDao.getInstance().removePlay(id, authorizationBearer);
            return Response.noContent().link(uriInfo.getAbsolutePathBuilder().path("../").build(),
					 						 "parent").build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }    
    
}
