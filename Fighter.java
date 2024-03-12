import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * CS351 Julian Fong Arcade Game Project 3
 *
 * Fighter class: This is a set of paths for the type of fighter that will be used to animate their,
 * light, heavy, walking, and jumping, and crouching animations
 */

public class Fighter{

    final String fighterName;

    //Sprites
    Image jumpRight;
    Image crouchRight;
    Image stepHalfOneRight;
    Image stepHalfTwoRight;
    Image stepFullOneRight;
    Image stepFullTwoRight;
    Image stillRight;
    Image lightOneRight;
    Image lightTwoRight;
    Image lightThreeRight;
    Image heavyOneRight;
    Image heavyTwoRight;
    Image heavyThreeRight;
    Image damageOneRight;
    Image damageTwoRight;

    Fighter(String fighter) {
        fighterName = fighter;
        switch(fighter) {
            case ("Goku"):
                try {
                    String preface = "resources/com/JFong/CS351Project3_fightnight/Fighters/Goku/Sprites/";
                    jumpRight = new Image(new FileInputStream(preface + "GokuJumpRight.png"));
                    crouchRight = new Image(new FileInputStream(preface + "GokuCrouchRightTest.png"));
                    stepHalfOneRight = new Image(new FileInputStream(preface + "GokuStepHalfOneRight.png"));
                    stepHalfTwoRight = new Image(new FileInputStream(preface + "GokuStepHalfTwoRight.png"));
                    stepFullOneRight = new Image(new FileInputStream(preface + "GokuStepFullOneRight.png"));
                    stepFullTwoRight = new Image(new FileInputStream(preface + "GokuStepFullTwoRight.png"));
                    stillRight = new Image(new FileInputStream(preface + "GokuStanding.png"));
                    lightOneRight = new Image(new FileInputStream(preface + "GokuLightOneRight.png"));
                    lightTwoRight = new Image(new FileInputStream(preface + "GokuLightTwoRight.png"));
                    lightThreeRight = new Image(new FileInputStream(preface + "GokuLightThreeRight.png"));
                    heavyOneRight = new Image(new FileInputStream(preface + "GokuHeavyOneRight.png"));
                    heavyTwoRight = new Image(new FileInputStream(preface + "GokuHeavyTwoRight.png"));
                    heavyThreeRight = new Image(new FileInputStream(preface + "GokuHeavyThreeRight.png"));
                    damageOneRight = new Image(new FileInputStream(preface + "GokuDamageOneRight.png"));
                    damageTwoRight = new Image(new FileInputStream(preface + "GokuDamageTwoRight.png"));
                } catch (FileNotFoundException ex) {
                    System.err.println("File Not Found!");
                    System.exit(1);
                }
                break;
            case ("Vegeta"):
                try {
                    String preface = "resources/com/JFong/CS351Project3_fightnight/Fighters/Vegeta/Sprites/";

                    jumpRight = new Image(new FileInputStream(preface + "VegetaJumpRight.png"));
                    crouchRight = new Image(new FileInputStream(preface + "VegetaCrouchRightTest.png"));
                    stepHalfOneRight = new Image(new FileInputStream(preface + "VegetaStepHalfOneRight.png"));
                    stepHalfTwoRight = new Image(new FileInputStream(preface + "VegetaStepHalfTwoRight.png"));
                    stepFullOneRight = new Image(new FileInputStream(preface + "VegetaStepFullOneRight.png"));
                    stepFullTwoRight = new Image(new FileInputStream(preface + "VegetaStepFullTwoRight.png"));
                    stillRight = new Image(new FileInputStream(preface +  "VegetaStanding.png"));
                    lightOneRight = new Image(new FileInputStream(preface + "VegetaLightOneRight.png"));
                    lightTwoRight = new Image(new FileInputStream(preface + "VegetaLightTwoRight.png"));
                    lightThreeRight = new Image(new FileInputStream(preface + "VegetaLightThreeRight.png"));
                    heavyOneRight = new Image(new FileInputStream(preface + "VegetaHeavyOneRight.png"));
                    heavyTwoRight = new Image(new FileInputStream(preface + "VegetaHeavyTwoRight.png"));
                    heavyThreeRight = new Image(new FileInputStream(preface + "VegetaHeavyThreeRight.png"));
                    damageOneRight = new Image(new FileInputStream(preface + "VegetaDamageOneRight.png"));
                    damageTwoRight = new Image(new FileInputStream(preface + "VegetaDamageTwoRight.png"));
                } catch (FileNotFoundException ex) {
                    System.err.println("File Not Found!");
                    System.exit(1);
                }
                break;
            case ("Test"):
                try {
                    String preface = "resources/com/JFong/CS351Project3_fightnight/Fighters/Test/Sprites/";

                    jumpRight = new Image(new FileInputStream(preface + "TestJumpRight.png"));
                    crouchRight = new Image(new FileInputStream(preface + "TestCrouchRightTest.png"));
                    stepHalfOneRight = new Image(new FileInputStream(preface + "TestStepHalfOneRight.png"));
                    stepHalfTwoRight = new Image(new FileInputStream(preface + "TestStepHalfTwoRight.png"));
                    stepFullOneRight = new Image(new FileInputStream(preface + "TestStepFullOneRight.png"));
                    stepFullTwoRight = new Image(new FileInputStream(preface + "TestStepFullTwoRight.png"));
                    stillRight = new Image(new FileInputStream(preface +  "TestStanding.png"));
                    lightOneRight = new Image(new FileInputStream(preface + "TestLightOneRight.png"));
                    lightTwoRight = new Image(new FileInputStream(preface + "TestLightTwoRight.png"));
                    lightThreeRight = new Image(new FileInputStream(preface + "TestLightThreeRight.png"));
                    heavyOneRight = new Image(new FileInputStream(preface + "TestHeavyOneRight.png"));
                    heavyTwoRight = new Image(new FileInputStream(preface + "TestHeavyTwoRight.png"));
                    heavyThreeRight = new Image(new FileInputStream(preface + "TestHeavyThreeRight.png"));
                    damageOneRight = new Image(new FileInputStream(preface + "TestDamageOneRight.png"));
                    damageTwoRight = new Image(new FileInputStream(preface + "TestDamageTwoRight.png"));
                } catch (FileNotFoundException ex) {
                    System.err.println("File Not Found!");
                    System.exit(1);
                }
                break;
            case("King"):
                try {
                    String preface = "resources/com/JFong/CS351Project3_fightnight/Fighters/King/Sprites/";

                    jumpRight = new Image(new FileInputStream(preface + "KingJumpRight.png"));
                    crouchRight = new Image(new FileInputStream(preface + "KingCrouchRightTest.png"));
                    stepHalfOneRight = new Image(new FileInputStream(preface + "KingStepHalfOneRight.png"));
                    stepHalfTwoRight = new Image(new FileInputStream(preface + "KingStepHalfTwoRight.png"));
                    stepFullOneRight = new Image(new FileInputStream(preface + "KingStepFullOneRight.png"));
                    stepFullTwoRight = new Image(new FileInputStream(preface + "KingStepFullTwoRight.png"));
                    stillRight = new Image(new FileInputStream(preface +  "KingStanding.png"));
                    lightOneRight = new Image(new FileInputStream(preface + "KingLightOneRight.png"));
                    lightTwoRight = new Image(new FileInputStream(preface + "KingLightTwoRight.png"));
                    lightThreeRight = new Image(new FileInputStream(preface + "KingLightThreeRight.png"));
                    heavyOneRight = new Image(new FileInputStream(preface + "KingHeavyOneRight.png"));
                    heavyTwoRight = new Image(new FileInputStream(preface + "KingHeavyTwoRight.png"));
                    heavyThreeRight = new Image(new FileInputStream(preface + "KingHeavyThreeRight.png"));
                    damageOneRight = new Image(new FileInputStream(preface + "KingDamageOneRight.png"));
                    damageTwoRight = new Image(new FileInputStream(preface + "KingDamageTwoRight.png"));
                } catch (FileNotFoundException ex) {
                    System.err.println("File Not Found!");
                    System.exit(1);
                }
                break;
            default:
                break;
        }
    }

}

