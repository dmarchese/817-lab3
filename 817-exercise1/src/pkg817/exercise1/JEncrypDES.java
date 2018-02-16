/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg817.exercise1;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 *
 * @author domenic
 */
public class JEncrypDES {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter this message (No body can see me)");
        String input = sc.nextLine();
        
        byte[] message = input.getBytes();
        
        try
        {
            KeyGenerator DESkeyGen = KeyGenerator.getInstance("DES");
            SecretKey desKey = DESkeyGen.generateKey();

            Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            byte[] encryptedMessage = encodeMessage(desCipher, desKey, message );
            System.out.println("Encrypted message: "+encryptedMessage);
            System.out.println("Decrypted message: "+decodeMessage(desCipher,desKey,encryptedMessage));
        }
        
        catch(Exception e)
        {
            System.out.print(e.getMessage());
        }
        
    }
    
    public static String decodeMessage(Cipher cipher,SecretKey key, byte[] message ){
        try
        {
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedMessage = cipher.doFinal(message);
            return new String(decryptedMessage); 
        }
        catch(Exception e)
        {
            return "Error Decoding Message: "+e.getMessage();
        }
        
    }
    
    public static byte[] encodeMessage(Cipher cipher,SecretKey key, byte[] message ){
        try
        {
            cipher.init(Cipher.ENCRYPT_MODE,key);

            byte[] encryptedMessage = cipher.doFinal(message);
            return encryptedMessage; 
        }
        catch(Exception e)
        {
            return null;
        }
        
    }
    
}
