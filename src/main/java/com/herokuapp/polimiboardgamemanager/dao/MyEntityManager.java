package com.herokuapp.polimiboardgamemanager.dao;

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
