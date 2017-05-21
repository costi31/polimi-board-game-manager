package com.herokuapp.polimiboardgamemanager.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.herokuapp.polimiboardgamemanager.model.Play;

public class PlayDao {
    
    private static final Logger LOGGER = LogManager.getLogger(PlayDao.class);
    
    private static PlayDao instance = null;
    
    /**
     * Separator between user id and username in the subject field of the token
     */
    public static final String SUBJECT_ID_SEPARATOR = "@";    
    
    /**
     * Gets the instance of BoardGameDao
     * @return instance of BoardGameDao
     */
    public static PlayDao getInstance() {
        if (instance == null)
            instance = new PlayDao();

        return instance;
    }

    private PlayDao() {
    }
    
    public Play findById(long id) {
        return (Play) MyEntityManager.getInstance().findEntity(Play.class, id);
    }
    
    public List<Play> findPlaysByUser(long userCreatorId, String orderByString, String orderTypeString) throws Exception {
        EntityManager em = MyEntityManager.getInstance().getEm();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Play> cq = cb.createQuery(Play.class);
        Root<Play> pl = cq.from(Play.class);
        cq.select(pl);
        // Filter the plays created by the desired user
        cq.where(cb.equal(pl.get("userCreator").get("id"), userCreatorId));
        
        // Get the order criteria
        // it throws exception if strings don't correspond to allowed enum values
        Play.OrderBy orderBy = Play.OrderBy.valueOf(orderByString);
        Play.OrderType orderType = Play.OrderType.valueOf(orderTypeString.toUpperCase());
        
        List<Order> orderCriteria = new ArrayList<Order>();
        Expression exp;
        if (! orderBy.equals(Play.OrderBy.id)) {
            exp = pl.get(orderBy.toString());
            orderCriteria.add(
                              (orderType.equals(Play.OrderType.DESC)) ? cb.desc(exp) : cb.asc(exp)
                             );
        }
        
        exp = pl.get(Play.OrderBy.id.toString());
        orderCriteria.add(
                          (orderType.equals(Play.OrderType.DESC)) ? cb.desc(exp) : cb.asc(exp)
                         );
        
        cq.orderBy(orderCriteria);
        
        TypedQuery<Play> q = em.createQuery(cq);
        return q.getResultList();
    }    
}
