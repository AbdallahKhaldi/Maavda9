package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class ArithmeticAppFileTest {

    private ArithmeticApp arithmeticApp;

    @BeforeEach
    void setUp() {
        arithmeticApp = new ArithmeticApp();
    }

    private void testExpressionFromFile(String inputFile, String outputFile) throws IOException {
        List<String> inputLines = Files.readAllLines(Path.of("src/test/resources/" + inputFile));
        int base = Integer.parseInt(inputLines.get(0).trim());
        String expression = inputLines.get(1).trim();

        List<String> outputLines = Files.readAllLines(Path.of("src/test/resources/" + outputFile));
        String expectedOutput = outputLines.get(outputLines.size() - 1).split(":")[1].trim();

        String actualOutput = arithmeticApp.main(expression, base);

        // Debugging Print Statements
        System.out.println("==== TESTING: " + inputFile + " ====");
        System.out.println("Expression: " + expression);
        System.out.println("Base: " + base);
        System.out.println("Expected: " + expectedOutput);
        System.out.println("Actual: " + actualOutput);
        System.out.println("==============================");

        assertEquals(expectedOutput, actualOutput, "Mismatch in output for " + inputFile);
    }

    @Test
    void testCase1() throws IOException {
        testExpressionFromFile("in1.txt", "out1.txt");
    }

    @Test
    void testCase2_DivideByZero() throws IOException {
        testExpressionFromFile("in2.txt", "out2.txt");
    }

    @Test
    void testCase3() throws IOException {
        testExpressionFromFile("in3.txt", "out3.txt");
    }

    @Test
    void testCase4_InvalidBase() throws IOException {
        testExpressionFromFile("in4.txt", "out4.txt");
    }

    @Test
    void testCase5_BitwiseOperations() throws IOException {
        testExpressionFromFile("in5.txt", "out5.txt");
    }

    @Test
    void testCase6() throws IOException {
        testExpressionFromFile("in6.txt", "out6.txt");
    }


    @Test
    void testCase7() throws IOException {
        testExpressionFromFile("in7.txt", "out7.txt");
    }

    @Test
    void testCase8() throws IOException {
        testExpressionFromFile("in8.txt", "out8.txt");
    }

    @Test
    void testCase9() throws IOException {
        testExpressionFromFile("in9.txt", "out9.txt");
    }

    @Test
    void testCase10() throws IOException {
        testExpressionFromFile("in10.txt", "out10.txt");
    }
    @Test
    void testCase11() throws IOException {
        testExpressionFromFile("in11.txt", "out11.txt");
    }
    @Test
    void testCase12() throws IOException {
        testExpressionFromFile("in12.txt", "out12.txt");
    }
    @Test
    void testCase13() throws IOException {
        testExpressionFromFile("in13.txt", "out13.txt");
    }
    @Test
    void testCase14() throws IOException {
        testExpressionFromFile("in14.txt", "out14.txt");
    }
    @Test
    void testCase15() throws IOException {
        testExpressionFromFile("in15.txt", "out15.txt");
    }
}
