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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
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
        loadAppropriateUsers();
    }

public void setCurrentUser(String user) {
    if (user == null || user.isEmpty()) {
        System.out.println("❌ No user provided to UserManagementController.");
        return;
    }

    this.currentUser = user;
    this.userRole = User.currentUser.getRole(); // Get role from User class
    System.out.println("✅ UserManagementController received user: " + this.currentUser + ", Role: " + this.userRole);

    checkAdminStatus();
    loadAppropriateUsers();
}




@FXML
public void initialize() {
    System.out.println("UserManagementController initialized!");
    setupTableColumns();
    checkAdminStatus();

    if (User.currentUser == null) {
        System.out.println("No current user found!");
    } else {
        System.out.println("Current User: " + User.currentUser.getUser() + ", Role: " + User.currentUser.getRole());
    } boolean isAdmin = User.currentUser != null && User.currentUser.isAdmin();
    loadAppropriateUsers();
    addUserBtn.setVisible(isAdmin);
    updateRoleBtn.setVisible(isAdmin);
    deleteUserBtn.setVisible(isAdmin);
    changePasswordBtn.setVisible(true);

    
}


    private void setupTableColumns() {
    usernameColumn.setCellValueFactory(new PropertyValueFactory<>("user"));
    roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
    
    // Hide passwords for security
    passwordColumn.setCellValueFactory(cellData -> new SimpleStringProperty("******"));
    passwordColumn.setVisible(false);
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
    System.out.println("Admin Mode: " + adminMode.get());

    if (adminMode.get()) {
        loadAllUsers();  // Admins see all users
    } else {
        loadCurrentUser();  // Regular users see only themselves
    }

    System.out.println("Final user list size: " + userList.size());
    userTable.setItems(userList); // Ensure table updates
}




    private void loadAllUsers() {
    userList.clear(); // Clear old data

    try (Connection conn = DB.getConnection();
         PreparedStatement stmt = conn.prepareStatement("SELECT name, password, role FROM users");
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            String name = rs.getString("name");
            String password = rs.getString("password");
            String role = rs.getString("role");

            System.out.println("User found: " + name + ", Role: " + role); // Debugging
            userList.add(new User(name, password, role));
        }

        System.out.println("Total users loaded: " + userList.size()); // Debugging
        userTable.setItems(userList); // Update table data

    } catch (SQLException | ClassNotFoundException e) {
        showErrorAlert("Database Error", "Failed to load users: " + e.getMessage());
        e.printStackTrace();
    }
}




    private void loadCurrentUser() {
        if (User.currentUser != null) {
            userList.add(User.currentUser);
            userTable.setItems(userList);
        }
    }

    @FXML
private void handleUpdateName() {
    User selectedUser = userTable.getSelectionModel().getSelectedItem();
    
    if (selectedUser == null) {
        showErrorAlert("No User Selected", "Please select a user to update the name.");
        return;
    }

    // Allow admins to change any user's name; normal users can change their own name
    if (!User.currentUser.isAdmin() && !selectedUser.getUser().equals(User.currentUser.getUser())) {
        showErrorAlert("Permission Denied", "You can only update your own username.");
        return;
    }

    // Show input dialog to enter new name
    TextInputDialog dialog = new TextInputDialog(selectedUser.getUser());
    dialog.setTitle("Update Username");
    dialog.setHeaderText("Enter a new username for " + selectedUser.getUser());
    Optional<String> result = dialog.showAndWait();

    result.ifPresent(newName -> {
        if (newName.trim().isEmpty()) {
            showErrorAlert("Invalid Name", "Username cannot be empty.");
            return;
        }

        try {
            new DB().updateUserName(selectedUser.getUser(), newName);  // Update database
            selectedUser.setUser(newName);  // Update UI
            showSuccessAlert("Username Updated", "Username successfully changed!");
            loadAppropriateUsers();  // Refresh the user list
        } catch (SQLException e) {
            showErrorAlert("Update Failed", "Error updating username: " + e.getMessage());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(UserManagementController.class.getName()).log(Level.SEVERE, null, ex);
        }
    });
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
    if (!adminMode.get()) {
        showErrorAlert("Access Denied", "You cannot change roles.");
        return null; // Prevent dialog from even opening
    }

    Dialog<String> dialog = new Dialog<>();
    dialog.setTitle("Change Role");
    ComboBox<String> roleComboBox = new ComboBox<>(FXCollections.observableArrayList("user", "admin"));
    roleComboBox.setValue(user.getRole());

    dialog.getDialogPane().setContent(roleComboBox);
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

    dialog.setResultConverter(button -> button == ButtonType.OK ? roleComboBox.getValue() : null);
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
    User selected = userTable.getSelectionModel().getSelectedItem();
    
    if (selected == null) {
        showErrorAlert("No User Selected", "Please select a user.");
        return;
    }

    // Admin can update anyone's password, users can only update their own
    if (!adminMode.get() && !selected.getUser().equals(currentUser)) {
        showErrorAlert("Permission Denied", "You can only change your own password.");
        return;
    }

    createPasswordDialog(selected.getUser()).showAndWait().ifPresent(newPassword -> {
        try {
            new DB().updateUserPassword(selected.getUser(), newPassword);
            showSuccessAlert("Password Updated", "Password changed successfully!");
        } catch (SQLException e) {
            showErrorAlert("Update Failed", "Error changing password: " + e.getMessage());
        }
    });
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