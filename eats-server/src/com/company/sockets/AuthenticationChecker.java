package com.company.sockets;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by brian on 4/16/16.
 */
public class AuthenticationChecker {

    public Request.RequestAuthentication auth;

    public AuthenticationChecker(Request.RequestAuthentication auth){
        this.auth = auth;
    }

    public boolean isValid(){
        if(auth.username.equals("admin") && auth.password.equals(hash("password"))){
            return true;
        }else{
            return false;
        }
    }


    public static String hash(String toHash){
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        m.reset();
        m.update(toHash.getBytes());
        byte[] digest = m.digest();
        BigInteger bigInt = new BigInteger(1,digest);
        String hashtext = bigInt.toString(16);
        while(hashtext.length() < 32 ){
            hashtext = "0"+hashtext;
        }
        return hashtext;
    }
}

