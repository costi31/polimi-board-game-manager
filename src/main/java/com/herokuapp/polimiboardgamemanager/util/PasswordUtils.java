package com.herokuapp.polimiboardgamemanager.util;

import java.security.MessageDigest;
import javax.xml.bind.DatatypeConverter;

public class PasswordUtils {

    // ======================================
    // =          Business methods          =
    // ======================================

    public static String digestPassword(String plainTextPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(plainTextPassword.getBytes("UTF-8"));
            byte[] passwordDigest = md.digest();
            return new String(DatatypeConverter.printBase64Binary(passwordDigest));
        } catch (Exception e) {
            throw new RuntimeException("Exception encoding password", e);
        }
    }
}