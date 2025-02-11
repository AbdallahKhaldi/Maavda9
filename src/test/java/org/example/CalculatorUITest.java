package org.example;

import static org.testfx.api.FxAssert.verifyThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.api.FxRobot;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

@ExtendWith(ApplicationExtension.class)
public class CalculatorUITest {

    private Parent root;

    @Start
    private void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/primary.fxml"));
        root = fxmlLoader.load();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void testInitialTextField(FxRobot robot) {
        TextField display = robot.lookup("#Display").queryAs(TextField.class);
        assert display != null;
        assert display.getText().isEmpty();
    }

    @Test
    void testButtonPress(FxRobot robot) {
        TextField display = robot.lookup("#Display").queryAs(TextField.class);
        assert display != null;

        robot.clickOn("5");
        assert display.getText().equals("5");
    }

    @Test
    void testMultipleButtonPress(FxRobot robot) {
        TextField display = robot.lookup("#Display").queryAs(TextField.class);
        assert display != null;

        robot.clickOn("1");
        robot.clickOn("2");
        robot.clickOn("3");
        assert display.getText().equals("123");
    }

    @Test
    void testOperators(FxRobot robot) {
        TextField display = robot.lookup("#Display").queryAs(TextField.class);
        assert display != null;

        robot.clickOn("7");
        robot.clickOn("+");
        robot.clickOn("3");
        robot.clickOn("=");
        assert !display.getText().isEmpty(); // Assuming the result appears here
    }

    @Test
    void testBaseChooserExists(FxRobot robot) {
        ComboBox<?> baseChooser = robot.lookup("#BaseChooser").queryAs(ComboBox.class);
        assert baseChooser != null;
    }

    @Test
    void testClearButton(FxRobot robot) {
        TextField display = robot.lookup("#Display").queryAs(TextField.class);
        assert display != null;

        robot.clickOn("9");
        assert display.getText().equals("9");

        robot.clickOn("Clear");
        assert display.getText().isEmpty();
    }

}
