package lab3_exercise2;

import java.security.*;
import java.util.Scanner;
import javax.crypto.*;
import java.util.Base64;

/**
 *
 * @author domenic
 */
public class JEncryptDES {

    private final KeyGenerator DESkeyGen;
    private final SecretKey desKey;
    private final Cipher desCipher;
    private final Base64.Decoder decoder;
    private final Base64.Encoder encoder;
    
    private static JEncryptDES instance = null;

    private JEncryptDES() throws NoSuchAlgorithmException, NoSuchPaddingException {
        DESkeyGen = KeyGenerator.getInstance("DES");
        desKey = DESkeyGen.generateKey();
        desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        decoder = Base64.getDecoder();
        encoder = Base64.getEncoder();
    }
    
    public static JEncryptDES getInstance() throws NoSuchAlgorithmException, NoSuchPaddingException {
        if(instance == null) {
            instance = new JEncryptDES();
        }
      return instance;
   }
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter this message (No body can see me)");
        String input = sc.nextLine();
        
        try
        {
            JEncryptDES DES = new JEncryptDES();

            //String encryptedMessage = DES.encodeMessage(input);
            //System.out.println("Encrypted message: " + encryptedMessage);
            //System.out.println("Decrypted message: " + DES.decodeMessage(encryptedMessage));
        }
        catch(NoSuchAlgorithmException | NoSuchPaddingException e)
        {
            System.out.print(e.getMessage());
        }
    }
    
    public String decodeMessage(String message,SecretKey key) {
        String decryptedMessage = null;
        try
        {         
            System.out.println("decode key: "+key);
            desCipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encryptedTextByte = decoder.decode(message);
            byte[] someData = desCipher.update(encryptedTextByte);
            byte[] decryptedByte = desCipher.doFinal();
            String decryptedText = new String(decryptedByte);
            decryptedMessage = decryptedText;
        }
        catch(InvalidKeyException | BadPaddingException | IllegalBlockSizeException e)
        {
            System.out.println("Error Decoding Message: " + e.getMessage());
        }
        return decryptedMessage;
    }
    
    public String encodeMessage(String message,SecretKey key){
        String encryptedText = null;
        try
        {
            //desCipher.getClass()
            System.out.println("encode key: "+key);
            desCipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] someData = desCipher.update(message.getBytes());
            byte[] encryptedMessage = desCipher.doFinal();
            encryptedText = encoder.encodeToString(encryptedMessage);
        }
        catch(InvalidKeyException | BadPaddingException | IllegalBlockSizeException e)
        {
            System.out.println("Error Encoding Message: " + e.getMessage());
        }
        return encryptedText;
    }
}
