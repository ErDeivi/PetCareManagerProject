package com.example.petcaremanagerproject.Util;

import java.io.*;
import java.sql.*;
import java.util.*;

public class DatabaseConnection {
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost/petcaredb?useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static final String DB_NAME = "petcaredb";
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