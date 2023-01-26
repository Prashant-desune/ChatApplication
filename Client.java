import java.io.*;
import java.net.*;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Font;
import java.awt.BorderLayout;


public class Client extends JFrame {
    Socket socket;
    BufferedReader br;
    PrintWriter out; 

    // Declare Components : 
    private JLabel heading = new JLabel("Client Area");
    private JTextArea messageArea = new JTextArea();
    private  JTextField messageInput = new JTextField();
    private Font font = new Font("Roboto",Font.PLAIN,20);

    // make constructor 
    public Client(){
        try {
            System.out.println("Sending request to server");
            socket = new Socket("127.0.0.1",7778); 
            // for request
            System.out.println("Connection done. ");

            br = new BufferedReader(new InputStreamReader
                (socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            createGUI();
            handleEvents();
            startReading();
                // startWriting();

        } catch (Exception e) {
            
            // e.printStackTrace();
        }
    }


    private void handleEvents() {

        messageInput.addKeyListener(new KeyListener(){

            @Override
            public void keyPressed(KeyEvent e) {
                
                
            }

            @Override
            public void keyTyped(KeyEvent e) {
                
            }

            @Override
            public void keyReleased(KeyEvent e) {
                System.out.println("key released" + e.getKeyCode());
                if(e.getKeyCode()==10){
                    String contentToSend = messageInput.getText();
                    messageArea.append("Me : "+contentToSend+"\n");
                    out.println(contentToSend);
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();
                }
            }
        });
    }


    private void createGUI(){
        //gui code....
        this.setTitle("Client Messanger[END]");
        this.setSize(600,700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // coding for components : 
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);
        heading.setIcon(new ImageIcon("./icon.png"));
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        messageArea.setEditable(false);
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);

        // Frame Layout
        this.setLayout(new BorderLayout());

        // Adding components to the frame 
        this.add(heading,BorderLayout.NORTH);
        JScrollPane jScrollPane = new JScrollPane(messageArea);
        this.add(jScrollPane,BorderLayout.CENTER);
        this.add(messageInput,BorderLayout.SOUTH);
        
        
        this.setVisible(true);
    }
    // we use the concept of multithreading in the methods 
    public void startReading(){
        // thread - gives us reading value 
        // lambda expression
        Runnable r1 =()->{
            System.out.println("Reader Started..");
            // infinite loop for reading message 
            try{
            while(true){
                
                String msg = br.readLine();
                if(msg.equals("good bye")){
                    System.out.println("Server terminated the chat");
                    JOptionPane.showMessageDialog(this, "Server Terminated the chat");
                    messageInput.setEnabled(false);
                    socket.close(); 
                    break;
                }
                // System.out.println("Server : " + msg);
                messageArea.append("Server : " + msg+"\n");
            }
        }catch(Exception e){
            // e.printStackTrace();
            System.out.println("Connection Closed ...");
        }
        };
        // for start thread make thread class
        new Thread(r1).start();
    }

    public void startWriting(){
        // thread - take data from the user and send it to the client 
        Runnable r2 =()->{
            System.out.println("Writer started ...");
            // infinite loop for writing  message 
            try {
            while(!socket.isClosed()){
                
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String content = br1.readLine();
                    out.println(content);
                    out.flush();
                    if(content.equals("good bye")){
                        socket.close();
                        break;
                    }
                }
                
        }catch (Exception e) {
            // e.printStackTrace(); 
            System.out.println("Connection Closed...");
            // this one is to print the techinacal info on the console page what is the error given by the message ....
            }
            // System.out.println("Connection Closed...");
        };
        // for start thread make thread class and object
        new Thread(r2).start();

}

    public static void main(String[] args) {
        System.out.println("This is Client Side...");
        new Client();
    }
}