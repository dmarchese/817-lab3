package lab3_exercise2;

import java.security.*;
import javax.crypto.*;
import java.util.Base64;

/**
 *
 * @author domenic
 */
public class JEncryptDES {

    private final Cipher desCipher;
    private final Base64.Decoder decoder;
    private final Base64.Encoder encoder;
    
    private static JEncryptDES instance = null;

    private JEncryptDES() throws NoSuchAlgorithmException, NoSuchPaddingException {
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
    
    public String decodeMessage(String message,SecretKey key) {
        String decryptedMessage = null;
        try
        {         
            desCipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encryptedTextByte = decoder.decode(message);
            byte[] decryptedByte = desCipher.doFinal(encryptedTextByte);
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
            desCipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedMessage = desCipher.doFinal(message.getBytes());
            encryptedText = encoder.encodeToString(encryptedMessage);
        }
        catch(InvalidKeyException | BadPaddingException | IllegalBlockSizeException e)
        {
            System.out.println("Error Encoding Message: " + e.getMessage());
        }
        return encryptedText;
    }
    
    public static KeyPair getKeyPair() throws NoSuchAlgorithmException {
        final int keySize = 512;
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);      
        return keyPairGenerator.genKeyPair();
    }
}
