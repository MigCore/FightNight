/**
 * CS351 Project 3 Julian Fong
 *
 * GameUI class: Uses to create/switch from gameplay to mode picking screen
 */

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GameUI {

    private int activeLobbyButtonIndex = 0;
    private List<StackPane> lobbyButtons;

    private final Stage mainWindow;
    private final Scene menuScreen;
    private final Scene gameScreen;
    private final Scene lobbyScreen;
    private VBox characterButtons;
    private Pane gamePane;
    private Pane characterSelectPane;
    private Scene characterSelectScreen;
    final int GAME_WIDTH = 600;
    final int GAME_HEIGHT = 600;

    //MusicPlayer
    private final MediaView songViewer;
    private final MusicPlayer music;
    private final Media menuSong;
    private final Media gameplaySong;

    //Setup variables
    private boolean needsComputerPlayer;
    private boolean onlineGame;
    private int activeButtonIndex;
    private Rectangle[] buttonArray;
    private Label[] btnLabelArray;
    private Color BUTTON_BORDER_COLOR;
    private Color BUTTON_HIGHLIGHT_COL;
    //Font variables
    //license: Public Domain
    //link: https://www.fontspace.com/public-pixel-font-f72305
    // Load the custom pixel font
    private final Font myFont = Font
            .loadFont("file:resources/com/JFong/CS351Project3_fightnight/fonts/PublicPixel.ttf", 20);

    //Player variables that can be changed from humans or computers
    private Player p1;
    private Player p2;
    private VBox lobbyBox;


    public GameUI(Stage mainWindow){
        //setup songs
        String menuSongPath = "resources/com/JFong/CS351Project3_fightnight/Songs/TropicalIsland.mp3";
        String gameSongPath = "resources/com/JFong/CS351Project3_fightnight/Songs/SparringArena.mp3";
        File menuSongFile = new File(menuSongPath);
        File gameSongFIle = new File(gameSongPath);
        menuSong = new Media(menuSongFile.toURI().toString());
        gameplaySong = new Media(gameSongFIle.toURI().toString());

        mainWindow.setTitle("Fight Night");
        mainWindow.setResizable(false);
        needsComputerPlayer = false;
        songViewer = new MediaView();
        music = new MusicPlayer(menuSong);
        activeButtonIndex = 0;
        menuScreen = setUpMenu();
        gameScreen = setUpGameScreen();
        lobbyScreen = setupLobbyScreen();
        this.mainWindow = mainWindow;
        goToMenu(); //Start with the menu screen
    }


    //Creates the menu window to be switched to periodically
    private Scene setUpMenu(){
        //Size constants
        int MENU_WIDTH = 600;
        int MENU_HEIGHT = 600;

        Pane menu = createMenuButtons(MENU_WIDTH,MENU_HEIGHT);
        //Create scene and add listeners
        Scene menuWindow = new Scene(menu);

        //Key Listeners for the scene
        menuWindow.setOnKeyPressed(event -> {
            switch (event.getCode()){
                case UP -> {
                    if (activeButtonIndex > 0){
                        buttonArray[activeButtonIndex].setStroke(BUTTON_BORDER_COLOR);
                        activeButtonIndex--;
                        buttonArray[activeButtonIndex].setStroke(BUTTON_HIGHLIGHT_COL);
                    }
                }
                case DOWN -> {
                    if (activeButtonIndex < buttonArray.length - 1){
                        buttonArray[activeButtonIndex].setStroke(BUTTON_BORDER_COLOR);
                        activeButtonIndex++;
                        buttonArray[activeButtonIndex].setStroke(BUTTON_HIGHLIGHT_COL);
                    }
                }
                case ENTER -> {
                    if(btnLabelArray[activeButtonIndex].getText().equals("ONE PLAYER!")){
                        needsComputerPlayer = true;
                        characterSelectScreen = setUpCharacterSelect();
                        onlineGame = false;
                        goToCharSelectScreen();

                    }
                    else if(btnLabelArray[activeButtonIndex].getText().equals("TWO PLAYERS!")){
                        needsComputerPlayer = false;
                        characterSelectScreen = setUpCharacterSelect();
                        onlineGame = false;
                        goToCharSelectScreen();
                    }
                    else if(btnLabelArray[activeButtonIndex].getText().equals("MULTIPLAYER!"))
                    {
                        onlineGame = true;
                        needsComputerPlayer = false;
                        characterSelectScreen = setUpCharacterSelect();
                        goToCharSelectScreen();
                    }
                    else{
                        //Quit was pressed
                        System.exit(0);
                    }
                }
            }
        });
        //set up the title for the pane
        createTitle(menu);

        return menuWindow;
    }

    private void createTitle(Pane mainPane){
        double menuWidth = mainPane.getPrefWidth();
        double menuHeight = mainPane.getPrefHeight();
        Rectangle title = new Rectangle(menuWidth / 2.0, menuHeight / 4.0);
        title.relocate((menuWidth / 2.0) - title.getWidth() / 2.0,
                menuHeight * (0.1));
        title.setFill(Color.WHITE);
        mainPane.getChildren().add(title);
        try{
            ImageView titleView = new ImageView(new Image(new FileInputStream(
                    "resources/com/JFong/CS351Project3_fightnight/backgrounds/FightnightTitle.png")));
            mainPane.getChildren().add(titleView);
            titleView.relocate(title.getLayoutX(),title.getLayoutY());
        }catch (IOException ex){
            System.err.println("Could not find title image!");
        }

    }

    //Creates the pane that will hold the menu buttons
    private Pane createMenuButtons(int MENU_WIDTH, int MENU_HEIGHT){
        //Active button variables
        //Changed to atomic integer to be accessible from lambdas
        int BUTTON_WIDTH = MENU_WIDTH / 2;
        int BUTTON_HEIGHT = BUTTON_WIDTH / 4;
        BUTTON_BORDER_COLOR = Color.WHITE;
        BUTTON_HIGHLIGHT_COL = new Color(0,1,0,1);
        //main pain for window
        Pane menu = new Pane();
        menu.setPrefSize(MENU_WIDTH, MENU_HEIGHT);
        menu.setStyle("-fx-background-color: black;");
        //VBOX to hold buttons
        VBox menuButtons = new VBox(5.0);
        //Button rectangles and panes
        StackPane onePlayerPane = new StackPane();
        StackPane twoPlayerPane = new StackPane();
        StackPane multiPlayerPane = new StackPane();
        StackPane quitPane = new StackPane();
        Rectangle onePlayerMode = new Rectangle(BUTTON_WIDTH, BUTTON_HEIGHT);
        Rectangle twoPlayerMode = new Rectangle(BUTTON_WIDTH, BUTTON_HEIGHT);
        Rectangle quit = new Rectangle(BUTTON_WIDTH, BUTTON_HEIGHT);
        Rectangle multiplayerMode = new Rectangle(BUTTON_WIDTH, BUTTON_HEIGHT);
        //Put the panes in the vbox and create list of buttons/iterator
        menuButtons.getChildren().addAll(onePlayerPane,twoPlayerPane,multiPlayerPane,quitPane);
        //Set up button array
        buttonArray = new Rectangle[] {onePlayerMode, twoPlayerMode,multiplayerMode, quit};

        //Set up one player mode button
        onePlayerMode.setStrokeWidth(3);
        onePlayerMode.setStroke(BUTTON_BORDER_COLOR);
        onePlayerMode.setFill(Color.TRANSPARENT);
        Label onePlayerLabel = new Label("ONE PLAYER!");
        onePlayerLabel.setFont(myFont);
        onePlayerLabel.setStyle("-fx-text-fill: white;");
        //Add rectangle to pane
        onePlayerPane.getChildren().addAll(onePlayerMode,onePlayerLabel);

        //Set up two player mode button
        twoPlayerMode.setStrokeWidth(3);
        twoPlayerMode.setStroke(BUTTON_BORDER_COLOR);
        twoPlayerMode.setFill(Color.TRANSPARENT);
        Label twoPlayerLabel = new Label("TWO PLAYERS!");
        twoPlayerLabel.setStyle("-fx-text-fill: white;");
        twoPlayerLabel.setFont(myFont);
        //Add rectangle to pane
        twoPlayerPane.getChildren().addAll(twoPlayerMode,twoPlayerLabel);

        multiplayerMode.setStrokeWidth(3);
        multiplayerMode.setStroke(BUTTON_BORDER_COLOR);
        multiplayerMode.setFill(Color.TRANSPARENT);
        Label multiplayerLabel = new Label("MULTIPLAYER!");
        multiplayerLabel.setStyle("-fx-text-fill: white;");
        multiplayerLabel.setFont(myFont);
        //Add rectangle to pane
        multiPlayerPane.getChildren().addAll(multiplayerMode,multiplayerLabel);

        //Set up one player mode button
        quit.setStrokeWidth(3);
        quit.setStroke(BUTTON_BORDER_COLOR);
        quit.setFill(Color.TRANSPARENT);
        Label quitLabel = new Label("QUIT");
        quitLabel.setStyle("-fx-text-fill: white;");
        quitLabel.setFont(myFont);
        //Add rectangle to pane
        quitPane.getChildren().addAll(quit,quitLabel);

        //Default to onePlayer button highlighted
        onePlayerMode.setStroke(BUTTON_HIGHLIGHT_COL);
        btnLabelArray = new Label[] {onePlayerLabel,twoPlayerLabel,multiplayerLabel,quitLabel};

        //Add buttons to pane
        menu.getChildren().add(menuButtons);
        menuButtons.relocate((MENU_WIDTH / 2.0) - (BUTTON_WIDTH / 2.0),
                (MENU_HEIGHT / 2.0) - (buttonArray.length * BUTTON_HEIGHT) / 4.0);
        return menu;
    }

    //Creates the character select screen
    private Scene setUpCharacterSelect(){
        characterSelectPane = createCharacterSelectButtons();
        characterSelectScreen = new Scene(characterSelectPane);
        setUpCharacterSelectListeners();
        activeButtonIndex = 0; // Add this line to set activeButtonIndex to 0
        return characterSelectScreen;
    }

    // Creates the pane that will hold the character select buttons
    private Pane createCharacterSelectButtons() {
        int BUTTON_WIDTH = 300;
        int BUTTON_HEIGHT = 75;
        Color BUTTON_BORDER_COLOR = Color.WHITE;
        Color BUTTON_HIGHLIGHT_COL = new Color(0, 1, 0, 1);

        // Create a VBox or any layout to hold character buttons
        characterButtons = new VBox(10.0);

        // Add buttons for each character
        for (Fighters fighter : Fighters.values()) {
            if (!fighter.equals(Fighters.DEFAULT)) {
                StackPane characterButtonPane = new StackPane();
                Rectangle characterButton = new Rectangle(BUTTON_WIDTH, BUTTON_HEIGHT);
                Label characterLabel = new Label(fighter.name());

                characterButton.setStrokeWidth(3);
                characterButton.setStroke(BUTTON_BORDER_COLOR);
                characterButton.setFill(Color.TRANSPARENT);
                characterLabel.setFont(myFont);
                characterLabel.setTextFill(Color.WHITE);

                if (!characterButtons.getChildren().isEmpty()) {
                    StackPane firstCharacterButtonPane = (StackPane) characterButtons.getChildren().get(0);
                    Rectangle firstCharacterButton = (Rectangle) firstCharacterButtonPane.getChildren().get(0);
                    firstCharacterButton.setStroke(BUTTON_HIGHLIGHT_COL);
                }

                characterButtonPane.getChildren().addAll(characterButton, characterLabel);
                characterButtonPane.setOnMouseClicked(e -> handleCharacterSelection(fighter));

                characterButtons.getChildren().add(characterButtonPane);
            }
        }

        Pane characterSelectPane = new Pane(characterButtons);
        characterSelectPane.setPrefSize(GAME_WIDTH, GAME_HEIGHT);
        characterSelectPane.setStyle("-fx-background-color: black;");

        // Center the buttons both horizontally and vertically
        double totalHeight = (Fighters.values().length - 1) * (BUTTON_HEIGHT + 10.0); // total height of buttons + spacing
        double translateY = (GAME_HEIGHT - totalHeight) / 2.0;
        characterButtons.setTranslateY(translateY);

        double totalWidth = BUTTON_WIDTH; // width is constant for all buttons
        double translateX = (GAME_WIDTH - totalWidth) / 2.0;
        characterButtons.setTranslateX(translateX);

        return characterSelectPane;
    }

    // Handle character selection when a button is clicked
    private void handleCharacterSelection(Fighters selectedFighter) {
        if (needsComputerPlayer) {
            if (p1 == null) {
                p1 = new UserPlayer(selectedFighter.getFighter());
            } else if (p2 == null) {
                p2 = new COMPlayer(selectedFighter.getFighter());
            }
        } else {
            if (p1 == null) {
                p1 = new UserPlayer(selectedFighter.getFighter());
            } else if (p2 == null) {
                p2 = new UserPlayer(selectedFighter.getFighter());
            }
        }

        // Check if both players have selected characters
        if (p1 != null && p2 != null) {
            // Optionally, you can proceed to the game screen after character selection
            if(onlineGame) goToLobby();
            else goToGame(false, 0,null);
        }
    }

    // Set up key listeners for character selection
    private void setUpCharacterSelectListeners() {
        characterSelectScreen.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP -> {
                    if (activeButtonIndex > 0) {
                        setActiveCharacter(activeButtonIndex - 1);
                    }
                }
                case DOWN -> {
                    if (activeButtonIndex < characterButtons.getChildren().size() - 1) {
                        setActiveCharacter(activeButtonIndex + 1);
                    }
                }
                case ENTER -> {
                    // Add logic to set the character when the Enter key is pressed
                    setCharacterForActivePlayer();
                }
            }
        });
    }

    // Set the active character and update the button highlights
    private void setActiveCharacter(int newIndex) {
        StackPane oldCharacterButtonPane = (StackPane) characterButtons.getChildren().get(activeButtonIndex);
        Rectangle oldCharacterButton = (Rectangle) oldCharacterButtonPane.getChildren().get(0);
        oldCharacterButton.setStroke(BUTTON_BORDER_COLOR);

        StackPane newCharacterButtonPane = (StackPane) characterButtons.getChildren().get(newIndex);
        Rectangle newCharacterButton = (Rectangle) newCharacterButtonPane.getChildren().get(0);
        newCharacterButton.setStroke(BUTTON_HIGHLIGHT_COL);

        activeButtonIndex = newIndex;
    }

    // Add logic to set the character for the active player
    private void setCharacterForActivePlayer() {
        Label messageLabel;

        // Inside createCharacterSelectButtons() method
        messageLabel = new Label();
        messageLabel.setFont(myFont);
        messageLabel.setTextFill(Color.WHITE);
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setPrefWidth(GAME_WIDTH);
        messageLabel.setTranslateY(GAME_HEIGHT - 550);
        characterSelectPane.getChildren().add(messageLabel);

        Fighters selectedFighter = Fighters.valueOf(((Label) ((StackPane) characterButtons.getChildren().get(activeButtonIndex)).getChildren().get(1)).getText());

        if (needsComputerPlayer) {
            if (p1 == null) {
                p1 = new UserPlayer(selectedFighter.getFighter());
                showMessage(messageLabel,"Player 1 selected: " + selectedFighter.name());
            } else if (p2 == null) {
                p2 = new COMPlayer(selectedFighter.getFighter());
                showMessage(messageLabel,"Player 2 selected: " + selectedFighter.name());
            }
        } else {
            if (p1 == null) {
                p1 = new UserPlayer(selectedFighter.getFighter());
                showMessage(messageLabel,"Player 1 selected: " + selectedFighter.name());
            } else if (p2 == null) {
                p2 = new UserPlayer(selectedFighter.getFighter());
                showMessage(messageLabel,"Player 2 selected: " + selectedFighter.name());
            }
        }

        setActiveCharacter(0);

        // Check if both players have selected characters
        if (p1 != null && p2 != null) {
            // Optionally, you can proceed to the game screen after character selection
            if (onlineGame) goToLobby();
            else goToGame(false, 0, null);
        }
    }

    private void showMessage(Label messageLabel, String message) {
        messageLabel.setText(message);

        // Use a Timeline to remove the message after one second
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> messageLabel.setText("")));
        timeline.play();
    }

    //Switches to char select screen
    private void goToCharSelectScreen(){
        mainWindow.setScene(characterSelectScreen);
        showScreen();
    }


    private Scene setupLobbyScreen() {
        // Constants for button dimensions and styles
        int BUTTON_WIDTH = GAME_WIDTH / 2;
        int BUTTON_HEIGHT = BUTTON_WIDTH / 4;
        Color BUTTON_BORDER_COLOR = Color.WHITE;
        Color BUTTON_HIGHLIGHT_COL = new Color(0, 1, 0, 1);

        // Main pane for the window
        StackPane lobbyPane = new StackPane();
        lobbyPane.setPrefSize(GAME_WIDTH, GAME_HEIGHT);
        lobbyPane.setStyle("-fx-background-color: black;");

        // Initialize VBox to hold buttons
        lobbyBox = new VBox(5.0);

        // Create buttons as Rectangles and Labels
        Rectangle backRect = new Rectangle(BUTTON_WIDTH, BUTTON_HEIGHT);
        Label backLabel = new Label("Back <--");
        setupButtonStyle(backRect, backLabel, BUTTON_BORDER_COLOR, myFont);


        Rectangle joinRect = new Rectangle(BUTTON_WIDTH, BUTTON_HEIGHT);
        Label joinLabel = new Label("Join Game");
        setupButtonStyle(joinRect, joinLabel, BUTTON_BORDER_COLOR, myFont);

        Rectangle hostRect = new Rectangle(BUTTON_WIDTH, BUTTON_HEIGHT);
        Label hostLabel = new Label("Host Game");
        setupButtonStyle(hostRect, hostLabel, BUTTON_BORDER_COLOR, myFont);

        // Add Rectangles and Labels to StackPanes
        StackPane backPane = new StackPane(backRect, backLabel);
        StackPane joinPane = new StackPane(joinRect, joinLabel);
        StackPane hostPane = new StackPane(hostRect, hostLabel);

        // Add panes to VBox
        lobbyBox.getChildren().addAll(backPane, joinPane, hostPane);

        // Initialize the lobbyButtons list
        lobbyButtons = Arrays.asList(backPane, joinPane, hostPane);

        // Set the initial active button
        setActiveLobbyButton(activeLobbyButtonIndex);

        // Add VBox to the main pane
        lobbyPane.getChildren().add(lobbyBox);

        // Positioning the VBox in the center
        lobbyBox.relocate((GAME_WIDTH / 2.0) - (BUTTON_WIDTH / 2.0),
                (GAME_HEIGHT / 2.0) - (4 * BUTTON_HEIGHT) / 2.0);

        // Set up key listeners for lobby screen
        Scene lobbyScene = new Scene(lobbyPane);
        lobbyScene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP -> {
                    if (activeLobbyButtonIndex > 0) {
                        setActiveLobbyButton(activeLobbyButtonIndex - 1);
                    }
                }
                case DOWN -> {
                    if (activeLobbyButtonIndex < lobbyButtons.size() - 1) {
                        setActiveLobbyButton(activeLobbyButtonIndex + 1);
                    }
                }
                case ENTER -> {
                    triggerActiveButtonAction();
                }
            }
        });

        return lobbyScene;
    }



    private void setActiveLobbyButton(int index) {
        // Remove highlight from all buttons
        lobbyButtons.forEach(pane -> ((Rectangle) pane.getChildren().get(0)).setStroke(BUTTON_BORDER_COLOR));

        // Highlight the active button
        StackPane activeButton = lobbyButtons.get(index);
        ((Rectangle) activeButton.getChildren().get(0)).setStroke(BUTTON_HIGHLIGHT_COL);

        // Update the active button index
        activeLobbyButtonIndex = index;
    }



    private void triggerActiveButtonAction() {
        switch (activeLobbyButtonIndex) {
            case 0 -> goToMenu();
            case 1 -> joinGame();
            case 2 -> hostGame();
        }
    }

    private void joinGame() {
        TextField portField = new TextField("Game ID");
        TextField hostNameField = new TextField("Host Name");
        Button findGame = new Button("Find Game!");
        Button backButton = new Button("Back");
        findGame.setOnAction(evt1 -> {
            int portnum = Integer.parseInt(portField.getText());
            String hostname = hostNameField.getText();
            Thread findThread = new Thread(() -> {
                try (Socket clientSocket = new Socket(hostname, portnum)) {
                    InputStream in = clientSocket.getInputStream();
                    System.out.println("Joining game...");
                    int clientNum = in.read();
                    System.out.println("You are player " + (clientNum + 1));
                    Platform.runLater(() -> goToGame(true, clientNum, clientSocket));
                    while (!clientSocket.isClosed()) {
                        Thread.onSpinWait();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Failed to find game!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            findThread.start();
        });
        backButton.setOnAction(evt -> {
            lobbyBox.getChildren().clear();
            lobbyBox.getChildren().addAll(lobbyButtons);
            setActiveLobbyButton(0);
        });

        lobbyBox.getChildren().clear();
        lobbyBox.getChildren().addAll(portField, hostNameField, findGame, backButton);
    }

    private void hostGame() {
        int hostPort = ThreadLocalRandom.current().nextInt(2000, 8000);
        String hostName = "";
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Label hostAndPort = new Label("Game ID: " + hostPort + " Host Name: " + hostName);
        hostAndPort.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        lobbyBox.getChildren().add(hostAndPort);
        lobbyBox.getChildren().removeIf(element -> element instanceof StackPane);

        HostServer server = new HostServer(hostPort);
        Thread serverThread = new Thread(server);
        serverThread.start();
        Button startgame = new Button("Start!");
        final String finalHostName = hostName;
        Thread hostClient = new Thread(() -> {
            try {
                final Socket firstClient = new Socket(finalHostName, hostPort);
                startgame.setOnAction(event3 -> {
                    server.stopSearching();
                    int clientNum = -1;
                    try {
                        clientNum = firstClient.getInputStream().read();
                        System.out.println("You are player " + (clientNum + 1));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    assert clientNum != -1;
                    final int finalClientNum = clientNum;
                    goToCharSelectScreen();
                    Platform.runLater(() -> goToGame(true, finalClientNum, firstClient));
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        hostClient.start();
        lobbyBox.getChildren().add(startgame);
    }


    private void setupButtonStyle(Rectangle rect, Label label, Color borderColor, Font font) {
        rect.setStrokeWidth(3);
        rect.setStroke(borderColor);
        rect.setFill(Color.TRANSPARENT);
        label.setFont(font);
        label.setStyle("-fx-text-fill: white;");
    }



    private void setAndStartMusic(Media song, Scene currScene){
        Pane currScreen = (Pane) currScene.getRoot();
        currScreen.getChildren().removeIf(node -> node instanceof MediaView);
        songViewer.setMediaPlayer(music.changeSong(song));
        currScreen.getChildren().add(songViewer);
        music.startSong();
    }

    /**
     * Shows the screen to the main stage that it has been switched to
     */
    public void showScreen(){
        mainWindow.show();
    }

    /**
     * Switch main stage to menu scene
     */
    public void goToMenu(){
        mainWindow.setScene(menuScreen);
        showScreen();
        setAndStartMusic(menuSong, menuScreen);
    }

    //Goes to the lobby screen
    public void goToLobby(){
        mainWindow.setScene(lobbyScreen);
        showScreen();
    }

    //initialize the game pane scene
    private Scene setUpGameScreen(){
        gamePane = new Pane();
        gamePane.setPrefSize(GAME_WIDTH,GAME_HEIGHT);
        return new Scene(gamePane);
    }

    /**
     * Switch main stage to game scene and create a game loop based off of characters
     * Starts the game loop to start logic
     */
    public void goToGame(boolean online, int clientNumber, Socket clientSocket){
        GameLoop gameLoop = new GameLoop(new GameLogic(p1, p2, gamePane, gameScreen, GAME_WIDTH, GAME_HEIGHT, online, clientNumber),
                this, clientSocket);
        mainWindow.setScene(gameScreen);
        showScreen();
        gameLoop.start();
        setAndStartMusic(gameplaySong, gameScreen);
    }

    /**
     * Resets both players to have no value (no character selected).
     */
    public void resetPlayers() {
        p1 = null;
        p2 = null;
    }
}