/**
 * CS351 Project 3 Arcade Game Julian Fong
 *
 * This is the GameLoop class that will be updating the screen based on what is happening with the game logic
 * It will update the screen based off of whatever FPS the game should be running at
 */

import javafx.animation.AnimationTimer;

import java.io.*;
import java.net.Socket;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class GameLoop extends AnimationTimer {

    private final GameLogic gameLogic;
    private long lastUpdate = -1;
    private long lastSync = -1;
    private final GameUI gameUI;
    private final boolean ONLINE_GAME;
    private InputStream onlineInput;
    private OutputStream onlineOutput;
    private final Queue<Byte> incoming = new ConcurrentLinkedDeque<>();
    public final DataSerializer ds = new DataSerializer();
    private int CLIENT_NUMBER;
    private final int P1HP_MASK = 0xF0;
    private final int P2HP_MASK = 0xE0;


    GameLoop(GameLogic gameLogic, GameUI gameUI, Socket clientSocket){
        if(clientSocket != null){
            try {
                onlineInput = clientSocket.getInputStream();
                onlineOutput = clientSocket.getOutputStream();
                Thread readThead = new Thread(() -> {
                    while(true){
                        try {
                            int b = onlineInput.read();
                            if (b == P1HP_MASK) {
                                System.out.println("p1update");
                                gameLogic.syncHealthBars(onlineInput.read(), true);
                            }
                            else if(b == P2HP_MASK) {
                                System.out.println("p2update");
                                gameLogic.syncHealthBars(onlineInput.read(), false);
                            }
                            if(b != -1 && ds.isButtonData((byte) b)) incoming.add((byte) b);
                            else System.out.println(Integer.toHexString(b));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                readThead.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            onlineInput = null;
            onlineOutput = null;
        }
        this.gameUI = gameUI;
        this.gameLogic = gameLogic;
        this.CLIENT_NUMBER = gameLogic.getCLIENT_PLAYER_NUMBER();
        this.ONLINE_GAME = gameLogic.ONLINE_GAME;
        if(!ONLINE_GAME) this.CLIENT_NUMBER = -1;
    }

    /**
     * The main game loop that will run while the game is running
     * @param now current system time in nanoseconds
     */
    @Override
    public void handle(long now) {
        if(lastUpdate == -1) lastUpdate = now;
        if(lastSync == -1) lastSync = now;
        //Update screen if it has reached fps rate cool-down length
        int FPS = 60;
        long timeoutMillis = 5;
        if(now - lastUpdate >= Duration.ofSeconds(1).toNanos()/ FPS){
            lastUpdate = now;
            gameLogic.updateScreen();

            //Transfer online data
            if(ONLINE_GAME){
                Queue<Byte> outGoing = gameLogic.sendData();
                long startIncoming = System.currentTimeMillis();
                while(!incoming.isEmpty() && (System.currentTimeMillis() - startIncoming < timeoutMillis)){
                    byte b = incoming.remove();
                    gameLogic.getOnlineData(b);
                }
                long startOutGoing = System.currentTimeMillis();
                while(!outGoing.isEmpty() && (System.currentTimeMillis() - startOutGoing < timeoutMillis)){
                    try {
                        byte b = outGoing.remove();
                        onlineOutput.write(b);
                        onlineOutput.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if(CLIENT_NUMBER == 0){
                        int[] healths = gameLogic.sendServerMasterHealthData();
                        int p1Health = healths[0];
                        int p2Health = healths[1];
                        onlineOutput.write(P1HP_MASK);
                        onlineOutput.write(p1Health);
                        onlineOutput.write(P2HP_MASK);
                        onlineOutput.write(p2Health);
                        onlineOutput.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(gameLogic.checkIfOver()){
            stop();
            gameUI.resetPlayers();
            gameUI.goToMenu();
        }
    }

}
