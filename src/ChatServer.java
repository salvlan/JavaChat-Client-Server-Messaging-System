import java.io.*;
import java.net.*;
import java.util.*;

// Main class for the ChatServer
public class ChatServer {

    // ArrayList to store client output streams
    ArrayList clientOutputStream;

    // Main method to start the ChatServer
    public static void main(String[] args) {
        new ChatServer().go();
    }

    // Inner class for handling individual client connections
    public class ClientHandler implements Runnable {
        BufferedReader reader;
        Socket sock;

        // Constructor for ClientHandler, initializes input stream for the client
        public ClientHandler (Socket clientSocket){
            try {
                sock = clientSocket;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader (isReader);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Run method to continuously read messages from the client and broadcast them
        public void run (){
            String message;
            try{
                while ((message = reader.readLine()) != null){
                    System.out.println("read " + message);
                    tellEveryone (message); // Broadcast the received message to all clients
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // Method to start the ChatServer
    public void go(){
        clientOutputStream = new ArrayList();

        try {
            ServerSocket serverSock = new ServerSocket(5000);

            // Continuously accept new client connections
            while (true){
                Socket clientSocket = serverSock.accept(); // Wait for a client to connect
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                clientOutputStream.add(writer); // Add client's output stream to the list

                Thread t = new Thread(new ClientHandler(clientSocket)); // Create a thread for handling the new client
                t.start(); // Start the thread
                System.out.print("got a connection"); // Print a message when a connection is established
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Method to broadcast a message to all connected clients
    public void tellEveryone (String message){
        Iterator it = clientOutputStream.iterator();

        // Iterate through all client output streams and send the message
        while (it.hasNext()){
            try {
                PrintWriter writer = (PrintWriter) it.next();
                writer.println(message);
                writer.flush(); // Flush the writer to send the message immediately
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

}