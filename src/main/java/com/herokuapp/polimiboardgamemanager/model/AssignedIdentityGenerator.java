package com.herokuapp.polimiboardgamemanager.model;

import java.io.Serializable;

import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentityGenerator;

/**
 * A custom identity generator that can generate a new id or assign a specific id
 * when it's set.
 */
public class AssignedIdentityGenerator extends IdentityGenerator {

    /* (non-Javadoc)
     * @see org.hibernate.id.AbstractPostInsertGenerator#generate(org.hibernate.engine.spi.SessionImplementor, java.lang.Object)
     */
    @Override
    public Serializable generate(SessionImplementor session, Object obj) {
        if(obj instanceof Identifiable) {
            Identifiable<Serializable> identifiable = (Identifiable<Serializable>) obj;
            Serializable id = identifiable.getId();
            if(id != null) {
                return id;
            }
        }
        
        return super.generate(session, obj);
    }
}