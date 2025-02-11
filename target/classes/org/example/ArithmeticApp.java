package org.example;

import java.util.Arrays;
import java.util.Objects;

public class ArithmeticApp
{
    public static String main (String exp,int base)
    {

        String invalid="Error: invalid expression";
        String Expression = exp;
        String RealExpression = Expression;
        Expression = handleFirstMinus(Expression);
        Expression = addSpaces(Expression);
        //base=CheckIfValid(Expression,base);
        if (Expression.length() == 1 && "+-*/".contains(Expression)) {
            System.out.println("Error: invalid expression");
            return "Error: invalid expression";
        }


        String[] Components = Expression.split("\\s+");
        Components = validateComponents(Components);

        if (base == 2) {
            boolean isArithmetic = false, isBitwise = false;
            if (Components.length == 1 && isBinary(Components[0]))
            {
                System.out.println("The value of expression " + RealExpression + " is : " + Components[0]);
                return Components[0];
            }
            // Determine the type of operation (arithmetic or bitwise)
            for (int i = 0; i < Components.length; i++) {
                if ("+*/-".contains(Components[i])) {
                    isArithmetic = true;
                } else if ("&|^".contains(Components[i]) || Components[i].charAt(0) == '~') {
                    isBitwise = true;
                }

                // Error if both arithmetic and bitwise operators are present
                if (isArithmetic && isBitwise) {
                    System.out.println("Error: invalid expression");
                  return invalid;
                }
            }

            // Handle bitwise operations
            if (isBitwise) {
                // Check for invalid sequences of bitwise operators
                for (int i = 0; i < Components.length - 1; i++) {
                    if ("&|^~".contains(Components[i]) && "&|^".contains(Components[i + 1])) {
                        System.out.println("Error: invalid expression");
                        return invalid;
                    }
                }

                // Validate and process components for bitwise operations
                for (int i = 0; i < Components.length; i++) {
                    if (!isBinary(Components[i])) { // Not a binary number
                        if (Components[i].length() == 9 && Components[i].charAt(0) == '~') {
                            continue; // Allow NOT operator with a valid binary string (~binary)
                        } else if ("&|^~".contains(Components[i])) {
                            continue; // Allow bitwise operators
                        } else {
                            System.out.println("Error: invalid expression");
                            return invalid;
                        }
                    }

                    // Ensure binary strings are 8 bits long
                    if (Components[i].length() != 8 && !Components[i].startsWith("~")) {
                        System.out.println("Error: invalid expression");
                        return invalid;
                    }
                }

                // Handle bitwise NOT (~)
                for (int i = 0; i < Components.length; i++) {
                    if (Components[i].equals("~")) {
                        if (i + 1 < Components.length && isBinary(Components[i + 1])) {
                            // Apply NOT to the next binary number
                            int value = Integer.parseInt(Components[i + 1], 2);
                            int result = ~value & 0xFF; // Limit to 8 bits
                            Components[i + 1] = to8BitBinary(result);

                            // Remove the '~' operator
                            for (int j = i; j < Components.length - 1; j++) {
                                Components[j] = Components[j + 1];
                            }
                            Components[Components.length - 1] = null; // Mark the last element as null
                            i--; // Adjust the index after shifting
                        } else {
                            System.out.println("Error: invalid expression");
                           return invalid;
                        }
                    } else if (Components[i].charAt(0) == '~') {
                        // Apply NOT to the binary number prefixed by ~
                        String binaryPart = Components[i].substring(1); // Extract binary part
                        if (isBinary(binaryPart)) {
                            int value = Integer.parseInt(binaryPart, 2);
                            int result = ~value & 0xFF; // Limit to 8 bits
                            Components[i] = to8BitBinary(result);
                        } else {
                            System.out.println("Error: invalid expression");
                            return invalid;
                        }
                    }
                }

                // Evaluate bitwise operators (&, |, ^) in order of precedence
                for (int precedence = 1; precedence <= 3; precedence++) {
                    for (int i = 1; i < Components.length - 1; i++) {
                        if (Components[i] == null) break;

                        boolean isOperator =
                                (precedence == 1 && Components[i].equals("&")) ||
                                        (precedence == 2 && Components[i].equals("^")) ||
                                        (precedence == 3 && Components[i].equals("|"));

                        if (isOperator) {
                            if (!isBinary(Components[i - 1]) || !isBinary(Components[i + 1])) {
                                System.out.println("Error: invalid expression");
                                return invalid;
                            }

                            // Parse operands
                            int left = Integer.parseInt(Components[i - 1], 2);
                            int right = Integer.parseInt(Components[i + 1], 2);

                            // Perform the operation
                            int result = 0;
                            if (Components[i].equals("&")) result = left & right;
                            if (Components[i].equals("^")) result = left ^ right;
                            if (Components[i].equals("|")) result = left | right;

                            // Replace the left operand with the result
                            Components[i - 1] = to8BitBinary(result);

                            // Shift elements left to remove the operator and right operand
                            for (int j = i; j < Components.length - 2; j++) {
                                Components[j] = Components[j + 2];
                            }
                            Components[Components.length - 1] = null;
                            Components[Components.length - 2] = null;

                            i--; // Adjust index after shifting
                        }
                    }
                }

                // Print the final result
                System.out.println("The value of expression " + RealExpression + " is : " + Components[0]);
                return Components[0];

            } else if (isArithmetic) {
                // Handle unary minus first
                for (int i = 0; i < Components.length; i++) {
                    if (Components[i].charAt(0) == '-' && isBinary(Components[i].substring(1))) {
                        String binaryPart = Components[i].substring(1);
                        int value = binaryToSigned(binaryPart) * -1;
                        Components[i] = signedToBinary(value);
                    }
                }

                // Validate components for arithmetic operations
                for (int i = 0; i < Components.length; i++) {
                    if (!isBinary(Components[i])) { // Not a valid binary number
                        if (!"+*/-".contains(Components[i])) { // Allow arithmetic operators
                            System.out.println("Error: invalid expression");
                            return invalid;
                        }
                    }

                    // Ensure binary strings are 8 bits long
                    if (Components[i].length() != 8 && !"+*/-".contains(Components[i])) {
                        System.out.println("Error: invalid expression");
                        return invalid;
                    }
                }

                // Convert binary strings to signed integers
                for (int i = 0; i < Components.length; i++) {
                    if (isBinary(Components[i])) {
                        Components[i] = String.valueOf(binaryToSigned(Components[i]));
                    }
                }

                // First pass: Handle multiplication (*) and division (/)
                for (int i = 1; i < Components.length - 1; i++) {
                    if (Components[i] == null) break;

                    if (Components[i].equals("*") || Components[i].equals("/")) {
                        int left = Integer.parseInt(Components[i - 1]);
                        int right = Integer.parseInt(Components[i + 1]);

                        if (Components[i].equals("/") && right == 0) {
                            System.out.println("Error: trying to divide by 0 (evaluated:" + "0");
                            return ("Error: trying to divide by 0 (evaluated:" + "0");
                        }

                        // Perform the operation
                        int result = Components[i].equals("*") ? left * right : left / right;

                        // Replace the left operand with the result
                        Components[i - 1] = String.valueOf(result);

                        // Shift elements left to remove the operator and right operand
                        for (int j = i; j < Components.length - 2; j++) {
                            Components[j] = Components[j + 2];
                        }
                        Components[Components.length - 1] = null;
                        Components[Components.length - 2] = null;

                        i--; // Adjust index after shifting
                    }
                }

                // Second pass: Handle addition (+) and subtraction (-)
                int result = Integer.parseInt(Components[0]);
                for (int i = 1; i < Components.length && Components[i] != null; i += 2) {
                    String operator = Components[i];
                    if (i + 1 >= Components.length || Components[i + 1] == null) {
                        System.out.println("Error: invalid expression");
                        return invalid;
                    }

                    int right = Integer.parseInt(Components[i + 1]);
                    if (operator.equals("+")) {
                        result += right;
                    } else if (operator.equals("-")) {
                        result -= right;
                    }
                }

                // Convert the result back to an 8-bit binary string
                String finalResult = signedToBinary(result);
                System.out.println("The value of expression " + RealExpression + " is : " + finalResult);
                return Components[0];
            }
        }



        if (base == 8) {


            // Validate components for base 8
            for (int i = 0; i < Components.length; i++) {
                if (!isNumber(Components[i],base)){ // Check for valid base 8 number
                    if (!("+*/-".contains(Components[i]))) {
                        System.out.println("Error: invalid expression");
                        return invalid;
                    }
                }
            }

            // First pass: Handle multiplication (*) and division (/)
            for (int i = 1; i < Components.length - 1; i++) {
                if (Components[i] == null) break; // Stop if null is encountered

                if (Components[i].equals("*") || Components[i].equals("/")) {
                    // Parse left and right operands from base 8 to base 10
                    int left = Integer.parseInt(Components[i - 1], 8);
                    int right = Integer.parseInt(Components[i + 1], 8);

                    // Handle division by zero
                    if (Components[i].equals("/") && right == 0) {
                        System.out.println("Error: trying to divide by 0 (evaluated: \"0\")");
                      return "Error: trying to divide by 0 (evaluated: \"0\")";
                    }

                    // Perform the operation in base 10
                    int result = Components[i].equals("*") ? left * right : left / right;

                    // Replace the left operand with the result converted back to base 8
                    Components[i - 1] = Integer.toString(result, 8);

                    // Shift elements left to overwrite the operator and the right operand
                    for (int j = i; j < Components.length - 2; j++) {
                        Components[j] = Components[j + 2];
                    }

                    // Mark trailing elements as null
                    Components[Components.length - 1] = null;
                    Components[Components.length - 2] = null;

                    i--; // Adjust index after shifting
                }
            }

            // Second pass: Handle addition (+) and subtraction (-)
            // Start with the first number, converted to base 10
            int result = Integer.parseInt(Components[0], 8);
            for (int i = 1; i < Components.length && Components[i] != null; i += 2) {
                String operator = Components[i];
                // Ensure the next operand exists and is valid
                if (i + 1 >= Components.length || Components[i + 1] == null) {
                    System.out.println("Error: invalid expression");
                    return invalid;
                }

                // Parse the next operand from base 8 to base 10
                int right = Integer.parseInt(Components[i + 1], 8);

                // Perform the operation
                if (operator.equals("+")) {
                    result += right;
                } else if (operator.equals("-")) {
                    result -= right;
                }
            }

            // Convert the final result back to base 8 and print it
            System.out.println("The value of expression " + RealExpression + " is : " + Integer.toString(result, 8));
            return Integer.toString(result, 8);
        }

        if (base == 10)
        {

            for(int i=0;i<Components.length;i++)
            {
                if(!isNumber(Components[i],base))
                {
                    if(!("+*/-".contains(Components[i])))
                    {
                        System.out.println("Error: invalid expression");
                       return invalid;
                    }
                }

            }
            for (int i = 1; i < Components.length - 1; i++) {
                if (Components[i] == null) break; // Stop if null is encountered

                if (Components[i].equals("*") || Components[i].equals("/")) {

                    int left = Integer.parseInt(Components[i - 1]);
                    int right = Integer.parseInt(Components[i + 1]);

                    if (Components[i].equals("/") && right == 0) {
                        System.out.println("Error: trying to divide by 0 (evaluated: \"0\")");
                     return "Error: trying to divide by 0 (evaluated: \"0\")";
                    }

                    // Perform the operation
                    int result = Components[i].equals("*") ? left * right : left / right;

                    // Replace the left operand with the result
                    Components[i - 1] = String.valueOf(result);

                    // Shift elements left to overwrite the operator and the right operand
                    for (int j = i; j < Components.length - 2; j++) {
                        Components[j] = Components[j + 2];
                    }

                    // Mark trailing elements as null
                    Components[Components.length - 1] = null;
                    Components[Components.length - 2] = null;

                    i--; // Adjust index after shifting
                }
            }


            int result = Integer.parseInt(Components[0]); // Start with the first number

            for (int i = 1; i < Components.length && Components[i] != null; i += 2) {
                String operator = Components[i];
                // Ensure the next operand exists and is valid
                if (i + 1 >= Components.length || Components[i + 1] == null) {
                    System.out.println("Error: invalid expression");
                   return invalid;
                }

                int right = Integer.parseInt(Components[i + 1]);

                // Perform the operation
                if (operator.equals("+")) {
                    result += right;
                } else if (operator.equals("-")) {
                    result -= right;
                }
            }


            // Print the final result
            System.out.println("The value of expression " + RealExpression + " is : " + result);
           return   Integer.toString(result, 10);

        }


        if (base == 16) {


            // Validate components for base 16
            for (int i = 0; i < Components.length; i++) {
                if (!isNumber(Components[i],base)) { // Check for valid base 16 number
                    if (!("+*/-".contains(Components[i]))) {
                        System.out.println("Error: invalid expression");
                      return invalid;
                    }
                }
            }

            // First pass: Handle multiplication (*) and division (/)
            for (int i = 1; i < Components.length - 1; i++) {
                if (Components[i] == null) break; // Stop if null is encountered

                if (Components[i].equals("*") || Components[i].equals("/")) {
                    // Parse left and right operands from base 16 to base 10
                    int left = Integer.parseInt(Components[i - 1], 16);
                    int right = Integer.parseInt(Components[i + 1], 16);

                    // Handle division by zero
                    if (Components[i].equals("/") && right == 0) {
                        System.out.println("Error: trying to divide by 0 (evaluated: \"0\")");
                      return "Error: trying to divide by 0 (evaluated: \"0\")";
                    }

                    // Perform the operation in base 10
                    int result = Components[i].equals("*") ? left * right : left / right;

                    // Replace the left operand with the result converted back to base 16
                    Components[i - 1] = Integer.toString(result, 16).toUpperCase();

                    // Shift elements left to overwrite the operator and the right operand
                    for (int j = i; j < Components.length - 2; j++) {
                        Components[j] = Components[j + 2];
                    }

                    // Mark trailing elements as null
                    Components[Components.length - 1] = null;
                    Components[Components.length - 2] = null;

                    i--; // Adjust index after shifting
                }
            }

            // Second pass: Handle addition (+) and subtraction (-)
            // Start with the first number, converted to base 10
            int result = Integer.parseInt(Components[0], 16);
            for (int i = 1; i < Components.length && Components[i] != null; i += 2) {
                String operator = Components[i];
                // Ensure the next operand exists and is valid
                if (i + 1 >= Components.length || Components[i + 1] == null) {
                    System.out.println("Error: invalid expression");
                    return invalid;
                }

                // Parse the next operand from base 16 to base 10
                int right = Integer.parseInt(Components[i + 1], 16);

                // Perform the operation
                if (operator.equals("+")) {
                    result += right;
                } else if (operator.equals("-")) {
                    result -= right;
                }
            }

            // Convert the final result back to base 16 and print it
            System.out.println("The value of expression " + RealExpression + " is : " + Integer.toString(result, 16).toUpperCase());
           return Integer.toString(result, 16).toUpperCase();
        }


        return Expression;
    }
    public static int CheckIfValid(String Expression, int base) {
        String[] Components = Expression.split("\\s+");

        if (Components.length == 0) {
            System.out.println("Error: invalid expression");
            return -1;
        }

        // Check the first component
        if (Components[0].startsWith("-") && Components[0].length()>1) {

            if (!isNumber(Components[0].substring(1), base)) { // Check if valid number after '-'
                System.out.println("Error: invalid expression");
                return -1;
            }
        } else if ("+*/".contains(Components[0])) {
            // Reject other operators at the start
            System.out.println("Error: invalid expression");
            return -1;
        }

        // Check other components
        for (int i = 1; i < Components.length; i++) {
            String current = Components[i];

            // Handle unary '-' as part of a number
            if (current.startsWith("-") && isNumber(current.substring(1), base) && current.length()>1) {
                continue;
            }

            // Check for consecutive operators
            if ("+/".contains(current) && "+-/".contains(Components[i - 1])) {
                System.out.println("Error: invalid expression");
                return -1;
            }

            // Validate numbers
            if (!"+-*/".contains(current) && !isNumber(current, base)) {
                System.out.println("Error: invalid expression");
                return -1;
            }

            // Check for division by zero
            if (current.equals("/") && i + 1 < Components.length && Components[i + 1].equals("0")) {
                System.out.println("Error: trying to divide by 0 (evaluated: \"0\")");
                return -1;
            }
        }

        return base;
    }

    public static boolean isNumber(String str, int base) {
        try {
            Integer.parseInt(str, base); // Try to parse the number in the given base
            return true; // If parsing is successful, it's valid
        } catch (NumberFormatException e) {
            return false; // If parsing fails, it's invalid
        }
    }

    public static boolean isBinary(String str) {
        return str.matches("[01]{8}"); // Must be exactly 8 bits
    }
    public static String to8BitBinary(int num) {
        return String.format("%8s", Integer.toBinaryString(num & 0xFF)).replace(' ', '0');
    }
    // Convert Binary String to Signed Integer
    public static int binaryToSigned(String binary) {
        int value = Integer.parseInt(binary, 2);
        if (binary.charAt(0) == '1') { // MSB is 1, meaning negative
            value -= 256; // Subtract 2^8 to convert to negative
        }
        return value;
    }

    // Convert Signed Integer to 8-Bit Binary String
    public static String signedToBinary(int num) {
        if (num < 0) {
            num = (num + 256) & 0xFF; // Add 256 and limit to 8 bits
        }
        return String.format("%8s", Integer.toBinaryString(num & 0xFF)).replace(' ', '0');
    }
    public static String addSpaces(String expression) {
        StringBuilder result = new StringBuilder();
        char[] chars = expression.toCharArray();

        // Early check: Ensure the expression is not starting with "-" alone
        if (expression.trim().equals("-")) {
            return "Error: invalid expression"; // Return error string for invalid input
        }

        for (int i = 0; i < chars.length; i++) {
            char current = chars[i];

            // Keep unary '-' or '~' attached to the number
            if ((current == '-' || current == '~') &&
                    (i == 0 || "+-*/&|^".indexOf(chars[i - 1]) >= 0)) {
                result.append(current);
            }
            // Add spaces around operators
            else if ("+-*/&|^".indexOf(current) >= 0) {
                result.append(" ").append(current).append(" ");
            }
            // Append other characters (digits, letters, etc.)
            else {
                result.append(current);
            }
        }

        // Trim and normalize spaces
        return result.toString().trim().replaceAll("\\s+", " ");
    }


    public static String handleFirstMinus(String expression) {
        // Trim the expression to remove leading/trailing spaces
        expression = expression.trim();

        // Check if the first character is '-' and followed by a space
        if (expression.length() > 2 && expression.charAt(0) == '-' && expression.charAt(1) == ' ') {
            // Combine the minus with the number following it
            int nextSpaceIndex = expression.indexOf(' ', 2);
            if (nextSpaceIndex == -1) {
                // No other spaces; combine '-' with the rest of the string
                return "-" + expression.substring(2).trim();
            }
            // Combine '-' with the next number
            String combined = "-" + expression.substring(2, nextSpaceIndex).trim();
            // Reconstruct the expression
            return combined + expression.substring(nextSpaceIndex);
        }

        // Return the original expression if no changes are needed
        return expression;
    }

    public static String[] validateComponents(String[] components) {
        for (int i = 0; i < components.length - 1; i++) {
            if (components[i] != null && components[i].equals("-")) {
                // Check the context of the minus
                if (i == 0 || "+-*/".contains(components[i - 1])) {
                    // Combine '-' with the next number if it follows an operator or is the first element
                    if (isNumber(components[i + 1], 10)) {
                        components[i + 1] = "-" + components[i + 1];
                        components[i] = null; // Remove the standalone '-'
                    }
                }
            }
        }

        // Remove null entries and shift elements
        return Arrays.stream(components).filter(Objects::nonNull).toArray(String[]::new);
    }


// Validate if a string is an


}