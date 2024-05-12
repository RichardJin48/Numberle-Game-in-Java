package src;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Observer;

public class NumberleView implements Observer {
    private final INumberleModel model;
    private final NumberleController controller;
    private final JFrame frame = new JFrame("Numberle");
    private final JLabel targetLabel = new JLabel();
    private final StringBuilder input = new StringBuilder();
    private final JTextField[][] fields = new JTextField[INumberleModel.MAX_ATTEMPTS][7];  // to store 6x7 text fields
    private final JButton[] buttons = new JButton[18];  // to store number buttons, "Backspace", sign buttons, and "Enter"
    private final JButton[] flags = new JButton[3];  // to store three flag buttons
    private int line = 0;  // to store the line cursor
    private int cursor = 0;  // to store the column cursor
    private final Character[] buttonChars = {'1','2','3','4','5','6','7','8','9','0','+','-','*','/','='};


    public NumberleView(INumberleModel model, NumberleController controller) {
        this.controller = controller;
        this.model = model;
        this.controller.startNewGame();
        ((NumberleModel)this.model).addObserver(this);
        initializeFrame();
        this.controller.setView(this);
        update((NumberleModel)this.model, null);
    }

    public void initializeFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(750, 600);
        frame.setLocation(600,200);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.X_AXIS));
        center.add(new JPanel());
        center.add(new JPanel());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(INumberleModel.MAX_ATTEMPTS, 7,5,5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        for (int i = 0; i < INumberleModel.MAX_ATTEMPTS; i++) {
            for (int j = 0; j < 7; j++) {
                fields[i][j] = new JTextField();  // Create the 6x7 text fields
                fields[i][j].setEditable(false);
                fields[i][j].setPreferredSize(new Dimension (60,60));
                fields[i][j].setHorizontalAlignment(JTextField.CENTER);
                fields[i][j].setFont(new Font("Arial",Font.PLAIN,32));
                fields[i][j].setBackground(Color.white);
                inputPanel.add(fields[i][j]);
            }
        }
        center.add(inputPanel);
        center.add(new JPanel());

        JPanel targetPanel = new JPanel();
        targetPanel.setLayout(new GridLayout(1, 1));
        targetLabel.setText("Target: "+controller.getTargetNumber());  // Create the target display label
        targetLabel.setVisible(controller.getDisplayTarget());  // Set the visibility of target label depends on the display target flag
        targetPanel.add(targetLabel);
        center.add(targetLabel);
        center.add(new JPanel());
        frame.add(center, BorderLayout.NORTH);

        JPanel keyboardPanel = new JPanel();
        keyboardPanel.setLayout(new GridLayout(4, 1,5,5));
        keyboardPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        JPanel numberPanel = new JPanel();
        numberPanel.setLayout(new GridLayout(1, 10,5,5));
        keyboardPanel.add(numberPanel);

        for (int i = 0; i < 10; i++) {
            buttons[i] = new JButton(String.valueOf(buttonChars[i]));  // Create 0-9 number buttons
            buttons[i].setEnabled(true);
            int finalI = i;
            buttons[i].addActionListener(e -> {
                if (cursor < 7) {  // If the cursor is not out of range
                    fields[line][cursor].setText(buttons[finalI].getText());  // Input the number to the current text field
                    cursor++;  // Cursor moves right
                }
            });
            buttons[i].setFont(new Font("Arial",Font.PLAIN,16));
            buttons[i].setPreferredSize(new Dimension (50,50));
            numberPanel.add(buttons[i]);
        }

        JPanel signPanel = new JPanel();
        signPanel.setLayout(new GridLayout(1, 7,5,5));
        keyboardPanel.add(signPanel);

        buttons[15] = new JButton("Backspace");  // Create the "Backspace" button
        buttons[15].setEnabled(true);
        buttons[15].addActionListener(e -> {
            if(!controller.isGameOver()) {  // if the game is not over
                if (cursor > 0) {  // if the cursor is not at the most left-hand side
                    fields[line][cursor - 1].setText("");  // Delete the character in the last text field
                    cursor--;  // Cursor moves left
                }
            }
        });
        buttons[15].setPreferredSize(new Dimension (50,50));
        signPanel.add(buttons[15]);

        for (int i = 10; i < 15; i++) {
            buttons[i] = new JButton(String.valueOf(buttonChars[i]));  // Create the sign buttons
            buttons[i].setEnabled(true);
            int finalI = i;
            buttons[i].addActionListener(e -> {
                if (cursor < 7) {  // if the cursor is not out of range
                    fields[line][cursor].setText(buttons[finalI].getText());  // Input the sign to the current text field
                    cursor++;  // Cursor moves right
                }
            });
            buttons[i].setFont(new Font("Arial",Font.PLAIN,16));
            buttons[i].setPreferredSize(new Dimension (50,50));
            signPanel.add(buttons[i]);
        }

        buttons[16] = new JButton("Enter");  // Create the "Enter" button
        buttons[16].setEnabled(true);
        buttons[16].addActionListener(e -> {
            if(!controller.isGameOver()) {  // if the game is not over
                for (int i = 0; i < cursor; i++) {
                    input.append(fields[line][i].getText());  // Store the equation into the input StringBuilder
                }
                controller.processInput(input.toString());  // Process the equation by the model
            }
        });
        buttons[16].setPreferredSize(new Dimension (50,50));
        signPanel.add(buttons[16]);

        JPanel newGamePanel = new JPanel();
        newGamePanel.setLayout(new GridLayout(1, 1,5,5));
        keyboardPanel.add(newGamePanel);

        buttons[17] = new JButton("New Game");  // Create the "New Game" button
        buttons[17].setEnabled(false);  // Disable the "New Game" button
        buttons[17].addActionListener(e -> {
            int option = 0;
            if(!controller.isGameOver()) {  // if the game is not over, ask user to confirm if start the new game
                String[] options = {"Cancel", "Start"};
                option = JOptionPane.showOptionDialog(frame, "Start a new game?", "New Game?",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            }
            if (controller.isGameOver() || option == 1) {  // if the game is over, start the new game directly
                controller.startNewGame();
            }
        });
        buttons[17].setPreferredSize(new Dimension (50,50));
        newGamePanel.add(buttons[17]);

        JPanel flagPanel = new JPanel();
        flagPanel.setLayout(new GridLayout(1, 3,5,5));
        keyboardPanel.add(flagPanel);

        flags[0] = new JButton("Check Equation Valid: " + (controller.getCheckValid()?"ON":"OFF"));  // Create the "Check Equation Valid" flag button
        flags[0].setEnabled(true);
        flags[0].addActionListener(e -> {
            controller.setCheckValid(!controller.getCheckValid());  // Reverse the flag status
            flags[0].setText("Check Equation Valid: " + (controller.getCheckValid()?"ON":"OFF"));  // Update the flag status on the button
        });
        flags[0].setPreferredSize(new Dimension (50,50));
        flagPanel.add(flags[0]);

        flags[1] = new JButton("Display Target: " + (controller.getDisplayTarget()?"ON":"OFF"));  // Create the "Display Target" flag button
        flags[1].setEnabled(true);
        flags[1].addActionListener(e -> {
            controller.setDisplayTarget(!controller.getDisplayTarget());  // Reverse the flag status
            flags[1].setText("Display Target: " + (controller.getDisplayTarget()?"ON":"OFF"));  // Update the flag status on the button
            targetLabel.setVisible(controller.getDisplayTarget());  // Update the visibility of the target field
        });
        flags[1].setPreferredSize(new Dimension (50,50));
        flagPanel.add(flags[1]);

        flags[2] = new JButton("Random Target: " + (controller.getRandom()?"ON":"OFF"));  // Create the "Random Target" flag button
        flags[2].setEnabled(true);
        flags[2].addActionListener(e -> {
            int option = 0;
            if(!controller.isGameOver()) {  // if the game is not over, ask user to confirm if restart the game
                String[] options = {"Cancel", "Restart"};
                option = JOptionPane.showOptionDialog(frame, "Change the target needs to restart the game.",
                        "Restart?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
            }
            if(controller.isGameOver() || option == 1) {  // if the game is over, restart the game directly
                controller.setRandom(!controller.getRandom());  // Reverse the flag status
                flags[2].setText("Random Target: " + (controller.getRandom()?"ON":"OFF"));  // Update the flag status on the button
                controller.startNewGame();  // Restart the game after changing the target
            }
        });
        flags[2].setPreferredSize(new Dimension (50,50));
        flagPanel.add(flags[2]);

        frame.add(keyboardPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    /**
     * the function used by the controller to enable or disable the "New Game" button
     */
    public void enableNewGameButton(boolean bool) {
        buttons[17].setEnabled(bool);
    }

    /**
     * the function to update the color of fields
     */
    private void updateFields() {
        Integer[] fieldStatus = controller.getFieldStatus();
        for(int i=0; i<7; i++){
            if(fieldStatus[i] == 1){  // if the equation does not contain the character
                fields[line][i].setBackground(new Color(160, 160, 180));  // Set field color to gray
            }
            else if(fieldStatus[i] == 2){  // if the equation contains the character
                fields[line][i].setBackground(new Color(255, 150, 0));  // Set field color to orange
            }
            else if(fieldStatus[i] == 3){  // if the equation contains the character and the position is correct
                fields[line][i].setBackground(new Color(0, 200, 150));  // Set field color to green
            }
            fields[line][i].setForeground(Color.white);  // Set field text color to white
        }
    }

    /**
     * the function to update the color of buttons
     */
    private void updateButtons() {
        HashMap<Character, Integer> buttonStatus = controller.getButtonStatus();
        for(int i=0; i<buttonChars.length; i++){
            if(buttonStatus.get(buttonChars[i]) == 1){  // if the equation does not contain the character
                buttons[i].setBackground(new Color(160, 160, 180));  // Set button color to gray
                buttons[i].setForeground(Color.white);  // Set button text color to white
            }
            else if(buttonStatus.get(buttonChars[i]) == 2){  // if the equation contains the character
                buttons[i].setBackground(new Color(255, 150, 0));  // Set button color to orange
                buttons[i].setForeground(Color.white);  // Set button text color to white
            }
            else if(buttonStatus.get(buttonChars[i]) == 3){  // if the equation contains the character and the position is correct
                buttons[i].setBackground(new Color(0, 200, 150));  // Set button color to green
                buttons[i].setForeground(Color.white);  // Set button text color to white
            }
        }
    }

    @Override
    public void update(java.util.Observable o, Object arg) {
        if (arg instanceof String) {
            String[] options = {"Back", "New Game"};
            int option;
            String message = (String) arg;
            switch (message) {
                case "New Game":
                    for (int i = 0; i < INumberleModel.MAX_ATTEMPTS; i++) {  // Initialize the text and color of all fields
                        for (int j = 0; j < 7; j++) {
                            fields[i][j].setText("");
                            fields[i][j].setBackground(Color.white);
                            fields[i][j].setForeground(null);
                        }
                    }
                    for(int i=0; i<buttonChars.length; i++){  // Initialize the color of all buttons
                        buttons[i].setBackground(null);
                        buttons[i].setForeground(null);
                    }
                    cursor = 0;  // Initialize the column cursor to 0
                    line = 0;  // Initialize the line cursor to 0
                    input.setLength(0);  // Clear the input buffer
                    targetLabel.setText("Target: "+controller.getTargetNumber());  // Update the target shows on the target label
                    controller.updateNewGameButton();  // Disable the "New Game" button
                    break;
                case "Game Won":
                    updateButtons();
                    updateFields();
                    option = JOptionPane.showOptionDialog(frame,"Game won!","Game Won",
                            JOptionPane.YES_NO_OPTION,JOptionPane.INFORMATION_MESSAGE,null,options,options[0]);
                    controller.updateNewGameButton();  // Enable the "New Game" button
                    if(option == 1) {  // if the user choose to start the new game
                        controller.startNewGame();
                    }
                    break;
                case "Game Over":
                    updateButtons();
                    updateFields();
                    option = JOptionPane.showOptionDialog(frame,
                            "Game over! The answer is: " + controller.getTargetNumber(), "Game Over",
                            JOptionPane.YES_NO_OPTION,JOptionPane.INFORMATION_MESSAGE,null,options,options[0]);
                    if(option == 1) {  // if the user choose to start the new game
                        controller.startNewGame();
                    }
                    break;
                case "Try Again":
                    updateButtons();
                    updateFields();
                    cursor = 0;  // Reset the column cursor
                    line = INumberleModel.MAX_ATTEMPTS - controller.getRemainingAttempts();  // Go to next line
                    input.setLength(0);  // Clear the input buffer
                    controller.updateNewGameButton();  // Enable the "New Game" button
                    break;
                case "Invalid Length":
                    JOptionPane.showMessageDialog(frame,
                            "Invalid input length! The input must be 7 characters.",
                            "Invalid Length", JOptionPane.ERROR_MESSAGE);
                    input.setLength(0);  // Clear the input buffer
                    break;
                case "Without Equals":
                    JOptionPane.showMessageDialog(frame,
                            "Invalid equation! The equation must contains equals sign.",
                            "Without Equals", JOptionPane.ERROR_MESSAGE);
                    input.setLength(0);  // Clear the input buffer
                    break;
                case "Without Operator":
                    JOptionPane.showMessageDialog(frame,
                            "Invalid equation! The equation must contains at least one operator.",
                            "Without Operator", JOptionPane.ERROR_MESSAGE);
                    input.setLength(0);  // Clear the input buffer
                    break;
                case "Without Number":
                    JOptionPane.showMessageDialog(frame,
                            "Invalid equation! The equation must contains numbers.",
                            "Without Number", JOptionPane.ERROR_MESSAGE);
                    input.setLength(0);  // Clear the input buffer
                    break;
                case "End Operator":
                    JOptionPane.showMessageDialog(frame,
                            "Invalid equation! The equation must not contain operator at beginning or end.",
                            "End Operator", JOptionPane.ERROR_MESSAGE);
                    input.setLength(0);  // Clear the input buffer
                    break;
                case "Adjacent Operators":
                    JOptionPane.showMessageDialog(frame,
                            "Invalid equation! The equation must not contain adjacent operators.",
                            "Adjacent Operators", JOptionPane.ERROR_MESSAGE);
                    input.setLength(0);  // Clear the input buffer
                    break;
                case "Beginning Zero":
                    JOptionPane.showMessageDialog(frame,
                            "Invalid equation! The equation must not contain a zero at the beginning of a number.",
                            "Beginning Zero", JOptionPane.ERROR_MESSAGE);
                    input.setLength(0);  // Clear the input buffer
                    break;
                case "Divide by Zero":
                    JOptionPane.showMessageDialog(frame,
                            "Invalid equation! The equation must not contain a number divided by zero.",
                            "Divide by Zero", JOptionPane.ERROR_MESSAGE);
                    input.setLength(0);  // Clear the input buffer
                    break;
                case "Not Equal":
                    JOptionPane.showMessageDialog(frame,
                            "Invalid equation! The left side must equals to the right side.",
                            "Not Equal", JOptionPane.ERROR_MESSAGE);
                    input.setLength(0);  // Clear the input buffer
                    break;
            }
        }
    }
}
