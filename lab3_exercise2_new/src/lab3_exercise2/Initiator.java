package lab3_exercise2;

import java.io.*;
import java.net.*;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.*;
import java.util.Scanner;
import java.util.Base64;
import java.util.Random;

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
    JEncrypRSA RSACipher;
    
    Scanner chatMessageSC;
    
    Random nonce;
    
    SecretKey Ks;
    public Initiator(String host, int port) throws NoSuchAlgorithmException, NoSuchPaddingException{
        socket = this.setupSocket(host, port);
        DESCipher = JEncryptDES.getInstance();
        RSACipher = JEncrypRSA.getInstance();
        nonce = new Random();
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
    
    private void publicKeyDistribution(){
        // Initial Key distribution for A
        try
        {
            // Step 1: Generate RSA key pair and send public key to B
            KeyPair aKeys = RSACipher.getKeyPair();
            
            PublicKey aPublicKey = aKeys.getPublic();
            String encodedKey = Base64.getEncoder().encodeToString(aPublicKey.getEncoded());
            String initMessage = encodedKey;
            writer.write(initMessage);
            writer.newLine();
            writer.flush();

            // Step 4: receive public key from B
            String keyMessage = reader.readLine();
            byte[] decodedBKey = Base64.getDecoder().decode(keyMessage);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedBKey);
            KeyFactory keyFact = KeyFactory.getInstance("RSA");
            PublicKey pubKeyB = keyFact.generatePublic(keySpec);
            
            // Step 5: Send nonce and id to B
            int nonce1 = nonce.nextInt(9001) + 1;
            String idA = "A";
            String message1 = idA + " " + nonce1;
            String message1toSend = RSACipher.encode(pubKeyB, message1);
            writer.write(message1toSend);
            writer.newLine();
            writer.flush();
            
            // Step 7: Receive message 2 from B
            String message2 = RSACipher.decode(aKeys.getPrivate(), reader.readLine());
            
            String[] message2Split = message2.split(" ");
            String nonceA = message2Split[0];
            String nonceB = message2Split[1];
            
            String initialNonce = Integer.toString(nonce1);
            if(initialNonce.equals(nonceA))
            {
                // Step 8: send nonceB received from B back to B to ensure only communication between A and B
                String message3 = nonceB;
                String message3toSend = RSACipher.encode(pubKeyB, message3);
                writer.write(message3toSend);
                writer.newLine();
                writer.flush();

                // Step 9: generate DES SecretKey and send to B
                KeyGenerator DESkeyGen = KeyGenerator.getInstance("DES");
                Ks = DESkeyGen.generateKey();
                String encodedSecKey = Base64.getEncoder().encodeToString(Ks.getEncoded());
                String priKeyEncoded = RSACipher.encodeWithPrivate(aKeys.getPrivate(), encodedSecKey);

                String message4part1 = priKeyEncoded.substring(0, priKeyEncoded.length()/2);
                String message4part2 = priKeyEncoded.substring(priKeyEncoded.length()/2);
                String message4part1toSend = RSACipher.encode(pubKeyB, message4part1);
                String message4part2toSend = RSACipher.encode(pubKeyB, message4part2);
                writer.write(message4part1toSend);
                writer.newLine();
                writer.write(message4part2toSend);
                writer.newLine();
                writer.flush();
            }
            else
            {
                System.out.println("ERROR: A's Nonces do not match");
            }
       }
       catch(Exception e)
       {
           System.out.println("Error during key distribution: "+ e.getLocalizedMessage());
       }
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
        
        // Initial Key distribution for A
        this.publicKeyDistribution();
        
        Reciever recieverA = new Reciever(reader, DESCipher, Ks);
        recieverA.start();
        
        Sender senderA = new Sender(writer, DESCipher, Ks);
        senderA.start();
    }
    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException{
        Initiator i = new Initiator("localhost", 10104);
        i.start();
    }
}
