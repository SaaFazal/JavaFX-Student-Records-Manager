/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.javafxapplication1;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;
import javafx.scene.control.Button;

public class UserManagementController {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> passwordColumn;
    
    private final BooleanProperty adminMode = new SimpleBooleanProperty(false);
    private final ObservableList<User> userList = FXCollections.observableArrayList();
@FXML private Button addUserBtn, updateRoleBtn, deleteUserBtn, changePasswordBtn;

private String userRole;
    private String currentUser;

public void setUserRole(String role) {
        this.userRole = role;
        loadAllUsers();
    }

public void setCurrentUser(String user) {
    this.currentUser = user;
    loadAllUsers();  
}

@FXML
public void initialize() {
    System.out.println("UserManagementController initialized!");
    setupTableColumns();
    checkAdminStatus();
    loadAppropriateUsers();

    // Bind button visibility to adminMode
    addUserBtn.visibleProperty().bind(adminMode);
    updateRoleBtn.visibleProperty().bind(adminMode);
    deleteUserBtn.visibleProperty().bind(adminMode);
    changePasswordBtn.visibleProperty().bind(adminMode.not());
}

    private void setupTableColumns() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("user"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("pass"));
        passwordColumn.visibleProperty().bind(adminMode);
    }

    private void checkAdminStatus() {
    if (User.currentUser != null) {
        System.out.println("Current User: " + User.currentUser.getUser() + ", Role: " + User.currentUser.getRole());
        adminMode.set(User.currentUser.isAdmin()); // Set adminMode based on the current user's role
        System.out.println("Admin Mode Set To: " + adminMode.get());
    } else {
        System.out.println("No current user found!");
    }
}


    private void loadAppropriateUsers() {
    userList.clear();
    System.out.println("Admin Mode Check: " + adminMode.get());

    if (adminMode.get()) {
        loadAllUsers(); // Admins see all users
    } else {
        loadCurrentUser(); // Regular users see only themselves
    }
}



    private void loadAllUsers() {
    try (Connection conn = DB.getConnection();
         PreparedStatement stmt = conn.prepareStatement("SELECT name, password, role FROM users");
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            String name = rs.getString("name");
            String password = rs.getString("password");
            String role = rs.getString("role");
            System.out.println("User found: " + name + ", Role: " + role); // Debug print

            userList.add(new User(name, password, role));
        }

        System.out.println("Total users loaded: " + userList.size()); // Debug print
        userTable.setItems(userList);
        
    } catch (SQLException | ClassNotFoundException e) {
        showErrorAlert("Database Error", "Failed to load users: " + e.getMessage());
    }
}



    private void loadCurrentUser() {
        if (User.currentUser != null) {
            userList.add(User.currentUser);
            userTable.setItems(userList);
        }
    }

    @FXML
    private void handleAddUser() {
        Dialog<Pair<String, String>> dialog = createAddUserDialog();
        Optional<Pair<String, String>> result = dialog.showAndWait();
        result.ifPresent(this::processNewUser);
    }

    private Dialog<Pair<String, String>> createAddUserDialog() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Add New User");
        
        GridPane grid = new GridPane();
        TextField username = new TextField();
        PasswordField password = new PasswordField();
        ComboBox<String> role = new ComboBox<>(FXCollections.observableArrayList("user", "admin"));
        
        grid.addRow(0, new Label("Username:"), username);
        grid.addRow(1, new Label("Password:"), password);
        grid.addRow(2, new Label("Role:"), role);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button -> button == ButtonType.OK ? 
            new Pair<>(username.getText(), password.getText() + "|" + role.getValue()) : null);
        
        return dialog;
    }

    private void processNewUser(Pair<String, String> userData) {
        try {
            String[] parts = userData.getValue().split("\\|");
            new DB().addUser(userData.getKey(), parts[0], parts[1]);
            loadAppropriateUsers();
        } catch (Exception e) {
            showErrorAlert("Add User Failed", "Error creating user: " + e.getMessage());
        }
    }
private Dialog<String> createRoleDialog(User user) {
    Dialog<String> dialog = new Dialog<>();
    dialog.setTitle("Change Role");
    
    // If the current user is an admin, let them choose the role
    if (adminMode.get()) {
        // Allow admin to choose a new role from a ComboBox
        ComboBox<String> roleComboBox = new ComboBox<>(FXCollections.observableArrayList("user", "admin"));
        roleComboBox.setValue(user.getRole());  // Set the current role as the default

        dialog.getDialogPane().setContent(roleComboBox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return roleComboBox.getValue();  // Return the selected role
            }
            return null;
        });
    } else {
        // If it's a regular user, display an alert saying roles can't be changed
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Role Change Restricted");
        alert.setHeaderText("You cannot change the role for " + user.getUser());
        alert.setContentText("Roles are fixed and cannot be changed for regular users.");
        alert.showAndWait();
    }
    return dialog;
}


    @FXML
private void handleUpdateRole() {
    // Only allow admins to update roles
    if (adminMode.get()) {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Admin can change roles; we allow them to choose a new role.
            // For simplicity, let's say admins can choose between "user" and "admin" roles
            createRoleDialog(selected).showAndWait().ifPresent(newRole -> {
                try {
                    new DB().updateUserRole(selected.getUser(), newRole);
                    showSuccessAlert("Role Updated", "User role changed successfully!");
                    loadAppropriateUsers(); // Refresh the table after the role change
                } catch (SQLException | ClassNotFoundException e) {
                    showErrorAlert("Update Failed", "Error updating role: " + e.getMessage());
                }
            });
        } else {
            showErrorAlert("No User Selected", "Please select a user to update.");
        }
    } else {
        showErrorAlert("Permission Denied", "Only admins can update user roles.");
    }
}


    @FXML
private void handleUpdatePassword() {
    if (adminMode.get()) {
        // Admin can update any user's password
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            createPasswordDialog(selected.getUser()).showAndWait().ifPresent(newPassword -> {
                try {
                    new DB().updateUserPassword(selected.getUser(), newPassword);
                    showSuccessAlert("Password Updated", "Password changed successfully!");
                    loadAppropriateUsers(); // Refresh the table
                } catch (SQLException e) {
                    showErrorAlert("Update Failed", "Error changing password: " + e.getMessage());
                }
            });
        }
    } else {
        // Regular user can only update their own password
        createPasswordDialog(User.currentUser.getUser()).showAndWait().ifPresent(newPassword -> {
            try {
                new DB().updateUserPassword(User.currentUser.getUser(), newPassword);
                showSuccessAlert("Password Updated", "Password changed successfully!");
                loadAppropriateUsers(); // Refresh the table
            } catch (SQLException e) {
                showErrorAlert("Update Failed", "Error changing password: " + e.getMessage());
            }
        });
    }
}

private TextInputDialog createPasswordDialog(String username) {
    TextInputDialog dialog = new TextInputDialog();
    dialog.setTitle("Change Password");
    dialog.setHeaderText("Enter new password for " + username);
    return dialog;
}

    @FXML
private void handleDeleteUser() {
    if (adminMode.get()) {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                new DB().deleteUser(selected.getUser());
                showSuccessAlert("User Deleted", "User removed successfully!");
                loadAppropriateUsers(); // Refresh the table
            } catch (SQLException | ClassNotFoundException e) {
                showErrorAlert("Deletion Failed", "Error deleting user: " + e.getMessage());
            }
        }
    } else {
        showErrorAlert("Permission Denied", "Only admins can delete users.");
    }
}

    @FXML
    private void handleBack() {
        ((Stage) userTable.getScene().getWindow()).close();
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public BooleanProperty adminModeProperty() {
    return adminMode;
}


}