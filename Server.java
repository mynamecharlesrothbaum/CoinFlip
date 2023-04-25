import java.io.*;
import java.net.*;

class Server {
    public static void main(String[] args)
    {
        ServerSocket server = null;
        int threadID = 0;

        try {
            server = new ServerSocket(5000);
            server.setReuseAddress(true);
            while (true) {
                Socket client = server.accept();

                System.out.println("New client connected " + client.getInetAddress().getHostAddress());

                threadID++;

                ClientHandler clientSock = new ClientHandler(client, threadID);

                new Thread(clientSock).start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (server != null) {
                try {
                    server.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        int threadID;

        public ClientHandler(Socket socket, int threadID)
        {
            this.clientSocket = socket;
            this.threadID = threadID;
        }

        public void run()
        {
            PrintWriter socketWriter = null;
            BufferedReader socketReader = null;
            try {
                socketWriter = new PrintWriter(
                        clientSocket.getOutputStream(), true);

                socketReader = new BufferedReader(
                        new InputStreamReader(
                                clientSocket.getInputStream()));

                String line;
                while ((line = socketReader.readLine()) != null) {
                    System.out.printf(" Sent from the client: %s\n", line);
                    socketWriter.println(threadID);
                    socketWriter.println(line);
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
                        clientSocket.close();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}