
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.*;
import java.util.Arrays;

public class AdminSetup {

    public static void main(String[] args) {

        String fullName = JOptionPane.showInputDialog(
            null, "Enter administrator's full name:"
        );

        if (fullName == null || fullName.trim().isEmpty()) {
            return;
        }

        String username = JOptionPane.showInputDialog(
            null, "Create administrator username:"
        );

        if (username == null || username.trim().isEmpty()) {
            return;
        }

        JPasswordField passwordField = new JPasswordField();

        int option = JOptionPane.showConfirmDialog(
            null,
            passwordField,
            "Create administrator password:",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (option != JOptionPane.OK_OPTION) {
            return;
        }

        char[] password = passwordField.getPassword();

        if (password.length < 8) {
            JOptionPane.showMessageDialog(
                null,
                "Password must contain at least 8 characters."
            );
            Arrays.fill(password, '\0');
            return;
        }

        String hashedPassword =
            PasswordUtil.hashPassword(password);

        Arrays.fill(password, '\0');

        String sql = """
            INSERT INTO users
            (username, password_hash, full_name, role)
            VALUES (?, ?, ?, 'ADMIN')
            """;

        
Connection conn = DBConnection.getConnection();

if (conn == null) {
    JOptionPane.showMessageDialog(
        null,
        "Database connection unavailable."
    );
    return;
}

try (conn;
     PreparedStatement pstmt = conn.prepareStatement(sql)) {

    pstmt.setString(1, username.trim());
    pstmt.setString(2, hashedPassword);
    pstmt.setString(3, fullName.trim());

    pstmt.executeUpdate();

    JOptionPane.showMessageDialog(
        null,
        "Administrator account created successfully!"
    );

    System.out.println("Admin account registered!");

} catch (SQLException e) {

    JOptionPane.showMessageDialog(
        null,
        "Unable to create account:\n" + e.getMessage()
    );

    e.printStackTrace();
}

    }
}
