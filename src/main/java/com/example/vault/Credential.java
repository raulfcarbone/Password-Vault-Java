package com.example.vault;

public class Credential {
    public final int id;
    public final String service;
    public final String username;
    public final String password; // decrypted plaintext password

    public Credential(int id, String service, String username, String password) {
        this.id = id;
        this.service = service;
        this.username = username;
        this.password = password;
    }
}
