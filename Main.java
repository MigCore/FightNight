import javafx.application.Application;
import javafx.stage.Stage;

/**
 * CS351 Project 3 Arcade Game Julian Fong
 *
 * Main Class: Sets up application to be run and serves as entry point for the entire program
 */
public class Main extends Application {
    public static void main(String[] args) { launch(args); }
    @Override
    public void start(Stage primaryStage) {
        GameUI gameUI = new GameUI(primaryStage);
        //Encapsulated way of showing primary stage
        //Can be switched to new stage later
        gameUI.goToMenu();
    }
}
