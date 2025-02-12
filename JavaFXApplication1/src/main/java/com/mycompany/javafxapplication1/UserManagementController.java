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

@FXML
public void initialize() {
    System.out.println("UserManagementController initialized!");
    setupTableColumns();
    checkAdminStatus();
    loadAppropriateUsers();

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
        System.out.println("Current User: " + User.currentUser.getUser() + ", Role: " + User.currentUser.getRole()); // Debug print
        adminMode.set(User.currentUser.isAdmin());
        System.out.println("Admin Mode Set To: " + adminMode.get()); // Debug print
    } else {
        System.out.println("No current user found!"); // Debug print
    }
}


    private void loadAppropriateUsers() {
    userList.clear();
    System.out.println("Admin Mode Check: " + adminMode.get()); // Debug print

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

    @FXML
    private void handleUpdateRole() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            createRoleDialog(selected).showAndWait().ifPresent(newRole -> 
                updateUserRole(selected.getUser(), newRole));
        }
    }

    private ChoiceDialog<String> createRoleDialog(User user) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("user", Arrays.asList("user", "admin"));
        dialog.setTitle("Update Role");
        dialog.setHeaderText("Changing role for: " + user.getUser());
        return dialog;
    }

    private void updateUserRole(String username, String newRole) {
        try {
            new DB().updateUserRole(username, newRole);
            loadAppropriateUsers();
            showSuccessAlert("Role Updated", "User role changed successfully!");
        } catch (SQLException | ClassNotFoundException e) {
            showErrorAlert("Update Failed", "Error updating role: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdatePassword() {
        createPasswordDialog().showAndWait().ifPresent(this::processPasswordChange);
    }

    private TextInputDialog createPasswordDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Enter new password for " + User.currentUser.getUser());
        return dialog;
    }

    private void processPasswordChange(String newPassword) {
        try {
            new DB().updateUserPassword(User.currentUser.getUser(), newPassword);
            showSuccessAlert("Password Updated", "Password changed successfully!");
        } catch (SQLException e) {
            showErrorAlert("Update Failed", "Error changing password: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteUser() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                new DB().deleteUser(selected.getUser());
                loadAppropriateUsers();
                showSuccessAlert("User Deleted", "User removed successfully!");
            } catch (SQLException | ClassNotFoundException e) {
                showErrorAlert("Deletion Failed", "Error deleting user: " + e.getMessage());
            }
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