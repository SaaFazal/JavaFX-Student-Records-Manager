package com.mycompany.javafxapplication1;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
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
    private Button registerBtn, loginButton;
    
    @FXML
    private TextField userTextField;

    @FXML
    private PasswordField passPasswordField;
    
    @FXML
    private Label welcomeMessage;

    private String currentUserRole = null;

    @FXML
    public void initialize() {
        welcomeMessage.setText("Welcome! Existing users can log in, and their roles (Admin/User) are fetched automatically. New users must register.");
    }

    @FXML
    private void registerBtnHandler(ActionEvent event) {
        switchScene("/com/mycompany/javafxapplication1/register.fxml", "Register a new User");
    }

    @FXML
    private void switchToSecondary() {
        Stage primaryStage = (Stage) loginButton.getScene().getWindow();
        try {
            DB myObj = new DB();
            String username = userTextField.getText();
            String password = passPasswordField.getText();
            
            if (myObj.validateUser(username, password)) {
                String role = myObj.getUserRole(username);
                FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/mycompany/javafxapplication1/mainDashboard.fxml"
                ));
                Parent root = loader.load();
                
                MainDashboardController controller = loader.getController();
                controller.setUserRole(role);
                controller.setCurrentUser(username);
                
                primaryStage.setScene(new Scene(root, 1200, 800));
            } else {
                showAlert("Login Failed", "Invalid credentials");
            }
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

    private void showAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
