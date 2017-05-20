package com.herokuapp.polimiboardgamemanager.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
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

import com.herokuapp.polimiboardgamemanager.dao.UserDao;
import com.herokuapp.polimiboardgamemanager.filter.Secured;
import com.herokuapp.polimiboardgamemanager.model.User;

@Path("/users")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
public class UserResource {
    
    // ======================================
    // =          Injection Points          =
    // ======================================

    @Context
    UriInfo uriInfo;
    
    @Context
    Request request;
    
    // ======================================
    // =          Business methods          =
    // ======================================    

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response authenticateUser(@FormParam("username") String username, 
                                     @FormParam("password") String password) {

        try {

            // Authenticate the user using the credentials provided
            long userId = UserDao.getInstance().authenticate(username, password);

            // Issue a token for the user
            String token = UserDao.getInstance().issueToken(userId, username, uriInfo);

            // Return the token on the response
            return Response.ok().header(HttpHeaders.AUTHORIZATION,  "Bearer " + token).build();

        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }      
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response create(@FormParam("fullName") String fullName,
                           @FormParam("username") String username, 
                           @FormParam("password") String password
                           ) {
        try {
            long id = UserDao.getInstance().createUser(fullName, username, password);
            return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(id)).build()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        }
        
    }    
    
//    @POST
//    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON} )
//    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
//    public Response create(User user) {
//        try {
//            UserDao.getInstance().createUser(user);
//            return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(user.getId())).build()).build();
//        } catch (Exception e) {
//            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
//        }
//        
//    }
    
    @GET
    public Response findAllUsers(@DefaultValue("id") @QueryParam("orderBy") String orderBy,
                                 @DefaultValue("ASC") @QueryParam("orderType") String orderType) throws Exception {
        
        try {
            GenericEntity<List<User>> list = new GenericEntity<List<User>>(UserDao.getInstance().findAllUsers(orderBy, orderType)){};
            return Response.ok(list).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
    
    @GET
    @Path("/count")
    @Produces({MediaType.TEXT_PLAIN, MediaType.TEXT_XML, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public long getCount() {
        return UserDao.getInstance().getCount();
    }        
    
    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        User user = UserDao.getInstance().findById(id);

        if (user == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        return Response.ok(user).build();
    }
    
    @Path("/{userId}/plays/{playId}")
    public PlayResource getPlay(@PathParam("userId") Long userId, @PathParam("playId") Long playId) {
        return new PlayResource(uriInfo, request, playId);
    }
    
    @DELETE
    @Path("/{id}")
    @Secured
    public Response remove(@PathParam("id") Long id, @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationBearer) {
        try {
            UserDao.getInstance().removeUser(id, authorizationBearer);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }    
    
//    @POST
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public Response authenticateUserJson(String username, String password) {
//
//        try {
//
//            // Authenticate the user using the credentials provided
//            authenticate(username, password);
//
//            // Issue a token for the user
//            String token = issueToken(username);
//
//            // Return the token on the response
//            return Response.ok(token).build();
//
//        } catch (Exception e) {
//            return Response.status(Response.Status.UNAUTHORIZED).build();
//        }      
//    }    

}