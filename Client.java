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
        if(clientConnect(host, port)) {
            createPromptGui();
        }
        else{
            System.out.println("Error: could not connect to server");
        }
    }

    private boolean clientConnect(String host, int port){
        try {
            socket = new Socket(host, port);

            System.out.println("connected to server");
            return true;
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    private void sendServerMessage(String message) {
        BufferedReader socketReader = null;
        PrintWriter socketWriter = null;

        try {
            socketWriter = new PrintWriter(socket.getOutputStream());


            socketWriter.println(message);
            socketWriter.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String getServerMessage(BufferedReader socketReader){
        try {
            return socketReader.readLine();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return("Error: no message received from server.");
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

        getContentPane().add(loginPanel, BorderLayout.NORTH);

        setVisible(true);
    }
    private class loginButtonListener implements ActionListener{
        private boolean authStatus = false;
        private JTextField userText;
        private String signal = "auth";

        public loginButtonListener(JTextField userText) {
            this.userText = userText;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Login button pressed");

            sendServerMessage(signal);
            sendServerMessage(userText.getText());

            //TODO: validate user login with server and database

            authStatus = true; //temporary bypass

            BufferedReader socketReader = null;
            if(authStatus){
                try {
                    socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String userID = socketReader.readLine();
                    String username = socketReader.readLine();
                    String balance = socketReader.readLine();

                    createMainGui(userID, username, balance);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void createMainGui(String userID, String username, String balance){
        getContentPane().removeAll();

        setTitle("Coin Flip");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel leaderboardLabel;
        JLabel userInfoLabel;
        JButton flipCoinButton;
        JRadioButton headsRadioButton;
        JRadioButton tailsRadioButton;
        JTextField betAmountTextField;
        JButton confirmBetButton;

        leaderboardLabel = new JLabel("Leaderboard: 1. UserA 2. UserB 3. UserC");
        leaderboardLabel.setHorizontalAlignment(JLabel.LEFT);
        add(leaderboardLabel, BorderLayout.NORTH);

        userInfoLabel = new JLabel("User #"+ userID + " Account Balance: $"+ balance);
        userInfoLabel.setHorizontalAlignment(JLabel.RIGHT);
        add(userInfoLabel, BorderLayout.NORTH);

        flipCoinButton = new JButton("Flip Coin");
        flipCoinButton.setPreferredSize(new Dimension(600, 200));
        add(flipCoinButton, BorderLayout.CENTER);
        flipCoinButton.addActionListener(new flipCoinButtonListener());

        headsRadioButton = new JRadioButton("Heads");
        tailsRadioButton = new JRadioButton("Tails");
        ButtonGroup guessButtonGroup = new ButtonGroup();
        guessButtonGroup.add(headsRadioButton);
        guessButtonGroup.add(tailsRadioButton);

        JPanel bottomLeftPanel = new JPanel(new GridLayout(3, 1));
        bottomLeftPanel.add(headsRadioButton);
        bottomLeftPanel.add(tailsRadioButton);

        betAmountTextField = new JTextField();
        bottomLeftPanel.add(betAmountTextField);

        confirmBetButton = new JButton("Confirm Bet");
        bottomLeftPanel.add(confirmBetButton);
        add(bottomLeftPanel, BorderLayout.SOUTH);
        confirmBetButton.addActionListener(new confirmBetButtonListener((JTextField) betAmountTextField));

        pack();
        setVisible(true);
    }
    private class confirmBetButtonListener implements ActionListener{
        JTextField betAmount;
        private confirmBetButtonListener(JTextField betAmount){
            this.betAmount = betAmount;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Confirm bet button pressed");
            sendServerMessage(betAmount.getText());
        }
    }
    private class flipCoinButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            sendServerMessage("flip");

            BufferedReader socketReader = null;

            try {
                socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                if((socketReader.readLine()).equals("heads")){
                    System.out.println("Heads!");
                }
                else{
                    System.out.println("Tails!");
                }
            }
            catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}