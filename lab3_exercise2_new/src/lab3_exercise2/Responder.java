package lab3_exercise2;

import java.io.*;
import java.net.*;
import java.security.*;
import javax.crypto.*;
import java.util.Scanner;
import java.util.Base64;
import javax.crypto.spec.SecretKeySpec;
/**
 *
 * @author brentmarks
 */
public class Responder extends Thread // server
{
    private final Socket socket;
    
    OutputStreamWriter outSW;
    BufferedWriter writer;
    InputStreamReader inSR;
    BufferedReader reader;
    
    JEncryptDES DESCipher;
    
    boolean run;
    Scanner chatMessageSC;
    
    public Responder(int port) throws NoSuchAlgorithmException, NoSuchPaddingException{
        socket = setupSocket(port);
        DESCipher = JEncryptDES.getInstance();
        run = true;
    }
    
    private Socket setupSocket(int port){
        Socket s = null;
        try
        {
            ServerSocket ss = new ServerSocket(port);
            s = ss.accept();
        }
        catch(IOException e)
        {
            System.out.println("Error: " + e);
        }
        return s;
    }
    
    // this acts as B
    @Override
    public void run() 
    {
        System.out.println("Responder");
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
            System.out.println("Error: " + e);
        }
        
        Thread reciever = new Thread(() -> {
            while(run)
            {
                try 
                {
                    String recievedMessage = reader.readLine();
                    System.out.println(recievedMessage);
                    String[] splitString = recievedMessage.split("/");
                    //String decodedMessage = DESCipher.decodeMessage(recievedMessage,desKey);
                    System.out.println("Recieved Message: " + splitString[0]);
                    System.out.println("Key Message: " + splitString[1]);
                    byte[] decodedKey = Base64.getDecoder().decode(splitString[1]);
                    SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "DES");
                    String outMessage = DESCipher.decodeMessage(splitString[0],originalKey );
                    System.out.println("Decoded Message: " + outMessage);
                } 
                catch (IOException e) 
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
                    //String encodedMessage = DESCipher.encodeMessage(message);
                    writer.write(message);
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
        Responder r = new Responder(10104);
        r.start();
    }
}
