package com.herokuapp.polimiboardgamemanager.model;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

/**
 * Constants common for many classes.
 * @author Luca Luciano Costanzo
 *
 */
public interface CommonConstants {
	/** Base URI of the API. */
	public static final URI API_URI = UriBuilder.fromUri("https://polimi-board-game-manager.herokuapp.com/api/").build();
}
