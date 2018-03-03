package lab3_exercise2;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;
import javax.crypto.SecretKey;

/**
 *
 * @author brentmarks
 */
public class Sender extends Thread {
    
    private Scanner chatMessageSC;
    private final BufferedWriter writer;
    
    private final JEncryptDES DESCipher;
    
    private final SecretKey Ks;
    
    public Sender(BufferedWriter writer, JEncryptDES DESCipher, SecretKey Ks) {
        this.writer = writer;
        this.DESCipher = DESCipher;
        this.Ks = Ks;
    }
    
    @Override
    public void run() {
        chatMessageSC = new Scanner(System.in);
        
        while(true)
        {
            try 
            {
                String message = chatMessageSC.nextLine();
                String messageWithTime = message + "~" + new Date().getTime();
                String finalMessage = DESCipher.encodeMessage(messageWithTime, Ks);
                
                writer.write(finalMessage);
                writer.newLine();
                writer.flush();
            } 
            catch (IOException e) 
            {
                System.out.println("Error: " + e);
            }
        }
    }
}
