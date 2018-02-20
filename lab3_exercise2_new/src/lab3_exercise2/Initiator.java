package lab3_exercise2;

import java.io.*;
import java.net.*;
import java.security.*;
import javax.crypto.*;
import java.util.Scanner;
import java.util.Base64;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

/**
 *
 * @author brentmarks
 */
public class Initiator extends Thread // Client
{
    private final Socket socket;
    
    OutputStreamWriter outSW;
    BufferedWriter writer;
    InputStreamReader inSR;
    BufferedReader reader;
    
    JEncryptDES DESCipher;
    
    boolean run;
    Scanner chatMessageSC;
    
    private final Base64.Decoder decoder;
    private final Base64.Encoder encoder;
    
    SecretKey Ks;
    public Initiator(String host, int port) throws NoSuchAlgorithmException, NoSuchPaddingException{
        socket = this.setupSocket(host, port);
        DESCipher = JEncryptDES.getInstance();
        run = true;
        decoder = Base64.getDecoder();
        encoder = Base64.getEncoder();
    }
    
    private Socket setupSocket(String host, int port){
        Socket s = null;
        try
        {
            s = new Socket(host, port);
        }
        catch(IOException e)
        {
            System.out.println("Error: " + e);
        }
        return s;
    }
    
    
    // This acts as A
    @Override
    public void run() {
        System.out.println("Initiator");        
        try
        {
            inSR = new InputStreamReader(socket.getInputStream());
            reader = new BufferedReader(inSR);
            outSW = new OutputStreamWriter(socket.getOutputStream());
            writer = new BufferedWriter(outSW);
            chatMessageSC = new Scanner(System.in); 
        }
        catch(IOException e)
        {
            System.out.println("Error: "+ e.getLocalizedMessage());
        }
            //Initial Key distribution for A
            try
            {
                KeyPair initKeys = DESCipher.getKeyPair();
                String initID = "A";
                String encodedKey = Base64.getEncoder().encodeToString(initKeys.getPublic().getEncoded());
                String initMessage = encodedKey+" "+initID;
                writer.write(initMessage);
                writer.newLine();
                writer.flush();
                
                String receivedSecKs = reader.readLine();
                System.out.println("B's private key: "+receivedSecKs);
                byte[] decodedKs = Base64.getDecoder().decode(receivedSecKs);
                //Main key to use with message encryption:
                Ks = new SecretKeySpec(decodedKs, 0, decodedKs.length, "DES");
            }
            catch(NoSuchAlgorithmException|IOException e)
            {
                System.out.println("Error: "+ e.getLocalizedMessage());
            }
        
        Thread reciever = new Thread(() -> {
            while(run)
            {
                try 
                {   
                    String recievedMessage = reader.readLine();  
                    String outMessage = DESCipher.decodeMessage(recievedMessage,Ks);
                    System.out.println("Decoded Message: " + outMessage + " (received at: "+new Date()+")");
                } 
                catch (Exception e) 
                {
                    System.out.println("Error: " + e);
                }
            }
        });
        reciever.start();
        
        Thread sender = new Thread(() -> {
            while(run)
            {
                try 
                {
                    String message = chatMessageSC.nextLine();
                    String finalMessage = DESCipher.encodeMessage(message, Ks);
                    writer.write(finalMessage);
                    writer.newLine();
                    writer.flush();
                } 
                catch (IOException e) 
                {
                    System.out.println("Error: " + e);
                }
            }
        });
        sender.start();
    }
    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException{
        Initiator i = new Initiator("localhost", 10104);
        i.start();
    }
}
