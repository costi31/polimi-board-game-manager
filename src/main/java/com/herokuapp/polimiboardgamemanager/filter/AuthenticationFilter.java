package com.herokuapp.polimiboardgamemanager.filter;

import java.io.IOException;
import java.security.Key;

import javax.annotation.Priority;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.DatatypeConverter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
	
    /**
     * Separator between user id and username in the subject field of the token
     */
    public static final String SUBJECT_ID_SEPARATOR = "@";  	
    
    public static final Key SIGNING_KEY = new SecretKeySpec(DatatypeConverter.parseBase64Binary(
            System.getenv("SIGNING_KEY")
            ), SignatureAlgorithm.HS512.getJcaName());
    
    public static String validateToken(String token) throws Exception {
        // Check if it was issued by the server and if it's not expired
        // Throw an Exception if the token is invalid
        return Jwts.parser().setSigningKey(SIGNING_KEY).parseClaimsJws(token).getBody().getSubject();
    }
    
    public static long getAuthIdFromBearer(String authorizationBearer) throws Exception {
        String token = authorizationBearer.substring("Bearer".length()).trim();
        String authenticatedSubject = AuthenticationFilter.validateToken(token);
        return Long.parseLong(authenticatedSubject.split(SUBJECT_ID_SEPARATOR)[0]);
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Get the HTTP Authorization header from the request
        String authorizationHeader = 
            requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Check if the HTTP Authorization header is present and formatted correctly 
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("Authorization header must be provided");
        }

        // Extract the token from the HTTP Authorization header
        String token = authorizationHeader.substring("Bearer".length()).trim();

        try {

            // Validate the token
            validateToken(token);
            
        } catch (Exception e) {
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}
