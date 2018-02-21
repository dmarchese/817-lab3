package lab3_exercise2;

import java.util.Scanner;
import java.security.*;
import java.util.Base64;
import javax.crypto.*;

/**
 *
 * @author domenic
 */
public class JEncrypRSA {
    
    private final Cipher cipher;
    
    private final Base64.Decoder decoder;
    private final Base64.Encoder encoder;
    
    private static JEncrypRSA instance = null;
    
    public JEncrypRSA() throws NoSuchAlgorithmException, NoSuchPaddingException {
        cipher = Cipher.getInstance("RSA");
        decoder = Base64.getDecoder();
        encoder = Base64.getEncoder();
    }
    
    public static JEncrypRSA getInstance() throws NoSuchAlgorithmException, NoSuchPaddingException {
        if(instance == null) {
            instance = new JEncrypRSA();
        }
      return instance;
    }
    
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.println("(RSA) Please enter this message (No body can see me)");
        String input = sc.nextLine();
        
        JEncrypRSA RSACipher = new JEncrypRSA();
        KeyPair keys = RSACipher.getKeyPair();
        
        String encodedText = RSACipher.encode(keys.getPublic(), input);
        System.out.println("Encoded text with RSA public key: " + encodedText);
        System.out.println("Decoded text with RSA private key: " + RSACipher.decode(keys.getPrivate(), encodedText));
    }
    
    public KeyPair getKeyPair() throws NoSuchAlgorithmException {
        final int keySize = 512;
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);      
        return keyPairGenerator.genKeyPair();
    }

    public String encode(PublicKey publicKey, String message) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] someData = cipher.update(message.getBytes());
        byte[] encodedTextBytes = cipher.doFinal();
        String encodedText = encoder.encodeToString(encodedTextBytes);
        return encodedText;
    }

    public String decode(PrivateKey privateKey, String encryptedText) throws Exception {  
        this.cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] encryptedTextBytes = decoder.decode(encryptedText);
        byte[] decodedTextBytes = this.cipher.doFinal(encryptedTextBytes);
        String decodedText = new String(decodedTextBytes);
        return decodedText;
    }
    
    public String encodeWithPrivate(PrivateKey privateKey, String message) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] encodedTextBytes = cipher.doFinal(message.getBytes());
        String encodedText = encoder.encodeToString(encodedTextBytes);
        return encodedText;
    }

    public String decodeWithPublic(PublicKey publicKey, String encryptedText) throws Exception {  
        this.cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] encryptedTextBytes = decoder.decode(encryptedText);
        byte[] decodedTextBytes = this.cipher.doFinal(encryptedTextBytes);
        String decodedText = new String(decodedTextBytes);
        return decodedText;
    }
}
