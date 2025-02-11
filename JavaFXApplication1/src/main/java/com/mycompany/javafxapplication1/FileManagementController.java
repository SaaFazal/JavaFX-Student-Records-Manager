/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.javafxapplication1;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;

public class FileManagementController {

    @FXML
    private ListView<String> fileListView;
    
    @FXML
    private Button addFileBtn, updateFileBtn, deleteFileBtn;

    private String userRole;
    private String currentUser = "user123"; 

    private static final String FILE_DIRECTORY = "/home/ntu-user/NetBeansProjects/cwk/JavaFXApplication1/"; 

    public void setUserRole(String role) {
        this.userRole = role;
        loadFiles();
    }

    private void loadFiles() {
        File directory = new File(FILE_DIRECTORY);
        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("Invalid directory: " + FILE_DIRECTORY);
            return;
        }

        File[] files = directory.listFiles();
        if (files == null) return;

        ObservableList<String> fileNames;
        if ("admin".equals(userRole)) {
            // Admin can see all files
            fileNames = FXCollections.observableArrayList(Arrays.stream(files)
                    .map(File::getName)
                    .collect(Collectors.toList())); 
        } else {
            // Users can only see their own files
            fileNames = FXCollections.observableArrayList(Arrays.stream(files)
                    .filter(file -> file.getName().startsWith(currentUser + "_")) 
                    .map(File::getName)
                    .collect(Collectors.toList())); 
        }

        fileListView.setItems(fileNames);
    }

    @FXML
    private void handleAddFile() {
    FileChooser fileChooser = new FileChooser();
    File selectedFile = fileChooser.showOpenDialog(addFileBtn.getScene().getWindow());
    if (selectedFile != null) {
        // Copy file to FILE_DIRECTORY with user prefix
        String newFileName = currentUser + "_" + selectedFile.getName();
        File dest = new File(FILE_DIRECTORY + newFileName);
        try {
            Files.copy(selectedFile.toPath(), dest.toPath());
            loadFiles(); // Refresh list
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

    @FXML
    private void handleUpdateFile() {
        System.out.println("Update File Clicked"); 
    }

    @FXML
    private void handleDeleteFile() {
    String selectedFile = fileListView.getSelectionModel().getSelectedItem();
    if (selectedFile != null) {
        File fileToDelete = new File(FILE_DIRECTORY + selectedFile);
        if (fileToDelete.delete()) {
            loadFiles(); // Refresh list
        }
    }
}
    
    @FXML
private void handleBack() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/javafxapplication1/mainDashboard.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) addFileBtn.getScene().getWindow();
        stage.setScene(new Scene(root, 1200, 800));
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
    }
}
}

