package com.herokuapp.polimiboardgamemanager.resources;

import java.net.URI;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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

import com.herokuapp.polimiboardgamemanager.dao.PlayDao;
import com.herokuapp.polimiboardgamemanager.dao.UserDao;
import com.herokuapp.polimiboardgamemanager.filter.Secured;
import com.herokuapp.polimiboardgamemanager.model.Play;
import com.herokuapp.polimiboardgamemanager.model.User;

/**
 * Resource representing the users. It responds to http requests
 * sent to path "/users", to manage users and plays associated to the user.
 * 
 * @author Luca Luciano Costanzo
 *
 */
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
    // =          POST requests             =
    // ======================================

    /**
     * It receives POST requests sent to "/users/login" path
     * and authenticates a user with his username and password.
     * @param username the username of the user to authenticate
     * @param password the password of the user to authenticate
     * @return <b>200 OK</b> containing the authorization bearer in
     *         the header if the login is successful, <br />
     *         <b>401 Unauthorized</b> if the username and/or
     *         password are wrong.
     */
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response authenticateUser(@FormParam("username") String username, 
                                     @FormParam("password") String password) {

        try {

            // Authenticate the user using the credentials provided
            long userId = UserDao.getInstance().authenticate(username, password);

            // Issue a token for the user
            String token = UserDao.getInstance().issueToken(userId, username, uriInfo);

            // Return the token on the response
            return Response.ok("User authenticated with authorization: Bearer "+token).
                    header(HttpHeaders.AUTHORIZATION,  "Bearer " + token).build();

        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).type(MediaType.TEXT_XML).entity(e.getMessage()).build();
        }      
    }
    
    /**
     * It receives POST requests sent to "/users" path
     * and creates a user with the desired attributes.
     * @param fullName the full name of the user to create
     * @param username the username of the user to create
     * @param password the password of the user to create
     * @return <b>201 Created</b> containing the URI of
     *         the created user, if everything it's correct,<br />
     *         <b>409 Conflict</b> if an user with the desired
     *         username already exists.
     */
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
    
    /**
     * It receives POST requests sent to "/users/{userId}/plays" path
     * and creates a play for the user, with the desired attributes.
     * @param play the Play object representation to create
     * @param userId the id of the user, taken from the url
     * @param authorizationBearer the authorization bearer
     *        to ensure user authentication
     * @return <b>201 Created</b> containing the URI of
     *         the created play, if everything it's correct,<br />
     *         <b>401 Unauthorized</b> if the user it's not
     *         authenticated.
     */
    @POST
    @Path("/{userId}/plays")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Secured
    public Response createPlay(Play play, @PathParam("userId") Long userId, @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationBearer) {
        try {
            long id = PlayDao.getInstance().createPlay(play, authorizationBearer);
            return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(id)).build()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }        
    }
    
    
    // ======================================
    // =          GET requests              =
    // ======================================
    
    /**
     * It receives the GET requests sent to "/users" and returns
     * all the users existing in the database, sorted and filtered
     * with the desired criteria.
     * @param orderBy the name of the ordering attribute, it can be
     *        only one of
     *        {@link com.herokuapp.polimiboardgamemanager.model.User#OrderBy User.OrderBy}
     * @param orderType the name of the ordering type, it can be
     *        only one of
     *        {@link com.herokuapp.polimiboardgamemanager.model.User#OrderType User.OrderType}
     * @return <b>200 OK</b> if there are no errors in the parameters,<br />
     *         <b>400 Bad request</b> if the parameters are not correct.
     * @throws Exception
     */
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
    
    /**
     * It receives the GET requests sent to "/users" and returns
     * the count of all the users existing in the database.
     * @return count of users
     */
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

        return Response.ok(user).links(user.getLinksArray()).build();
    }
    
    @GET
    @Path("/{userId}/plays")
    public Response getPlaysByUser(@PathParam("userId") Long userId,
                                   @DefaultValue("id") @QueryParam("orderBy") String orderBy,
                                   @DefaultValue("ASC") @QueryParam("orderType") String orderType) throws Exception {
        
        try {
            GenericEntity<List<Play>> list = new GenericEntity<List<Play>>(PlayDao.getInstance().findPlaysByUser(userId, orderBy, orderType)){};
            return Response.ok(list).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }        
    }
    
    // ======================================
    // =          PUT requests              =
    // ======================================    
    
    @PUT
    @Path("/{userId}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response putUser(@FormParam("fullName") String fullName,
                            @FormParam("username") String username, 
                            @FormParam("password") String password,
                            @PathParam("userId") Long userId,
                            @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationBearer) {
        
        try {
            if (UserDao.getInstance().findById(userId) != null) {
                UserDao.getInstance().updateUser(userId, fullName, username, password, authorizationBearer);
                return Response.noContent().build();
            } else {
                UserDao.getInstance().createUser(userId, fullName, username, password);
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
    @Path("/{id}")
    @Secured
    public Response remove(@PathParam("id") Long id, @HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationBearer) {
        try {
            UserDao.getInstance().removeUser(id, authorizationBearer);
            return Response.noContent().link(uriInfo.getAbsolutePathBuilder().path("../").build(),
            								 "parent").build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
    
    // ======================================
    // =        Other play requests         =
    // ======================================      
    
    @Path("/{userId}/plays/{playId}")
    public PlayResource getPlay(@PathParam("userId") Long userId, @PathParam("playId") Long playId) {
        return new PlayResource(uriInfo, request, playId);
    }    

}