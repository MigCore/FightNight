/**
 * CS351 Project 3 Arcade Game: Julian Fong
 *
 * UserPlayer Class: This class is for the user to control for their player on screen. It does not contain any methods,
 * as it inherits them from player, the computer play will use it differently as it needs logic to know when to
 * do certain methods
 */

public class UserPlayer extends Player {


    public UserPlayer(Fighter userPlayer){
        this.fighter = userPlayer;
        createWalkAnimator();
    }

}
