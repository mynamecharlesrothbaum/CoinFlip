import java.io.*;
import java.net.*;
import java.util.HashMap;

class Server {
    public static void main(String[] args)
    {
        ServerSocket server = null;
        int threadID = 0;

        try {
            server = new ServerSocket(5000);
            server.setReuseAddress(true);
            while (true) {
                System.out.println("Server: waiting for connection...");

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
            UserMap userMap = new UserMap();
            try {
                socketWriter = new PrintWriter(
                        clientSocket.getOutputStream(), true);

                socketReader = new BufferedReader(
                        new InputStreamReader(
                                clientSocket.getInputStream()));

                String signal, username;
                String accountBal = "100";

                while ((signal = socketReader.readLine()) != null) {
                    System.out.printf(" Sent from the client: %s\n", signal);
                    if(signal.equals("auth")){
                        System.out.println("Server: authenticating...");
                        if(true){ //TODO: pull database and authenticate existing user
                            username = socketReader.readLine();
                            System.out.println(username);

                            userMap.addUser(threadID, new UserInfo(username, accountBal));
                            socketWriter.println(threadID);
                            socketWriter.println(username);
                            socketWriter.println(accountBal);
                        }
                        else{ //TODO: create a new user db entry

                        }
                    }
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
    public static class UserMap{
        private HashMap<Integer, UserInfo> userMap;

        public UserMap(){
            userMap = new HashMap<Integer, UserInfo>();
        }
        public void addUser(int threadID, UserInfo userInfo){
            userMap.put(threadID, userInfo);
        }
        public UserInfo getUser(int threadID) {
            return userMap.get(threadID);
        }
        public void removeUser(int threadID) {
            userMap.remove(threadID);
        }

    }
    public static class UserInfo {
        private String username;
        private String accountBalance;

        public UserInfo(String username, String accountBalance) {
            this.username = username;
            this.accountBalance = accountBalance;
        }

        public String getUserName() {
            return username;
        }

        public String getAccountBalance() {
            return accountBalance;
        }
    }
}