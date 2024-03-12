/**
 * CS351 Fight Night ONLINE Project 5
 *
 * ClientHandler class: This class is meant to store all connections to the server when a new online is initiated by
 * host. Their computer will server as a client that runs their game, AND a server that will handle incoming and
 * outgoing data.
 */

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler {

    private final List<Socket> clientConnections;
    private final List<InputStream> dataInList;
    private final List<OutputStream> dataOutList;


    public ClientHandler(List<Socket> clientConnections){
        this.clientConnections = clientConnections;
        dataInList = createInList();
        dataOutList = createOutList();
    }

    private void startDispersionThread(InputStream clientReader, int clientNum){
        Thread inputWatcher = new Thread(() -> {
           while(true){
               int b;
               try {
                   b = clientReader.read();
                   disperseData((byte) b, clientNum);
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
        });
        inputWatcher.start();
    }


    /**
     * Gives all the clients their client numbers
     * to start the game with
     * @throws IOException Something could go wrong with writing data to the output streams
     */
    public void initializeGame() throws IOException {
        for(int i = 0; i < dataOutList.size(); i++){
            dataOutList.get(i).write(i);
            dataOutList.get(i).flush();
        }
        //Start the data readers
        for(int i = 0; i < dataInList.size(); i ++){
            startDispersionThread(dataInList.get(i), i);
        }
    }

    /**
     * Write a message to the given client index
     * @param clientNum client index
     * @param message string message
     */
    public void writeToClient(int clientNum, String message){
        if(testBounds(clientNum)) return;
        else{
            System.out.print("[Player " + clientNum + "]:");
            PrintWriter writer = new PrintWriter(dataOutList.get(clientNum));
            writer.println(message);
        }
    }

    private boolean testBounds(int index){
        if(index < 0 || index >= dataOutList.size()) {
            System.err.println("Client " + index + " does not exist!");
            return false;
        }
        else return true;
    }

    /**
     * Sends given data to all clients
     * @param b byte of data
     * @param clientNum number of the client its coming from
     * @throws IOException Can go wrong while writing data
     */
    public void disperseData(byte b, int clientNum) throws IOException {
        for(int i = 0; i < dataOutList.size(); i++){
            if(i != clientNum){
                dataOutList.get(i).write(b);
                dataOutList.get(i).flush();
            }
        }
    }

    public List<Byte> readData(int clientNum) throws IOException {
        List<Byte> read = new ArrayList<>();
        for(int i = 0; i < dataInList.size(); i++){
            if(i != clientNum){
                int b = 0;
                while(b != -1){
                    if((b = dataInList.get(i).read()) != -1){
                        read.add((byte) b);
                    }
                }
            }
        }
        return read;
    }



    //Constructs the list of inputStreams
    private List<InputStream> createInList(){
        List<InputStream> inList = new ArrayList<>();
        for (Socket clientConnection : clientConnections) {
            try {
                inList.add(clientConnection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return inList;
    }

    //Constructs the list of outputStreams
    private List<OutputStream> createOutList(){
        List<OutputStream> outList = new ArrayList<>();
        for (Socket clientConnection : clientConnections) {
            try {
                outList.add( clientConnection.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return outList;
    }


}
