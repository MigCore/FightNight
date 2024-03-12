import javafx.scene.shape.Rectangle;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * CS351 Julian Fong Arcade Game Project 3
 *
 * COMPlayer class: This is the class that makes an AI player for the one player mode user to play against. It
 * will fight the other player using a basic way of attacking that is not too hard to beat.
 */

public class COMPlayer extends Player {

    private int stallFrames;
    private int crouchFrames;

    public COMPlayer(Fighter comPlayer){
        stallFrames = 0;
        crouchFrames = 0;
        this.fighter = comPlayer;
        createWalkAnimator();
    }

    public void computeMoves(Rectangle compHitBox, Rectangle otherHitBox,
                             boolean[] moveArray, int moveRightCode, int moveLeftCode,
                             int jumpCode, int crouchCode, int heavyCode, int lightCode){
        //If COMP is stalled it cannot move
        if(checkIfStalled()) return;
        contemplateMoveHorizontal(compHitBox, otherHitBox, moveArray, moveRightCode, moveLeftCode);
        contemplateReaction(compHitBox, otherHitBox, moveArray, jumpCode, crouchCode, heavyCode, lightCode);
    }

    //Randomly stall the computer
    private boolean checkIfStalled(){
        if(stallFrames == 0 && ThreadLocalRandom.current().nextInt(1, 100) == 1) stallFrames = 60;
        if(stallFrames > 0) {
            stallFrames--;
            return true;
        }
        return false;
    }




    /**
     * This will have the computer move left or right depending on if it is too far from the player
     * @param compHitBox Computer's rectangle representing its hit box
     * @param otherHitBox Player's rectangle representing its hit box
     * @param moveArray Computer move array
     * @param moveRightCode Code to move right in array
     * @param moveLeftCode Code to move left in array
     */
    private void contemplateMoveHorizontal(Rectangle compHitBox, Rectangle otherHitBox,
                                     boolean[] moveArray, int moveRightCode, int moveLeftCode){
        //See if the other player is too far right
        double compX = compHitBox.getLayoutX();
        double otherX = otherHitBox.getLayoutX();

        if(facingRight && compX + compHitBox.getWidth() < otherX - 1) {
            moveArray[moveRightCode] = true;
        }
        else if(!facingRight && compX > otherX + otherHitBox.getWidth() + 1){
            moveArray[moveLeftCode] = true;
        }
    }

    private void contemplateReaction(Rectangle compHitBox, Rectangle otherHitBox,
                                          boolean[] moveArray, int jumpCode, int crouchCode,
                                     int heavyCode, int lightCode){
        double compX = compHitBox.getLayoutX();
        double otherX = otherHitBox.getLayoutX();
        if(Math.abs((otherX - compX)) <= compHitBox.getWidth() + 10){
            int chanceAction = ThreadLocalRandom.current().nextInt(1,100);
            //10 percent chance to jump
            if(chanceAction >= 1 && chanceAction <= 10) moveArray[jumpCode] = true;
            //10 percent chance to light attack
            if(chanceAction >= 11 && chanceAction <= 20) moveArray[lightCode] = true;
            //10 percent chance to heavy attack
            if(chanceAction >= 21 && chanceAction <= 30) moveArray[heavyCode] = true;
            //10 percent chance to crouch
            if(crouchFrames == 0 && chanceAction >= 31 && chanceAction <= 40){
                moveArray[crouchCode] = true;
                crouchFrames = 30;
            }
        }
    }


    /**
     * This will turn all move inputs off for the computer
     * @param moveArray the computer's move array
     */
    public void relaxMoves(boolean[] moveArray, int crouchCode, Rectangle compRec){
        Arrays.fill(moveArray, false);
        if(crouchFrames > 0){
            moveArray[crouchCode] = true;
            crouchFrames--;
            if(crouchFrames == 0) {
                moveArray[crouchCode] = false;
                stand(compRec);
            }
        }
    }

}
