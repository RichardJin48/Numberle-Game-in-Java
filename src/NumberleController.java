package src;
import java.util.HashMap;

public class NumberleController {
    private INumberleModel model;
    private NumberleView view;

    public NumberleController(INumberleModel model) {
        this.model = model;
    }

    public void setView(NumberleView view) {
        this.view = view;
    }

    public boolean processInput(String input) {
        return model.processInput(input);
    }

    public boolean isGameOver() {
        return model.isGameOver();
    }

    public String getTargetNumber() {
        return model.getTargetNumber();
    }

    public int getRemainingAttempts() {
        return model.getRemainingAttempts();
    }

    public void startNewGame() {
        model.startNewGame();
    }
    public void setCheckValid(boolean checkValid){
        model.setCheckValid(checkValid);
    }
    public boolean getCheckValid() {
        return model.getCheckValid();
    }
    public void setDisplayTarget(boolean displayTarget) {
        model.setDisplayTarget(displayTarget);
    }
    public boolean getDisplayTarget() {
        return model.getDisplayTarget();
    }
    public void setRandom(boolean random) {
        model.setRandom(random);
    }
    public boolean getRandom() {
        return model.getRandom();
    }
    public Integer[] getFieldStatus() {
        return model.getFieldStatus();
    }
    public HashMap<Character, Integer> getButtonStatus() {
        return model.getButtonStatus();
    }

    /**
     * the function to enable the "New Game" button after the first valid guess
     */
    public void updateNewGameButton() {
        view.enableNewGameButton(INumberleModel.MAX_ATTEMPTS - model.getRemainingAttempts() != 0 || model.isGameWon());
    }
}
