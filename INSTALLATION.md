# Installation Guide

## System Requirements
- Windows, macOS, or Linux operating system
- Java 8 or later with JavaFX (included in Oracle JDK 8)
- Maven for building
- 100MB free disk space

## Step-by-Step Installation

1. **Install Java 8 or Later**
   - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://adoptium.net/)
   - Run the installer for your operating system
   - Verify installation in terminal/command prompt:
     ```bash
     java -version
     ```

2. **Install Maven**
   - Download from [Maven website](https://maven.apache.org/download.cgi)
   - Follow [installation guide](https://maven.apache.org/install.html) for your OS
   - Add Maven to your PATH
   - Verify installation:
     ```bash
     mvn -version
     ```

3. **Get the Password Vault**
   - Clone the repository:
     ```bash
     git clone https://github.com/yourusername/password-vault-java.git
     cd password-vault-java
     ```
   - Or download and extract the ZIP file

4. **Build the Application**
   ```bash
   mvn clean package
   ```

5. **Run the Application**
   ```bash
   java -jar target/password-vault-1.0-SNAPSHOT.jar
   ```

## First Time Setup

1. **Launch the Application**
   - Double-click the generated JAR file, or
   - Run from terminal: `java -jar target/password-vault-1.0-SNAPSHOT.jar`

2. **Create Your Vault**
   - Enter your desired master password
   - Confirm the master password
   - Click "Create Vault"
   - The vault file (vault.db) will be created in the current directory

3. **Add Your First Password**
   - Click "Add New"
   - Enter the service name (e.g., "Gmail")
   - Enter your username
   - Enter your password
   - Click "Save"

## Daily Usage

1. **Start the Application**
   - Double-click the JAR file, or
   - Run from terminal as shown above

2. **Access Your Passwords**
   - Enter your master password
   - Click "Login"
   - View all stored credentials
   - Use search to filter entries

3. **Managing Passwords**
   - **View**: Click "Show" (auto-hides after 10 seconds)
   - **Copy**: Click "Copy" (clears clipboard after 30 seconds)
   - **Delete**: Click "Delete" to remove entries
   - **Search**: Type in the search box to filter

## Backup Instructions

1. **Locate Your Vault File**
   - Find `vault.db` in the application directory
   - This file contains all your encrypted passwords

2. **Create Regular Backups**
   - Copy `vault.db` to a secure location
   - Keep multiple backup copies
   - Store backups securely - they contain your encrypted passwords

3. **Restore from Backup**
   - Close the application
   - Replace `vault.db` with your backup copy
   - Restart the application

## Troubleshooting

1. **Application Won't Start**
   - Verify Java installation: `java -version`
   - Check for error messages in the terminal
   - Ensure JavaFX is included (part of Oracle Java 8)

2. **Can't Log In**
   - Check if `vault.db` exists in the current directory
   - Verify your master password
   - If using a new computer, copy your `vault.db` file

3. **Database Errors**
   - Ensure write permissions in the current directory
   - Check disk space
   - Verify SQLite support (included in JAR)

## Important Security Notes

1. **Master Password**
   - Choose a strong master password
   - Cannot be recovered if forgotten
   - Only you know your master password

2. **Data Security**
   - All passwords are encrypted with AES-GCM
   - Master password never stored, only a verification hash
   - Passwords auto-hide after viewing
   - Clipboard auto-clears after copying

3. **System Security**
   - Keep your system updated
   - Use antivirus protection
   - Protect against malware and keyloggers
   - Lock your computer when away