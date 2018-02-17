package lab3_exercise2;

import java.io.*;
import java.net.*;
import java.security.*;
import javax.crypto.*;
import java.util.Scanner;

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
    
    public Initiator(String host, int port) throws NoSuchAlgorithmException, NoSuchPaddingException{
        socket = this.setupSocket(host, port);
        DESCipher = JEncryptDES.getInstance();
        run = true;
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
            System.out.println("Error: e");
        }
        
        Thread reciever = new Thread(() -> {
            while(run)
            {
                try 
                {   
                    String recievedMessage = reader.readLine();
                    String decodedMessage = DESCipher.decodeMessage(recievedMessage);
                    System.out.println("Recieved Message: " + recievedMessage);
                    System.out.println("Decoded Message: " + decodedMessage);
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
        Initiator i = new Initiator("localhost", 10104);
        i.start();
    }
}
