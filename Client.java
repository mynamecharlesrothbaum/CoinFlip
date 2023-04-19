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


public class Client extends JFrame{
    private Socket socket;
    private BufferedReader socketReader;
    private PrintWriter socketWriter;
    private Scanner scanner;
    String line;

    public Client() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);

        JPanel inputPanel = new JPanel();
        getContentPane().add(inputPanel, BorderLayout.NORTH);

        setVisible(true);

        try {
            socket = new Socket("127.0.1.1", 5000);
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
    }
    public static void main(String[] args) {
        new Client();
    }
}