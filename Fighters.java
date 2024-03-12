/**
 *
 * CS351 Julian Fong Arcade Game Project 3
 * Fighters enum
 *
 * This is for all the different fighters I may or may not add to the game
 */
public enum Fighters {


    DEFAULT(new Fighter("NULL")),
    GOKU(new Fighter("Goku")),
    VEGETA(new Fighter("Vegeta")),
    KING(new Fighter("King")),
    TEST(new Fighter("Test"));

    private final Fighter fighter;

    Fighters(Fighter fighter) {
        this.fighter = fighter;
    }

    /**
     * @return Gets the class version of the enum fighter
     */
    public Fighter getFighter(){
        return fighter;
    }
}
