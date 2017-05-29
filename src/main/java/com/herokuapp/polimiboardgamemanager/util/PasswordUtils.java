package com.herokuapp.polimiboardgamemanager.util;

import java.security.MessageDigest;
import javax.xml.bind.DatatypeConverter;

/**
 * The Class PasswordUtils.
 */
public class PasswordUtils {
	
	/**
	 * Instantiates a new password utils.
	 */
	private PasswordUtils() {
		/*
		 * Empty and private constructor because this class has only static methods
		 */
	}

    // ======================================
    // =          Business methods          =
    // ======================================

    /**
     * Computes the password digest in SHA-256.
     *
     * @param plainTextPassword the plain text password
     * @return the string representing the digest of the password
     */
    public static String digestPassword(String plainTextPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(plainTextPassword.getBytes("UTF-8"));
            byte[] passwordDigest = md.digest();
            return DatatypeConverter.printBase64Binary(passwordDigest);
        } catch (Exception e) {
            throw new RuntimeException("Exception encoding password", e);
        }
    }
}