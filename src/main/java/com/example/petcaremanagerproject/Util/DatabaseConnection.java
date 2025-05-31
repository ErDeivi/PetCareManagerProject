package com.example.petcaremanagerproject.Util;

import java.io.*;
import java.sql.*;
import java.util.*;

public class DatabaseConnection {
    // MySQL connection details
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver"; // Corrected driver class name for modern MySQL Connector/J
    private static final String URL = "jdbc:mysql://localhost:3306/petcaredb?useSSL=false&serverTimezone=UTC"; // Added timezone
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Assume empty password for XAMPP default

    static {
        try {
            // Load MySQL driver
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            // Updated error message
            throw new RuntimeException("Error al cargar el driver de MySQL", e);
        }
    }
    
    public static Connection getConnection() throws SQLException {
        // Use MySQL connection details
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

}