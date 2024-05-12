package src;
import java.util.HashMap;
import java.util.Scanner;

public class CLIApp {
    public static void main(String[] args) {
        INumberleModel model = new NumberleModel();
        Scanner scanner = new Scanner(System.in);

        model.setCheckValid(true);
        model.setDisplayTarget(false);
        model.setRandom(true);

        System.out.println("Welcome to Numberle!");
        model.startNewGame();  // start the game

        HashMap<Character,Integer> buttonStatus;
        Character[] buttons = {'1','2','3','4','5','6','7','8','9','0','+','-','*','/','='};

        while (!model.isGameOver()) {
            System.out.println("Remaining attempts: " + model.getRemainingAttempts());  // Show remaining attempts

            buttonStatus = model.getButtonStatus();
            System.out.print("Not used characters: ");
            for(int i=0; i<buttons.length; i++){  // Show all characters that not used in guessing yet
                if(buttonStatus.get(buttons[i]) == 0){
                    System.out.print(buttons[i]+" ");
                }
            }
            System.out.print("\nNot contained characters: ");
            for(int i=0; i<buttons.length; i++){  // Show all guessed characters that not contained in the equation
                if(buttonStatus.get(buttons[i]) == 1){
                    System.out.print(buttons[i]+" ");
                }
            }
            System.out.print("\nContained characters: ");
            for(int i=0; i<buttons.length; i++){  // Show all guessed characters that contained in the equation but in wrong place
                if(buttonStatus.get(buttons[i]) == 2){
                    System.out.print(buttons[i]+" ");
                }
            }
            System.out.print("\nCorrect characters: ");
            for(int i=0; i<buttons.length; i++){  // Show all correctly guessed characters
                if(buttonStatus.get(buttons[i]) == 3){
                    System.out.print(buttons[i]+" ");
                }
            }

            System.out.print("\nPlease enter a 7-character equation: ");  // Ask user to input the equation
            String input = scanner.nextLine();  // Scan the inputted equation

            model.processInput(input);  // Process the inputted equation by the model

            if (model.isGameOver()) {  // if the game is over
                if (model.isGameWon()) {  // if the game is won
                    System.out.println("Game won!");  // Show game won message
                } else {
                    System.out.println("Game over! The answer is: " + model.getTargetNumber());  // Show game over message
                }
            }
        }
    }
}
