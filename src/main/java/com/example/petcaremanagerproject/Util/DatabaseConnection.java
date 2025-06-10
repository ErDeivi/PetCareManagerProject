package com.example.petcaremanagerproject.Util;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.file.Paths;

/**
 * Clase que gestiona la conexión y operaciones con la base de datos MySQL.
 * Proporciona métodos para establecer conexiones, exportar e importar copias de seguridad.
 */
public class DatabaseConnection {
    /** Driver de MySQL */
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    /** URL de conexión a la base de datos */
    private static final String URL = "jdbc:mysql://localhost:3306/petcaredb?useSSL=false&serverTimezone=UTC";
    /** Usuario de la base de datos */
    private static final String USER = "root";
    /** Contraseña de la base de datos */
    private static final String PASSWORD = "";
    
    /** Ruta al ejecutable mysqldump */
    private static final String MYSQLDUMP_PATH = "C:\\xampp\\mysql\\bin\\mysqldump.exe";
    /** Ruta al ejecutable mysql */
    private static final String MYSQL_PATH = "C:\\xampp\\mysql\\bin\\mysql.exe";

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error al cargar el driver de MySQL", e);
        }
    }
    
    /**
     * Establece una conexión con la base de datos.
     * @return Objeto Connection que representa la conexión establecida
     * @throws SQLException si ocurre un error al conectar con la base de datos
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Exporta la base de datos a un archivo SQL.
     * @return Ruta del archivo de respaldo generado
     * @throws IOException si ocurre un error al crear o escribir el archivo
     * @throws InterruptedException si el proceso de exportación es interrumpido
     */
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

    /**
     * Importa una base de datos desde un archivo SQL.
     * @param selectedFile Archivo SQL a importar
     * @throws IOException si ocurre un error al leer el archivo
     * @throws InterruptedException si el proceso de importación es interrumpido
     */
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
    
    /**
     * Obtiene la ruta de la carpeta donde se almacenan las copias de seguridad.
     * @return Ruta absoluta de la carpeta de respaldos
     */
    public static String getBackupFolderPath() {
        String userHome = System.getProperty("user.home");
        return Paths.get(userHome, "Documents", "copias de seguridad").toString();
    }
}