package com.example.petcaremanagerproject.Util;

import java.io.*;
import java.sql.*;
import java.util.*;

public class DatabaseConnection {
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost/pirristation?useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static final String DB_NAME = "pirristation";
    private static final String DB_URL = "jdbc:sqlite:petcare.db";
    
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error al cargar el driver de SQLite", e);
        }
    }
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private static String escapeString(String str) {
        if (str == null) return "NULL";
        return "'" + str.replace("\\", "\\\\")
                      .replace("'", "\\'")
                      .replace("\r", "\\r")
                      .replace("\n", "\\n")
                      .replace("\t", "\\t") + "'";
    }

    public static boolean exportarBaseDatos(String rutaArchivo) {
        try {
            File backupDir = new File("backups");
            if (!backupDir.exists()) {
                backupDir.mkdirs();
            }
            Connection conn = getConnection();
            if (conn == null) {
                throw new SQLException("No se pudo conectar a la base de datos");
            }
            try (PrintWriter writer = new PrintWriter(new FileWriter(rutaArchivo))) {
                writer.println("-- Backup de la base de datos " + DB_NAME);
                writer.println("-- Fecha: " + new java.util.Date());
                writer.println("\nSET FOREIGN_KEY_CHECKS=0;");
                writer.println("SET NAMES utf8mb4;");
                writer.println("SET CHARACTER SET utf8mb4;");
                writer.println("CREATE DATABASE IF NOT EXISTS `" + DB_NAME + "`;");
                writer.println("USE `" + DB_NAME + "`;");
                DatabaseMetaData metaData = conn.getMetaData();
                ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    System.out.println("Procesando tabla: " + tableName);
                    try (Statement stmt = conn.createStatement()) {
                        ResultSet rs = stmt.executeQuery("SHOW CREATE TABLE `" + tableName + "`");
                        if (rs.next()) {
                            writer.println("\n-- Estructura de la tabla `" + tableName + "`");
                            writer.println("DROP TABLE IF EXISTS `" + tableName + "`;");
                            writer.println(rs.getString(2) + ";\n");
                        }
                    }
                    try (Statement stmt = conn.createStatement()) {
                        ResultSet rs = stmt.executeQuery("SELECT * FROM `" + tableName + "`");
                        ResultSetMetaData rsmd = rs.getMetaData();
                        int numColumns = rsmd.getColumnCount();
                        List<String> columnNames = new ArrayList<>();
                        for (int i = 1; i <= numColumns; i++) {
                            columnNames.add(rsmd.getColumnName(i));
                        }
                        while (rs.next()) {
                            StringBuilder columns = new StringBuilder();
                            StringBuilder values = new StringBuilder();
                            for (int i = 1; i <= numColumns; i++) {
                                if (i > 1) {
                                    columns.append(",");
                                    values.append(",");
                                }
                                columns.append("`").append(columnNames.get(i-1)).append("`");
                                Object value = rs.getObject(i);
                                if (value == null) {
                                    values.append("NULL");
                                } else if (value instanceof Number) {
                                    values.append(value.toString());
                                } else {
                                    values.append(escapeString(value.toString()));
                                }
                            }
                            writer.println("INSERT INTO `" + tableName + "` (" + columns + ") VALUES (" + values + ");");
                        }
                        writer.println();
                    }
                }
                writer.println("SET FOREIGN_KEY_CHECKS=1;");
            }
            return true;
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.err.println("Error detallado: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean importarBaseDatos(String rutaArchivo) {
        try {
            File file = new File(rutaArchivo);
            if (!file.exists()) {
                throw new IOException("No se encuentra el archivo de backup: " + rutaArchivo);
            }
            Connection conn = getConnection();
            if (conn == null) {
                throw new SQLException("No se pudo conectar a la base de datos");
            }
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SET FOREIGN_KEY_CHECKS=0");
                stmt.execute("SET NAMES utf8mb4");
                stmt.execute("SET CHARACTER SET utf8mb4");
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo));
                 Statement stmt = conn.createStatement()) {
                StringBuilder query = new StringBuilder();
                String line;
                int lineNumber = 0;
                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    if (line.startsWith("--") || line.trim().isEmpty()) {
                        continue;
                    }
                    query.append(line);
                    if (line.trim().endsWith(";")) {
                        String sqlQuery = query.toString().trim();
                        System.out.println("Ejecutando query (línea " + lineNumber + "): " + sqlQuery);
                        try {
                            stmt.execute(sqlQuery);
                        } catch (SQLException e) {
                            System.err.println("Error en la línea " + lineNumber + ": " + sqlQuery);
                            System.err.println("Error SQL: " + e.getMessage());
                            // Continuar con la siguiente consulta en lugar de lanzar el error
                            System.err.println("Continuando con la siguiente consulta...");
                        }
                        query.setLength(0);
                    }
                }
            } finally {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("SET FOREIGN_KEY_CHECKS=1");
                }
            }
            return true;
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.err.println("Error detallado: " + e.getMessage());
            return false;
        }
    }

    public static void inicializarBaseDatos() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Crear tabla de clientes
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS clientes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    apellidos TEXT NOT NULL,
                    telefono TEXT NOT NULL,
                    email TEXT NOT NULL
                )
            """);
            
            // Crear tabla de cuidadores
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS cuidadores (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    apellidos TEXT NOT NULL,
                    telefono TEXT NOT NULL,
                    email TEXT NOT NULL,
                    especialidad TEXT NOT NULL,
                    disponibilidad TEXT NOT NULL
                )
            """);
            
            // Crear tabla de dueños
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS duenos (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    apellidos TEXT NOT NULL,
                    telefono TEXT NOT NULL,
                    email TEXT NOT NULL,
                    direccion TEXT NOT NULL
                )
            """);
            
            // Crear tabla de mascotas
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS mascotas (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    especie TEXT NOT NULL,
                    raza TEXT NOT NULL,
                    edad INTEGER NOT NULL,
                    peso REAL NOT NULL,
                    id_cliente INTEGER NOT NULL,
                    FOREIGN KEY (id_cliente) REFERENCES clientes(id)
                )
            """);
            
        } catch (SQLException e) {
            throw new RuntimeException("Error al inicializar la base de datos", e);
        }
    }
}