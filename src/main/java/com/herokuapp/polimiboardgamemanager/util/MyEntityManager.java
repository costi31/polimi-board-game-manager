package com.herokuapp.polimiboardgamemanager.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Singleton class to manage entities of JPA
 * @author Luca Luciano Costanzo
 *
 */
public class MyEntityManager {
    
    private static MyEntityManager instance = null;
    
    private EntityManagerFactory emfactory;
    private EntityManager em;    

    public static MyEntityManager getInstance() {
        if (instance == null)
            instance = new MyEntityManager();

        return instance;
    }    
    
    private MyEntityManager() {
        Map<String, Object> configOverrides = new HashMap<String, Object>();
        
        try {
            URI dbUri = new URI(System.getenv("CLEARDB_DATABASE_URL"));
            String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            String dbUrl = "jdbc:mysql://" + dbUri.getHost() + dbUri.getPath();
            configOverrides.put("hibernate.connection.url", dbUrl);
            configOverrides.put("hibernate.connection.username", username);
            configOverrides.put("hibernate.connection.password", password);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        
        
        emfactory = Persistence.createEntityManagerFactory("BoardGameManagerPU", configOverrides);
    }
    
    /**
     * Gets JPA EntityManager
     * @return the entity manager
     */
    public EntityManager getEm() {
        em = emfactory.createEntityManager();
        return em;
    }

}
