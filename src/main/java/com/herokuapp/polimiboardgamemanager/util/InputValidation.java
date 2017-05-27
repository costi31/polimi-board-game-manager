/**
 * 
 */
package com.herokuapp.polimiboardgamemanager.util;

/**
 * @author Luca Luciano Costanzo
 *
 */
public class InputValidation {
	
	public static final String USERNAME_ALLOWED_CHARACTERS = "[A-Za-z0-9]+";
	public static final String PASSWORD_ALLOWED_CHARACTERS = "[\\w\\Q!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~\\E]+";
	public static final String GENERIC_INPUT_ALLOWED_CHARACTERS = "[A-Za-z0-9 ;,.'\"-]+";
	
	private InputValidation() {
		/*
		 * Empty and private constructor because this class has only static methods
		 */
	}	
	
	/**
	 * Checks if a username input is valid, i.e. if it contains legal characters
	 * @param username the username String to check
	 * @return <b>true</b> if the username is valid, <b>false</b> otherwise.
	 */
	public static boolean isUsernameValid(String username) {	
		return username.matches(USERNAME_ALLOWED_CHARACTERS);
	}
	
	/**
	 * Checks if a password input is valid, i.e. if it contains legal characters
	 * @param password the password String to check
	 * @return <b>true</b> if the password is valid, <b>false</b> otherwise.
	 */
	public static boolean isPasswordValid(String password) {	
		return password.matches(PASSWORD_ALLOWED_CHARACTERS);
	}
	
	/**
	 * Checks if a generic input is valid, i.e. if it contains legal characters
	 * @param input the password String to check
	 * @return <b>true</b> if the password is valid, <b>false</b> otherwise.
	 */
	public static boolean isGenericInputValid(String input) {	
		return input.matches(GENERIC_INPUT_ALLOWED_CHARACTERS);
	}
	

}
