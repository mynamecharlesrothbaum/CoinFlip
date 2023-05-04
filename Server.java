import java.io.*;
import java.net.*;
import java.util.ArrayList;
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

    private static class ClientHandler implements Runnable  {
        private final Socket clientSocket;
        int threadID;

        public ClientHandler(Socket socket, int threadID)
        {
            this.clientSocket = socket;
            this.threadID = threadID;

        }

        public boolean foundUser(String username){
            UserDatabase userDatabase = new UserDatabase();
            int userID = userDatabase.getUserID(username);
            if(userID == 0){
                return false;
            } else {
                return true;
            }
        }
        private void sendClientMessage(String message) {
            BufferedReader socketReader = null;
            PrintWriter socketWriter = null;

            try {
                socketWriter = new PrintWriter(clientSocket.getOutputStream());


                socketWriter.println(message);
                socketWriter.flush();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run()
        {
            UserDatabase userDatabase = new UserDatabase();
            PrintWriter socketWriter = null;
            BufferedReader socketReader = null;
            UserMap userMap = new UserMap();
            int betAmt = 0;
            String betUsername = "";
            String guess = "";
            try {
                socketWriter = new PrintWriter(
                        clientSocket.getOutputStream(), true);

                socketReader = new BufferedReader(
                        new InputStreamReader(
                                clientSocket.getInputStream()));

                String signal, username;
                String accountBal = "100";
                String foundUser = "false";

                while ((signal = socketReader.readLine()) != null) {
                    System.out.printf(" Sent from the client: %s\n", signal);
                    if(signal.equals("auth")){
                        System.out.println("Server: authenticating...");
                        username = socketReader.readLine();
                        if (foundUser(username)) {
                            System.out.println(username);
                            accountBal = String.valueOf(userDatabase.getBalance(userDatabase.getUserID(username)));
                            foundUser = "true";

                            socketWriter.println(foundUser);
                            userMap.addUser(threadID, new UserInfo(username, accountBal));
                            socketWriter.println(threadID);
                            socketWriter.println(username);
                            socketWriter.println(accountBal);
                        } else {
                            userDatabase.createUser(username, 10000);
                            accountBal = "10000";

                            userMap.addUser(threadID, new UserInfo(username, accountBal));
                            socketWriter.println(threadID);
                            socketWriter.println(username);
                            socketWriter.println(accountBal);
                        }
                    }
                    if(signal.equals("confirm bet")){
                        betUsername = socketReader.readLine();
                        guess = socketReader.readLine();
                        betAmt = Integer.valueOf(socketReader.readLine());
                    }
                    if(signal.equals("flip")){
                        int betUserId = userDatabase.getUserID(betUsername);
                        int currentBal = userDatabase.getBalance(betUserId);
                        if(flipCoin()){
                            socketWriter.println("heads");
                            if(guess.equals("heads")) {
                                int plusBalance = currentBal + betAmt;
                                userDatabase.updateBalance(betUserId, plusBalance);
                                socketWriter.println("You won " + String.valueOf(betAmt) + "! :)");
                            } else {
                                int minusBalance = currentBal - betAmt;
                                userDatabase.updateBalance(betUserId, minusBalance);
                                socketWriter.println("You Loss " + String.valueOf(betAmt) + ". :(");
                            }

                        }
                        else{
                            socketWriter.println("tails");
                            if(guess.equals("tails")) {
                                int plusBalance = currentBal + betAmt;
                                userDatabase.updateBalance(betUserId, plusBalance);
                                socketWriter.println("You won " + String.valueOf(betAmt) + "! :)");
                            } else {
                                int minusBalance = currentBal - betAmt;
                                userDatabase.updateBalance(betUserId, minusBalance);
                                socketWriter.println("You Loss " + String.valueOf(betAmt) + ". :(");
                            }
                        }
                    }

                    if(signal.equals("leaderboard")){
                        ArrayList<String> leaderboardUser = new ArrayList<String>();
                        ArrayList<Integer> leaderboardBalance = new ArrayList<Integer>();

                        leaderboardUser = userDatabase.leaderboard();

                        for(int i = 0; i < leaderboardUser.size(); i++){
                            String temp = leaderboardUser.get(i);
                            int tempId = userDatabase.getUserID(temp);
                            leaderboardBalance.add(userDatabase.getBalance(tempId));
                        }

                        for(int j = 0; j < leaderboardBalance.size(); j++){
                            String tempUser = leaderboardUser.get(j);
                            String tempBal = String.valueOf(leaderboardBalance.get(j));
                            socketWriter.println(tempUser);
                            socketWriter.println(tempBal);
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
    public static boolean flipCoin(){
        double randomNumber = Math.random();

        if (randomNumber < 0.5) {
            return true;
        } else {
            return false;
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