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
        createStartGui();
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
    private void createStartGui(){
        JFrame frame = new JFrame("Start Window");
        JLabel label = new JLabel("waiting for connection...");
        setSize(400, 300);

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

    private void createMainGui(){
        getContentPane().removeAll();

        setTitle("Coin Flip");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel circlePanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.YELLOW);
                g.fillOval(100, 50, 200, 200);
            }
        };
        add(circlePanel, BorderLayout.CENTER);

        JButton flipButton = new JButton("Flip Coin");
        add(flipButton, BorderLayout.NORTH);

        JTextField betAmountField = new JTextField(10);
        JButton confirmBetButton = new JButton("Confirm Bet");
        confirmBetButton.addActionListener(new confirmBetButtonListener((JTextField) betAmountField));
        JPanel betPanel = new JPanel(new FlowLayout());
        betPanel.add(new JLabel("Enter bet amount:"));
        betPanel.add(betAmountField);
        betPanel.add(confirmBetButton);
        add(betPanel, BorderLayout.SOUTH);

        JRadioButton headsRadioButton = new JRadioButton("Heads");
        JRadioButton tailsRadioButton = new JRadioButton("Tails");
        ButtonGroup guessButtonGroup = new ButtonGroup();
        guessButtonGroup.add(headsRadioButton);
        guessButtonGroup.add(tailsRadioButton);
        JPanel guessPanel = new JPanel(new GridLayout(2, 1));
        guessPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        guessPanel.add(new JLabel("Guess:"));
        guessPanel.add(headsRadioButton);
        guessPanel.add(tailsRadioButton);
        add(guessPanel, BorderLayout.WEST);

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

    public static void main(String[] args) {
        new Client();
    }
}