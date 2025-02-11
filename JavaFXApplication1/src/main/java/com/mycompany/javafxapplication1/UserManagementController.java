/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.javafxapplication1;

import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;

public class UserManagementController {

    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private TableColumn<User, String> roleColumn;
    @FXML
    private TableColumn<User, String> passwordColumn;

    private ObservableList<User> userList = FXCollections.observableArrayList();
    List<User> result = new ArrayList<>();

    @FXML
public void initialize() {
    usernameColumn.setCellValueFactory(new PropertyValueFactory<>("user"));
    roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

    // Check if the current user is an admin
    if (User.currentUser != null && User.currentUser.isAdmin()) {
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("pass"));
        passwordColumn.setVisible(true); // Show password column for admins
    } else {
        passwordColumn.setVisible(false); // Hide password column for standard users
    }

    loadUsers();
}

    private void loadUsers() {
    userList.clear();
    try (Connection conn = DB.getConnection();
         PreparedStatement stmt = conn.prepareStatement("SELECT name, password, role FROM users");
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            // Create user and add to the result list
            result.add(new User(rs.getString("name"), rs.getString("password"), rs.getString("role")));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    // Add the result list to the observable list and set it to the table
    userList.addAll(result);
    userTable.setItems(userList);
}

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) userTable.getScene().getWindow();
        stage.close();
    }
    
    @FXML
private void handleUpdate() {
    // Logic to update a user
}

@FXML
private void handleAdd() {
    // Open a dialog to add a new user
    TextInputDialog dialog = new TextInputDialog();
    dialog.setTitle("Add User");
    dialog.setHeaderText("Enter username:");
    Optional<String> result = dialog.showAndWait();
    result.ifPresent(username -> {
        // Add user to database (implement your logic)
        DB myObj = new DB();
        try {
            myObj.addDataToDB(username, "defaultPassword", "user");
            loadUsers(); // Refresh table
        } catch (Exception e) {
            e.printStackTrace();
        }
    });
}

@FXML
private void handleDelete() {
    User selectedUser = userTable.getSelectionModel().getSelectedItem();
    if (selectedUser != null) {
        // Delete user from database
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE name = ?")) {
            stmt.setString(1, selectedUser.getUser());
            stmt.executeUpdate();
            loadUsers(); // Refresh table
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

@FXML
private void handleBack() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/javafxapplication1/mainDashboard.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) userTable.getScene().getWindow();
        stage.setScene(new Scene(root, 1200, 800));
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
    }
}
}

