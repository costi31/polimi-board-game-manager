package com.herokuapp.polimiboardgamemanager.resources;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAccessorType;

import com.herokuapp.polimiboardgamemanager.dao.UserDao;
import com.herokuapp.polimiboardgamemanager.model.User;

import javax.xml.bind.annotation.XmlAccessType;

@Path("/users")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
@XmlAccessorType(XmlAccessType.FIELD)
public class UserResource {
    
    // ======================================
    // =          Injection Points          =
    // ======================================

    @Context
    private UriInfo uriInfo;
    
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
            UserDao.getInstance().authenticate(username, password);

            // Issue a token for the user
            String token = UserDao.getInstance().issueToken(username, uriInfo);

            // Return the token on the response
            return Response.ok(token).build();

        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }      
    }
    
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON} )
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
    public Response create(User user) {
        UserDao.getInstance().createUser(user);
        return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(user.getId())).build()).build();
    }
    
    @GET
    public Response findAllUsers() {
        GenericEntity<List<User>> list = new GenericEntity<List<User>>(UserDao.getInstance().findAllUsersNameOrd()){};
        return Response.ok(list).build();
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