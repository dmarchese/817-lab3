package lab3_exercise2;

import java.io.*;
import java.net.*;
import java.util.Scanner;

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
    
    boolean run;
    Scanner chatMessageSC;
    
    public Responder(int port){
        socket = setupSocket(port);
        //chatMessageSC = new Scanner(System.in);
        run = true;
    }
    
    private void sendMessage(Socket s, String message) {
        try
        {
            OutputStreamWriter outSW = new OutputStreamWriter(s.getOutputStream());
            BufferedWriter writer = new BufferedWriter(outSW);

            System.out.println("Responder - sent: " + message);
            writer.write(message);
            writer.newLine();
            writer.flush();
        }
        catch(IOException e)
        {
            System.out.println("Error: " + e);
        }
    }
    
    private String recieveMessage(Socket s) {
        String message = null;
        try 
        {
            InputStreamReader inputSR = new InputStreamReader(s.getInputStream());
            BufferedReader reader = new BufferedReader(inputSR);
            
            String line;
            while ((line = reader.readLine()) != null) {
                message = line;
                System.out.println("Responder - recieved: " + message);
                break;
            }
        }
        catch(IOException e)
        {
            System.out.println("Error: " + e);
        }
        return message;
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
        Thread reciever = new Thread(() -> {
            System.out.println("got here");
            while(run)
            {
                try 
                {
                    inSR = new InputStreamReader(socket.getInputStream());
                    reader = new BufferedReader(inSR);
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
    
    public static void main(String[] args){
        Responder r = new Responder(10104);
        r.start();
    }
}
