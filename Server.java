import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            while (true) {
                System.out.println("Server: waiting for connection...");

                Socket conn = serverSocket.accept();

                System.out.println("Server: client successfully connected.");


            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
