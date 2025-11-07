package com.example.vault.gui;

import com.example.vault.Credential;
import com.example.vault.DBManager;
import com.example.vault.VaultManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javax.crypto.spec.SecretKeySpec;
import java.util.List;

public class VaultController {
    @FXML private TableView<Credential> credentialsTable;
    @FXML private TableColumn<Credential, Integer> idColumn;
    @FXML private TableColumn<Credential, String> serviceColumn;
    @FXML private TableColumn<Credential, String> usernameColumn;
    @FXML private TableColumn<Credential, String> passwordColumn;
    @FXML private TableColumn<Credential, Void> actionsColumn;
    
    private VaultManager vault;
    private SecretKeySpec aesKey;
    @FXML private TextField searchField;
    
    public void initialize(DBManager db, SecretKeySpec aesKey) {
        this.vault = new VaultManager(db);
        this.aesKey = aesKey;
        
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        serviceColumn.setCellValueFactory(new PropertyValueFactory<>("service"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        
        setupActionsColumn();
        refreshCredentials();
    }
    
    private void setupActionsColumn() {
        actionsColumn.setCellFactory(col -> new TableCell<Credential, Void>() {
            private final Button showButton = new Button("Show");
            private final Button copyButton = new Button("Copy");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttons = new HBox(5);
            
            {
                buttons.getChildren().addAll(showButton, copyButton, deleteButton);
                
                showButton.setOnAction(e -> {
                    Credential cred = (Credential) getTableRow().getItem();
                    if (cred != null) {
                        if (showButton.getText().equals("Show")) {
                            // Show password
                            passwordColumn.setCellFactory(col2 -> new TableCell<Credential, String>() {
                                @Override
                                protected void updateItem(String item, boolean empty) {
                                    super.updateItem(item, empty);
                                    if (empty) {
                                        setText(null);
                                    } else {
                                        Credential c = getTableView().getItems().get(getIndex());
                                        setText(c.id == cred.id ? c.password : "••••••••");
                                    }
                                }
                            });
                            showButton.setText("Hide");
                            
                            // Auto-hide after 10 seconds
                            new Thread(() -> {
                                try {
                                    Thread.sleep(10000);
                                    Platform.runLater(() -> {
                                        passwordColumn.setCellFactory(col2 -> new TableCell<Credential, String>() {
                                            @Override
                                            protected void updateItem(String item, boolean empty) {
                                                super.updateItem(item, empty);
                                                setText(empty ? null : "••••••••");
                                            }
                                        });
                                        showButton.setText("Show");
                                        credentialsTable.refresh();
                                    });
                                } catch (InterruptedException ignored) {}
                            }).start();
                        } else {
                            // Hide password
                            passwordColumn.setCellFactory(col2 -> new TableCell<Credential, String>() {
                                @Override
                                protected void updateItem(String item, boolean empty) {
                                    super.updateItem(item, empty);
                                    setText(empty ? null : "••••••••");
                                }
                            });
                            showButton.setText("Show");
                        }
                        credentialsTable.refresh();
                    }
                });
                
                copyButton.setOnAction(e -> {
                    Credential cred = (Credential) getTableRow().getItem();
                    if (cred != null) {
                        final Clipboard clipboard = Clipboard.getSystemClipboard();
                        final ClipboardContent content = new ClipboardContent();
                        content.putString(cred.password);
                        clipboard.setContent(content);
                        showInfo("Password copied to clipboard");
                        
                        // Clear clipboard after 30 seconds
                        new Thread(() -> {
                            try {
                                Thread.sleep(30000);
                                Platform.runLater(() -> {
                                    if (clipboard.getString() != null && 
                                        clipboard.getString().equals(cred.password)) {
                                        content.putString("");
                                        clipboard.setContent(content);
                                    }
                                });
                            } catch (InterruptedException ignored) {}
                        }).start();
                    }
                });
                
                deleteButton.setOnAction(e -> {
                    Credential cred = (Credential) getTableRow().getItem();
                    if (cred != null) {
                        try {
                            vault.delete(cred.id);
                            refreshCredentials();
                        } catch (Exception ex) {
                            showError("Delete failed: " + ex.getMessage());
                        }
                    }
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttons);
                }
            }
        });
    }
    
    @FXML
    private void handleAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vault/add-credential.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            AddCredentialController controller = loader.getController();
            controller.initialize(vault, aesKey, () -> {
                refreshCredentials();
                stage.close();
            });
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            showError("Could not open add dialog: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSearch() {
        try {
            String term = searchField.getText();
            System.out.println("Searching for term: " + term);
            
            if (term == null || term.trim().isEmpty()) {
                System.out.println("Empty search, showing all credentials");
                refreshCredentials(); // Show all if search is empty
                return;
            }
            term = term.trim();
            List<Credential> results = vault.search(term, aesKey);
            System.out.println("Found " + results.size() + " results");
            
            if (results.isEmpty()) {
                System.out.println("No results found, showing message");
                showInfo("No credentials found matching '" + term + "'");
            }
            credentialsTable.setItems(FXCollections.observableArrayList(results));
        } catch (Exception e) {
            e.printStackTrace();
            showError("Search failed: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleLogout() {
        // Clear sensitive data
        if (aesKey != null) {
            try {
                byte[] k = aesKey.getEncoded();
                for (int i = 0; i < k.length; i++) k[i] = 0;
            } catch (Exception ignored) {}
        }
        ((Stage) credentialsTable.getScene().getWindow()).close();
        VaultApp.showLoginScreen();
    }
    
    private void refreshCredentials() {
        try {
            List<Credential> all = vault.listAll(aesKey);
            System.out.println("Refreshing credentials, found " + all.size() + " entries");
            credentialsTable.setItems(FXCollections.observableArrayList(all));
            if (searchField != null) {
                searchField.clear(); // Clear search when refreshing
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not load credentials: " + e.getMessage());
        }
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }
    
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.showAndWait();
    }
}