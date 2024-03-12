/**
 * HostServer class; Setups and runs the server from the hosts computer, uses the hosts computer to be the
 * server and client for their own game. Will only stop searching for clients when a game is started and will continue
 * to run the server
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class HostServer implements Runnable{

    private volatile boolean searchingConnections;
    private volatile boolean gameRunning;
    private final int portNumber;

    /**
     * Close the connection portal and go on to starting the game
     */
    public synchronized void stopSearching() { searchingConnections = false; }

    /**
     * Stop the server
     */
    public synchronized void stopGame() { gameRunning = false; }

    public HostServer(int portNumber){
        this.portNumber = portNumber;
        searchingConnections = true;
    }

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Socket> clientConnections = new ArrayList<>();
        int connections = 0;
        while(searchingConnections){
            try {
                assert serverSocket != null;
                serverSocket.setSoTimeout(500);
                Socket clientConnection = serverSocket.accept();
                clientConnections.add(clientConnection);
                connections++;
                String plural = connections > 1 ? "s" : "";
                System.out.println(connections + " player" + plural + " connected!");
            } catch (SocketTimeoutException ignored) {
                continue;
            } catch (IOException io ){
                io.printStackTrace();
            }
        }
        ClientHandler handler = new ClientHandler(clientConnections);
        try {
            handler.initializeGame();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Connections Closed!");
//        handler.writeToClient(0, "Hello World?");
//        handler.readFromClient(0);
        /**
         * Keep the Thread running while the game is running
         */
        while(gameRunning){
            Thread.onSpinWait();
        }
    }
}


