package com.herokuapp.polimiboardgamemanager.filter;

import javax.ws.rs.NameBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The Interface Secured.
 */
@NameBinding
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface Secured { }