package com.example.hsm_demo.configs;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Pkcs11ConfigLoader {

    public static String loadConfig() {

        try (InputStream in = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("hsm/pkcs11.cfg")) {

            if (in == null) {
                throw new RuntimeException("pkcs11.cfg not found in resources/hsm/");
            }

            File temp = File.createTempFile("pkcs11-", ".cfg");
            temp.deleteOnExit();

            Files.copy(in, temp.toPath(), StandardCopyOption.REPLACE_EXISTING);

            System.out.println("PKCS11 config loaded: " + temp.getAbsolutePath());

            return temp.getAbsolutePath();

        } catch (Exception e) {
            throw new RuntimeException("Failed to load PKCS11 config", e);
        }
    }
}