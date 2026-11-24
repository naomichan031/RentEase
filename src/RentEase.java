
import javax.swing.*;
import java.awt.*;

public class RentEase {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("RentEase - Equipment Rental Management System");

            frame.setSize(900, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);

            JLabel title = new JLabel(
                "Welcome to RentEase",
                SwingConstants.CENTER
            );

            title.setFont(new Font("Arial", Font.BOLD, 28));

            JLabel subtitle = new JLabel(
                "Equipment Rental Management System",
                SwingConstants.CENTER
            );

            subtitle.setFont(new Font("Arial", Font.PLAIN, 16));

            JPanel panel = new JPanel(new GridLayout(2, 1));
            panel.add(title);
            panel.add(subtitle);

            frame.add(panel);
            frame.setVisible(true);

        });
    }
}
