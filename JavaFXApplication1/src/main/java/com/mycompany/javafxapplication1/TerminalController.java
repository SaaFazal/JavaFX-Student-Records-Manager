/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javafxapplication1;

import java.io.BufferedReader;
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
        String command = commandInput.getText();
        if (command.isEmpty()) {
            showError("Empty Command", "Please enter a command to execute.");
            return;
        }

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

    @FXML
private void backButtonHandler() {
    try {
        // Check if the user is logged in
        if (User.currentUser == null) {
            showError("Error", "User is not logged in or session expired.");
            return;
        }

        // Load the Main Dashboard FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/javafxapplication1/mainDashboard.fxml"));
        Parent root = loader.load();

        // Get the controller for MainDashboard
        MainDashboardController dashboardController = loader.getController();
        dashboardController.setUserRole(User.currentUser.getRole());  // Pass the role to the MainDashboard

        // Close the terminal window
        stage.close();

        // Reuse the existing stage (MainDashboard)
        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.setTitle("Main Dashboard");
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
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
