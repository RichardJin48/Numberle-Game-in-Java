package src;
import java.util.HashMap;

public interface INumberleModel {
    int MAX_ATTEMPTS = 6;
    boolean invariant();
    void initialize();
    boolean processInput(String input);
    boolean isGameOver();
    boolean isGameWon();
    String getTargetNumber();
    int getRemainingAttempts();
    void startNewGame();
    void setCheckValid(boolean checkValid);
    boolean getCheckValid();
    void setDisplayTarget(boolean displayTarget);
    boolean getDisplayTarget();
    void setRandom(boolean random);
    boolean getRandom();
    Integer[] getFieldStatus();
    HashMap<Character, Integer> getButtonStatus();
}
