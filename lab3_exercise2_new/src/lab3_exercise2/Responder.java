package lab3_exercise2;

import java.io.*;
import java.net.*;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.*;
import java.util.Scanner;
import java.util.Base64;
import javax.crypto.spec.SecretKeySpec;
import java.util.Random;

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
    JEncrypRSA RSACipher;
    
    Scanner chatMessageSC;
    
    Random nonce;
    
    SecretKey Ks;
    
    public Responder(int port) throws NoSuchAlgorithmException, NoSuchPaddingException{
        socket = setupSocket(port);
        DESCipher = JEncryptDES.getInstance();
        RSACipher = JEncrypRSA.getInstance();
        nonce = new Random();
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
    
    private void publicKeyDistribution(){
        try
        {
            // Step 2: Receive public key from A
            String initMessage = reader.readLine();
            byte[] decodedAKey = Base64.getDecoder().decode(initMessage);

            // Step 3: Generate RSA keypair and send public key to B
            KeyPair bKeys = RSACipher.getKeyPair();
            String encodedKey = Base64.getEncoder().encodeToString(bKeys.getPublic().getEncoded());
            writer.write(encodedKey);
            writer.newLine();
            writer.flush();

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedAKey);
            KeyFactory keyFact = KeyFactory.getInstance("RSA");
            PublicKey pubKeyA = keyFact.generatePublic(keySpec);

            // Step 6: receive A's id and nonce
            String message1 = reader.readLine();
            String message1decoded = RSACipher.decode(bKeys.getPrivate(), message1);               
            String[] message1Split = message1decoded.split(" ");
            String nonceA = message1Split[1];

            // Step 7: generate B's nonce and send to A
            int nonceB = nonce.nextInt(9001) + 1;
            String message2 = nonceA + " " + nonceB;
            String message2toSend = RSACipher.encode(pubKeyA, message2);
            writer.write(message2toSend);
            writer.newLine();
            writer.flush();

            // Step 8: receive nonceB from A and check if it equals nonceB
            String message3 = RSACipher.decode(bKeys.getPrivate(), reader.readLine());
            
            if(Integer.toString(nonceB).equals(message3))
            {
                // Step 10: receive message #4 and decrypt to get Ks
                String message4part1 = RSACipher.decode(bKeys.getPrivate(), reader.readLine());
                String message4part2 = RSACipher.decode(bKeys.getPrivate(), reader.readLine());
                String concat1 = message4part1 + message4part2;
                
                String message4Ks1 = RSACipher.decodeWithPublic(pubKeyA, concat1);

                byte[] decodedKs = Base64.getDecoder().decode(message4Ks1);
                
                // Main key to use with message encryption:
                Ks = new SecretKeySpec(decodedKs, 0, decodedKs.length, "DES");
            }
            else
            {
                System.out.println("ERROR: B's Nonces do not match");
            }
        }
        catch(Exception e)
        {
            System.out.println("Error: "+ e.getLocalizedMessage());
        }
    }
    
    // This acts as B
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
        
        this.publicKeyDistribution();
        
        Reciever recieverB = new Reciever(reader, DESCipher, Ks);
        recieverB.start();
        
        Sender senderB = new Sender(writer, DESCipher, Ks);
        senderB.start();
    }
    
    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException{
        Responder r = new Responder(10104);
        r.start();
    }
}
