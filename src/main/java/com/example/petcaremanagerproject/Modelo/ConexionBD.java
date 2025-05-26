package com.example.petcaremanagerproject.Modelo;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConexionBD {
    private static final String URL = "jdbc:mysql://localhost:3306/pirristation";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private String findMySQLExecutable(String executableName) {
        String[] possiblePaths = {
            "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\" + executableName,
            "C:\\Program Files\\MySQL\\MySQL Server 5.7\\bin\\" + executableName,
            "C:\\xampp\\mysql\\bin\\" + executableName,
            "C:\\wamp64\\bin\\mysql\\mysql8.0.31\\bin\\" + executableName,
            "C:\\wamp\\bin\\mysql\\mysql5.7.36\\bin\\" + executableName
        };
        for (String path : possiblePaths) {
            if (new File(path).exists()) {
                return path;
            }
        }
        return null;
    }
    
    public boolean exportarBaseDatos(String rutaArchivo) {
        try {
            String mysqldumpPath = findMySQLExecutable("mysqldump.exe");
            if (mysqldumpPath == null) {
                throw new IOException("No se pudo encontrar mysqldump.exe. Asegúrate de tener MySQL instalado.");
            }
            ProcessBuilder pb = new ProcessBuilder(
                mysqldumpPath,
                "-u" + USER,
                PASSWORD.isEmpty() ? "" : "-p" + PASSWORD,
                "--add-drop-database",
                "-B",
                "pirristation",
                "-r",
                rutaArchivo
            );
            pb.command().removeIf(String::isEmpty);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                    System.out.println(line);
                }
            }
            int processComplete = p.waitFor();
            if (processComplete != 0) {
                throw new IOException("Error al exportar: " + output.toString());
            }
            return true;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.err.println("Error detallado: " + e.getMessage());
            return false;
        }
    }
    public boolean importarBaseDatos(String rutaArchivo) {
        try {
            String mysqlPath = findMySQLExecutable("mysql.exe");
            if (mysqlPath == null) {
                throw new IOException("No se pudo encontrar mysql.exe. Asegúrate de tener MySQL instalado.");
            }
            File file = new File(rutaArchivo);
            if (!file.exists()) {
                throw new IOException("No se encuentra el archivo de backup: " + rutaArchivo);
            }
            ProcessBuilder pb = new ProcessBuilder(
                mysqlPath,
                "-u" + USER,
                PASSWORD.isEmpty() ? "" : "-p" + PASSWORD,
                "pirristation"
            );
            pb.command().removeIf(String::isEmpty);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
                 BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.newLine();
                }
                writer.flush();
                writer.close(); // Importante cerrar el writer para que el proceso reciba EOF
            }
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                    System.out.println(line);
                }
            }
            int processComplete = p.waitFor();
            if (processComplete != 0) {
                throw new IOException("Error al importar: " + output.toString());
            }
            return true;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.err.println("Error detallado: " + e.getMessage());
            return false;
        }
    }
    public Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}