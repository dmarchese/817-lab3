package lab3_exercise2;

import java.io.*;
import java.net.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.*;
import java.util.Scanner;
import java.util.Base64;
import java.util.Date;
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
    
    SecretKey Ks;
    
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
        
            try
            {
                //Initial Key distribution for B
                String[] initMessage = reader.readLine().split(" ");
                System.out.println("A's public key: "+initMessage[0]);
                System.out.println("A's ID: "+initMessage[1]);
                
                KeyPair initKeys = DESCipher.getKeyPair();
                String encodedKey = Base64.getEncoder().encodeToString(initKeys.getPrivate().getEncoded());
                byte[] decodedKey = Base64.getDecoder().decode(initMessage[0]);
                
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
                KeyFactory keyFact = KeyFactory.getInstance("RSA");
                PublicKey pubKey = keyFact.generatePublic(keySpec);
                
                KeyGenerator DESkeyGen = KeyGenerator.getInstance("DES");
                Ks = DESkeyGen.generateKey();
                String encodedSecKey = Base64.getEncoder().encodeToString(Ks.getEncoded());
                byte[] priKeyEncoded = DESCipher.encode(pubKey,encodedSecKey);//encodedKey is too long
                System.out.println("private key to send: "+priKeyEncoded.toString());
                writer.write(priKeyEncoded.toString());
                writer.newLine();
                writer.flush();
                
            }
            catch(Exception e)
            {
                e.printStackTrace();
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
        Responder r = new Responder(10104);
        r.start();
    }
}
