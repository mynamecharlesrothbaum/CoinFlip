import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class Client extends JFrame {
    String host = "127.0.0.1";
    int port = 5000;

    public Client() {
        createPromptGui();
        clientConnect(host, port);

    }

    private void clientConnect(String host, int port){
        Socket socket = null;
        BufferedReader socketReader = null;
        PrintWriter socketWriter = null;

        try {
            socket = new Socket(host, port);
            socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socketWriter = new PrintWriter(socket.getOutputStream());

            Scanner scanner = new Scanner(System.in);
            String line = null;

            while(true) {
                line = scanner.nextLine();

                socketWriter.println(line);
                socketWriter.flush();

                System.out.println("Server replied: " + socketReader.readLine());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (socketWriter != null) {
                    socketWriter.close();
                }
                if (socketReader != null) {
                    socketReader.close();
                    socket.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createPromptGui(){
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
        loginButton.addActionListener(new loginButtonListener());

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
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Login button pressed");

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