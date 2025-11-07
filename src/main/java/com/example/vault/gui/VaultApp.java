package com.example.vault.gui;

import com.example.vault.DBManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.crypto.spec.SecretKeySpec;

public class VaultApp extends Application {
    private static Stage primaryStage;
    private static DBManager db;
    
    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        db = new DBManager("vault.db");
        showLoginScreen();
    }
    
    public static void showLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(VaultApp.class.getResource("/com/example/vault/login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Password Vault - Login");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public static void showMainScreen(SecretKeySpec aesKey) {
        try {
            FXMLLoader loader = new FXMLLoader(VaultApp.class.getResource("/com/example/vault/vault.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            VaultController controller = loader.getController();
            controller.initialize(db, aesKey);
            stage.setScene(scene);
            stage.setTitle("Password Vault");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void stop() {
        try {
            if (db != null) db.close();
        } catch (Exception ignored) {}
    }
}