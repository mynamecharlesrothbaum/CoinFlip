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
            String username = userText.getText();
            BufferedReader socketReader = null;

            sendServerMessage(signal);
            sendServerMessage(username);

            //TODO: validate user login with server and database

            try {
                socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                if(socketReader.readLine().equals("true")) {
                    String name = socketReader.readLine();
                    String balance = socketReader.readLine();

                    createMainGui(name, balance);
                }
                else{
                    createNewUserPrompt(username);
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    private void createMainGui(String username, String balance){
        getContentPane().removeAll();

        setTitle("Coin Flip");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel userInfoLabel;
        JButton flipCoinButton;
        JRadioButton headsRadioButton;
        JRadioButton tailsRadioButton;
        JTextField betAmountTextField;
        JLabel leaderboardLabel;
        JButton confirmBetButton;

        leaderboardLabel = new JLabel("Leaderboard:");
        leaderboardLabel.setHorizontalAlignment(JLabel.RIGHT);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(leaderboardLabel, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        userInfoLabel = new JLabel( " Name: " + username + " Account Balance: $" + balance);
        userInfoLabel.setHorizontalAlignment(JLabel.CENTER);
        add(userInfoLabel, BorderLayout.CENTER);

        flipCoinButton = new JButton("Flip Coin");
        flipCoinButton.setPreferredSize(new Dimension(600, 200));
        add(flipCoinButton, BorderLayout.SOUTH);
        flipCoinButton.addActionListener(new flipCoinButtonListener(username, leaderboardLabel, userInfoLabel));

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

        String guess;
        if(headsRadioButton.isSelected()){
            guess = "heads";
        }
        else{
            guess = "tails";
        }

        confirmBetButton = new JButton("Confirm Bet");
        bottomLeftPanel.add(confirmBetButton);
        add(bottomLeftPanel, BorderLayout.EAST);
        confirmBetButton.addActionListener(new confirmBetButtonListener((JTextField) betAmountTextField, username, guess));

        pack();
        setVisible(true);
    }


    private void updateLeaderboard(JLabel leaderBoardLabel){
        BufferedReader socketReader = null;
        String leader1, leader2, leader3, leader1Bal, leader2Bal, leader3Bal;

        sendServerMessage("leaderboard");
        try {
            socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            leader1 = socketReader.readLine();
            leader1Bal = socketReader.readLine();
            leader2 = socketReader.readLine();
            leader2Bal = socketReader.readLine();
            leader3 = socketReader.readLine();
            leader3Bal = socketReader.readLine();

            leaderBoardLabel.setText("Leaderboard " + leader1 + " " + leader1Bal + "||" + " " + leader2Bal + leader2 + "||" + leader3 + " " + leader3Bal);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createNewUserPrompt(String username){
        getContentPane().removeAll();

        setTitle("Coin Flip");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel userLabel;
        JButton newAcctButton;
        JPanel loginPanel;

        userLabel = new JLabel("User not found. Create a new user account?");
        newAcctButton = new JButton("Create");
        newAcctButton.addActionListener(new newAcctButtonListener(username));

        loginPanel = new JPanel(new GridLayout(3, 2));
        loginPanel.add(userLabel);
        loginPanel.add(new JLabel(""));
        loginPanel.add(newAcctButton);

        getContentPane().add(loginPanel, BorderLayout.NORTH);

        setVisible(true);
    }
    private class newAcctButtonListener implements ActionListener{
        String name;
        private newAcctButtonListener(String username){
            this.name = username;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            sendServerMessage("create");
            sendServerMessage(name);
            createMainGui(name, "1000");
        }
    }

    private class confirmBetButtonListener implements ActionListener{
        JTextField betAmount;
        String name;
        String guess;
        private confirmBetButtonListener(JTextField betAmount, String username, String guess){
            this.betAmount = betAmount;
            this.name = username;
            this.guess = guess;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            String betAmountString = betAmount.getText();
            ;
            System.out.println("Confirm bet button pressed");
            if (betAmountString.matches("\\d+")) {
                sendServerMessage("confirm bet");
                sendServerMessage(name);
                sendServerMessage(guess);
                sendServerMessage(betAmount.getText());
            } else {
                betAmountString = "0";
                sendServerMessage("confirm bet");
                sendServerMessage(name);
                sendServerMessage(guess);
                sendServerMessage(betAmountString);

            }
        }
    }
    private class flipCoinButtonListener implements ActionListener{
        String name;
        JLabel textField;
        JLabel userInfoLabel;
        private flipCoinButtonListener(String username, JLabel leaderBoardLabel, JLabel userInfoLabel){
            this.name = username;
            this.textField = leaderBoardLabel;
            this.userInfoLabel = userInfoLabel;
        }
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

                String newBal = socketReader.readLine();
                userInfoLabel.setText(" Name: " + name + " Account Balance: $" + newBal);

                updateLeaderboard(textField);
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