/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.javafxapplication1;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class RegisterController {

    @FXML
    private Button registerBtn;

    @FXML
    private Button backLoginBtn;

    @FXML
    private PasswordField passPasswordField;

    @FXML
    private PasswordField rePassPasswordField;

    @FXML
    private TextField userTextField;
    
    @FXML
    private PasswordField adminPasscodeField;
    
    @FXML
    private Text fileText;
    
    @FXML
    private Button selectBtn;

    @FXML
    private ComboBox<String> roleComboBox;  // Role ComboBox

    // Handle Select File Button Click
    @FXML
    private void selectBtnHandler(ActionEvent event) throws IOException {
        Stage primaryStage = (Stage) selectBtn.getScene().getWindow();
        primaryStage.setTitle("Select a File");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        
        if (selectedFile != null) {
            fileText.setText(selectedFile.getCanonicalPath());
        }
    }

    // Method to show confirmation or error dialog
    private void showDialog(String headerMsg, String contentMsg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(headerMsg);
        alert.setContentText(contentMsg);
        alert.showAndWait();
    }

    // Handle Register Button Click
    @FXML
private void registerBtnHandler(ActionEvent event) {
    Stage secondaryStage = new Stage();
    Stage primaryStage = (Stage) registerBtn.getScene().getWindow();

    try {
        FXMLLoader loader = new FXMLLoader();
        DB myObj = new DB();

        // Get the selected role based on the passcode
        String selectedRole = "user"; // Default role is "user"
        String adminPasscode = adminPasscodeField.getText(); // Get the entered passcode

        // Check if the passcode is correct (e.g., "admin123")
        if ("admin123".equals(adminPasscode)) {
            selectedRole = "admin"; // Assign "admin" role if passcode is correct
        }

        if (passPasswordField.getText().equals(rePassPasswordField.getText())) {
            // Register user to the database, including the role
            myObj.addDataToDB(userTextField.getText(), passPasswordField.getText(), selectedRole);
            showDialog("Registration Successful", "User registered successfully!", Alert.AlertType.INFORMATION);

            // Load secondary view after successful registration
            String[] credentials = {userTextField.getText(), passPasswordField.getText()};
            loader.setLocation(getClass().getResource("secondary.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1000, 700);
            secondaryStage.setScene(scene);
            SecondaryController controller = loader.getController();
            controller.initialise(credentials);
            secondaryStage.setTitle("Show Users");
            secondaryStage.show();
            primaryStage.close();
        } else {
            // Passwords don't match, show error message
            showDialog("Registration Failed", "Passwords do not match, please try again.", Alert.AlertType.ERROR);
        }
    } catch (Exception e) {
        e.printStackTrace();
        showDialog("Error", "An error occurred during registration. Please try again.", Alert.AlertType.ERROR);
    }
}

    // Handle Back to Login Button Click
    @FXML
    private void backLoginBtnHandler(ActionEvent event) {
        Stage primaryStage = (Stage) backLoginBtn.getScene().getWindow();
        
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("primary.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1000, 700);  
            Stage secondaryStage = new Stage();
            secondaryStage.setScene(scene);
            secondaryStage.setTitle("Login");
            secondaryStage.show();
            primaryStage.close(); // Close current Register stage
        } catch (Exception e) {
            e.printStackTrace();
            showDialog("Error", "An error occurred while loading the login page.", Alert.AlertType.ERROR);
        }
    }
}
