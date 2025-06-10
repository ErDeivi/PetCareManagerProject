package com.example.petcaremanagerproject.Util;

import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Clase que gestiona la configuración del administrador del sistema.
 * Maneja el almacenamiento y recuperación segura de la contraseña del administrador
 * utilizando hashing BCrypt.
 */
public class AdminConfigManager {

    /** Nombre del archivo de configuración */
    private static final String CONFIG_FILE = "admin_config.properties";
    
    /** Clave para almacenar el hash de la contraseña en el archivo de propiedades */
    private static final String PASSWORD_HASH_KEY = "admin.password.hash";
    
    /** 
     * Contraseña por defecto si el archivo no existe.
     * ¡CAMBIAR ESTA CONTRASEÑA POR DEFECTO LA PRIMERA VEZ QUE EJECUTES LA APP!
     */
    private static final String DEFAULT_ADMIN_PASSWORD = "admin";

    /** Cache para almacenar el hash de la contraseña */
    private static String adminPasswordHash = null;

    /**
     * Carga el hash de la contraseña del administrador desde el archivo de configuración.
     * Si el archivo no existe, lo crea con un hash por defecto.
     * @return El hash de la contraseña del administrador
     * @throws RuntimeException si ocurre un error al leer o escribir el archivo de configuración
     */
    public static String loadAdminHash() {
        if (adminPasswordHash == null) {
            Properties props = new Properties();
            File configFile = new File(CONFIG_FILE);

            if (!configFile.exists()) {
                // Si el archivo no existe, crear uno con el hash por defecto
                System.out.println("Archivo de configuración no encontrado. Creando con contraseña por defecto.");
                String defaultHash = BCrypt.hashpw(DEFAULT_ADMIN_PASSWORD, BCrypt.gensalt());
                props.setProperty(PASSWORD_HASH_KEY, defaultHash);
                try (FileOutputStream fos = new FileOutputStream(configFile)) {
                    props.store(fos, "Admin Configuration");
                    adminPasswordHash = defaultHash;
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Error al crear archivo de configuración: " + e.getMessage());
                }
            } else {
                // Si el archivo existe, cargar el hash
                try (FileInputStream fis = new FileInputStream(configFile)) {
                    props.load(fis);
                    adminPasswordHash = props.getProperty(PASSWORD_HASH_KEY);
                    if (adminPasswordHash == null) {
                        // Si el hash no está en el archivo (archivo vacío o corrupto)
                        System.out.println("Hash de administrador no encontrado en el archivo. Creando con contraseña por defecto.");
                        String defaultHash = BCrypt.hashpw(DEFAULT_ADMIN_PASSWORD, BCrypt.gensalt());
                        props.setProperty(PASSWORD_HASH_KEY, defaultHash);
                        try (FileOutputStream fos = new FileOutputStream(configFile)) {
                            props.store(fos, "Admin Configuration");
                            adminPasswordHash = defaultHash;
                        } catch (IOException e) {
                            e.printStackTrace();
                            throw new RuntimeException("Error al actualizar archivo de configuración: " + e.getMessage());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Error al leer archivo de configuración: " + e.getMessage());
                }
            }
        }
        return adminPasswordHash;
    }

    /**
     * Guarda un nuevo hash para la contraseña del administrador en el archivo de configuración.
     * @param newHash El nuevo hash a guardar
     * @throws RuntimeException si ocurre un error al guardar el archivo de configuración
     */
    public static void saveAdminHash(String newHash) {
        Properties props = new Properties();
        // Cargar las propiedades existentes (si hay otras, aunque solo tengamos el hash por ahora)
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            props.load(fis);
        } catch (IOException e) {
            System.out.println("Error al cargar archivo para guardar nuevo hash: " + e.getMessage());
            // Continuar para intentar guardar aunque no se haya podido cargar
        }

        props.setProperty(PASSWORD_HASH_KEY, newHash);
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            props.store(fos, "Admin Configuration");
            adminPasswordHash = newHash; // Actualizar cache
            System.out.println("Nuevo hash de administrador guardado exitosamente.");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al guardar nuevo hash: " + e.getMessage());
        }
    }

    /**
     * Genera un nuevo hash BCrypt para una contraseña dada.
     * Útil para el primer inicio o si se olvida la contraseña.
     * @param password La contraseña a hashear
     * @return El hash generado
     */
    public static String generateHash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}