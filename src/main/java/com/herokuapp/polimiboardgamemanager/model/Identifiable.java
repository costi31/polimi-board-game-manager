package com.herokuapp.polimiboardgamemanager.model;

import java.io.Serializable;

/**
 * The Interface Identifiable.
 *
 * @param <T> the generic type
 */
public interface Identifiable<T extends Serializable> {
    
    /**
     * Gets the id.
     *
     * @return the id
     */
    T getId();
}
