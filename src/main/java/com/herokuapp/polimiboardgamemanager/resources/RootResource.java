package com.herokuapp.polimiboardgamemanager.resources;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.herokuapp.polimiboardgamemanager.model.CommonConstants;
import com.herokuapp.polimiboardgamemanager.model.LinkAdapter;

/**
 * Root resource that responds only to http GET requests to "/" and
 * gives the links to access other resources.
 * 
 * @author Luca Luciano Costanzo
 *
 */
@Path("/")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
public class RootResource {
    
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
    // =          GET requests              =
    // ======================================     
    
    /**
     * Get links to users and board games.
     *
     * @return the response with links in body and header
     */
    @GET
    public Response getLinks() {
    	Root res = new Root();
    	
    	List<Link> links = res.getLinks();
    	
        return Response.ok(res).links(links.toArray(new Link[links.size()])).build();
    }
    
    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public class Root {        
        /**
         * Gets the links list.
         *
         * @return the links list
         */
        @XmlElement(name = "link")
        @XmlJavaTypeAdapter(LinkAdapter.class)
        public List<Link> getLinks() {
            List<Link> links = new ArrayList<>();
            links.add(Link.fromUri(CommonConstants.API_URI+"users/login").rel("login").build());
            links.add(Link.fromUri(CommonConstants.API_URI+"users/").rel("users").build());
            links.add(Link.fromUri(CommonConstants.API_URI+"boardgames/").rel("boardgames").build());
            
            return links;
        }
        
        /**
         * Gets the links array.
         *
         * @return the links array
         */
        public Link[] getLinksArray() {
            List<Link> links = getLinks();
            return links.toArray(new Link[links.size()]);
        }    	
    }

}