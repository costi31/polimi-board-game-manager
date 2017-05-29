package com.herokuapp.polimiboardgamemanager.dao;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Singleton class to manage entities of JPA.
 *
 * @author Luca Luciano Costanzo
 */
public class MyEntityManager {
	
	/** Split character between a search criteria (filter name or order by) and its value. */
	public static final String SEARCH_CRITERIA_SPLIT = "@";
	
    /**
     * The Enum OrderMode.
     */
    public enum OrderMode {
        
		/** The asc. */
		ASC, 
		/** The desc. */
		DESC
    }
    
    /** The instance. */
    private static MyEntityManager instance = null;
    
    /** The Constant LOGGER. */
    private static final Logger LOGGER = LogManager.getLogger(MyEntityManager.class);
    
    /** The emfactory. */
    private EntityManagerFactory emfactory;
    
    /** The em. */
    private EntityManager em;    

    /**
     * Gets the single instance of MyEntityManager.
     *
     * @return single instance of MyEntityManager
     */
    public static MyEntityManager getInstance() {
        if (instance == null)
            instance = new MyEntityManager();

        return instance;
    }    
    
    /**
     * Instantiates a new my entity manager.
     */
    private MyEntityManager() {
        Map<String, Object> configOverrides = new HashMap<>();
        
        try {
            URI dbUri = new URI(System.getenv("CLEARDB_DATABASE_URL"));
            String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            String dbUrl = "jdbc:mysql://" + dbUri.getHost() + dbUri.getPath();
            configOverrides.put("javax.persistence.jdbc.url", dbUrl);
            configOverrides.put("javax.persistence.jdbc.user", username);
            configOverrides.put("javax.persistence.jdbc.password", password);
        } catch (URISyntaxException e) {
            LOGGER.fatal(e.getMessage(), e);
        }
        
        
        emfactory = Persistence.createEntityManagerFactory("BoardGameManagerPU", configOverrides);
        em = emfactory.createEntityManager();
    }
    
    /**
     * Gets JPA EntityManager.
     *
     * @return the entity manager
     */
    public EntityManager getEm() {
        return em;
    }
    
    /**
     * Persists a generic object in the entity manager, commits and flushes.
     *
     * @param entity Object to persist in the entity manager
     */
    public void persistEntity(Object entity) {
        em.getTransaction().begin();
        em.persist(entity);
        em.flush();
        em.getTransaction().commit();
        em.refresh(entity);
    }
    
    /**
     * Merges a generic entity in the entity manager, commits and flushes.
     *
     * @param <T> the generic type of the entity
     * @param entity entity to merge in the entity manager
     * @return the entity merged
     */    
    public <T> T mergeEntity(T entity) {
        em.getTransaction().begin();
        T managedEntity = em.merge(entity);
        em.flush();
        em.getTransaction().commit();
        return managedEntity;
    }
    
    /**
     * Find entity.
     *
     * @param <T> the generic type of the entity
     * @param cls the Class type of the entity
     * @param id the id of the entity
     * @return the object representing the entity
     */
    public <T> T findEntity(Class<T> cls, long id) {
        return em.find(cls, id);
    }
    
    /**
     * Find all entities with specified filters and orders criteria.
     *
     * @param <T> the generic type of the entity
     * @param <E1> the generic type of the enum representing the allowed filters
     * @param <E2> the generic type of the enum representing the allowed orders
     * @param resultClass the result class of the entities
     * @param filtersString the list of filters string
     * @param ordersString the list of orders string
     * @param allowedFilters the enum of allowed filters
     * @param allowedOrders the enum of allowed orders
     * @return the list of entities
     * @throws Exception the exception thrown when there are errors with parameters
     */
    public <T extends Object, E1 extends Enum<E1>, E2 extends Enum<E2>>
    	List<T> findAllEntities(Class<T> resultClass,
							    List<String> filtersString,
							    List<String> ordersString,
							    Class<E1> allowedFilters,
							    Class<E2> allowedOrders) throws Exception {
    	
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(resultClass);
        Root<T> entity = cq.from(resultClass);
        cq.select(entity);
        
        // Map filters and orders couples
        // it throws exception if the filters or the orders criteria are invalid
        Map<E1, String> filtersMap = mapFilters(filtersString, allowedFilters);
        Map<E2, OrderMode> ordersMap = mapOrders(ordersString, allowedOrders);
        
        // Now I set all the filters
        List<Predicate> filtersPredicates = new ArrayList<>();
        
        for (Map.Entry<E1, String> entry : filtersMap.entrySet()) {
            E1 filterBy = entry.getKey();
            String filterVal = entry.getValue();
            
            Path<T> fieldPath = entity;
            for (String filterByString : filterBy.toString().split("_")) {
           		fieldPath = fieldPath.get(filterByString);
            }
            
            filtersPredicates.add(cb.equal(fieldPath.as(String.class),
            							   filterVal));
        }    
        
        cq.where(filtersPredicates.toArray(new Predicate[filtersPredicates.size()]));
        
        // Now I add all the desired orders criteria
        List<Order> orderCriteria = new ArrayList<>();
        Expression<Path<T>> exp;
        
        for (Map.Entry<E2, OrderMode> entry : ordersMap.entrySet()) {
            E2 orderBy = entry.getKey();
            OrderMode orderMode = entry.getValue();
            
            exp = entity.get(orderBy.toString());
            orderCriteria.add(
                              orderMode.equals(OrderMode.DESC) ? cb.desc(exp) : cb.asc(exp)
                             );
        }        
        
        // If id order was not added I added as an extra criteria
        if (! ordersMap.containsKey("id")) {
	        exp = entity.get("id");
	        orderCriteria.add(cb.asc(exp));
        }
        
        cq.orderBy(orderCriteria);
        
        TypedQuery<T> q = em.createQuery(cq);
        return q.getResultList();
    }
    
    /**
     * Removes the entity.
     *
     * @param cls the Class type of the entity to remove
     * @param id the id of the entity to remove
     */
    public void removeEntity(Class<?> cls, long id) {
        em.getTransaction().begin();        
        em.remove(em.getReference(cls, id));
        em.flush();
        em.getTransaction().commit();
    }
    
    /**
     * Map filters.
     *
     * @param <E> the type of the enum representing the allowed filters
     * @param filtersString the list of filters string
     * @param allowedFilters the list of allowed filters
     * @return the map of filter_name -> filter_value
     * @throws Exception the exception when there are errors in filters format or they are not allowed
     */
    private <E extends Enum<E>> Map<E, String> mapFilters(List<String> filtersString, Class<E> allowedFilters) throws Exception {
        Map<E, String> filtersMap = new HashMap<>();
    	
    	for (String filter: filtersString) {
    		String[] filterCouple = filter.split(SEARCH_CRITERIA_SPLIT);
    		E filterName = Enum.valueOf(allowedFilters, filterCouple[0]);
    		filtersMap.put(filterName, filterCouple[1]);
    	}
    	
    	return filtersMap;	
    }
    
    /**
     * Map orders.
     *
     * @param <E> the type of the enum representing the allowed orders
     * @param ordersString the list of orders string
     * @param allowedOrders the list of allowed orders
     * @return the map of order_by -> order_mode
     * @throws Exception the exception when there are errors in orders format or they are not allowed
     */
    private <E extends Enum<E>> Map<E, OrderMode> mapOrders(List<String> ordersString, Class<E> allowedOrders) throws Exception {
        Map<E, OrderMode> ordersMap = new HashMap<>();
    	
    	for (String order: ordersString) {
    		String[] orderCouple = order.split(SEARCH_CRITERIA_SPLIT);
    		E orderBy = Enum.valueOf(allowedOrders, orderCouple[0]);
    		ordersMap.put(orderBy, OrderMode.valueOf(orderCouple[1].toUpperCase()));
    	}
    	
    	return ordersMap;	    	
    }

}
