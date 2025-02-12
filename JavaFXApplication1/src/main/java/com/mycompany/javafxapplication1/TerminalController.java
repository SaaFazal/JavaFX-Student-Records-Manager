/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javafxapplication1;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TerminalController {

    @FXML
    private TextArea commandDisplay;

    @FXML
    private Button enterBtn;

    @FXML
    private TextField commandInput;

    @FXML
    private Button backButton; // Added back button

    @FXML
private void onEnterButtonClicked() {
    String command = commandInput.getText().trim();
    if (command.startsWith("nano ")) {
        handleNanoCommand(command);
    } else {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            commandDisplay.setText(output.toString());
        } catch (IOException e) {
            showError("Command Execution Failed", "There was an error executing the command.");
        }
    }
    commandInput.clear();
}

    @FXML
private void backButtonHandler() {
    Stage stage = (Stage) backButton.getScene().getWindow();
    stage.close();
}

private void handleNanoCommand(String command) {
    String filename = command.substring(5).trim();
    try {
        File file = new File(filename);
        if (!file.exists()) file.createNewFile();
        
        Process process = new ProcessBuilder("gnome-terminal", "--", "nano", filename).start();
        process.waitFor();
    } catch (Exception e) {
        showError("Editor Error", "Failed to open nano editor");
    }
}
private Stage stage;

public void setStage(Stage stage) {
    this.stage = stage;
}




    private void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
