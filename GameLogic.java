/**
 * CS351 Project 3 Arcade Game Julian Fong
 *
 * GameLogic class: This is the brain of the game, it will be listening to input keys from the user to
 * determine what will happen on screen with the fighters. This is how the "scoring" and animation will take place,
 * by doing calculations on screen based off of where the fighters currently are. It contains a very important method,
 * updateScreen that will send the data to the GameLoop to change the animations on screen in a time controlled
 * manor.
 */

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayDeque;
import java.util.Queue;

public class GameLogic {
    //Player variables
    private final Player playerOne;
    private final Player playerTwo;

    //Game state variables
    private boolean gameRunning;
    private boolean gameHadEnded;

    //Environment variables
    private final Scene gameScene;
    private final Pane gameGrid;
    private final int GROUND_LEVEL;
    private final int X_BORDER;
    private final int PLAYER_RIGHT_BORDER;
    private final int PLAYER_LEFT_BORDER;
    private final int PLAYER_GROUND_BORDER;
    private int PLAYER_WIDTH_OFFSET;
    private final double CROUCH_HEIGHT;
    private final double GAME_WIDTH;
    private final double GAME_HEIGHT;

    //Font variables
    //license: Public Domain
    //link: https://www.fontspace.com/public-pixel-font-f72305
    // Load the custom pixel font
    private final Font myFont = Font
            .loadFont("file:resources/com/JFong/CS351Project3_fightnight/fonts/PublicPixel.ttf", 20);

    //replace with sprites later
    private Rectangle p1Rec;
    private Rectangle p2Rec;
    private final int HITBOX_WIDTH = 50;
    private final int HITBOX_HEIGHT = 80;
    private double p1XPos;
    private double p2XPos;
    private double p1YPos;
    private double p2YPos;
    private double p1JumpVelocity;
    private double p2JumpVelocity;
    private final double MAX_HEALTH = 200;

    //Sprites
    private final ImageView p1ScreenSprite;
    private final ImageView p2ScreenSprite;
    private Image p1CurrentSprite;
    private Image p2CurrentSprite;
    private final Queue<Image> p1FrameQueue;
    private final Queue<Image> p2FrameQueue;

    private final int JUMP = 0;
    private final int CROUCH = 1;
    private final int MOVE_RIGHT = 2;
    private final int MOVE_LEFT = 3;
    private final int LIGHT = 4;
    private final int HEAVY = 5;
    private boolean[] p1Actions;
    private boolean[] p2Actions;
    private long lastP1Light;
    private long lastP2Light;
    private long lastP1Heavy;
    private long lastP2Heavy;
    private Rectangle p1HealthRec;
    private Rectangle p2HealthRec;
    private final boolean againstComputer;

    //Variables for online features
    private final Queue<Byte> dataQueue;
    public final boolean ONLINE_GAME;
    private final int CLIENT_PLAYER_NUMBER;
    private final DataSerializer dataSerializer = new DataSerializer();

    //Constructor sets players and sets variables to initialized states
    public GameLogic(Player p1, Player p2, Pane gameGrid, Scene gameScene,
                     int GAME_WIDTH, int GAME_HEIGHT, boolean ONLINE_GAME, int CLIENT_PLAYER_NUMBER){
        this.gameScene = gameScene;
        this.ONLINE_GAME = ONLINE_GAME;
        this.CLIENT_PLAYER_NUMBER = CLIENT_PLAYER_NUMBER;
        this.dataQueue = new ArrayDeque<>();
        this.gameGrid = gameGrid;
        this.GAME_WIDTH = GAME_WIDTH;
        this.GAME_HEIGHT = GAME_HEIGHT;
        lastP1Light = -1;
        lastP2Light = -1;
        lastP1Heavy = -1;
        lastP2Heavy = -1;
        playerOne = p1;
        playerTwo = p2;
        //TEST
        p1CurrentSprite = p1.fighter.stillRight;
        p1ScreenSprite = new ImageView(p1CurrentSprite);
        p2CurrentSprite = p2.fighter.stillRight;
        p2ScreenSprite = new ImageView(p2CurrentSprite);
        //END TEST
        p1FrameQueue = new ArrayDeque<>();
        p2FrameQueue = new ArrayDeque<>();
        againstComputer = p2 instanceof COMPlayer;
        createHealthBars(GAME_WIDTH,GAME_HEIGHT);
        createPlayers();
        initActionsArray();
        initializeControlsForPlayers();
        X_BORDER = GAME_WIDTH;
        PLAYER_LEFT_BORDER = 0;
        PLAYER_RIGHT_BORDER = GAME_HEIGHT - HITBOX_WIDTH;
        GROUND_LEVEL = (int) (GAME_HEIGHT * 0.9);
        PLAYER_GROUND_BORDER = GROUND_LEVEL - HITBOX_HEIGHT;
        CROUCH_HEIGHT = HITBOX_HEIGHT/2.0;
        constructGround();
        resetGame();
    }

    //Creates the action array for each player and sets them both to false;
    private void initActionsArray(){
        int NUM_ACTIONS = 6;
        p1Actions = new boolean[NUM_ACTIONS];
        p2Actions = new boolean[NUM_ACTIONS];

        for(int i = 0; i < NUM_ACTIONS; i++){
            p1Actions[i] = false;
            p2Actions[i] = false;
        }
    }
    //Updates the hit-box locations
    private void whereAreRectangles(){
        p1XPos = p1Rec.getLayoutX();
        p2XPos = p2Rec.getLayoutX();
        p1YPos = p1Rec.getLayoutY();
        p2YPos = p2Rec.getLayoutY();
    }

    private void createHealthBars(int GAME_WIDTH, int GAME_HEIGHT){
        Label p1Label = new Label("PLAYER ONE");
        Label p2Label = new Label("PLAYER TWO");
        p1Label.setFont(myFont);
        p2Label.setFont(myFont);
        p1Label.setStyle("-fx-text-fill: white;");
        p2Label.setStyle("-fx-text-fill: white;");

        VBox p1Health = new VBox();
        VBox p2Health = new VBox();
        Group p1Group = constructHealthBar(true);
        Group p2Group = constructHealthBar(false);

        p1Health.getChildren().addAll(p1Group,p1Label);
        p2Health.getChildren().addAll(p2Group,p2Label);

        gameGrid.getChildren().addAll(p1Health,p2Health);

        p1Health.relocate(GAME_WIDTH * (0.1), GAME_HEIGHT * (0.01));
        p2Health.relocate(GAME_WIDTH * (0.6), GAME_HEIGHT * (0.01));
    }

    private Group constructHealthBar(boolean forP1){
        Group barGroup = new Group();
        Rectangle outline = new Rectangle(200,20);
        outline.setFill(Color.TRANSPARENT);
        outline.setStroke(Color.WHITE);
        outline.setStrokeWidth(2);
        Rectangle healthBar = new Rectangle(200,20);
        healthBar.setFill(Color.RED);

        if(forP1) p1HealthRec = healthBar;
        else p2HealthRec = healthBar;
        barGroup.getChildren().addAll(outline, healthBar);
        return barGroup;
    }

    //Ground line that represents the ground they walk on
    private void constructGround(){
        Line groundLine = new Line();
        groundLine.setStartX(0);
        groundLine.setStartY(GROUND_LEVEL);
        groundLine.setEndX(X_BORDER);
        groundLine.setEndY(GROUND_LEVEL);
        groundLine.setFill(Color.TRANSPARENT);
        groundLine.setStroke(Color.TRANSPARENT);
        groundLine.setStrokeWidth(3);
        gameGrid.getChildren().add(groundLine);
    }

    private void createPlayers(){
        p1Rec = new Rectangle(HITBOX_WIDTH,HITBOX_HEIGHT);
        p1Rec.setFill(Color.TRANSPARENT);
        p2Rec = new Rectangle(HITBOX_WIDTH,HITBOX_HEIGHT);
        p2Rec.setFill(Color.TRANSPARENT);
        PLAYER_WIDTH_OFFSET = HITBOX_WIDTH;

        gameGrid.getChildren().addAll(p1Rec,p2Rec);
        //Add SpriteViewer
        gameGrid.getChildren().add(p1ScreenSprite);
        gameGrid.getChildren().add(p2ScreenSprite);
    }

    //Sets up the controls for the game
    private void initializeControlsForPlayers() {
        gameScene.setOnKeyPressed(event -> handlePlayerKeyPress(event.getCode(), true));
        gameScene.setOnKeyReleased(event -> handlePlayerKeyPress(event.getCode(), false));
    }

    //Sets the action array to true or false based on pressed or not pressed for each player
    private void handlePlayerKeyPress(KeyCode code, boolean isPressed) {
        // Player One controls
        if (playerOneControls(code)) {
            int action = getPlayerOneAction(code);
            if(CLIENT_PLAYER_NUMBER == 0) {
                p1Actions[action] = isPressed;
                updateOnlineInputQueue(action, isPressed);
            }
            else if(ONLINE_GAME){
                p2Actions[action] = isPressed;
                updateOnlineInputQueue(action, isPressed);
            }
            if (action == CROUCH && !isPressed && p1Rec.getHeight() == CROUCH_HEIGHT) {
                if(CLIENT_PLAYER_NUMBER == 0) playerOne.stand(p1Rec);

            }else if(CLIENT_PLAYER_NUMBER != 0 && p2Rec.getHeight() == CROUCH_HEIGHT){
                playerTwo.stand(p2Rec);
            }
        }

        // Player Two controls (if not a computer player) and it CANNOT be an online game
        if (!(playerTwo instanceof COMPlayer) && playerTwoControls(code) && !ONLINE_GAME){
            int action = getPlayerTwoAction(code);
            p2Actions[action] = isPressed;
            if (action == CROUCH && !isPressed && p2Rec.getHeight() == CROUCH_HEIGHT) {
                playerTwo.stand(p2Rec);
            }
        }
    }

    //This will fill the queue with user inputs every time a button is pressed or released
    private void updateOnlineInputQueue(int actionNum, boolean pressedDown){
        byte dataToSend = dataSerializer.serialize(CLIENT_PLAYER_NUMBER, actionNum, pressedDown);
        dataQueue.add(dataToSend);
    }

    /**
     * Updates the player action array based off of given byte
     * @param b byte of player data
     */
    public void getOnlineData(byte b){
        int action = dataSerializer.extractMoveNumber(b);
        boolean wasPressed = dataSerializer.extractPressedDownBool(b);
        int playerNum = dataSerializer.extractPlayerNumber(b);
        if(playerNum == 0) p1Actions[action] = wasPressed;
        else p2Actions[action] = wasPressed;
    }

    /**
     * Gets the client number of this game
     * @return clinet number as int
     */
    public int getCLIENT_PLAYER_NUMBER() {
        return CLIENT_PLAYER_NUMBER;
    }

    /**
     * Sends the data that was batched across the network!!
     * @return data queue of bytes containing user input data
     */
    public Queue<Byte> sendData() { return dataQueue; }

    //Sets the keycodes for player one
    private boolean playerOneControls(KeyCode code) {
        return switch (code) {
            case W, S, D, A, G, H -> true;
            default -> false;
        };
    }

    //Sets the keycodes for player two
    private boolean playerTwoControls(KeyCode code) {
        return switch (code) {
            case UP, DOWN, RIGHT, LEFT, MINUS, EQUALS -> true;
            default -> false;
        };
    }

    //Maps the keycodes for player two
    private int getPlayerOneAction(KeyCode code) {
        return mapKeyCodeToAction(code, true);
    }

    //Maps the keycodes for player one
    private int getPlayerTwoAction(KeyCode code) {
        return mapKeyCodeToAction(code, false);
    }

    //Determines which keycode does what action depending on what player
    private int mapKeyCodeToAction(KeyCode code, boolean isPlayerOne) {
        return switch (code) {
            case W -> isPlayerOne ? JUMP : -1;
            case S -> isPlayerOne ? CROUCH : -1;
            case D -> isPlayerOne ? MOVE_RIGHT : -1;
            case A -> isPlayerOne ? MOVE_LEFT : -1;
            case G -> isPlayerOne ? LIGHT : -1;
            case H -> isPlayerOne ? HEAVY : -1;
            case UP -> !isPlayerOne ? JUMP : -1;
            case DOWN -> !isPlayerOne ? CROUCH : -1;
            case RIGHT -> !isPlayerOne ? MOVE_RIGHT : -1;
            case LEFT -> !isPlayerOne ? MOVE_LEFT : -1;
            case MINUS -> !isPlayerOne ? LIGHT : -1;
            case EQUALS -> !isPlayerOne ? HEAVY : -1;
            default -> -1;
        };
    }

    //sets the background
    private void setUpBackGround(){
        try{
            Image backGround = new Image(new FileInputStream(
                    "resources/com/JFong/CS351Project3_fightnight/backgrounds/mainStage.png"));
            BackgroundImage backgroundImage = new BackgroundImage(
                    backGround,
                    BackgroundRepeat.NO_REPEAT, // No repeat
                    BackgroundRepeat.NO_REPEAT, // No repeat
                    BackgroundPosition.DEFAULT, // Default position
                    new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO,
                            false, false, true, false)
            );
            Background backGroundView = new Background(backgroundImage);
            gameGrid.setBackground(backGroundView);
        }
        catch(FileNotFoundException ex){
            System.err.println("Cannot find background file!");
        }
    }

    //Makes a player take damage
    private void takeDamage(Player target, int damage) {
        target.setHealth(target.getHealth() - damage);
        boolean isP1 = target.equals(playerOne);
        Queue<Image> frameQueue = isP1 ? p1FrameQueue : p2FrameQueue;

        frameQueue.clear();
        addFrameToQueue(frameQueue, target.fighter.damageOneRight, target.facingRight, 3);
        addFrameToQueue(frameQueue, target.fighter.damageTwoRight, target.facingRight, 3);
        addFrameToQueue(frameQueue, target.fighter.damageOneRight, target.facingRight, 3);
    }


    //Moves the players up or down depending on upwards force and gravity
    private void doGravity(){
        whereAreRectangles();
        int JUMP_HEIGHT = 15;
        int GRAVITY = 15;
        if(p1JumpVelocity > 0){
            moveP1(p1XPos, p1YPos - JUMP_HEIGHT);
            p1JumpVelocity--;
        }
        else if(p1YPos < PLAYER_GROUND_BORDER){
            //Don't go under floor
            if(p1YPos + GRAVITY > PLAYER_GROUND_BORDER) {
                moveP1(p1XPos, PLAYER_GROUND_BORDER);
                return;
            }
            moveP1(p1XPos, p1YPos + GRAVITY);
        }
        //Player two gravity
        if(p2JumpVelocity > 0){
            moveP2(p2XPos, p2YPos - JUMP_HEIGHT);
            p2JumpVelocity--;
        }
        else if(p2YPos < PLAYER_GROUND_BORDER){
            //Don't go under floor
            if(p2YPos + GRAVITY > PLAYER_GROUND_BORDER) {
                moveP2(p2XPos, PLAYER_GROUND_BORDER);
                return;
            }
            moveP2(p2XPos, p2YPos + GRAVITY);
        }

        //update jump sprites
        if(p1YPos < PLAYER_GROUND_BORDER){
            addFrameToQueue(p1FrameQueue, playerOne.fighter.jumpRight, playerOne.facingRight,1);
        }
        if(p2YPos < PLAYER_GROUND_BORDER){
            addFrameToQueue(p2FrameQueue, playerTwo.fighter.jumpRight, playerTwo.facingRight,1);
        }
    }

    private void addFrameToQueue(Queue<Image> frameQueue, Image image, boolean facingRight, int times){
        if(!facingRight){
            for(int i = 0; i < times; i++){
                frameQueue.add(reverseImage(image));
            }

        }
        else{
            for(int i = 0; i < times; i++){
                frameQueue.add(image);
            }
        }
    }

    private Image reverseImage(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        PixelReader pixelReader = image.getPixelReader();
        WritableImage flippedImage = new WritableImage(width, height);
        PixelWriter pixelWriter = flippedImage.getPixelWriter();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Get the color of the pixel at (x, y)
                int argb = pixelReader.getArgb(width - x - 1, y);
                // Write the pixel with the same color to the flipped image
                pixelWriter.setArgb(x, y, argb);
            }
        }

        return flippedImage;
    }

    //Set Players sprite to current hitbox position
    private void setSprite(Rectangle hitBox, Image screenSprite, boolean isPlayer1){
        if(isPlayer1){
            p1ScreenSprite.setImage(screenSprite);
            if(screenSprite.getHeight() == 80 && p1Rec.getHeight() == CROUCH_HEIGHT) return;
            p1ScreenSprite.setLayoutX(hitBox.getLayoutX());
            p1ScreenSprite.setLayoutY(hitBox.getLayoutY());
        }
        else{
            p2ScreenSprite.setImage(screenSprite);
            if(screenSprite.getHeight() == 80 && p2Rec.getHeight() == CROUCH_HEIGHT) return;
            p2ScreenSprite.setLayoutX(hitBox.getLayoutX());
            p2ScreenSprite.setLayoutY(hitBox.getLayoutY());
        }
    }

    private void updateFrameQueue(Queue<Image> frameQueue, boolean isPlayer1){
        if(!frameQueue.isEmpty()){
            if(isPlayer1){
                p1CurrentSprite = frameQueue.remove();
            }
            else{
                p2CurrentSprite = frameQueue.remove();
            }
            return;
        }
        if(isPlayer1){
            Image stillP1 = playerOne.fighter.stillRight;
            if(p1Rec.getHeight() == CROUCH_HEIGHT) stillP1 = playerOne.fighter.crouchRight;
            p1CurrentSprite = playerOne.facingRight ? stillP1 : reverseImage(stillP1);
        }
        else{
            Image stillP2 = playerTwo.fighter.stillRight;
            if(p2Rec.getHeight() == CROUCH_HEIGHT) stillP2 = playerTwo.fighter.crouchRight;
            p2CurrentSprite = playerTwo.facingRight ? stillP2 : reverseImage(stillP2);
        }
    }

    /**
     * Sends the health data as an int array
     * @return int array of p1 and p2 health
     */
    public int[] sendServerMasterHealthData(){
        return new int[]{ (int) playerOne.getHealth(), (int) playerTwo.getHealth() };
    }


    /**
     * Syncs the health bar based off the given number and player
     * @param playerHealth Health to set the bar to
     * @param player1 which player it should be
     */
    public void syncHealthBars(int playerHealth, boolean player1){
        if(player1 && playerOne.getHealth() != playerHealth){
            playerOne.setHealth(playerHealth);
        }
        else if(playerTwo.getHealth() != playerHealth){
            playerTwo.setHealth(playerHealth);
        }
    }



    //This method will reset the game to the beginning of a fight state.
    //This will set the variables to proper values that starts off a game
    private void resetGame(){
        //Game variables
        gameRunning = true;
        gameHadEnded = false;
        setUpBackGround();
        playerOne.setHealth(MAX_HEALTH);
        playerTwo.setHealth(MAX_HEALTH);
        //Move p1 about 1/5 in
        moveP1((X_BORDER * (1/5.0)), PLAYER_GROUND_BORDER);
        //Move p2 opposite end
        moveP2((X_BORDER * (4/5.0)), PLAYER_GROUND_BORDER);
    }

    //Moves p1 to spot given
    private void moveP1(double x, double y){
        p1Rec.relocate(x,y);
    }

    //Moves p2 to spot given
    private void moveP2(double x, double y){
        p2Rec.relocate(x,y);
    }


    //Calls all the movement methods depending on given players
    private void updatePlayerPosition(Player player){
        //Find out which player it is for and update vars
        boolean isP1 = (player.equals(playerOne));
        Rectangle playerRec = isP1 ? p1Rec : p2Rec;
        Rectangle otherRec = isP1 ? p2Rec : p1Rec;
        boolean[] actionArray = isP1 ? p1Actions : p2Actions;
        Queue<Image> frameQueue = isP1 ? p1FrameQueue : p2FrameQueue;
        Image currentSprite;
        //Do animations depending on variables
        if(actionArray[JUMP] && playerRec.getLayoutY() == PLAYER_GROUND_BORDER) {
            int plyrJumpVel = player.jump();
            p1JumpVelocity = isP1 ? plyrJumpVel : p1JumpVelocity;
            p2JumpVelocity = isP1 ? p2JumpVelocity : plyrJumpVel;
            actionArray[JUMP] = false;
        }
        //Only crouch if not already
        if(actionArray[CROUCH] && playerRec.getHeight() != CROUCH_HEIGHT &&
                playerRec.getLayoutY() == PLAYER_GROUND_BORDER) {
            currentSprite = player.fighter.crouchRight;
            setSprite(playerRec,currentSprite,isP1);
            player.crouch(playerRec);
        }
        //Only move right if less than border and not crouched
        if(actionArray[MOVE_RIGHT] && playerRec.getLayoutX() < PLAYER_RIGHT_BORDER &&
            !actionArray[CROUCH]) {
            player.moveRight(playerRec);
            if(playerRec.getLayoutY() == PLAYER_GROUND_BORDER){
                addFrameToQueue(frameQueue, player.walkAnimator.next(), player.facingRight, 1);
            }
        }
        //Only move left if greater than left border and not crouched
        if(actionArray[MOVE_LEFT] && playerRec.getLayoutX() > PLAYER_LEFT_BORDER &&
                !actionArray[CROUCH]) {
            player.moveLeft(playerRec);
            if(playerRec.getLayoutY() == PLAYER_GROUND_BORDER){
                addFrameToQueue(frameQueue, player.walkAnimator.next(), player.facingRight, 1);
            }
        }
        if(actionArray[LIGHT]) {
            handleAttack(true, player, isP1, playerRec, otherRec, frameQueue);
        }
        if(actionArray[HEAVY]){
            handleAttack(false, player, isP1, playerRec, otherRec, frameQueue);
        }
        if(!actionArray[CROUCH] && playerRec.getHeight() == CROUCH_HEIGHT){
            player.stand(playerRec);
        }
        //set facing right variable for players
        player.setFacing(playerRec.getLayoutX() < otherRec.getLayoutX());
    }

    //Does attack based off of heavy or light
    private void handleAttack(boolean isLight, Player player, boolean isP1,
                              Rectangle playerRec, Rectangle otherRec, Queue<Image> frameQueue) {
        long now = System.currentTimeMillis();
        int COOLDOWN_LIGHT = 1000;
        int COOLDOWN_HEAVY = 2000;
        long cool_down = isLight ? COOLDOWN_LIGHT : COOLDOWN_HEAVY;
        long lastAttackTime = isP1 ? (isLight ? lastP1Light : lastP1Heavy) : (isLight ? lastP2Light : lastP2Heavy);

        //If last attack was greater than or equal to the cooldown period, do the attack
        if (lastAttackTime == -1 || now - lastAttackTime >= cool_down) {
            //do light or heavy animation
            if(isLight){
                SoundEffect.playPunchSound();
                addFrameToQueue(frameQueue, player.fighter.lightOneRight, player.facingRight, 3);
                addFrameToQueue(frameQueue, player.fighter.lightTwoRight, player.facingRight, 3);
                addFrameToQueue(frameQueue, player.fighter.lightThreeRight, player.facingRight, 3);
            }
            else{
                SoundEffect.playKickSound();
                addFrameToQueue(frameQueue, player.fighter.heavyOneRight, player.facingRight, 5);
                addFrameToQueue(frameQueue, player.fighter.heavyTwoRight, player.facingRight, 3);
                addFrameToQueue(frameQueue, player.fighter.heavyThreeRight, player.facingRight, 5);
            }
            if (player.tryAttack(playerRec, otherRec)) {
                int HEAVY_DAMAGE = 30;
                int LIGHT_DAMAGE = 10;
                int damage = isLight ? LIGHT_DAMAGE : HEAVY_DAMAGE;
                Player target = isP1 ? playerTwo : playerOne;
                takeDamage(target, damage);
                SoundEffect.playHitSound();
            }
            if (isP1) {
                if (isLight) {
                    lastP1Light = now;
                } else {
                    lastP1Heavy = now;
                }
            } else {
                if (isLight) {
                    lastP2Light = now;
                } else {
                    lastP2Heavy = now;
                }
            }
        }
    }

    //This check if one the players has died
    //Will end the game if true
    private boolean checkWin(){
        p1HealthRec.setWidth(MAX_HEALTH * (playerOne.getHealth() / MAX_HEALTH));
        p2HealthRec.setWidth(MAX_HEALTH * (playerTwo.getHealth() / MAX_HEALTH));
        if(playerOne.getHealth() <= 0){
            endGame(false);
            return true;
        }
        else if(playerTwo.getHealth() <= 0){
            endGame(true);
            return true;
        }

        return false;
    }

    //This will set the title and stop updating the screen since the game has ended
    private void endGame(boolean p1Won){
        Label winnerLabel = new Label();
        winnerLabel.setFont(myFont);
        winnerLabel.setStyle("-fx-text-fill: white;");
        if(p1Won){
            winnerLabel.setText("PLAYER ONE WINS!");
            deathAnimation(p2ScreenSprite,playerTwo);
        }
        else{
            winnerLabel.setText("PLAYER TWO WINS!");
            deathAnimation(p1ScreenSprite,playerOne);
        }

        gameGrid.getChildren().add(winnerLabel);
        winnerLabel.relocate(GAME_WIDTH/2 - (winnerLabel.getText().length() * 10), GAME_HEIGHT/2);
        gameOverControls();
    }

    //Stops player controlling by removing key listeners
    private void gameOverControls(){
        gameScene.removeEventHandler(KeyEvent.KEY_PRESSED, gameScene.getOnKeyPressed());
    }

    //Spins the loosing sprite and throws them offscreen
    private void deathAnimation(ImageView sprite, Player player){
        //Play the death punch sound
        SoundEffect.playDeathPunchSound();

        //Death animation
        double toX = GAME_WIDTH + PLAYER_WIDTH_OFFSET;
        if(player.getFacing()){
            toX = -(PLAYER_WIDTH_OFFSET * 3);
        }

        KeyValue pathX = new KeyValue(sprite.layoutXProperty(), toX);
        KeyValue pathY = new KeyValue(sprite.layoutYProperty(), GAME_HEIGHT * (1/3.0));
        KeyValue spin = new KeyValue(sprite.rotateProperty(), 3 * 360);

        Timeline deathAni = new Timeline();
        deathAni.getKeyFrames().add(new KeyFrame(Duration.seconds(0.5), pathX, pathY, spin));
        deathAni.setOnFinished(event -> {
                    gameGrid.getChildren().remove(sprite);
                    gameRunning = false;
                });

        //play animation
        deathAni.play();
    }

    /**
     * @return Boolean representing if the game is over, and it is time to go to menu
     */
    public boolean checkIfOver() { return gameHadEnded; }

    //Does countdown until game moves to menu
    private void startCountDown(){
        Timeline countDown = new Timeline();
        countDown.getKeyFrames().add(new KeyFrame(Duration.seconds(10)));
        countDown.setOnFinished(event -> {
            gameHadEnded = true;
            gameGrid.getChildren().clear();
        });
        countDown.play();
    }

    /**
     * This is the method that will move all the objects and or change what is happening on screen
     */
    public void updateScreen(){
        if(!gameRunning) return;
        if(checkWin()){
            startCountDown();
        }
        //turn on inputs for computer
        if(againstComputer){
            ((COMPlayer) playerTwo).computeMoves(p2Rec, p1Rec, p2Actions,
                    MOVE_RIGHT, MOVE_LEFT, JUMP, CROUCH, HEAVY, LIGHT);
        }
        updatePlayerPosition(playerOne);
        updatePlayerPosition(playerTwo);
        doGravity();
        //Update Sprites
        setSprite(p1Rec,p1CurrentSprite,true);
        updateFrameQueue(p1FrameQueue,true);
        setSprite(p2Rec,p2CurrentSprite,false);
        updateFrameQueue(p2FrameQueue,false);
        //Turn off inputs for computer
        if(againstComputer){
            ((COMPlayer) playerTwo).relaxMoves(p2Actions, CROUCH, p2Rec);
        }
    }

}
