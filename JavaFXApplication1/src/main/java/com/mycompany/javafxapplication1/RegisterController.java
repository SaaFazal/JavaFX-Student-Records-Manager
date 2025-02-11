/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.javafxapplication1;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import javafx.application.Platform;
import javafx.concurrent.Task;
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
    System.out.println("Register button clicked.");

    Task<Void> task = new Task<Void>() {   
        @Override                                         // @Override instructs the compiler to override this method
        protected Void call() throws Exception {
            try {
                System.out.println("Initializing database connection...");
                DB myObj = new DB();

                String selectedRole = "user";
                String adminPasscode = adminPasscodeField.getText();

                System.out.println("Admin passcode entered: " + adminPasscode);

                if ("admin123".equals(adminPasscode)) {
                    selectedRole = "admin";
                }

                System.out.println("Selected role: " + selectedRole);

                if (passPasswordField.getText().equals(rePassPasswordField.getText())) {
                    System.out.println("Passwords match. Registering user...");
                    myObj.addDataToDB(userTextField.getText(), passPasswordField.getText(), selectedRole);
                    System.out.println("User registered successfully.");

                    Platform.runLater(() -> {
                        showDialog("Registration Successful", "User registered successfully!", Alert.AlertType.INFORMATION);
                    });
                } else {
                    System.out.println("Passwords do not match.");
                    Platform.runLater(() -> {
                        showDialog("Registration Failed", "Passwords do not match, please try again.", Alert.AlertType.ERROR);
                    });
                }
            } catch (Exception e) {
                System.err.println("Error during registration: " + e.getMessage());
                e.printStackTrace();
                Platform.runLater(() -> {
                    showDialog("Error", "An error occurred during registration. Please try again.", Alert.AlertType.ERROR);
                });
            }
            return null;
        }
    };

    new Thread(task).start();
}

    // Handle Back to Login Button Click
    @FXML
    private void backLoginBtnHandler(ActionEvent event) {
        Stage primaryStage = (Stage) backLoginBtn.getScene().getWindow();
        
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("primary.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1200, 800);  
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
