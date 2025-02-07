/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.javafxapplication1;

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
    
    // Handle User Management Button
    @FXML
private void handleUserManagement(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/javafxapplication1/userManagement.fxml"));
        Parent root = loader.load();

        Stage userManagementStage = new Stage();
        userManagementStage.setTitle("User Management");
        userManagementStage.setScene(new Scene(root, 500, 400));
        userManagementStage.show();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
   @FXML
public void setUserRole(String role) {
    this.userRole = role;
    updateButtonVisibility();
}
@FXML
private void updateButtonVisibility() {
    if ("admin".equals(userRole)) {
        // Admin can see all buttons
        userManagementBtn.setVisible(true);
        terminalBtn.setVisible(true);
        fileManagementBtn.setVisible(true);
    } else {
        // Users can see all buttons except User Management
        userManagementBtn.setVisible(true);  // Users can manage their account
        terminalBtn.setVisible(true); // Users can access terminal
        fileManagementBtn.setVisible(true);  // Users can access file management too
    }
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
        terminalStage.setScene(new Scene(root, 1000, 700));
        terminalStage.setTitle("Terminal");
        terminalStage.show();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    // Handle File Management Button (Only accessible by Admins)
    @FXML
private void handleFileManagement(ActionEvent event) {
    if ("admin".equals(userRole)) {
        // Admin can access all files
        openFileManagementWindow();
    } else {
        // User can only access their own files (add your logic here)
        openFileManagementWindow();
    }
}

private void openFileManagementWindow() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/javafxapplication1/fileManagement.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1000, 700);
        Stage fileManagementStage = new Stage();
        fileManagementStage.setTitle("File Management");
        fileManagementStage.setScene(scene);
        fileManagementStage.show();
    } catch (Exception e) {
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
