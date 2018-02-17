package lab3_exercise2;

import java.io.*;
import java.net.*;
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
    
    boolean run;
    Scanner chatMessageSC;
    
    public Initiator(String host, int port){
        socket = this.setupSocket(host, port);
        //chatMessageSC = new Scanner(System.in);
        run = true;
    }
    
    private void sendMessage(Socket s, String message) {
        try
        {
            OutputStreamWriter outSW1 = new OutputStreamWriter(s.getOutputStream());
            BufferedWriter writer1 = new BufferedWriter(outSW1);

            System.out.println("Initiator - sent: " + message);
            writer1.write(message);
            writer1.newLine();
            writer1.flush();
        }
        catch(IOException e)
        {
            System.out.println("Error: " + e);
        }
    }
    
    private String recieveMessage(Socket s) {
        String line, message = null;
        try
        {
            InputStreamReader inputSR = new InputStreamReader(s.getInputStream());
            BufferedReader reader = new BufferedReader(inputSR);

            while ((line = reader.readLine()) != null) 
            {
                message = line;
                System.out.println("Initiator - recieved: " + line);
                break;
            }
        }
        catch(IOException e)
        {
            System.out.println("Error: " + e);
        }
        return message;
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
        Thread reciever = new Thread(() -> {
            System.out.println("got here");
            try
            {
                inSR = new InputStreamReader(socket.getInputStream());
                reader = new BufferedReader(inSR);
            }
            catch(IOException e)
            {
                System.out.println("Error: e");
            }
            while(run)
            {
                try 
                {   
                    String recievedMessage = reader.readLine();
                    System.out.println("Recieved Message: " + recievedMessage);
                    if(chatMessageSC.nextLine().compareTo("QUIT") == 0){
                        run = false;
                    }
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
                    outSW = new OutputStreamWriter(socket.getOutputStream());
                    writer = new BufferedWriter(outSW);
                    chatMessageSC = new Scanner(System.in);
                    //String message = chatMessageSC.nextLine();
                    writer.write(chatMessageSC.nextLine());
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
    public static void main(String[] args){
        Initiator i = new Initiator("localhost", 10104);
        i.start();
    }
}
