package lab3_exercise2;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import javax.crypto.SecretKey;

/**
 *
 * @author brentmarks
 */
public class Reciever extends Thread {
    
    private final BufferedReader reader;
    
    private final JEncryptDES DESCipher;
    
    private final SecretKey Ks;
    
    public Reciever(BufferedReader reader, JEncryptDES DESCipher, SecretKey Ks){
        this.reader = reader;
        this.DESCipher = DESCipher;
        this.Ks = Ks;
    }
    
    @Override
    public void run() {
        while(true)
        {
            try 
            {
                String recievedMessage = reader.readLine();  
                String outMessage = DESCipher.decodeMessage(recievedMessage, Ks);

                String[] messageParts = outMessage.split("~");

                long timeStamp = Long.parseLong(messageParts[1]);

                long current = new Date().getTime();
                if(current - timeStamp < 1000){
                    System.out.println("Ciphertext of message: " + recievedMessage);

                    String message = messageParts[0];
                    System.out.println("Decryption of message: " + message);
                }
            } 
            catch (IOException e) 
            {
                System.out.println("Error: " + e);
            }
        }
    }
}
