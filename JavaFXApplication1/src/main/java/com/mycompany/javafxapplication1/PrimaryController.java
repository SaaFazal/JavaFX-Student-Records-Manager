package com.mycompany.javafxapplication1;

import java.io.IOException; 
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class PrimaryController {

    @FXML
    private Button registerBtn;

    @FXML
    private TextField userTextField;

    @FXML
    private PasswordField passPasswordField;
    
    @FXML
    private TextArea terminalTextArea; // Text Area to display the results for the command

    @FXML
    private Button runCommandButton; // New Button is added for command

    @FXML
    private TextField commandInputField; // Text Field to enter the command

    @FXML
    private void registerBtnHandler(ActionEvent event) {
        Stage secondaryStage = new Stage();
        Stage primaryStage = (Stage) registerBtn.getScene().getWindow();
        DB myObj = new DB();
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("register.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 640, 480);
            secondaryStage.setScene(scene);
            secondaryStage.setTitle("Register a new User");
            secondaryStage.show();
            primaryStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void dialogue(String headerMsg, String contentMsg) {
        Stage secondaryStage = new Stage();
        Group root = new Group();
        Scene scene = new Scene(root, 300, 300, Color.DARKGRAY);

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(headerMsg);
        alert.setContentText(contentMsg);

        Optional<ButtonType> result = alert.showAndWait();
    }

    @FXML
    private void switchToSecondary() {
        Stage secondaryStage = new Stage();
        Stage primaryStage = (Stage) registerBtn.getScene().getWindow();
        try {
            DB myObj = new DB();
            String[] credentials = {userTextField.getText(), passPasswordField.getText()};
            if(myObj.validateUser(userTextField.getText(), passPasswordField.getText())){
                // Hide login screen
                primaryStage.hide();
                
                // Show terminal window
                showTerminalWindow(primaryStage);
            }
            else{
                dialogue("Invalid User Name / Password","Please try again!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showTerminalWindow(Stage primaryStage) {
        // Create a new window with terminal interface
        Stage terminalStage = new Stage();
        terminalStage.setTitle("Terminal");

        // Layout for terminal
        VBox terminalVBox = new VBox(10);
        terminalVBox.setAlignment(Pos.CENTER);

        terminalTextArea = new TextArea();
        terminalTextArea.setEditable(false);
        terminalTextArea.setPrefHeight(200);

        commandInputField = new TextField();
        commandInputField.setPromptText("Enter command...");

        runCommandButton = new Button("Run Command");
        runCommandButton.setOnAction(e -> executeCommand());

        terminalVBox.getChildren().addAll(terminalTextArea, commandInputField, runCommandButton);

        Scene terminalScene = new Scene(terminalVBox, 600, 400);
        terminalStage.setScene(terminalScene);
        terminalStage.show();
    }
  @FXML
    private void executeCommand() {
        String command = commandInputField.getText();
        if (command.isEmpty()) {
            showError("Empty Command", "Please enter a command to execute.");
            return;
        }
        
try {
            // Run the terminal command
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("bash", "-c", command);
            
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            terminalTextArea.setText(output.toString());
        } catch (IOException e) {
            showError("Command Execution Failed", "There was an error executing the command.");
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}