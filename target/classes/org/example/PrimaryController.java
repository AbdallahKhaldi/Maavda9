package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class PrimaryController {

    @FXML
    private TextField Display;

    @FXML
    private ComboBox<String> BaseChooser;

    private String BaseSelected = "DEC";

    @FXML
    public void initialize() {
        // Populate the ComboBox with base options and set the default value
        BaseChooser.getItems().addAll("HEX", "DEC", "OCT", "BIN");
        BaseChooser.setValue("DEC"); // Set default base to DEC
        BaseChooser.setOnAction(e -> BaseUpdate()); // Update base on selection change
    }

    @FXML
    public void handleButtonClick(ActionEvent actionEvent) {
        // Get the button that triggered the event
        Button button = (Button) actionEvent.getSource();
        String text = button.getText();
        String regex = getString(getBaseSelection(BaseChooser.getValue()));

        if (text.equals("Clear")) {
            // Clear the text field when "Clear" is pressed
            Display.clear();
        } else if (text.equals("=")) {
            // Evaluate the expression when "=" is pressed
            evaluateExpression();
        } else {
            // Append text if it matches the valid pattern for the selected base
            if (text.toUpperCase().matches(regex)) {
                Display.appendText(text);
            }
        }
    }
    
    // Get valid digits for a given base
    private static String getBase(int base) {
        switch (base) {
            case 2:
                return "01"; // Binary digits
            case 8:
                return "01234567"; // Octal digits
            case 10:
                return "0123456789"; // Decimal digits
            case 16:
                return "0123456789ABCDEF"; // Hexadecimal digits
            default:
                return ""; // Default to no valid digits
        }
    }
    // Generate the regex pattern for valid characters based on the base
    private static String getString(int base) {
        String validDigits = getBase(base);
        String validOperators = "0";

        // Define valid operators and adjust based on base
        if (base == 2) {
            validOperators = "^~|&()+\\-*/"; // Only bitwise operators for binary
        } else {
            validOperators += "+\\-*/"; // Include arithmetic operators for other bases
        }

        // Construct the regex pattern
        return "[" + validDigits + validOperators + "\\s]+";
    }
    private void evaluateExpression() {
        try {
            // Get the input expression from the Display
            String expression = Display.getText().trim();

            // Ensure the expression is not empty or contains only operators
            if (expression.isEmpty() || expression.matches("[-+*/]+")) {
                Display.setText("Error: invalid expression");
                return;
            }

            // Determine the base based on the selected value
            int base;
            switch (BaseSelected) {
                case "BIN":
                    base = 2;
                    break;
                case "OCT":
                    base = 8;
                    break;
                case "HEX":
                    base = 16;
                    break;
                default:
                    base = 10;
                    break;
            }

            // Evaluate the expression using the specified base
            String result = ArithmeticApp.main(expression, base);
            Display.setText(result);
        } catch (Exception e) {
            // Display error message if evaluation fails
            Display.setText("Error: invalid expression");
        }
    }


    // Convert base string to integer value for processing
    private int getBaseSelection(String baseString) {
        switch (baseString) {
            case "BIN":
                return 2;
            case "OCT":
                return 8;
            case "HEX":
                return 16;
            default:
                return 10;
        }
    }
    // Convert a number from one base to another
    public static String convertFromBaseToBase(String str, int fromBase, int toBase) {
        return Integer.toString(Integer.parseInt(str, fromBase), toBase).toUpperCase();
    }
    private void BaseUpdate() {
        // Save the previous base
        String old = BaseSelected;
        BaseSelected = BaseChooser.getValue();

        // If the display contains valid data, convert it to the new base
        if (!Display.getText().isBlank() && !Display.getText().equals("Error: trying to divide by 0") && !Display.getText().equals("Error: invalid expression")) {
            // Convert from the old base to the current base
            Display.setText(ArithmeticApp.main(Display.getText(), getBaseSelection(old)));
            Display.setText(convertFromBaseToBase(Display.getText(), getBaseSelection(old), getBaseSelection(BaseChooser.getValue())));
        }
    }

  
}
