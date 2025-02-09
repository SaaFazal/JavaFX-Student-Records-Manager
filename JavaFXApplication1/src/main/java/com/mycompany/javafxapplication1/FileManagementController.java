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
import java.util.Arrays;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.stream.Collectors;

public class FileManagementController {

    @FXML
    private ListView<String> fileListView;
    
    @FXML
    private Button addFileBtn, updateFileBtn, deleteFileBtn;

    private String userRole;
    private String currentUser = "user123"; // Replace this with actual logged-in username

    private static final String FILE_DIRECTORY = "/home/ntu-user/NetBeansProjects/cwk/JavaFXApplication1/"; // Change this to your actual file path

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
                    .collect(Collectors.toList())); // Replace toList() with collect(Collectors.toList())
        } else {
            // Users can only see their own files
            fileNames = FXCollections.observableArrayList(Arrays.stream(files)
                    .filter(file -> file.getName().startsWith(currentUser + "_")) // Naming convention: user123_file.txt
                    .map(File::getName)
                    .collect(Collectors.toList())); // Replace toList() with collect(Collectors.toList())
        }

        fileListView.setItems(fileNames);
    }

    @FXML
    private void handleAddFile() {
        System.out.println("Add File Clicked"); // Implement file creation logic
    }

    @FXML
    private void handleUpdateFile() {
        System.out.println("Update File Clicked"); // Implement file update logic
    }

    @FXML
    private void handleDeleteFile() {
        System.out.println("Delete File Clicked"); // Implement file deletion logic
    }
}

