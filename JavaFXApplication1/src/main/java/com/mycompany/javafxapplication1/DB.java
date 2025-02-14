/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
package com.mycompany.javafxapplication1;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 *
 * @author ntu-user
 */
public class DB {
    private static final String fileName = "jdbc:sqlite:comp20081.db";

    private int timeout = 30;
    private String dataBaseName = "COMP20081";
    private String dataBaseTableName = "Users";
    Connection connection = null;
    private Random random = new SecureRandom();
    private String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private int iterations = 10000;
    private int keylength = 256;
    private String saltValue;
    
    private static final String url = "jdbc:mysql://mysql-server:3306/Details";
    private static final String username = "root";
    private static final String password = "password";  // Replace with your MySQL password
    private int mysqltimeout = 30;
    private String mysqldataBaseTableName = "Users";
    Connection cmysqlonnection = null;
    /**
     * @brief constructor - generates the salt if it doesn't exist or loads it from the file .salt
     */
    DB() {
        try {
            File fp = new File(".salt");
            if (!fp.exists()) {
                saltValue = this.getSaltvalue(30);
                FileWriter myWriter = new FileWriter(fp);
                myWriter.write(saltValue);
                myWriter.close();
            } else {
                Scanner myReader = new Scanner(fp);
                while (myReader.hasNextLine()) {
                    saltValue = myReader.nextLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        
    /**
     * @brief create a new table
     * @param tableName name of type String
     */
    public void createTable(String tableName) throws ClassNotFoundException {
        try {
            // create a database connection
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(fileName);
            var statement = connection.createStatement();
            statement.setQueryTimeout(mysqltimeout);
            statement.executeUpdate("create table if not exists " + tableName + "(id integer primary key autoincrement, name string, password string, role string)");

        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * @brief delete table
     * @param tableName of type String
     */
    public void delTable(String tableName) throws ClassNotFoundException {
        try {
            // create a database connection
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(fileName);
            var statement = connection.createStatement();
            statement.setQueryTimeout(mysqltimeout);
            statement.executeUpdate("drop table if exists " + tableName);
        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }
    public static Connection getMySQLConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * @brief add data to the database method
     * @param user name of type String
     * @param password of type String
     * @param role of type String
     */
    public void addDataToDB(String user, String password, String role) throws InvalidKeySpecException, ClassNotFoundException {
    try {
        System.out.println("Connecting to database..."); // Log database connection
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection(fileName);
        System.out.println("Database connection established."); // Log successful connection

        var statement = connection.createStatement();
        statement.setQueryTimeout(mysqltimeout);

        System.out.println("Inserting user into database..."); // Log insertion attempt
        statement.executeUpdate("insert into " + dataBaseTableName + " (name, password, role) values('" 
                              + user + "','" + generateSecurePassword(password) + "','" + role + "')");
        System.out.println("User inserted successfully."); // Log successful insertion
    } catch (SQLException ex) {
        System.err.println("SQL Error: " + ex.getMessage()); // Log SQL errors
        Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Database connection closed."); // Log connection closure
            }
        } catch (SQLException ex) {
            System.err.println("Error closing connection: " + ex.getMessage()); // Log closure errors
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

    /**
     * @brief get data from the Database method
     * @return results as ObservableList<User>
     */
    public ObservableList<User> getDataFromTable() throws ClassNotFoundException {
    ObservableList<User> result = FXCollections.observableArrayList();
    try {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection(fileName);
        var statement = connection.createStatement();
        statement.setQueryTimeout(mysqltimeout);
        ResultSet rs = statement.executeQuery("select * from " + this.dataBaseTableName);
        while (rs.next()) {
            // Use the constructor that takes user, pass, and role, as defined in User class
            result.add(new User(rs.getString("name"), rs.getString("password"), rs.getString("role")));
        }

    } catch (SQLException ex) {
        Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            // connection close failed.
            System.err.println(e.getMessage());
        }
    }
    return result;
}


    /**
     * @brief decode password method
     * @param user name as type String
     * @param pass plain password of type String
     * @return true if the credentials are valid, otherwise false
     */
    public boolean validateUser(String user, String pass) throws Exception {
    String sql = "SELECT password FROM users WHERE name = ?";
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, user);
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            String storedHash = rs.getString("password");
            String inputHash = generateSecurePassword(pass);
            return storedHash.equals(inputHash);
        }
        return false;
    }
}

    /**
     * @brief get the role of a user from the database
     * @param username name of the user
     * @return role of the user as String
     */
    public String getUserRole(String username) throws Exception {
    String sql = "SELECT role FROM users WHERE name = ?";
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, username);
        ResultSet rs = pstmt.executeQuery();
        return rs.next() ? rs.getString("role") : null;
    }
}

    private String getSaltvalue(int length) {
        StringBuilder finalval = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            finalval.append(characters.charAt(random.nextInt(characters.length())));
        }

        return new String(finalval);
    }

    private byte[] hash(char[] password, byte[] salt) throws InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keylength);
        Arrays.fill(password, Character.MIN_VALUE);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
        } finally {
            spec.clearPassword();
        }
    }

    public String generateSecurePassword(String password) throws InvalidKeySpecException {
        String finalval = null;

        byte[] securePassword = hash(password.toCharArray(), saltValue.getBytes());

        finalval = Base64.getEncoder().encodeToString(securePassword);

        return finalval;
    }

    public String getTableName() {
        return this.dataBaseTableName;
    }

    public void log(String message) {
        System.out.println(message);
    }
    public static Connection getConnection() throws ClassNotFoundException, SQLException {
    Class.forName("org.sqlite.JDBC");
    return DriverManager.getConnection(fileName);
}
  
public void addUser(String username, String password, String role) throws SQLException {
    String sql = "INSERT INTO users (name, password, role) VALUES (?, ?, ?)";
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, username);
        pstmt.setString(2, generateSecurePassword(password));
        pstmt.setString(3, role);
        pstmt.executeUpdate();
    } catch (InvalidKeySpecException | ClassNotFoundException e) {
        throw new SQLException("Password hashing failed", e);
    }
}

public void updateUserRole(String username, String newRole) throws SQLException, ClassNotFoundException {
    String query = "UPDATE users SET role = ? WHERE name = ?";
    try (Connection conn = DB.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, newRole);
        stmt.setString(2, username);
        stmt.executeUpdate();
    }
}


public void deleteUser(String username) throws SQLException, ClassNotFoundException {
    String sql = "DELETE FROM users WHERE name = ?";
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, username);
        pstmt.executeUpdate();
    }
}

    public void updateUserPassword(String username, String newPassword) throws SQLException {
    String sql = "UPDATE users SET password = ? WHERE name = ?";
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, generateSecurePassword(newPassword));
        pstmt.setString(2, username);
        pstmt.executeUpdate();
    } catch (InvalidKeySpecException | ClassNotFoundException e) {
        throw new SQLException("Password update failed", e);
    }
}
    public void updateUserName(String oldName, String newName) throws SQLException, ClassNotFoundException {
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement("UPDATE users SET name = ? WHERE name = ?")) {

        stmt.setString(1, newName);
        stmt.setString(2, oldName);
        stmt.executeUpdate();
    }
}
}





//    public static void main(String[] args) throws InvalidKeySpecException {
//        DB myObj = new DB();
//        myObj.log("-------- Simple Tutorial on how to make JDBC connection to SQLite DB ------------");
//        myObj.log("\n---------- Drop table ----------");
//        myObj.delTable(myObj.getTableName());
//        myObj.log("\n---------- Create table ----------");
//        myObj.createTable(myObj.getTableName());
//        myObj.log("\n---------- Adding Users ----------");
//        myObj.addDataToDB("ntu-user", "12z34");
//        myObj.addDataToDB("ntu-user2", "12yx4");
//        myObj.addDataToDB("ntu-user3", "a1234");
//        myObj.log("\n---------- get Data from the Table ----------");
//        myObj.getDataFromTable(myObj.getTableName());
//        myObj.log("\n---------- Validate users ----------");
//        String[] users = new String[]{"ntu-user", "ntu-user", "ntu-user1"};
//        String[] passwords = new String[]{"12z34", "1235", "1234"};
//        String[] messages = new String[]{"VALID user and password",
//            "VALID user and INVALID password", "INVALID user and VALID password"};
//
//        for (int i = 0; i < 3; i++) {
//            System.out.println("Testing " + messages[i]);
//            if (myObj.validateUser(users[i], passwords[i], myObj.getTableName())) {
//                myObj.log("++++++++++VALID credentials!++++++++++++");
//            } else {
//                myObj.log("----------INVALID credentials!----------");
//            }
//        }
//    }


//    public static void main(String[] args) throws InvalidKeySpecException {
//        DB myObj = new DB();
//        myObj.log("-------- Simple Tutorial on how to make JDBC connection to SQLite DB ------------");
//        myObj.log("\n---------- Drop table ----------");
//        myObj.delTable(myObj.getTableName());
//        myObj.log("\n---------- Create table ----------");
//        myObj.createTable(myObj.getTableName());
//        myObj.log("\n---------- Adding Users ----------");
//        myObj.addDataToDB("ntu-user", "12z34");
//        myObj.addDataToDB("ntu-user2", "12yx4");
//        myObj.addDataToDB("ntu-user3", "a1234");
//        myObj.log("\n---------- get Data from the Table ----------");
//        myObj.getDataFromTable(myObj.getTableName());
//        myObj.log("\n---------- Validate users ----------");
//        String[] users = new String[]{"ntu-user", "ntu-user", "ntu-user1"};
//        String[] passwords = new String[]{"12z34", "1235", "1234"};
//        String[] messages = new String[]{"VALID user and password",
//            "VALID user and INVALID password", "INVALID user and VALID password"};
//
//        for (int i = 0; i < 3; i++) {
//            System.out.println("Testing " + messages[i]);
//            if (myObj.validateUser(users[i], passwords[i], myObj.getTableName())) {
//                myObj.log("++++++++++VALID credentials!++++++++++++");
//            } else {
//                myObj.log("----------INVALID credentials!----------");
//            }
//        }
//    }

