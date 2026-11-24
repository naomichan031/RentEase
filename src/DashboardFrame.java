
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class DashboardFrame extends JFrame {

    
    private final Color NAVY = new Color(25, 45, 75);
    private final Color BLUE = new Color(43, 91, 145);
    private final Color BACKGROUND = new Color(243, 246, 250);
    private final Color WHITE = Color.WHITE;
    private final Color TEXT = new Color(45, 55, 72);

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private String adminName;
    private int adminId;

    public DashboardFrame(String fullName, int userId) {

        adminName = fullName;
        adminId = userId;
        

        setTitle("RentEase - Dashboard");
        setSize(1200, 750);
        setMinimumSize(new Dimension(950, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // SIDEBAR
        add(createSidebar(), BorderLayout.WEST);

        // MAIN CONTENT
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BACKGROUND);

        contentPanel.add(createDashboard(), "Dashboard");

        add(contentPanel, BorderLayout.CENTER);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    // =====================================
    // SIDEBAR
    // =====================================

    private JPanel createSidebar() {

        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBackground(NAVY);
        sidebar.setLayout(new BorderLayout());

        JPanel top = new JPanel();
        top.setBackground(NAVY);
        top.setLayout(new GridLayout(2, 1, 5, 5));
        top.setBorder(
            BorderFactory.createEmptyBorder(30, 20, 25, 20)
        );

        JLabel logo = new JLabel("RentEase");
        logo.setForeground(WHITE);
        logo.setFont(new Font("Arial", Font.BOLD, 27));

        JLabel subtitle = new JLabel("RENTAL MANAGEMENT");
        subtitle.setForeground(new Color(190, 205, 225));
        subtitle.setFont(new Font("Arial", Font.PLAIN, 10));

        top.add(logo);
        top.add(subtitle);

        sidebar.add(top, BorderLayout.NORTH);

        JPanel menu = new JPanel();
        menu.setBackground(NAVY);
        menu.setLayout(new GridLayout(0, 1, 0, 8));
        menu.setBorder(
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        );

        String[] items = {
            "Dashboard",
            "Equipment",
            "Categories",
            "Customers",
            "Rentals",
            "Returns",
            "Payments",
            "Reports"
        };

        for (String item : items) {

            JButton button = createMenuButton(item);

            button.addActionListener(e -> {
                if (item.equals("Dashboard")) {
                    cardLayout.show(contentPanel, "Dashboard");
                } else {
                    showModule(item);
                }
            });

            menu.add(button);
        }

        sidebar.add(menu, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout(2, 1, 5, 5));
        bottom.setBackground(NAVY);
        bottom.setBorder(
            BorderFactory.createEmptyBorder(15, 12, 25, 12)
        );

        JLabel user = new JLabel("  Administrator");
        user.setForeground(WHITE);

        JButton logout = createMenuButton("Logout");

        logout.addActionListener(e -> {

            int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame();
            }
        });

        bottom.add(user);
        bottom.add(logout);

        sidebar.add(bottom, BorderLayout.SOUTH);

        return sidebar;
    }

    private JButton createMenuButton(String text) {

        JButton button = new JButton(text);

        button.setForeground(WHITE);
        button.setBackground(NAVY);
        button.setFont(new Font("Arial", Font.PLAIN, 15));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorder(
            BorderFactory.createEmptyBorder(12, 15, 12, 10)
        );
        button.setCursor(
            new Cursor(Cursor.HAND_CURSOR)
        );

        button.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(BLUE);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(NAVY);
            }
        });

        return button;
    }

    // =====================================
    // DASHBOARD CONTENT
    // =====================================

    private JPanel createDashboard() {

        JPanel dashboard = new JPanel(new BorderLayout(20, 20));
        dashboard.setBackground(BACKGROUND);
        dashboard.setBorder(
            BorderFactory.createEmptyBorder(30, 35, 30, 35)
        );

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BACKGROUND);

        JPanel heading = new JPanel(new GridLayout(2, 1, 0, 5));
        heading.setBackground(BACKGROUND);

        JLabel title = new JLabel("Dashboard Overview");
        title.setFont(new Font("Arial", Font.BOLD, 27));
        title.setForeground(TEXT);

        JLabel welcome = new JLabel(
            "Welcome back, " + adminName +
            "! Here's your rental business summary."
        );
        welcome.setFont(new Font("Arial", Font.PLAIN, 14));
        welcome.setForeground(Color.GRAY);

        heading.add(title);
        heading.add(welcome);

        header.add(heading, BorderLayout.WEST);

        dashboard.add(header, BorderLayout.NORTH);

        // STATISTICS CARDS
        JPanel center = new JPanel(new BorderLayout(15, 20));
        center.setBackground(BACKGROUND);

        JPanel statistics = new JPanel(new GridLayout(2, 2, 20, 20));
        statistics.setBackground(BACKGROUND);

        statistics.add(createStatCard(
            "Total Equipment",
            getCount("SELECT COUNT(*) FROM equipment"),
            "Equipment records",
            new Color(43, 91, 145)
        ));

        statistics.add(createStatCard(
            "Total Customers",
            getCount("SELECT COUNT(*) FROM customers"),
            "Registered customers",
            new Color(35, 140, 110)
        ));

        statistics.add(createStatCard(
            "Active Rentals",
            getCount("SELECT COUNT(*) FROM rentals WHERE rental_status = 'ACTIVE'"),
            "Currently active",
            new Color(205, 140, 45)
        ));

        statistics.add(createStatCard(
            "Payment Records",
            getCount("SELECT COUNT(*) FROM payments"),
            "Recorded payments",
            new Color(125, 90, 165)
        ));

        center.add(statistics, BorderLayout.CENTER);

        // QUICK ACTIONS
        JPanel quickActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        quickActions.setBackground(WHITE);
        quickActions.setBorder(
            BorderFactory.createTitledBorder("Quick Actions")
        );

        String[] actions = {
            "Manage Equipment",
            "Add Customer",
            "New Rental"
        };

        for (String action : actions) {

            JButton button = new JButton(action);
            button.setFont(new Font("Arial", Font.PLAIN, 13));
            button.setFocusPainted(false);

            button.addActionListener(e -> {

                if (action.equals("Manage Equipment")) {
                    showModule("Equipment");
                } else if (action.equals("Add Customer")) {
                    showModule("Customers");
                } else {
                    showModule("Rentals");
                }
            });

            quickActions.add(button);
        }

        center.add(quickActions, BorderLayout.SOUTH);

        dashboard.add(center, BorderLayout.CENTER);

        return dashboard;
    }

    // =====================================
    // STATISTIC CARD
    // =====================================

    private JPanel createStatCard(
        String title,
        int value,
        String description,
        Color accent
    ) {

        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(WHITE);
        card.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                    0, 5, 0, 0, accent
                ),
                BorderFactory.createEmptyBorder(22, 22, 22, 22)
            )
        );

        JLabel cardTitle = new JLabel(title);
        cardTitle.setFont(new Font("Arial", Font.BOLD, 16));
        cardTitle.setForeground(TEXT);

        JLabel number = new JLabel(String.valueOf(value));
        number.setFont(new Font("Arial", Font.BOLD, 34));
        number.setForeground(accent);

        JLabel detail = new JLabel(description);
        detail.setFont(new Font("Arial", Font.PLAIN, 12));
        detail.setForeground(Color.GRAY);

        JPanel middle = new JPanel(new BorderLayout());
        middle.setBackground(WHITE);
        middle.add(number, BorderLayout.CENTER);
        middle.add(detail, BorderLayout.SOUTH);

        card.add(cardTitle, BorderLayout.NORTH);
        card.add(middle, BorderLayout.CENTER);

        return card;
    }

    // =====================================
    // DATABASE STATISTICS
    // =====================================

    private int getCount(String sql) {

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            System.out.println(
                "Unable to load dashboard statistics: "
                + e.getMessage()
            );
        }

        return 0;
    }

    // =====================================
    // MODULE PLACEHOLDER
    // =====================================

    private void showModule(String moduleName) {

        if (moduleName.equals("Equipment")) {
            new EquipmentFrame(adminName);
            return;
        }
        if (moduleName.equals("Categories")) {
            new CategoriesFrame(adminName);
            return;
        }
        if (moduleName.equals("Customers")) {
            new CustomersFrame(adminName);
            return;
        }
        if (moduleName.equals("Rentals")) {
            new RentalsFrame(adminName);
            return;
        }
        if (moduleName.equals("Returns")) {
            new ReturnsFrame(adminName, adminId);
            return;
        }
        if (moduleName.equals("Payments")) {
            new PaymentsFrame(adminName, adminId);
            return;
        }
        if (moduleName.equals("Reports")) {
            new ReportsFrame(adminName);
            return;
        }

        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(BACKGROUND);
        page.setBorder(
            BorderFactory.createEmptyBorder(35, 35, 35, 35)
        );

        JLabel title = new JLabel(moduleName + " Management");
        title.setFont(new Font("Arial", Font.BOLD, 27));
        title.setForeground(TEXT);

        JLabel description = new JLabel(
            "The " + moduleName +
            " module will be developed in the next phase."
        );

        description.setFont(new Font("Arial", Font.PLAIN, 15));

        JPanel content = new JPanel(new GridLayout(2, 1, 5, 5));
        content.setBackground(WHITE);
        content.setBorder(
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        );

        content.add(title);
        content.add(description);

        page.add(content, BorderLayout.NORTH);

        String pageName = moduleName;

        if (contentPanel.getComponentCount() > 1) {
            contentPanel.remove(contentPanel.getComponent(1));
        }

        contentPanel.add(page, pageName);
        cardLayout.show(contentPanel, pageName);

        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
