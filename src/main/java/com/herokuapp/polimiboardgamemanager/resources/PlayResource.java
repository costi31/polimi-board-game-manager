package com.herokuapp.polimiboardgamemanager.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.herokuapp.polimiboardgamemanager.dao.PlayDao;
import com.herokuapp.polimiboardgamemanager.model.Play;

@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
public class PlayResource {
    
    // ======================================
    // =          Injection Points          =
    // ======================================    
    
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    private long id;
    
    public PlayResource(UriInfo uriInfo, Request request, long id) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.id = id;
    }
    
    // ======================================
    // =          Business methods          =
    // ======================================

    //Application integration
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
    public Response getPlay() {
        Play play = PlayDao.getInstance().findById(id);

        if (play == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        return Response.ok(play).build();
    }
    
}