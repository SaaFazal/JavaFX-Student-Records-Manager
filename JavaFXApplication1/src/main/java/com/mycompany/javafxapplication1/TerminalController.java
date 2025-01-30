/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javafxapplication1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TerminalController {

    @FXML
    private TextArea commandDisplay;  // Matches FXML
    @FXML
    private Button enterBtn;  // Matches FXML
    @FXML
    private TextField commandInput;  // Matches FXML
    
    @FXML
private void backButtonHandler(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("primary.fxml")); // Load Primary Scene
        Parent root = loader.load();
        
        // Get the current stage
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        
            Scene scene = new Scene(root, 640, 480); // Set scene size
        stage.setScene(scene);
        stage.setTitle("Primary View");
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
    }
}


    @FXML
    private void executeCommand() {
        String command = commandInput.getText();
        if (command.isEmpty()) {
            showError("Empty Command", "Please enter a command to execute.");
            return;
        }

        try {
            ProcessBuilder processBuilder;
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            } else {
                processBuilder = new ProcessBuilder("bash", "-c", command);
            }
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            commandDisplay.setText(output.toString());
        } catch (IOException e) {
            showError("Command Execution Failed", "Error executing command: " + e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
