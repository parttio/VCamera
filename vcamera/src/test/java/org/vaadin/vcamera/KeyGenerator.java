package org.vaadin.vcamera;

import java.util.Random;

public interface KeyGenerator {

    default String generateRandomKey() {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        char[] key = new char[4];
        Random r = new Random();
        for (int i = 0; i < key.length; i++) {
            key[i] = chars.charAt(r.nextInt(chars.length()));
        }
        return new String(key);
    }

}
