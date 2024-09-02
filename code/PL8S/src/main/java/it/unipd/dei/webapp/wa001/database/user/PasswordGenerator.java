package it.unipd.dei.webapp.wa001.database.user;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Password encoding manager
 */
public class PasswordGenerator {

    /**
     * Salt to be added to the password to be hashed
     */
    private static final String salt = "[B@6e022d6b";

    /**
     * Returns a password hashed using the SHA-1 algorithm
     *
     * @param passwordToHash in-clear password to be hashed
     * @return password hashed by SHA-1 algorithm
     * @throws NoSuchAlgorithmException if SHA-1 algorithm not found
     */
    public static String generatePassword(String passwordToHash) throws NoSuchAlgorithmException {

        String generatedPassword = null;

        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(salt.getBytes());

        byte[] bytes = md.digest(passwordToHash.getBytes());
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        generatedPassword = sb.toString();

        return generatedPassword;
    }

}
