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
    
    public void createUser(User user) {
        System.out.println(user);
        System.out.println("username: "+user.getUsername());
        System.out.println("pass: "+user.getPassword());
//        EntityManager em = MyEntityManager.getInstance().getEm();
//        em.getTransaction().begin();
//        em.persist(user);
//        em.flush();
//        em.getTransaction().commit();
        MyEntityManager.getInstance().persistEntity(user);
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
    
    public void authenticate(String username, String password) throws Exception {
        EntityManager em = MyEntityManager.getInstance().getEm();
        TypedQuery<User> query = em.createNamedQuery(User.FIND_BY_LOGIN_PASSWORD, User.class);
        query.setParameter("username", username);
        query.setParameter("password", PasswordUtils.digestPassword(password));
        User user = query.getSingleResult();

        if (user == null)
            throw new SecurityException("Invalid user/password");
    }

    public String issueToken(String username, UriInfo uriInfo) {
        long oneMinuteInMillis=60000;
        Calendar date = Calendar.getInstance();
        long t = date.getTimeInMillis();
        Date expirationDate = new Date(t + (60 * oneMinuteInMillis));
        
        String jwtToken = Jwts.builder()
                            .setSubject(username)
                            .setIssuer(uriInfo.getAbsolutePath().toString())
                            .setIssuedAt(new Date())
                            .setExpiration(expirationDate)
                            .signWith(SignatureAlgorithm.HS512, AuthenticationFilter.SIGNING_KEY)
                            .compact();
        
        return jwtToken;

    }
    
//    private Date toDate(LocalDateTime localDateTime) {
//        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
//    }    

}
