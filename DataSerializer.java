/**
 * Project 5 FIGHT_NIGHT_ONLINE
 * (project 5 class addition)
 *
 * The DataSerializer class will allow for easy transitions and translations of keyboard input data coming from
 * all game clients. The move set will be represented through a given index of boolean array that corresponds to a
 * certain move or action and the even of either a key down or key release a.k.a. 1 or 0.
 * This can be crammed nicely into a byte using bit-masking so the format of that will look like this
 *
 * players 1 through 8 can be assigned the most significant 3 bits at the front of the byte or (0-7)
 *
 *
 * Player number | Move index | key down or release
 * 000           | 0000       | 0
 *
 * Example:
 * player 3      | move 4     | released
 * 011           | 0100       | 0
 * 0110_1001 ->  0x68 hexadecimal byte
 */

public class DataSerializer {

    private final int PLAYER_MASK = 0xE0;
    private final int PLAYER_SHIFT = 5;

    private final int MOVE_NUM_MASK = 0x1E;
    private final int MOVE_SHIFT = 1;
    //don't have to shift for pressed down since it is the first bit
    private final int PRESSED_DOWN_MASK = 0x01;

    public DataSerializer(){}

    /**
     * This will turn the given data into a single network packet byte
     * @param player number of the player
     * @param moveNumber move the player invoked
     * @param pressedDown button was either pressed down or released
     * @return a byte representing the data
     */
    public byte serialize(int player, int moveNumber, boolean pressedDown){
        int pressed = pressedDown ? 1 : 0;

        int playerByte = PLAYER_MASK & (player << PLAYER_SHIFT);
        int moveByte = MOVE_NUM_MASK & (moveNumber << MOVE_SHIFT);
        int pressByte = pressed & PRESSED_DOWN_MASK;


        return (byte) (playerByte | moveByte | pressByte);
    }

    /**
     * Test if a byte is actually move data
     * @param b serialzed byte
     * @return boolean if is move data
     */
    public boolean isButtonData(byte b){
        int playerNum = extractPlayerNumber(b);
        int moveNum = extractMoveNumber(b);
        if(playerNum < 0 || playerNum > 1) return false;
        return moveNum >= 0 && moveNum < 6;
    }


    /**
     * This will extract a move number out of the serialized byte
     * @param serializedData byte containing move number data
     * @return the int value of the boolean array index that the move corresponds to
     */
    public int extractMoveNumber(byte serializedData){
        return (MOVE_NUM_MASK & serializedData) >> MOVE_SHIFT;
    }

    /**
     * This will get the player number from the serialized data
     * @param serializedData byte of data containing the player number
     * @return the number of the player
     */
    public int extractPlayerNumber(byte serializedData){
        return (PLAYER_MASK & serializedData) >> PLAYER_SHIFT;
    }

    /**
     * This will get the boolean that says whether a player pressed or released a button
     * @param serializedData data containing the 1 or 0
     * @return boolean representing a press or a release.
     */
    public boolean extractPressedDownBool(byte serializedData){
        return (serializedData & PRESSED_DOWN_MASK) == 1;
    }

    /**
     * Test functions
     */
    @SuppressWarnings("unused")
    public static void TestFunctions() {
        DataSerializer ds = new DataSerializer();
        byte serializedData = ds.serialize(3, 4, false);
        assert 0x68 == serializedData;
        System.out.printf("Player:%d, Move:%d, Pressed down? -> %s%n", ds.extractPlayerNumber(serializedData),
                ds.extractMoveNumber(serializedData), ds.extractPressedDownBool(serializedData));
    }

    /**
     * Weird two's compliments error when trying to use the full 8 bits
     * and turn into an int. You have to throw away the 1's java
     * fills the 3 bytes with when casting to an int. Why they don't fill
     * with zeros is beyond me
     * @param b byte to extract 8-bit number
     * @return int representation
     */
    public int magicCast(byte b){
        return 0xFF & b;
    }

    /**
     * DEBUG FUNCTION
     */
    @SuppressWarnings("unused")
    public void debugByte(byte data) {
        System.out.printf("Player:%d, Move:%d, Pressed down? -> %s%n", this.extractPlayerNumber(data),
                this.extractMoveNumber(data), this.extractPressedDownBool(data));
    }

}
