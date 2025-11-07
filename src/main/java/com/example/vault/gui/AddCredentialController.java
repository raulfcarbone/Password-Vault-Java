package com.example.vault.gui;

import com.example.vault.VaultManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javax.crypto.spec.SecretKeySpec;

public class AddCredentialController {
    @FXML private TextField serviceField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;
    
    private VaultManager vault;
    private SecretKeySpec aesKey;
    private Runnable onSaveCallback;
    
    public void initialize(VaultManager vault, SecretKeySpec aesKey, Runnable onSave) {
        this.vault = vault;
        this.aesKey = aesKey;
        this.onSaveCallback = onSave;
    }
    
    @FXML
    private void handleSave() {
        String service = serviceField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        if (service.isEmpty() || username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("All fields are required!");
            return;
        }
        
        try {
            System.out.println("Adding credential for service: " + service);
            vault.addCredential(service, username, password, aesKey);
            System.out.println("Credential added successfully");
            onSaveCallback.run();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Save failed: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleCancel() {
        serviceField.getScene().getWindow().hide();
    }
}