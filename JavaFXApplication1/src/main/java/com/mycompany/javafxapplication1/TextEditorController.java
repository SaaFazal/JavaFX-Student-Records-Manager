/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.javafxapplication1;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class TextEditorController {
    
    @FXML
    private TextArea contentArea;
    
    private File currentFile;
    
    private boolean readOnly = false;
 
    @FXML private Button saveBtn;

    public void loadFile(File file) throws IOException {
        this.currentFile = file;
        contentArea.setText(Files.readString(file.toPath()));
    }

    @FXML
    private void handleSave() {
        try {
            Files.writeString(currentFile.toPath(), contentArea.getText());
            closeWindow();
        } catch (IOException e) {
            showAlert("Save Error", "Failed to save file: " + e.getMessage());
        }
    }

    @FXML
    private void handleClose() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) contentArea.getScene().getWindow();
        stage.close();
    }
     public void setReadOnlyMode(boolean readOnly) {
        contentArea.setEditable(!readOnly);
        saveBtn.setVisible(!readOnly);
    }

    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.ERROR
        );
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
