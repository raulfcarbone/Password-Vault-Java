package com.example.vault;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBManager implements AutoCloseable {
    private final Connection conn;

    public DBManager(String dbPath) throws SQLException {
        String url = "jdbc:sqlite:" + dbPath;
        this.conn = DriverManager.getConnection(url);
        conn.setAutoCommit(true); // Ensure auto-commit is on
        initSchema();
    }

    private void initSchema() throws SQLException {
        try (Statement s = conn.createStatement()) {
            s.execute("CREATE TABLE IF NOT EXISTS meta (id INTEGER PRIMARY KEY, auth_salt BLOB, auth_hash BLOB, auth_iterations INTEGER, aes_salt BLOB, aes_iterations INTEGER)");
            s.execute("CREATE TABLE IF NOT EXISTS credentials (id INTEGER PRIMARY KEY AUTOINCREMENT, service TEXT NOT NULL, username TEXT, password TEXT, iv TEXT, created_at INTEGER)");
        }
    }

    public boolean hasUserMeta() throws SQLException {
        try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM meta")) {
            if (rs.next()) return rs.getInt(1) > 0;
            return false;
        }
    }

    public void saveUserMeta(byte[] authSalt, byte[] authHash, int authIterations, byte[] aesSalt, int aesIterations) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO meta (auth_salt, auth_hash, auth_iterations, aes_salt, aes_iterations) VALUES (?, ?, ?, ?, ?)")) {
            ps.setBytes(1, authSalt);
            ps.setBytes(2, authHash);
            ps.setInt(3, authIterations);
            ps.setBytes(4, aesSalt);
            ps.setInt(5, aesIterations);
            ps.executeUpdate();
        }
    }

    public UserMeta loadUserMeta() throws SQLException {
        try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery("SELECT auth_salt, auth_hash, auth_iterations, aes_salt, aes_iterations FROM meta LIMIT 1")) {
            if (rs.next()) {
                return new UserMeta(rs.getBytes(1), rs.getBytes(2), rs.getInt(3), rs.getBytes(4), rs.getInt(5));
            }
            return null;
        }
    }

    public void addCredential(String service, String username, String cipherTextBase64, String ivBase64) throws SQLException {
        System.out.println("Adding credential to database for service: " + service);
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO credentials (service, username, password, iv, created_at) VALUES (?, ?, ?, ?, strftime('%s','now'))")) {
            ps.setString(1, service);
            ps.setString(2, username);
            ps.setString(3, cipherTextBase64);
            ps.setString(4, ivBase64);
            int rows = ps.executeUpdate();
            System.out.println("Insert affected " + rows + " rows");
            // Removed explicit commit since we're using auto-commit
        }
        System.out.println("Credential added successfully to database");
    }

    public List<CredentialRecord> listAllCredentials() throws SQLException {
        List<CredentialRecord> out = new ArrayList<>();
        try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery("SELECT id, service, username, password, iv FROM credentials ORDER BY id")) {
            while (rs.next()) {
                out.add(new CredentialRecord(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)));
            }
        }
        return out;
    }

    public List<CredentialRecord> searchCredentials(String term) throws SQLException {
        List<CredentialRecord> out = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement("SELECT id, service, username, password, iv FROM credentials WHERE lower(service) LIKE lower(?) OR lower(username) LIKE lower(?) ORDER BY id")) {
            String like = "%" + term + "%";
            System.out.println("Executing search query with term: " + like);
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new CredentialRecord(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)));
                }
            }
        }
        System.out.println("Search found " + out.size() + " records in database");
        return out;
    }

    public boolean deleteCredential(int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM credentials WHERE id = ?")) {
            ps.setInt(1, id);
            int r = ps.executeUpdate();
            return r > 0;
        }
    }

    @Override
    public void close() throws Exception {
        if (conn != null && !conn.isClosed()) conn.close();
    }

    public static class UserMeta {
        public final byte[] authSalt;
        public final byte[] authHash;
        public final int authIterations;
        public final byte[] aesSalt;
        public final int aesIterations;

        public UserMeta(byte[] authSalt, byte[] authHash, int authIterations, byte[] aesSalt, int aesIterations) {
            this.authSalt = authSalt;
            this.authHash = authHash;
            this.authIterations = authIterations;
            this.aesSalt = aesSalt;
            this.aesIterations = aesIterations;
        }
    }

    public static class CredentialRecord {
        public final int id;
        public final String service;
        public final String username;
        public final String cipherTextBase64;
        public final String ivBase64;

        public CredentialRecord(int id, String service, String username, String cipherTextBase64, String ivBase64) {
            this.id = id;
            this.service = service;
            this.username = username;
            this.cipherTextBase64 = cipherTextBase64;
            this.ivBase64 = ivBase64;
        }
    }
}
