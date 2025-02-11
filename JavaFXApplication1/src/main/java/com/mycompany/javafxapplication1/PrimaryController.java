package com.mycompany.javafxapplication1;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PrimaryController {

    @FXML
    private Button registerBtn, userManagementButton, terminalButton, fileManagementButton;

    @FXML
    private TextField userTextField;

    @FXML
    private PasswordField passPasswordField;

    private String currentUserRole = null; // Stores logged-in user's role

    @FXML
    private void registerBtnHandler(ActionEvent event) {
        switchScene("/com/mycompany/javafxapplication1/register.fxml", "Register a new User");
    }

    @FXML
private void switchToSecondary() {
    Stage primaryStage = (Stage) registerBtn.getScene().getWindow();
    try {
        DB myObj = new DB();
        if (myObj.validateUser(userTextField.getText(), passPasswordField.getText())) {
            currentUserRole = myObj.getUserRole(userTextField.getText()); // Fetch user role
            if (currentUserRole != null) {
                // Set the current user
                User.currentUser = new User(userTextField.getText(), passPasswordField.getText(), currentUserRole);
                showMainDashboard(primaryStage);
            } else {
                showAlert("Role Fetching Error", "Failed to fetch the user role.");
            }
        } else {
            showAlert("Invalid User Name / Password", "Please try again!");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    private void showMainDashboard(Stage primaryStage) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/javafxapplication1/mainDashboard.fxml"));
        Parent root = loader.load();

        // Get MainDashboardController instance
        MainDashboardController dashboardController = loader.getController();
        dashboardController.setUserRole(currentUserRole); // Pass role to MainDashboardController

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    } catch (Exception e) {
        e.printStackTrace();
    }
}


    private void switchScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root, 1200, 800));
            newStage.setTitle(title);
            newStage.show();
            ((Stage) registerBtn.getScene().getWindow()).hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openTerminal(ActionEvent event) {
        switchScene("/com/mycompany/javafxapplication1/terminal.fxml", "Terminal");
    }

    @FXML
    private void openFileManagement(ActionEvent event) {
        if ("admin".equals(currentUserRole)) {
            switchScene("/com/mycompany/javafxapplication1/fileManagement.fxml", "File Management");
        } else {
            showAlert("Access Denied", "You do not have sufficient privileges.");
        }
    }

    private void showAlert(String header, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void logout() {
        Stage primaryStage = (Stage) registerBtn.getScene().getWindow();
        switchScene("/com/mycompany/javafxapplication1/primary.fxml", "Login");
    }
}
