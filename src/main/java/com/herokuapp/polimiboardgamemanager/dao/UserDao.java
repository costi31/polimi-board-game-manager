package com.herokuapp.polimiboardgamemanager.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.core.UriInfo;

import com.herokuapp.polimiboardgamemanager.filter.AuthenticationFilter;
import com.herokuapp.polimiboardgamemanager.model.User;
import com.herokuapp.polimiboardgamemanager.util.PasswordUtils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * The Class UserDao.
 */
public class UserDao {
	
	/** The Constant USER_UNAUTHORIZED_MSG. */
	private static final String USER_UNAUTHORIZED_MSG = "User unauthorized to do this operation!";
	
	/** The Constant ALREADY_EXISTING_USERNAME_MSG. */
	private static final String ALREADY_EXISTING_USERNAME_MSG = "Bad username: user with desired username already exists!";
    
    /** The instance. */
    private static UserDao instance = null;
    
    /** Separator between user id and username in the subject field of the token. */
    public static final String SUBJECT_ID_SEPARATOR = "@";    
    
    /**
     * Gets the instance of UserDao
     *
     * @return instance of UserDao
     */
    public static UserDao getInstance() {
        if (instance == null)
            instance = new UserDao();

        return instance;
    }

    /**
     * Instantiates a new user dao.
     */
    private UserDao() {
    }
     
    /**
     * Creates the user.
     *
     * @param fullName the full name
     * @param username the username
     * @param password the password
     * @return the id of the user
     * @throws Exception the exception
     */
    public long createUser(String fullName, String username, String password) throws Exception {
        return createUser(null, fullName, username, password);
    }
    
    /**
     * Creates the user with desired id.
     *
     * @param id the desired id of the user
     * @param fullName the full name
     * @param username the username
     * @param password the password
     * @return the id of the user
     * @throws Exception the exception
     */
    public long createUser(Long id, String fullName, String username, String password) throws Exception {
        try {
            if (!doesUsernameExist(username)) {
                // If username doesn't exists I create the user
                User user;
                if (id != null && findById(id) == null) // If I specify a valid id I create a user with that id
                    user = new User(id, fullName, username, password, false);
                else
                    user = new User(fullName, username, password, false);
                
                user = MyEntityManager.getInstance().mergeEntity(user);
                return user.getId();
            } else
                throw new IllegalArgumentException(ALREADY_EXISTING_USERNAME_MSG);
        } catch (Exception e) {
            throw new IllegalArgumentException(ALREADY_EXISTING_USERNAME_MSG, e);
        }
    }    
    
    /**
     * Update user.
     *
     * @param id the id of the user to update
     * @param fullName the full name
     * @param username the username
     * @param password the password
     * @param authorizationBearer the authorization bearer
     * @throws Exception the exception
     */
    public void updateUser(Long id, String fullName, String username,
            String password, String authorizationBearer) throws Exception {

        try {

        	long authenticatedId = AuthenticationFilter.getAuthIdFromBearer(authorizationBearer);

            // Verify if the id of authenticated user corresponds to the id of the user to update
            if (authenticatedId != id)
                throw new SecurityException(USER_UNAUTHORIZED_MSG);

            EntityManager em = MyEntityManager.getInstance().getEm();
            User user = MyEntityManager.getInstance().findEntity(User.class, id);

            em.getTransaction().begin();

            // updates the user attributes
            if (fullName != null) 
                user.setFullName(fullName);
            if (username != null) {
                User existingUser = findByUsername(username);
                // I check if already exists a user with same username and different id
                if (existingUser == null)
                    user.setUsername(username);
                else if (! id.equals(existingUser.getId()))
                    throw new IllegalArgumentException(ALREADY_EXISTING_USERNAME_MSG);
            }
            if (password != null)
                user.setPassword(password);

            em.merge(user);
            em.flush();
            em.getTransaction().commit();

        } catch (Exception e) {
            throw new SecurityException(USER_UNAUTHORIZED_MSG, e);
        }   

    }
    
    /**
     * Removes the user.
     *
     * @param id the id of the user to remove
     * @param authorizationBearer the authorization bearer
     * @throws Exception the exception
     */
    public void removeUser(long id, String authorizationBearer) throws Exception {     
        try {
        	long authenticatedId = AuthenticationFilter.getAuthIdFromBearer(authorizationBearer);
            
            // Verify if the id of authenticated user corresponds to the id of the user to remove
            if (authenticatedId != id)
                throw new SecurityException("User unauthorized");
            
            MyEntityManager.getInstance().removeEntity(User.class, id);
        } catch (Exception e) {
            throw new SecurityException(USER_UNAUTHORIZED_MSG, e);
        }
    }
    
    /**
     * Find by id.
     *
     * @param id the id of the user to find
     * @return the user found
     */
    public User findById(long id) {
        return MyEntityManager.getInstance().findEntity(User.class, id);
    }
    
    /**
     * Find user by username.
     *
     * @param username the username
     * @return the user found
     * @throws Exception the exception
     */
    public User findByUsername(String username) throws Exception {
        EntityManager em = MyEntityManager.getInstance().getEm();
        TypedQuery<User> query = em.createNamedQuery(User.FIND_BY_USERNAME, User.class);
        query.setParameter("username", username);
        return query.getSingleResult();
    }
    
    /**
     * Checks if a username exists.
     *
     * @param username the username
     * @return <b>true</b> if the username exists, <b>false</b> otherwise
     */
    public boolean doesUsernameExist(String username) {
        try {
        	return (findByUsername(username) != null);
        } catch (Exception e) {
            return false;
        }
    }
        
    /**
     * Find all users.
     *
     * @param filtersString the list of filters string
     * @param ordersString the list of orders string
     * @return the list of users found
     * @throws Exception the exception
     */
    public List<User> findAllUsers(List<String> filtersString, List<String> ordersString) throws Exception {
         return MyEntityManager.getInstance().findAllEntities(User.class,
        		 filtersString, ordersString, User.FilterBy.class, User.OrderBy.class);
    }    
        
    /**
     * Authenticate a user.
     *
     * @param username the username
     * @param password the password
     * @return the id of the authenticated user
     * @throws Exception the exception
     */
    public long authenticate(String username, String password) throws Exception {
        EntityManager em = MyEntityManager.getInstance().getEm();
        TypedQuery<User> query = em.createNamedQuery(User.FIND_BY_LOGIN_PASSWORD, User.class);
        query.setParameter("username", username);
        query.setParameter("password", PasswordUtils.digestPassword(password));
        User user = query.getSingleResult();

        if (user == null)
            throw new SecurityException("Invalid user/password");
        
        return user.getId();
    }

    /**
     * Issue token.
     *
     * @param userId the user id
     * @param username the username
     * @param uriInfo the uri info
     * @return the string representing the token
     */
    public String issueToken(long userId, String username, UriInfo uriInfo) {
        long oneMinuteInMillis=60000;
        Calendar date = Calendar.getInstance();
        long t = date.getTimeInMillis();
        Date expirationDate = new Date(t + (60 * oneMinuteInMillis));
        
        return Jwts.builder()
                            .setSubject(userId+SUBJECT_ID_SEPARATOR+username)
                            .setIssuer(uriInfo.getAbsolutePath().toString())
                            .setIssuedAt(new Date())
                            .setExpiration(expirationDate)
                            .signWith(SignatureAlgorithm.HS512, AuthenticationFilter.SIGNING_KEY)
                            .compact();

    }

    /**
     * Gets count of existing users.
     *
     * @return long count of users
     */
    public long getCount() {
        EntityManager em = MyEntityManager.getInstance().getEm();
        TypedQuery<Long> query = em.createNamedQuery(User.COUNT_ALL, Long.class);
        return query.getSingleResult();
    }
}
