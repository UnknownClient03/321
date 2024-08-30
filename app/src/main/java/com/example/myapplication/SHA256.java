package com.example.myapplication;

import java.security.MessageDigest;
import java.util.Random;

public class SHA256 {
    //method that converts a string into a hashed string
    public static String convert(final String base) {
        try{
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digest.digest(base.getBytes("UTF-8"));
            final StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                final String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public static String randomUTF8(int size)
    {
        Random rand = new Random();
        StringBuilder output = new StringBuilder();
        for(int i = 0; i < size; i++)
        {
            int randInt = 32 + rand.nextInt(96);
            output.append((char)randInt);
        }
        return output.toString();
    }
}
