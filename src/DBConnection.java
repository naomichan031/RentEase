
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
        "jdbc:mysql://localhost:3306/rentease_db";

    private static final String USER = "root";

    // Temporary local testing only.
    // Replace with your actual MySQL root password.
    private static final String PASSWORD =
        "jenniekim2025";

    public static Connection getConnection() {

        try {
            Connection connection = DriverManager.getConnection(
                URL, USER, PASSWORD
            );

            System.out.println("Database connected successfully!");

            return connection;

        } catch (SQLException e) {

            System.out.println("Database connection failed!");
            System.out.println(e.getMessage());

            return null;
        }
    }

    public static void main(String[] args) {

        Connection connection = getConnection();

        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connection closed successfully!");
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
