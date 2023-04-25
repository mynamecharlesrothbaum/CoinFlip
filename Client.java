import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Client extends JFrame {
    Socket socket = null;
    String host = "127.0.0.1";
    int port = 5000;

    public Client() {
        createStartUpGui();
        clientConnect(host, port);
        createPromptGui();
    }

    private void clientConnect(String host, int port){
        try {
            socket = new Socket(host, port);

            System.out.println("connected to server");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void sendServerMessage(String message) {
        BufferedReader socketReader = null;
        PrintWriter socketWriter = null;

        try {
            socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socketWriter = new PrintWriter(socket.getOutputStream());


            socketWriter.println(message);
            socketWriter.flush();
            System.out.println("Server replied: " + socketReader.readLine());

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void createStartUpGui(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);

        JFrame frame = new JFrame("Start Window");
        JLabel label = new JLabel("waiting for connection...");
        frame.add(label);
        frame.pack();
        frame.setVisible(true);
    }
    private void createPromptGui(){
        getContentPane().removeAll();

        JLabel userLabel;
        JTextField userText;
        JButton loginButton;
        JPanel loginPanel;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Coin Flipper");
        setSize(400, 300);

        userLabel = new JLabel("Username:");
        userText = new JTextField(20);
        loginButton = new JButton("Login");
        loginButton.addActionListener(new loginButtonListener((JTextField) userText));

        loginPanel = new JPanel(new GridLayout(3, 2));
        loginPanel.add(userLabel);
        loginPanel.add(userText);
        loginPanel.add(new JLabel(""));
        loginPanel.add(loginButton);

        JPanel mainPanel = new JPanel();
        mainPanel.add(new JLabel("You are logged in!"));

        getContentPane().add(loginPanel, BorderLayout.NORTH);

        setVisible(true);
    }
    private class loginButtonListener implements ActionListener{
        private boolean authStatus = false;
        private JTextField userText;

        public loginButtonListener(JTextField userText) {
            this.userText = userText;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Login button pressed");
            sendServerMessage(userText.getText());

            //TODO: validate user login with server and database

            authStatus = true; //temporary bypass
            if(authStatus){
                createMainGui();
            }
        }
    }

    public void createMainGui(){
        getContentPane().removeAll();
        JPanel mainPanel = new JPanel();
        mainPanel.add(new JLabel("You are logged in!"));

        getContentPane().add(mainPanel);

        setVisible(true);
    }

    public static void main(String[] args) {
        new Client();
    }
}