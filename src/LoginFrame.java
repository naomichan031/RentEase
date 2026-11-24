
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {

        setTitle("RentEase - Login");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 244, 248));

        JPanel card = new JPanel();
        card.setLayout(new GridLayout(7, 1, 10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(
            BorderFactory.createEmptyBorder(30, 35, 30, 35)
        );

        JLabel title = new JLabel(
            "RentEase", SwingConstants.CENTER
        );
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(new Color(31, 58, 95));

        JLabel subtitle = new JLabel(
            "Equipment Rental Management System",
            SwingConstants.CENTER
        );

        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 15));

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 15));

        JButton loginButton = new JButton("LOGIN");
        loginButton.setBackground(new Color(31, 58, 95));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 15));
        loginButton.setFocusPainted(false);

        JLabel footer = new JLabel(
            "Authorized Administrator Access",
            SwingConstants.CENTER
        );

        card.add(title);
        card.add(subtitle);
        card.add(new JLabel("Username"));
        card.add(usernameField);
        card.add(new JLabel("Password"));
        card.add(passwordField);
        card.add(loginButton);

        mainPanel.add(card);
        add(mainPanel, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        loginButton.addActionListener(e -> authenticate());

        setVisible(true);
    }

    private void authenticate() {

        String username = usernameField.getText().trim();
        char[] password = passwordField.getPassword();

        if (username.isEmpty() || password.length == 0) {
            JOptionPane.showMessageDialog(
                this, "Please enter your username and password."
            );
            java.util.Arrays.fill(password, '\0');
            return;
        }

        String sql = """
            SELECT user_id, password_hash, full_name, role
            FROM users
            WHERE username = ?
            """;

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) {
                JOptionPane.showMessageDialog(
                    this, "Database connection failed."
                );
                return;
            }

            try (PreparedStatement stmt =
                    conn.prepareStatement(sql)) {

                stmt.setString(1, username);

                try (ResultSet rs = stmt.executeQuery()) {

                    if (rs.next()) {

                        String storedHash =
                            rs.getString("password_hash");

                        boolean valid =
                            PasswordUtil.verifyPassword(
                                password, storedHash
                            );

                        if (valid) {
                            int userId = rs.getInt("user_id");

                            String fullName = rs.getString("full_name");

                            String role = rs.getString("role");

                            if (!"ADMIN".equals(role)) {
                                JOptionPane.showMessageDialog(
                                    this, "Access denied."
                                );
                                return;
                            }

                            JOptionPane.showMessageDialog(
                                this,
                                "Welcome, " + fullName + "!"
                            );

                            dispose();

                            new DashboardFrame(fullName, userId);

                        } else {
                            JOptionPane.showMessageDialog(
                                this,
                                "Incorrect username or password."
                            );
                        }

                    } else {
                        JOptionPane.showMessageDialog(
                            this,
                            "Incorrect username or password."
                        );
                    }
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                this, "Login error: " + e.getMessage()
            );
        } finally {
            java.util.Arrays.fill(password, '\0');
            passwordField.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
