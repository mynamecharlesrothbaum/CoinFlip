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
            while(true){
                System.out.println("Server: waiting for connection...");

                Socket conn = serverSocket.accept();

                System.out.println("Server: client successfully connected.");

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                PrintWriter printWriter = new PrintWriter(conn.getOutputStream());

                String line;
                int num1, num2, result = 0;
                String op;
                boolean err = false;

                while((line = bufferedReader.readLine()) != null){
                    System.out.println("Server: recieved " + line);

                    String tokens[] = line.split(" ");
                    num1 = Integer.parseInt(tokens[0]);
                    op = tokens[1];
                    num2 = Integer.parseInt(tokens[2]);


                    if(op.equals("+")){
                        result = num1 + num2;
                    }
                    if(op.equals("-")){
                        result = num1 - num2;
                    }
                    if(op.equals("/")){
                        result = num1 / num2;
                    }
                    if(op.equals("*")){
                        result = num1 * num2;
                    }
                    if(op.equals("%")){
                        result = num1 % num2;
                    }
                    if(op.equals("^")){
                        int a;
                        if(num2 == 1){
                            result = num1;
                        }
                        else {
                            a = num1;
                            for (int i = 1; i < num2; i++) {
                                a = a * num1;
                            }
                            result = a;
                        }
                    }
                    printWriter.println(result);
                    printWriter.flush();

                }
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
