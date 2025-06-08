package com.example.petcaremanagerproject.Util;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.file.Paths;

public class DatabaseConnection {
    // MySQL connection details
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver"; // Corrected driver class name for modern MySQL Connector/J
    private static final String URL = "jdbc:mysql://localhost:3306/petcaredb?useSSL=false&serverTimezone=UTC"; // Added timezone
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Assume empty password for XAMPP default
    
    // TODO: Configurar estas rutas según tu instalación de MySQL/XAMPP
    private static final String MYSQLDUMP_PATH = "C:\\xampp\\mysql\\bin\\mysqldump.exe"; // Ejemplo
    private static final String MYSQL_PATH = "C:\\xampp\\mysql\\bin\\mysql.exe"; // Ejemplo

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

    public static String exportDatabase() throws IOException, InterruptedException {
        String userHome = System.getProperty("user.home");
        String documentsPath = Paths.get(userHome, "Documents").toString();
        String backupFolderPath = Paths.get(documentsPath, "copias de seguridad").toString();

        File backupDir = new File(backupFolderPath);
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = "petcareDB_" + timestamp + ".sql";
        String filePath = Paths.get(backupFolderPath, fileName).toString();

        String[] command = {MYSQLDUMP_PATH, "-u", USER, "petcaredb", "-r", filePath};
        
        ProcessBuilder pb = new ProcessBuilder(command);
        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new IOException("Error al exportar la base de datos. Código de salida: " + exitCode);
        }
        return filePath;
    }

    public static void importDatabase(File selectedFile) throws IOException, InterruptedException {
        String[] command = {MYSQL_PATH, "-u", USER, "petcaredb"};
        
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectInput(selectedFile);
        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new IOException("Error al importar la base de datos. Código de salida: " + exitCode);
        }
    }
    
    public static String getBackupFolderPath() {
        String userHome = System.getProperty("user.home");
        return Paths.get(userHome, "Documents", "copias de seguridad").toString();
    }
}