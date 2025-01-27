/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javafxapplication1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class TerminalController {

    @FXML
    private TextArea terminalTextArea; // Text Area to display the results for the command

    @FXML
    private Button runCommandButton; // Button to run the command

    @FXML
    private TextField commandInputField; // Text Field to enter the command

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
