package com.example.vault;

import com.example.vault.gui.VaultController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.crypto.spec.SecretKeySpec;

public class VaultApp extends Application {
    private static Stage primaryStage;
    private static DBManager dbManager;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        showLoginScreen();
        primaryStage.show();
    }

    public static void showLoginScreen() {
        try {
            if (dbManager == null) {
                dbManager = new DBManager("vault.db");
            }
            
            FXMLLoader loader = new FXMLLoader(VaultApp.class.getResource("/com/example/vault/login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage loginStage = new Stage();
            loginStage.setScene(scene);
            loginStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void showVault(DBManager db, SecretKeySpec key) {
        try {
            FXMLLoader loader = new FXMLLoader(VaultApp.class.getResource("/com/example/vault/vault.fxml"));
            Scene scene = new Scene(loader.load());
            VaultController controller = loader.getController();
            controller.initialize(db, key);
            Stage vaultStage = new Stage();
            vaultStage.setScene(scene);
            vaultStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}