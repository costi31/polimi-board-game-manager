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

public class UserDao {
	
	private static final String USER_UNAUTHORIZED_MSG = "User unauthorized to do this operation!";
	private static final String ALREADY_EXISTING_USERNAME_MSG = "Bad username: user with desired username already exists!";
    
    private static UserDao instance = null;
    
    /**
     * Separator between user id and username in the subject field of the token
     */
    public static final String SUBJECT_ID_SEPARATOR = "@";    
    
    /**
     * Gets the instance of BoardGameDao
     * @return instance of BoardGameDao
     */
    public static UserDao getInstance() {
        if (instance == null)
            instance = new UserDao();

        return instance;
    }

    private UserDao() {
    }
     
    public long createUser(String fullName, String username, String password) throws Exception {
        return createUser(null, fullName, username, password);
    }
    
    public long createUser(Long id, String fullName, String username, String password) throws Exception {
        try {
            if (!doesUsernameExist(username)) {
                // If username doesn't exists I create the user
                User user;
                if (id != null && findById(id) == null) // If I specify a valid id I create a user with that id
                    user = new User(id, fullName, username, password, false);
                else
                    user = new User(fullName, username, password, false);
                
                user = (User) MyEntityManager.getInstance().mergeEntity(user);
                return user.getId();
            } else
                throw new IllegalArgumentException(ALREADY_EXISTING_USERNAME_MSG);
        } catch (Exception e) {
            throw new IllegalArgumentException(ALREADY_EXISTING_USERNAME_MSG, e);
        }
    }    
    
    public void updateUser(Long id, String fullName, String username,
            String password, String authorizationBearer) throws Exception {

        try {

        	long authenticatedId = AuthenticationFilter.getAuthIdFromBearer(authorizationBearer);

            // Verify if the id of authenticated user corresponds to the id of the user to update
            if (authenticatedId != id)
                throw new SecurityException(USER_UNAUTHORIZED_MSG);

            EntityManager em = MyEntityManager.getInstance().getEm();
            User user = (User) MyEntityManager.getInstance().findEntity(User.class, id);

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
    
    public User findById(long id) {
        return (User) MyEntityManager.getInstance().findEntity(User.class, id);
    }
    
    public User findByUsername(String username) throws Exception {
        EntityManager em = MyEntityManager.getInstance().getEm();
        TypedQuery<User> query = em.createNamedQuery(User.FIND_BY_USERNAME, User.class);
        query.setParameter("username", username);
        return query.getSingleResult();
    }
    
    public boolean doesUsernameExist(String username) {
        try {
            if (findByUsername(username) != null)
                return true;
        } catch (Exception e) {
            return false;
        }
        
        return false;
    }
        
    public List<User> findAllUsers(List<String> filtersString, List<String> ordersString) throws Exception {
         return MyEntityManager.getInstance().findAllEntities(User.class,
        		 filtersString, ordersString, User.FilterBy.class, User.OrderBy.class);
    }    
        
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
     * Gets count of existing users
     * @return long count of users
     */
    public long getCount() {
        EntityManager em = MyEntityManager.getInstance().getEm();
        TypedQuery<Long> query = em.createNamedQuery(User.COUNT_ALL, Long.class);
        return query.getSingleResult();
    }

        
//    private Date toDate(LocalDateTime localDateTime) {
//        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
//    }    

}
