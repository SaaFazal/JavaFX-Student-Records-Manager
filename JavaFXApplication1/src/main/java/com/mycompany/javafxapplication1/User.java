/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javafxapplication1;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author ntu-user
 */
public class User {
    private SimpleStringProperty user;
    private SimpleStringProperty pass;
    private SimpleStringProperty role; // Admin or Regular User

    // Static field to hold the currently logged-in user
    public static User currentUser = null;

    public User(String user, String pass, String role) {
    this.user = new SimpleStringProperty(user);
    this.pass = new SimpleStringProperty(pass);
    this.role = new SimpleStringProperty(role);
}


    public String getUser() {
        return user.get();
    }

    public void setUser(String user) {
        this.user.set(user);
    }

    public String getPass() {
        return pass.get();
    }

    public void setPass(String pass) {
        this.pass.set(pass);
    }

    public String getRole() {
        return role.get();
    }

    public void setRole(String role) {
        this.role.set(role);
    }

    // Check if the user is an admin
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(role.get());
    }

    // Method to log in a user
    public static void loginUser(User user) {
        currentUser = user; // Set the current user to the passed user
    }

    // Method to log out the user
    public static void logoutUser() {
        currentUser = null; // Reset the current user when logged out
    }
}
