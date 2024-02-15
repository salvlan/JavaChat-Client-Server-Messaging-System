import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

// Main class for the ChatClient
public class ChatClient {

    // GUI components
    JTextArea incoming;
    JTextField outGoing;

    // Input and output streams for communication with the server
    BufferedReader reader;
    PrintWriter writer;

    // Socket for connecting to the server
    Socket sock;

    // Main method to start the ChatClient
    public static void main(String[] args) {
        new ChatClient().go();
    }

    // Method to set up the GUI and networking for the ChatClient
    public void go (){
        JFrame frame = new JFrame("Chat Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();

        // Set up the text area for incoming messages
        incoming = new JTextArea(15,50);
        incoming.setLineWrap(true);
        incoming.setWrapStyleWord(true);
        incoming.setEditable(false);

        // Set up a scroll pane for the text area
        JScrollPane qScroller = new JScrollPane(incoming);
        qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // Set up the text field for outgoing messages
        outGoing = new JTextField(20);

        // Set up the send button and its action listener
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e->{
            try {
                // Send the message to the server
                writer.println(outGoing.getText());
                writer.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            // Clear the text field and focus on it
            outGoing.setText("");
            outGoing.requestFocus();
        });

        // Add components to the main panel
        mainPanel.add(qScroller);
        mainPanel.add(outGoing);
        mainPanel.add(sendButton);

        // Set up networking (connect to the server)
        setUpNetworking();

        // Set up a separate thread for reading incoming messages.
        // The Thread constructor expects a Runnable instance, and since Runnable is a functional interface,
        // the lambda expression can be used to represent an instance of that interface.
        Thread readerThread = new Thread(()->{
            String message;
            try {
                // Continuously read and display incoming messages
                while ((message = reader.readLine()) != null) {
                    System.out.println("read " + message);
                    incoming.append(message + "\n");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        readerThread.start();

        // Set up the frame and make it visible
        frame.getContentPane().add(BorderLayout.CENTER,mainPanel);
        frame.setSize(800,500);
        frame.setVisible(true);
    }

    // Method to set up the networking (connect to the server)
    private void setUpNetworking(){
        try{
            sock = new Socket("127.0.0.1", 5000);

            // Set up input and output streams for communication
            InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
            reader = new BufferedReader(streamReader);

            writer = new PrintWriter(sock.getOutputStream());

            System.out.println("networking established");
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

}

