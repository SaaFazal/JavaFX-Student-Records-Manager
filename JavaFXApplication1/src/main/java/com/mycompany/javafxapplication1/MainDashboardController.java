/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.javafxapplication1;

import static com.mycompany.javafxapplication1.User.currentUser;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class MainDashboardController {

    @FXML
    private Button userManagementBtn;
    
    @FXML
    private Button terminalBtn;
    
    @FXML
    private Button fileManagementBtn;
    
    @FXML
    private String userRole;
    
    private String currentUser;

public void setCurrentUser(String user) {
    this.currentUser = user;
}

    // Handle User Management Button
    @FXML
private void handleUserManagement(ActionEvent event) throws IOException {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/javafxapplication1/userManagement.fxml"));
        Parent root = loader.load();

        Stage userManagementStage = new Stage();
        userManagementStage.setTitle("User Management");
        userManagementStage.setScene(new Scene(root, 1200, 800));
        userManagementStage.show();
    } catch (Exception e) {
        showAlert("Error", "Failed to open user management: " + e.getMessage());
    }
}

   @FXML
public void setUserRole(String role) {
    this.userRole = role;
    updateButtonVisibility();
}
@FXML
private void updateButtonVisibility() {
    boolean isAdmin = "admin".equals(userRole);
    userManagementBtn.setVisible(true);  
    terminalBtn.setVisible(true);
    fileManagementBtn.setVisible(true);
}


    // Handle Terminal Button
    @FXML
private void handleTerminal(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/javafxapplication1/terminal.fxml"));
        Parent root = loader.load();

        // Get the TerminalController and pass the current stage
        TerminalController terminalController = loader.getController();
        terminalController.setStage((Stage) terminalBtn.getScene().getWindow());

        // Open the terminal window
        Stage terminalStage = new Stage();
        terminalStage.setScene(new Scene(root, 1200, 800));
        terminalStage.setTitle("Terminal");
        terminalStage.show();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    // Handle File Management Button (Only accessible by Admins)
   @FXML
private void handleFileManagement(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
            "/com/mycompany/javafxapplication1/fileManagement.fxml"
        ));
        Parent root = loader.load();

        // Pass both role and username to file management
        FileManagementController controller = loader.getController();
        controller.setUserRole(userRole);
        controller.setCurrentUser(currentUser); 

        Stage fileManagementStage = new Stage();
        fileManagementStage.setTitle("File Management");
        fileManagementStage.setScene(new Scene(root, 1200, 800));
        fileManagementStage.show();
    } catch (Exception e) {
        showAlert("Error", "Failed to open file manager: " + e.getMessage());
    }
}


private void openFileManagementWindow() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/javafxapplication1/fileManagement.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1200, 800);
        Stage fileManagementStage = new Stage();
        fileManagementStage.setTitle("File Management");
        fileManagementStage.setScene(scene);
        fileManagementStage.show();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
@FXML
private void handleLogout(ActionEvent event) {
    try {
        // Load the login screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/javafxapplication1/primary.fxml"));
        Parent root = loader.load();

        // Get the current stage
        Stage stage = (Stage) userManagementBtn.getScene().getWindow();

        // Set the new scene
        Scene scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
    }
}



    private void showAlert(String title, String message) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}


}
