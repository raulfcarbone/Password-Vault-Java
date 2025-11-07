package com.example.vault.gui;

import com.example.vault.AuthManager;
import com.example.vault.DBManager;
import com.example.vault.VaultApp;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javax.crypto.spec.SecretKeySpec;
import javafx.stage.Stage;

public class LoginController {
    @FXML private Label titleLabel;
    @FXML private Label messageLabel;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmField;
    @FXML private Label confirmLabel;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    
    private DBManager db;
    private boolean isRegistering = false;
    
    @FXML
    public void initialize() {
        try {
            db = new DBManager("vault.db");
            if (!db.hasUserMeta()) {
                switchToRegister();
            }
        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleLogin() {
        if (isRegistering) {
            String pass = passwordField.getText();
            String confirm = confirmField.getText();
            if (!pass.equals(confirm)) {
                messageLabel.setText("Passwords don't match!");
                return;
            }
            try {
                AuthManager.register(pass.toCharArray(), db);
                messageLabel.setText("Registration successful! Please login.");
                switchToLogin();
            } catch (Exception e) {
                messageLabel.setText("Registration failed: " + e.getMessage());
            }
            return;
        }
        
        try {
            SecretKeySpec key = AuthManager.login(passwordField.getText().toCharArray(), db);
            if (key == null) {
                messageLabel.setText("Invalid password!");
                return;
            }
            VaultApp.showVault(db, key);
            ((Stage) passwordField.getScene().getWindow()).close();
        } catch (Exception e) {
            messageLabel.setText("Login failed: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleRegister() {
        if (!isRegistering) {
            switchToRegister();
        }
    }
    
    private void switchToRegister() {
        isRegistering = true;
        titleLabel.setText("Register New Vault");
        confirmLabel.setVisible(true);
        confirmField.setVisible(true);
        loginButton.setText("Create Vault");
        registerButton.setVisible(false);
    }
    
    private void switchToLogin() {
        isRegistering = false;
        titleLabel.setText("Password Vault");
        confirmLabel.setVisible(false);
        confirmField.setVisible(false);
        loginButton.setText("Login");
        registerButton.setVisible(true);
        passwordField.clear();
        confirmField.clear();
    }
}