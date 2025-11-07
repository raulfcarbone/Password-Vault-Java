package com.example.vault;

import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;
import java.util.List;

public class VaultManager {
    private final DBManager db;

    public VaultManager(DBManager db) {
        this.db = db;
    }

    public void addCredential(String service, String username, String plaintextPassword, SecretKeySpec aesKey) throws Exception {
        CryptoUtils.EncryptedData ed = CryptoUtils.encryptAESGCM(plaintextPassword.getBytes("UTF-8"), aesKey);
        db.addCredential(service, username, ed.cipherTextBase64, ed.ivBase64);
    }

    public List<Credential> listAll(SecretKeySpec aesKey) throws Exception {
        List<Credential> out = new ArrayList<>();
        for (DBManager.CredentialRecord r : db.listAllCredentials()) {
            byte[] pt = CryptoUtils.decryptAESGCM(r.cipherTextBase64, r.ivBase64, aesKey);
            out.add(new Credential(r.id, r.service, r.username, new String(pt, "UTF-8")));
        }
        return out;
    }

    public List<Credential> search(String term, SecretKeySpec aesKey) throws Exception {
        List<Credential> out = new ArrayList<>();
        for (DBManager.CredentialRecord r : db.searchCredentials(term)) {
            byte[] pt = CryptoUtils.decryptAESGCM(r.cipherTextBase64, r.ivBase64, aesKey);
            out.add(new Credential(r.id, r.service, r.username, new String(pt, "UTF-8")));
        }
        return out;
    }

    public boolean delete(int id) throws Exception {
        return db.deleteCredential(id);
    }
}
