package com.herokuapp.polimiboardgamemanager.dao;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.DatatypeConverter;

import com.herokuapp.polimiboardgamemanager.model.User;
import com.herokuapp.polimiboardgamemanager.util.MyEntityManager;
import com.herokuapp.polimiboardgamemanager.util.PasswordUtils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class UserDao {
    
    private static final Key SIGNING_KEY = new SecretKeySpec(DatatypeConverter.parseBase64Binary(
                                                             System.getenv("SIGNING_KEY")
                                                             ), SignatureAlgorithm.HS512.getJcaName());
    
    private static UserDao instance = null;
    
    private EntityManager em;

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
    
    public List<User> findAllUsersNameOrd(boolean desc) {
        em = MyEntityManager.getInstance().getEm();
        String queryName = desc ? User.FIND_ALL_NAME_DESC : User.FIND_ALL_NAME_ASC;
        TypedQuery<User> query = em.createNamedQuery(queryName, User.class);
        List<User> allUsers = query.getResultList();

        em.close();
        return allUsers;
    }
    
    public List<User> findAllUsersNameOrd() {
        return findAllUsersNameOrd(false);
    }
    
    public void authenticate(String username, String password) throws Exception {
        TypedQuery<User> query = em.createNamedQuery(User.FIND_BY_LOGIN_PASSWORD, User.class);
        query.setParameter("username", username);
        query.setParameter("password", PasswordUtils.digestPassword(password));
        User user = query.getSingleResult();

        if (user == null)
            throw new SecurityException("Invalid user/password");
    }

    public String issueToken(String username, UriInfo uriInfo) {
        String jwtToken = Jwts.builder()
                            .setSubject(username)
                            .setIssuer(uriInfo.getAbsolutePath().toString())
                            .setIssuedAt(new Date())
                            .setExpiration(toDate(LocalDateTime.now().plusMinutes(15L)))
                            .signWith(SignatureAlgorithm.HS512, SIGNING_KEY)
                            .compact();
        
        return jwtToken;

    }
    
    private Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }    

}
