# Password Vault

A secure password manager built with Java and JavaFX that implements industry-standard encryption techniques to safely store and manage your credentials. This application provides a user-friendly graphical interface while maintaining high security standards through modern cryptographic methods.

## Project Overview

This password vault application is designed with security and usability in mind, implementing best practices for password management:

1. **Zero Knowledge Architecture**
   - Master password never stored
   - All data encrypted at rest
   - Secure key derivation with PBKDF2
   - Memory sanitization after use

2. **Modern Cryptography**
   - AES-256 in GCM mode for encryption
   - Authenticated encryption prevents tampering
   - Unique IV for each encrypted entry
   - PBKDF2 with high iteration count (200,000)

3. **User Experience**
   - JavaFX-based graphical interface
   - Password masking and auto-hide
   - Quick copy to clipboard
   - Search functionality
   - Intuitive credential management

## Project Structure

### Core Components

1. **Authentication Module (`AuthManager.java`)**
   - Handles user authentication
   - Implements PBKDF2 key derivation
   - Manages master password verification
   - Zero-knowledge design principles
   
2. **Cryptography Module (`CryptoUtils.java`)**
   - Implements AES-GCM encryption/decryption
   - Handles secure random number generation
   - Provides Base64 encoding utilities
   - Ensures proper IV management

3. **Database Module (`DBManager.java`)**
   - SQLite database management
   - Encrypted credential storage
   - Search functionality
   - Secure deletion capabilities

4. **Data Model (`Credential.java`)**
   - Represents stored credentials
   - Manages service, username, password data
   - Supports table view integration
   - Ensures proper data encapsulation

### GUI Components

1. **Main Application (`VaultApp.java`)**
   - JavaFX application entry point
   - Manages application lifecycle
   - Handles window management
   - Controls authentication flow

2. **Login Interface (`LoginController.java`)**
   - Manages user authentication
   - Handles vault creation
   - Implements secure password entry
   - Provides registration functionality

3. **Main Vault Interface (`VaultController.java`)**
   - Displays credential table
   - Implements password viewing logic
   - Manages clipboard operations
   - Handles credential searching

4. **Add Credential Dialog (`AddCredentialController.java`)**
   - New credential entry form
   - Input validation
   - Secure credential creation
   - User feedback handling

### Resource Files

1. **FXML Layouts**
   - `login.fxml`: Authentication interface
   - `vault.fxml`: Main application window
   - `add-credential.fxml`: New credential form

## Security Implementation

### Password Storage
1. **Master Password**
   - Never stored in any form
   - Used to derive two different keys:
     - Authentication key (verify login)
     - Encryption key (protect data)

2. **Credential Encryption**
   - AES-256-GCM encryption
   - Unique IV per password
   - Authenticated encryption
   - Protected against tampering

3. **Runtime Security**
   - Memory sanitization
   - Auto-hiding passwords
   - Clipboard clearing
   - Secure random number generation

## Database Schema

### Credentials Table
```sql
CREATE TABLE credentials (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    service TEXT NOT NULL,
    username TEXT,
    password TEXT,
    iv TEXT,
    created_at INTEGER
)
```

## Getting Started

For installation and usage instructions, please see [INSTALLATION.md](INSTALLATION.md).

## License

This project is open source and available under the MIT License.
