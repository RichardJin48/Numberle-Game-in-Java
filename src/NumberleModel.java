package src;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class NumberleModel extends Observable implements INumberleModel {
    private String targetNumber;
    private int remainingAttempts;
    private boolean gameWon;
    private boolean checkValid = true;  // to store the check valid flag
    private boolean displayTarget = false;  // to store the display target flag
    private boolean random = true;  // to store the random equation flag
    private Integer[] fieldStatus;  // to store the status of each field in a line. 1: Not Contained, 2: Contained, 3: Correct
    private HashMap<Character, Integer> buttonStatus;
    // to store the status of each button. 0: Not Used, 1: Not Contained, 2: Contained, 3: Correct

    public boolean invariant() {
        return (!gameWon && remainingAttempts >= 0 || gameWon && remainingAttempts > 0) &&
               (remainingAttempts <= MAX_ATTEMPTS) &&
               (targetNumber != null && targetNumber.length() == 7 && checkValid(targetNumber)) &&
               (fieldStatus != null && fieldStatus.length == 7) &&
               (buttonStatus != null && buttonStatus.size() == 15);
    }

    /*@ invariant invariant();
      @ requires true;
      @ ensures remainingAttempts == MAX_ATTEMPTS
      @ && !gameWon
      @ && targetNumber != null && targetNumber.length() == 7 && checkValid(targetNumber)
      @ && fieldStatus != null && fieldStatus.length == 7
      @ && buttonStatus != null && buttonStatus.size() == 15;
      @*/
    @Override
    public void initialize() {
        generateTarget();  // Generate the target equation

        if(displayTarget){  // if the display target mode is on
            System.out.println("The target equation is: "+targetNumber);
        }

        remainingAttempts = MAX_ATTEMPTS;
        gameWon = false;

        fieldStatus = new Integer[]{0, 0, 0, 0, 0, 0, 0};
        buttonStatus = new HashMap<>();
        buttonStatus.put('1',0);buttonStatus.put('2',0);buttonStatus.put('3',0);buttonStatus.put('4',0);
        buttonStatus.put('5',0);buttonStatus.put('6',0);buttonStatus.put('7',0);buttonStatus.put('8',0);
        buttonStatus.put('9',0);buttonStatus.put('0',0);buttonStatus.put('+',0);buttonStatus.put('-',0);
        buttonStatus.put('*',0);buttonStatus.put('/',0);buttonStatus.put('=',0);

        setChanged();
        notifyObservers("New Game");

        assert invariant():"Invariant must be maintained!";
        assert remainingAttempts == MAX_ATTEMPTS:"Remaining Attempts must be Max Attempts!";
        assert !gameWon :"Game won status must be false!";
    }

    /**
     * the function to generate the target equation
     */
    private void generateTarget() {
        List<String> equations = new ArrayList<>();  // Create an array to store all equations
        String fileName = "equations.txt";  // the equations file name

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {  // Create the reader to read the file
            String equation;  // Create the string to store each equation
            while ((equation = reader.readLine()) != null) {  // Read each line in the file if it has line
                equations.add(equation);  // Add the equation to the array
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(random) {  // if the random mode is on
            Random rand = new Random();
            targetNumber = equations.get(rand.nextInt(equations.size()));  // Choose a random question from the array
        }
        else {  // if the random mode is off
            targetNumber = equations.get(0);  // Generate the fixed equation
        }
    }

    /**
     * the function to check the equation if is valid
     */
    private boolean checkValid(String input) {
        if (checkValid) {  // if the check equation valid mode is on
            if(!input.contains("=")){
                setChanged();
                notifyObservers("Without Equals");
                System.out.println("Invalid equation! The equation must contains equals sign.");
                return false;
            }
            if(!input.contains("+")&&!input.contains("-")&&!input.contains("*")&&!input.contains("/")){
                setChanged();
                notifyObservers("Without Operator");
                System.out.println("Invalid equation! The equation must contains at least one operator.");
                return false;
            }
            if(!input.contains("1")&&!input.contains("2")&&!input.contains("3")&&!input.contains("4")&&
               !input.contains("5")&&!input.contains("6")&&!input.contains("7")&&!input.contains("8")&&
               !input.contains("9")&&!input.contains("0")){
                setChanged();
                notifyObservers("Without Number");
                System.out.println("Invalid equation! The equation must contains numbers.");
                return false;
            }
            if(input.charAt(0)=='+'||input.charAt(0)=='-'||input.charAt(0)=='*'||input.charAt(0)=='/'||input.charAt(0)=='='||
                    input.charAt(6)=='+'||input.charAt(6)=='-'||input.charAt(6)=='*'||input.charAt(6)=='/'||input.charAt(6)=='='){
                setChanged();
                notifyObservers("End Operator");
                System.out.println("Invalid equation! The equation must not contain a operator at beginning or end.");
                return false;
            }
            if(input.charAt(0)=='0'){
                if(input.charAt(1)=='1'||input.charAt(1)=='2'||input.charAt(1)=='3'||input.charAt(1)=='4'||input.charAt(1)=='5'||
                   input.charAt(1)=='6'||input.charAt(1)=='7'||input.charAt(1)=='8'||input.charAt(1)=='9'||input.charAt(1)=='0') {
                    setChanged();
                    notifyObservers("Beginning Zero");
                    System.out.println("Invalid equation! The equation must not contain a zero at the beginning of a number.");
                    return false;
                }
            }
            for(int i=0; i<6; i++){
                if(input.charAt(i)=='+'||input.charAt(i)=='-'||input.charAt(i)=='*'||input.charAt(i)=='/'||input.charAt(i)=='='){
                    if(i<5 && input.charAt(i+1)=='0'){
                        if(input.charAt(i+2)=='1'||input.charAt(i+2)=='2'||input.charAt(i+2)=='3'||input.charAt(i+2)=='4'||
                           input.charAt(i+2)=='5'|| input.charAt(i+2)=='6'||input.charAt(i+2)=='7'||input.charAt(i+2)=='8'||
                           input.charAt(i+2)=='9'||input.charAt(i+2)=='0') {
                            setChanged();
                            notifyObservers("Beginning Zero");
                            System.out.println("Invalid equation! The equation must not contain a zero at the beginning of a number.");
                            return false;
                        }
                    }
                    if(input.charAt(i+1)=='+'||input.charAt(i+1)=='-'||input.charAt(i+1)=='*'||
                       input.charAt(i+1)=='/'||input.charAt(i+1)=='='){
                        setChanged();
                        notifyObservers("Adjacent Operators");
                        System.out.println("Invalid equation! The equation must not contain adjacent operators.");
                        return false;
                    }
                }
                if(input.charAt(i)=='/'&&input.charAt(i+1)=='0'){
                    setChanged();
                    notifyObservers("Divide by Zero");
                    System.out.println("Invalid equation! The equation must not contain a number divided by zero.");
                    return false;
                }
            }

            String[] parts = input.split("=");  // Split the equation by "="
            ArrayList<Double> partValues = new ArrayList<>();  // Create an array to store values of each part of the equation
            try {
                ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
                ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("nashorn");
                for (String part : parts) {  // for each part of the equation
                    partValues.add(Double.parseDouble(String.valueOf(scriptEngine.eval(part))));  // Calculate the value
                }
            } catch (Exception e) {
                e.getMessage();
            }
            for(int i=0; i<partValues.size()-1; i++){
                if(!Objects.equals(partValues.get(i), partValues.get(i + 1))) {  // if the values of two parts are different
                    setChanged();
                    notifyObservers("Not Equal");
                    System.out.println("Invalid equation! The left side must equals to the right side.");
                    return false;
                }
            }
        }
        return true;
    }

    /*@ invariant invariant();
      @ requires input != null;
      @ ensures \old(!isGameOver())
      @ && (!gameWon && remainingAttempts >= 0 || gameWon && remainingAttempts > 0)
      @ && remainingAttempts <= MAX_ATTEMPTS
      @ && targetNumber != null && targetNumber.length() == 7 && checkValid(targetNumber)
      @ && fieldStatus != null && fieldStatus.length == 7
      @ && buttonStatus != null && buttonStatus.size() == 15;
      @*/
    @Override
    public boolean processInput(String input) {
        assert invariant():"Invariant must be maintained!";
        assert !isGameOver():"The game over status must be false!";
        assert input != null:"The input must not be null!";

        if (input.length() != 7) {  // if the input length is invalid
            setChanged();
            notifyObservers("Invalid Length");
            System.out.println("Invalid input length! The input must be 7 characters.");
            assert invariant():"Invariant must be maintained!";
            return false;
        }

        if(!checkValid(input)){  // if the equation is invalid
            assert invariant():"Invariant must be maintained!";
            return false;
        }

        remainingAttempts--;  // Remaining attempts decrease 1

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == targetNumber.charAt(i)) {
                System.out.print(c + ": Correct  ");
                fieldStatus[i] = 3;  // Change the field status to "Correct"
                buttonStatus.put(c,3);  // Change the button status to "Correct"
            }
            else if (targetNumber.contains(String.valueOf(c))) {
                System.out.print(c + ": Contained  ");
                fieldStatus[i] = 2;  // Change the field status to "Contained"
                if(buttonStatus.get(c)<2){  // if the button status is not "Correct" or "Contained"
                    buttonStatus.put(c,2);  // Change the button status to "Contained"
                }
            }
            else {
                System.out.print(c + ": Not Contained  ");
                fieldStatus[i] = 1;  // Change the field status to "Not Contained"
                buttonStatus.put(c,1);  // Change the button status to "Not Contained"
            }
        }
        System.out.println();

        if (input.equals(targetNumber)) {  // if the input is same as the target number
            gameWon = true;
        }

        if (isGameOver()) {  // if game over
            setChanged();
            notifyObservers(gameWon ? "Game Won" : "Game Over");
        }
        else {  // if not game over
            setChanged();
            notifyObservers("Try Again");
            System.out.println("Try again!");
            if(displayTarget){  // if the display target mode is on
                System.out.println("The target equation is: "+targetNumber);
            }
        }

        assert invariant():"Invariant must be maintained!";
        assert remainingAttempts < MAX_ATTEMPTS:"Remaining Attempts must be less than Max Attempts!";
        return true;
    }

    @Override
    public boolean isGameOver() {
        return remainingAttempts <= 0 || gameWon;
    }

    @Override
    public boolean isGameWon() {
        return gameWon;
    }

    @Override
    public String getTargetNumber() {
        return targetNumber;
    }

    @Override
    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    @Override
    public void startNewGame() {
        initialize();
    }

    @Override
    public void setCheckValid(boolean checkValid) {
        this.checkValid = checkValid;
    }

    @Override
    public boolean getCheckValid() {
        return checkValid;
    }

    @Override
    public void setDisplayTarget(boolean displayTarget) {
        this.displayTarget = displayTarget;
    }

    @Override
    public boolean getDisplayTarget() {
        return displayTarget;
    }

    @Override
    public void setRandom(boolean random) {
        this.random = random;
    }

    @Override
    public boolean getRandom() {
        return random;
    }

    @Override
    public Integer[] getFieldStatus() {
        return fieldStatus;
    }

    @Override
    public HashMap<Character, Integer> getButtonStatus() {
        return buttonStatus;
    }
}
