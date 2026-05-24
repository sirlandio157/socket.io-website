package com.malalu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

@Service
public class CredentialService {

    private static final Path CREDENTIALS_FILE = Paths.get("credentials.properties");

    private final InMemoryUserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;
    private String currentUsername;

    @Autowired
    public CredentialService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;

        Properties props = new Properties();
        try {
            if (Files.exists(CREDENTIALS_FILE)) {
                try (InputStream is = Files.newInputStream(CREDENTIALS_FILE)) {
                    props.load(is);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível ler o arquivo de credenciais", e);
        }

        String username = props.getProperty("username", "MALALU");
        String encodedPassword = props.getProperty("password");
        if (encodedPassword == null) {
            encodedPassword = passwordEncoder.encode("malalu");
            props.setProperty("username", username);
            props.setProperty("password", encodedPassword);
            try (OutputStream os = Files.newOutputStream(CREDENTIALS_FILE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                props.store(os, "Credenciais iniciais (pode ser modificada via /admin/credentials)");
            } catch (IOException e) {
                throw new RuntimeException("Não foi possível gravar arquivo de credenciais", e);
            }
        }

        UserDetails user = User.withUsername(username)
                .password(encodedPassword)
                .roles("ADMIN")
                .build();

        this.userDetailsManager = new InMemoryUserDetailsManager(user);
        this.currentUsername = username;
    }

    public InMemoryUserDetailsManager getUserDetailsManager() {
        return userDetailsManager;
    }

    public String getUsername() {
        return currentUsername;
    }

    public synchronized void updateCredentials(String newUsername, String rawPassword) {
        String encoded = passwordEncoder.encode(rawPassword);
        UserDetails newUser = User.withUsername(newUsername)
                .password(encoded)
                .roles("ADMIN")
                .build();

        try {
            if (userDetailsManager.userExists(currentUsername)) {
                userDetailsManager.deleteUser(currentUsername);
            }
            userDetailsManager.createUser(newUser);
            currentUsername = newUsername;

            Properties props = new Properties();
            props.setProperty("username", newUsername);
            props.setProperty("password", encoded);
            try (OutputStream os = Files.newOutputStream(CREDENTIALS_FILE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                props.store(os, "Credenciais atuais");
            }
        } catch (IOException e) {
            throw new RuntimeException("Falha ao atualizar credenciais", e);
        }
    }
}
