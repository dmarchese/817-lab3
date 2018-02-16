/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg817.exercise1;

import java.util.Scanner;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

/**
 *
 * @author domenic
 */
public class JEncrypRSA {
    
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.println("(RSA) Please enter this message (No body can see me)");
        String input = sc.nextLine();
        
        KeyPair keys = getKeyPair();
        byte[] encodedText = encode(keys.getPublic(),input);
        System.out.println("Encoded text with RSA public key: "+encodedText);
        System.out.println("Decoded text with RSA private key: "+new String(decode(keys.getPrivate(),encodedText)));
    }
    
    public static KeyPair getKeyPair() throws NoSuchAlgorithmException {
        final int keySize = 2048;
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);      
        return keyPairGenerator.genKeyPair();
    }

    public static byte[] encode(PublicKey publicKey, String message) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");  
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);  

        return cipher.doFinal(message.getBytes());  
    }
    
    public static byte[] decode(PrivateKey privateKey, byte [] encryptedText) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");  
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        
        return cipher.doFinal(encryptedText);
}
    
}
