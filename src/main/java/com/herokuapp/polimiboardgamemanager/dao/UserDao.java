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
    
    private static UserDao instance = null;
    
    /**
     * Separator between user id and username in the subject field of the token
     */
    private static final String SUBJECT_ID_SEPARATOR = "@";    
    
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
        try {
            if (!doesUsernameExist(username)) {
                // If username doesn't exists I create the user
                User user = new User(fullName, username, password, false);
                MyEntityManager.getInstance().persistEntity(user);
                return user.getId();
            } else
                throw new IllegalArgumentException("Bad username: user with desired username already exists!");
        } catch (Exception ex) {
            throw new IllegalArgumentException("Bad username: user with desired username already exists!");
        }
    }
    
    
    public void removeUser(long id, String authorizationBearer) throws Exception {     
        try {
            String token = authorizationBearer.substring("Bearer".length()).trim();
            String authenticatedSubject = AuthenticationFilter.validateToken(token);
            long authenticatedId = Long.parseLong(authenticatedSubject.split(SUBJECT_ID_SEPARATOR)[0]);
            
            // Verify if the id of authenticated user corresponds to the id of the user to remove
            if (authenticatedId != id)
                throw new SecurityException("User unauthorized");
            
            MyEntityManager.getInstance().removeEntity(User.class, id);
        } catch (Exception e) {
            throw new SecurityException("User unauthorized");
        }
    }
    
    public User findById(long id) {
        return (User) MyEntityManager.getInstance().findEntity(User.class, id);
    }
    
    public User findByUsername(String username) throws Exception {
        EntityManager em = MyEntityManager.getInstance().getEm();
        TypedQuery<User> query = em.createNamedQuery(User.FIND_BY_USERNAME, User.class);
        query.setParameter("username", username);
        User user = query.getSingleResult();

        return user;
    }
    
    public boolean doesUsernameExist(String username) {
        try {
            findByUsername(username);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    
    public List<User> findAllUsersNameOrd(boolean desc) {
        EntityManager em = MyEntityManager.getInstance().getEm();
        String queryName = desc ? User.FIND_ALL_NAME_DESC : User.FIND_ALL_NAME_ASC;
        TypedQuery<User> query = em.createNamedQuery(queryName, User.class);
        List<User> allUsers = query.getResultList();

        return allUsers;
    }
    
    public List<User> findAllUsersNameOrd() {
        return findAllUsersNameOrd(false);
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
        
        String jwtToken = Jwts.builder()
                            .setSubject(userId+SUBJECT_ID_SEPARATOR+username)
                            .setIssuer(uriInfo.getAbsolutePath().toString())
                            .setIssuedAt(new Date())
                            .setExpiration(expirationDate)
                            .signWith(SignatureAlgorithm.HS512, AuthenticationFilter.SIGNING_KEY)
                            .compact();
        
        return jwtToken;

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
