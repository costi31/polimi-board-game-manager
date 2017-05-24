package com.herokuapp.polimiboardgamemanager.dao;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Singleton class to manage entities of JPA
 * @author Luca Luciano Costanzo
 *
 */
public class MyEntityManager {
    
    private static MyEntityManager instance = null;
    
    private static final Logger LOGGER = LogManager.getLogger(MyEntityManager.class);
    
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
            configOverrides.put("javax.persistence.jdbc.url", dbUrl);
            configOverrides.put("javax.persistence.jdbc.user", username);
            configOverrides.put("javax.persistence.jdbc.password", password);
        } catch (URISyntaxException e) {
            LOGGER.fatal(e.getMessage());
        }
        
        
        emfactory = Persistence.createEntityManagerFactory("BoardGameManagerPU", configOverrides);
        em = emfactory.createEntityManager();
    }
    
    /**
     * Gets JPA EntityManager
     * @return the entity manager
     */
    public EntityManager getEm() {
        return em;
    }
    
    /**
     * Persists a generic object in the entity manager, commits and flushes
     * @param entity Object to persist in the entity manager
     */
    public void persistEntity(Object entity) {
        em.getTransaction().begin();
        em.persist(entity);
        em.flush();
        em.getTransaction().commit();
    }
    
    public Object findEntity(Class<?> cls, long id) {
        return em.find(cls, id);
    }
    
    public void removeEntity(Class<?> cls, long id) {
        em.getTransaction().begin();        
        em.remove(em.getReference(cls, id));
        em.flush();
        em.getTransaction().commit();
    }

}
