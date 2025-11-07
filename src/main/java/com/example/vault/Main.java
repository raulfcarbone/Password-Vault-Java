package com.example.vault;

import javax.crypto.spec.SecretKeySpec;
import java.io.Console;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String DB_PATH = "vault.db";

    public static void main(String[] args) {
        try (DBManager db = new DBManager(DB_PATH)) {
            Scanner scanner = new Scanner(System.in);
            Console console = System.console();

            if (!db.hasUserMeta()) {
                System.out.println("No vault found. Please register a master password.");
                char[] pw = readPassword(console, scanner, "Enter new master password: ");
                char[] pw2 = readPassword(console, scanner, "Confirm master password: ");
                if (!String.valueOf(pw).equals(String.valueOf(pw2))) {
                    System.out.println("Passwords do not match. Exiting.");
                    return;
                }
                AuthManager.register(pw, db);
                zero(pw);
                zero(pw2);
                System.out.println("Registration complete. Restart to login.");
                return;
            }

            // login
            SecretKeySpec aesKey = null;
            for (int attempt = 0; attempt < 5 && aesKey == null; attempt++) {
                char[] pw = readPassword(console, scanner, "Enter master password: ");
                aesKey = AuthManager.login(pw, db);
                zero(pw);
                if (aesKey == null) System.out.println("Invalid password.");
            }
            if (aesKey == null) {
                System.out.println("Too many failed attempts. Exiting.");
                return;
            }

            VaultManager vault = new VaultManager(db);
            boolean running = true;
            while (running) {
                System.out.println();
                System.out.println("-- Password Vault --");
                System.out.println("1) Add Credential");
                System.out.println("2) View All Credentials");
                System.out.println("3) Search Credential");
                System.out.println("4) Delete Credential");
                System.out.println("5) Exit");
                System.out.print("Choose an option: ");
                String opt = scanner.nextLine().trim();

                switch (opt) {
                    case "1":
                        System.out.print("Service: ");
                        String service = scanner.nextLine().trim();
                        System.out.print("Username: ");
                        String username = scanner.nextLine().trim();
                        char[] pass = readPassword(console, scanner, "Password: ");
                        vault.addCredential(service, username, String.valueOf(pass), aesKey);
                        zero(pass);
                        System.out.println("Credential added.");
                        break;
                    case "2":
                        List<Credential> all = vault.listAll(aesKey);
                        for (Credential c : all) {
                            System.out.printf("%d) %s - %s - %s%n", c.id, c.service, c.username, c.password);
                        }
                        break;
                    case "3":
                        System.out.print("Search term: ");
                        String term = scanner.nextLine().trim();
                        List<Credential> found = vault.search(term, aesKey);
                        for (Credential c : found) {
                            System.out.printf("%d) %s - %s - %s%n", c.id, c.service, c.username, c.password);
                        }
                        break;
                    case "4":
                        System.out.print("Credential ID to delete: ");
                        String idStr = scanner.nextLine().trim();
                        try {
                            int id = Integer.parseInt(idStr);
                            if (vault.delete(id)) System.out.println("Deleted."); else System.out.println("Not found.");
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid id.");
                        }
                        break;
                    case "5":
                        running = false;
                        break;
                    default:
                        System.out.println("Unknown option.");
                }
            }

            // zero out AES key bytes if possible
            try {
                byte[] k = aesKey.getEncoded();
                for (int i = 0; i < k.length; i++) k[i] = 0;
            } catch (Exception ignored) {}

            System.out.println("Goodbye.");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static char[] readPassword(Console console, Scanner scanner, String prompt) {
        if (console != null) {
            return console.readPassword(prompt);
        }
        System.out.print(prompt);
        String s = scanner.nextLine();
        return s.toCharArray();
    }

    private static void zero(char[] a) {
        if (a == null) return;
        for (int i = 0; i < a.length; i++) a[i] = 0;
    }
}
