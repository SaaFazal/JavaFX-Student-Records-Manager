/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.javafxapplication1;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;

public class FileManagementController {

    @FXML
    private ListView<String> fileListView;
    
    @FXML
    private Button addFileBtn, updateFileBtn, deleteFileBtn;
    
    @FXML
    private Button openFileBtn;

    private String userRole;
    private String currentUser; // Dynamically set

    private static final String FILE_DIRECTORY = "/home/ntu-user/NetBeansProjects/comp20081_cwk/JavaFXApplication1/"; 
    
    private LoadBalancer loadBalancer;  // Declare the LoadBalancer object

    public FileManagementController() {
        loadBalancer = new LoadBalancer(); // Initialize the load balancer
    }

    public void setUserRole(String role) {
        this.userRole = role;
        loadFiles();
    }

    public void setCurrentUser(String user) {
        this.currentUser = user;
        loadFiles();  
    }

    private void loadFiles() {
        File directory = new File(FILE_DIRECTORY);
        if (!directory.exists() || !directory.isDirectory()) {
            showAlert("Error", "Invalid directory");
            return;
        }

        File[] files = directory.listFiles();
        if (files == null) return;

        fileListView.getItems().clear();
        for (File file : files) {
            if (userRole.equals("admin") || file.getName().startsWith(currentUser + "_")) {
                fileListView.getItems().add(file.getName());
            }
        }
    }

    @FXML
private void handleAddFile() {
    // Open a file chooser dialog for the user to choose between adding a new file or selecting an existing one
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Select Action");
    alert.setHeaderText("Choose an option to add a file");

    ButtonType newFileButton = new ButtonType("Create New File");
    ButtonType uploadFileButton = new ButtonType("Upload Existing File");

    alert.getButtonTypes().setAll(newFileButton, uploadFileButton);
    Optional<ButtonType> result = alert.showAndWait();

    if (result.isPresent()) {
        if (result.get() == newFileButton) {
            // Handle creating a new file
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("New File");
            dialog.setHeaderText("Enter filename:");

            Optional<String> filename = dialog.showAndWait();
            filename.ifPresent(f -> {
                createFile(f);  // Create the new file in the folder
                File newFile = new File(FILE_DIRECTORY + f);
                loadBalancer.handleFileUpload(newFile);  // Upload the new file via load balancer
            });
        } else if (result.get() == uploadFileButton) {
            // Handle uploading an existing file
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
            File selectedFile = fileChooser.showOpenDialog(new Stage());

            if (selectedFile != null) {
                // Check if the file already exists in the directory
                File destinationFile = new File(FILE_DIRECTORY + selectedFile.getName());
                if (destinationFile.exists()) {
                    showAlert("Error", "File already exists.");
                } else {
                    try {
                        // Copy the file to the destination directory
                        Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        loadFiles();  // Reload the file list
                        loadBalancer.handleFileUpload(destinationFile);  // Upload the file via load balancer
                    } catch (IOException e) {
                        showAlert("Error", "Failed to upload the file: " + e.getMessage());
                    }
                }
            }
        }
    }
}

private void createFile(String filename) {
    try {
        // Create a new file in the target directory
        File newFile = new File(FILE_DIRECTORY + filename);
        if (newFile.createNewFile()) {
            loadFiles();  // Reload the file list after creating the file
        } else {
            showAlert("Error", "File already exists.");
        }
    } catch (IOException e) {
        showAlert("Error", "File creation failed: " + e.getMessage());
    }
}


    @FXML
    private void handleUpdateFile() {
        String selected = fileListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            File file = new File(FILE_DIRECTORY + selected);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TextEditor.fxml"));
            Parent root = loader.load();
            
            TextEditorController controller = loader.getController();
            controller.loadFile(file);
            
            Stage editorStage = new Stage();
            editorStage.setTitle("Editing: " + file.getName());
            editorStage.setScene(new Scene(root, 600, 500));
            editorStage.show();
            
            // Once the file is updated, trigger the load balancer to handle the upload
            loadBalancer.handleFileUpload(file); 
        } catch (IOException e) {
            showAlert("Error", "Failed to open editor: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteFile() {
        String selectedFileName = fileListView.getSelectionModel().getSelectedItem();
        if (selectedFileName == null) {
            showAlert("Error", "No file selected. Please select a file to delete.");
            return;
        }

        if (!"admin".equals(userRole)) {
            if (!selectedFileName.startsWith(currentUser + "_")) {
                showAlert("Permission Denied", "You can only delete your own files");
                return;
            }
        }

        File fileToDelete = new File(FILE_DIRECTORY + selectedFileName);
        if (fileToDelete.delete()) {
            loadFiles();
        } else {
            showAlert("Error", "Failed to delete the file.");
        }
    }

    @FXML
private void handleOpenFile() {
    String selected = fileListView.getSelectionModel().getSelectedItem();
    if (selected != null) {
        openFileInEditor(selected);
    } else {
        showAlert("Error", "No file selected. Please select a file to open.");
    }
}

private void openFileInEditor(String filename) {
    try {
        File file = new File(FILE_DIRECTORY + filename);
        if (!file.exists()) {
            showAlert("Error", "File does not exist.");
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("TextEditor.fxml"));
        Parent root = loader.load();
        
        TextEditorController controller = loader.getController();
        controller.loadFile(file); 
        
        Stage editorStage = new Stage();
        editorStage.setTitle("Editing: " + file.getName());
        editorStage.setScene(new Scene(root, 600, 500));
        editorStage.show();
    } catch (IOException e) {
        showAlert("Error", "Failed to open file: " + e.getMessage());
    }
}

    @FXML
    private void handleBack() {
        Stage stage = (Stage) addFileBtn.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
