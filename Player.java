import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

import java.util.Iterator;
import java.util.List;

/**
 * CS351 Project 3 Arcade Game: Julian Fong
 *
 * Player Interface: Acts as the placeholder for what kind of player will be used in the game, either a user
 * or a computer that can do a couple of different moves that are the response of input or comp AI.
 */

public class Player {

    //Player variables
    protected Fighter fighter;
    protected double health;
    protected boolean facingRight;

    public Iterator<Image> walkAnimator;


    /**
     * Moves a player on screen to the right one pixel
     */
    
    public void moveRight(Rectangle player){
        player.relocate(player.getLayoutX() + 5, player.getLayoutY());
    }

    /**
     * Moves a player on screen to the left one pixel
     */
    
    public void moveLeft(Rectangle player) {
        player.relocate(player.getLayoutX() - 5, player.getLayoutY());
    }

    /**
     * Makes a player crouch
     *
     * @param player Rectangle hit-box of player
     */
    
    public void crouch(Rectangle player) {
        double HIT_BOX_HEIGHT = player.getHeight()/2.0;
        player.setHeight(HIT_BOX_HEIGHT);
        player.relocate(player.getLayoutX(), player.getLayoutY() + HIT_BOX_HEIGHT);
    }

    /**
     * Player does light attack
     */
    
    public boolean tryAttack(Rectangle thisPlayer, Rectangle otherPlayer) {
        boolean wasHit;
        // Other player was crouched, and you were standing, cannot hit
        if(otherPlayer.getHeight() < thisPlayer.getHeight()) return false;
        //The difference in jump heights was greater than a crouch height, cannot hit
        if(Math.abs((int) (thisPlayer.getLayoutY() - otherPlayer.getLayoutY())) >
                otherPlayer.getHeight() * (0.8)) {
            return false;
        }
        //Reach of players
        int ATTACK_RANGE = 10;
        if(facingRight){
            //in range of light attack return true for hit
            wasHit =  thisPlayer.getLayoutX() + thisPlayer.getWidth() >= otherPlayer.getLayoutX() - ATTACK_RANGE;
        }
        else{
            //in range of light attack from right to left, send hit
            wasHit = thisPlayer.getLayoutX() <= otherPlayer.getLayoutX() + otherPlayer.getWidth() + ATTACK_RANGE;
        }


        return wasHit;
    }


    /**
     * Makes a player jump
     */
    
    public int jump() {
        //Returns the jump height of all players
        return 12;
    }

    /**
     * Makes a player stand up
     *
     * @param player Hit box to stand up
     */
    
    public void stand(Rectangle player) {
        double CROUCH_HEIGHT = player.getHeight();
        double STAND_HEIGHT = CROUCH_HEIGHT * 2;
        player.relocate(player.getLayoutX(), player.getLayoutY() - CROUCH_HEIGHT);
        player.setHeight(STAND_HEIGHT);
    }

    /**
     * @return The number of health points a player has
     */
    
    public double getHealth() {
        return health;
    }

    /**
     * Sets a player's health to this number
     *
     * @param HP the number of points the health should be set to
     */
    
    public void setHealth(double HP) {
        health = HP;
    }

    /**
     * Sets the direction a player is facing
     *
     * @param facing The fact that a player is facing right
     */
    
    public void setFacing(boolean facing) {
        facingRight = facing;
    }

    /**
     * @return The fact that the direction the player is facing is right
     */
    
    public boolean getFacing() {
        return facingRight;
    }



    //The circle of walking animations to be repeated everytime the player is moving
    protected void createWalkAnimator(){
        Image h1 = fighter.stepHalfOneRight;
        Image h2 = fighter.stepHalfTwoRight;
        Image f1 = fighter.stepFullOneRight;
        Image f2 = fighter.stepFullTwoRight;

        walkAnimator = new CircularIterator(List.of(h1,h1,h1,h1,h1,
                f1,f1,f1,f1,f1,
                h2,h2,h2,h2,h2,
                f2,f2,f2,f2,f2));
    }

}
