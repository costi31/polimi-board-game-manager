package com.herokuapp.polimiboardgamemanager.util;

import org.apache.commons.validator.routines.UrlValidator;

/**
 * Class with only static methods to check if input strings are valid
 * @author Luca Luciano Costanzo
 *
 */
public class InputValidator {
	
	public static final String USERNAME_ALLOWED_CHARACTERS = "[A-Za-z0-9]+";
	public static final String PASSWORD_ALLOWED_CHARACTERS = "[\\w\\Q!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~\\E]+";
	public static final String GENERIC_INPUT_ALLOWED_CHARACTERS = "[A-Za-z0-9 ;,.'\"-]+";
	
	private InputValidator() {
		/*
		 * Empty and private constructor because this class has only static methods
		 */
	}	
	
	/**
	 * Checks if a username input is valid, i.e. if it contains legal characters
	 * @param username the username String to check
	 * @return <b>true</b> if the username is valid, <b>false</b> otherwise.
	 */
	public static boolean isValidUsername(String username) {
		if (username == null)
			return false;
		
		return username.matches(USERNAME_ALLOWED_CHARACTERS);
	}
	
	/**
	 * Checks if a password input is valid, i.e. if it contains legal characters
	 * @param password the password String to check
	 * @return <b>true</b> if the password is valid, <b>false</b> otherwise.
	 */
	public static boolean isValidPassword(String password) {	
		if (password == null)
			return false;
		
		return password.matches(PASSWORD_ALLOWED_CHARACTERS);
	}
	
	/**
	 * Checks if a generic input is valid, i.e. if it contains legal characters
	 * @param input the password String to check
	 * @return <b>true</b> if the password is valid, <b>false</b> otherwise.
	 */
	public static boolean isValidGenericInput(String input) {	
		if (input == null)
			return false;
		
		return input.matches(GENERIC_INPUT_ALLOWED_CHARACTERS);
	}
	
	/**
	 * Checks if an url is valid, i.e. if it contains legal characters
	 * @param url the url String to check
	 * @return <b>true</b> if the url is valid, <b>false</b> otherwise.
	 */	
	public static boolean isValidUrl(String url) {	
		if (url == null)
			return false;
		
	    String[] schemes = {"http","https"};
	    UrlValidator urlValidator = new UrlValidator(schemes);
		return urlValidator.isValid(url);
	}
	
}
