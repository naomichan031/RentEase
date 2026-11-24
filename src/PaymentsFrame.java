
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;

public class PaymentsFrame extends JFrame {

    private final String adminName;

    private final Color NAVY = new Color(25, 45, 75);
    private final Color BACKGROUND = new Color(243, 246, 250);
    private final Color TEXT = new Color(45, 55, 72);

    private JComboBox<RentalOption> rentalCombo;
    private JTextField amountField;
    private JComboBox<String> methodCombo;
    private JComboBox<String> statusCombo;
    private JTextField referenceField;
    private JTextField searchField;

    private JTable paymentTable;
    private DefaultTableModel paymentModel;

    public PaymentsFrame(String adminName, int adminId) {
        this.adminName = adminName;

        setTitle("RentEase - Payments Management");
        setSize(1200, 750);
        setMinimumSize(new Dimension(950, 600));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(NAVY);
        header.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("Payments Management");
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(Color.WHITE);

        JLabel adminLabel = new JLabel("Administrator: " + adminName);
        adminLabel.setFont(new Font("Arial", Font.BOLD, 14));
        adminLabel.setForeground(Color.WHITE);

        header.add(title, BorderLayout.WEST);
        header.add(adminLabel, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // MAIN PANEL
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // PAYMENT FORM
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Payment Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        rentalCombo = new JComboBox<>();
        amountField = new JTextField(18);

        methodCombo = new JComboBox<>(
                new String[]{"Cash", "GCash", "Bank Transfer", "Credit Card"}
        );

        statusCombo = new JComboBox<>(
                new String[]{"PENDING", "PAID", "FAILED"}
        );

        referenceField = new JTextField(18);

        addFormRow(formPanel, gbc, 0, "Rental:", rentalCombo);
        addFormRow(formPanel, gbc, 1, "Amount:", amountField);
        addFormRow(formPanel, gbc, 2, "Payment Method:", methodCombo);
        addFormRow(formPanel, gbc, 3, "Payment Status:", statusCombo);
        addFormRow(formPanel, gbc, 4, "Reference Number:", referenceField);

        JButton addButton = new JButton("Add Payment");
        JButton updateButton = new JButton("Update Status");
        JButton clearButton = new JButton("Clear");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // PAYMENT TABLE
        String[] columns = {
            "Payment ID",
            "Rental ID",
            "Customer",
            "Amount",
            "Method",
            "Payment Date",
            "Status",
            "Reference Number"
        };

        paymentModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        paymentTable = new JTable(paymentModel);
        paymentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        paymentTable.setRowHeight(25);

        JScrollPane tableScroll = new JScrollPane(paymentTable);

        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBackground(BACKGROUND);
        tablePanel.setBorder(BorderFactory.createTitledBorder("Payment Records"));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(BACKGROUND);

        searchField = new JTextField(22);
        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        tablePanel.add(searchPanel, BorderLayout.NORTH);
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // BUTTON ACTIONS
        addButton.addActionListener(e -> addPayment());
        updateButton.addActionListener(e -> updatePaymentStatus());
        clearButton.addActionListener(e -> clearForm());
        searchButton.addActionListener(e -> searchPayments());
        refreshButton.addActionListener(e -> refreshData());

        paymentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedPayment();
            }
        });

        loadRentals();
        loadPayments();

        setVisible(true);
    }

    private void addFormRow(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String label,
            JComponent component) {

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = row;

        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("Arial", Font.BOLD, 13));
        fieldLabel.setForeground(TEXT);

        panel.add(fieldLabel, gbc);

        gbc.gridx = 1;
        panel.add(component, gbc);
    }

    // LOAD RENTALS
    private void loadRentals() {

        rentalCombo.removeAllItems();

        String sql = """
            SELECT r.rental_id, c.full_name, r.total_amount
            FROM rentals r
            JOIN customers c ON r.customer_id = c.customer_id
            ORDER BY r.rental_id DESC
            """;

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) {
                showError("Database connection failed.");
                return;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    rentalCombo.addItem(new RentalOption(
                            rs.getInt("rental_id"),
                            rs.getString("full_name"),
                            rs.getBigDecimal("total_amount")
                    ));
                }
            }

        } catch (SQLException e) {
            showError("Unable to load rentals: " + e.getMessage());
        }
    }

    // ADD PAYMENT
    private void addPayment() {

        RentalOption rental =
                (RentalOption) rentalCombo.getSelectedItem();

        if (rental == null) {
            showError("No rental record is available.");
            return;
        }

        BigDecimal amount;

        try {
            amount = new BigDecimal(amountField.getText().trim());

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showError("Payment amount must be greater than zero.");
                return;
            }

        } catch (NumberFormatException e) {
            showError("Please enter a valid payment amount.");
            return;
        }

        String sql = """
            INSERT INTO payments
            (rental_id, amount, payment_method, payment_status, reference_number)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) {
                showError("Database connection failed.");
                return;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, rental.id);
                stmt.setBigDecimal(2, amount);
                stmt.setString(3, methodCombo.getSelectedItem().toString());
                stmt.setString(4, statusCombo.getSelectedItem().toString());

                String reference = referenceField.getText().trim();

                if (reference.isEmpty()) {
                    stmt.setNull(5, Types.VARCHAR);
                } else {
                    stmt.setString(5, reference);
                }

                stmt.executeUpdate();

                JOptionPane.showMessageDialog(
                        this, "Payment added successfully!"
                );

                clearForm();
                loadPayments();
            }

        } catch (SQLException e) {
            showError("Unable to add payment: " + e.getMessage());
        }
    }

    // UPDATE PAYMENT STATUS
    private void updatePaymentStatus() {

        int selectedRow = paymentTable.getSelectedRow();

        if (selectedRow == -1) {
            showError("Please select a payment record first.");
            return;
        }

        int paymentId = Integer.parseInt(
                paymentModel.getValueAt(selectedRow, 0).toString()
        );

        String newStatus = statusCombo.getSelectedItem().toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Update payment status to " + newStatus + "?",
                "Confirm Update",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = """
            UPDATE payments
            SET payment_status = ?
            WHERE payment_id = ?
            """;

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) {
                showError("Database connection failed.");
                return;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, newStatus);
                stmt.setInt(2, paymentId);

                int updated = stmt.executeUpdate();

                if (updated > 0) {
                    JOptionPane.showMessageDialog(
                            this, "Payment status updated successfully!"
                    );
                    loadPayments();
                    clearForm();
                } else {
                    showError("Payment record was not found.");
                }
            }

        } catch (SQLException e) {
            showError("Unable to update payment: " + e.getMessage());
        }
    }

    // LOAD PAYMENT RECORDS
    private void loadPayments() {

        paymentModel.setRowCount(0);

        String sql = """
            SELECT p.payment_id, p.rental_id, c.full_name,
                   p.amount, p.payment_method, p.payment_date,
                   p.payment_status, p.reference_number
            FROM payments p
            JOIN rentals r ON p.rental_id = r.rental_id
            JOIN customers c ON r.customer_id = c.customer_id
            ORDER BY p.payment_id DESC
            """;

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) {
                showError("Database connection failed.");
                return;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    paymentModel.addRow(new Object[]{
                        rs.getInt("payment_id"),
                        rs.getInt("rental_id"),
                        rs.getString("full_name"),
                        rs.getBigDecimal("amount"),
                        rs.getString("payment_method"),
                        rs.getTimestamp("payment_date"),
                        rs.getString("payment_status"),
                        rs.getString("reference_number")
                    });
                }
            }

        } catch (SQLException e) {
            showError("Unable to load payments: " + e.getMessage());
        }
    }

    // SEARCH PAYMENTS
    private void searchPayments() {

        String keyword = searchField.getText().trim();

        paymentModel.setRowCount(0);

        String sql = """
            SELECT p.payment_id, p.rental_id, c.full_name,
                   p.amount, p.payment_method, p.payment_date,
                   p.payment_status, p.reference_number
            FROM payments p
            JOIN rentals r ON p.rental_id = r.rental_id
            JOIN customers c ON r.customer_id = c.customer_id
            WHERE CAST(p.payment_id AS CHAR) LIKE ?
               OR CAST(p.rental_id AS CHAR) LIKE ?
               OR c.full_name LIKE ?
               OR p.reference_number LIKE ?
            ORDER BY p.payment_id DESC
            """;

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) {
                showError("Database connection failed.");
                return;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                String search = "%" + keyword + "%";

                for (int i = 1; i <= 4; i++) {
                    stmt.setString(i, search);
                }

                try (ResultSet rs = stmt.executeQuery()) {

                    while (rs.next()) {
                        paymentModel.addRow(new Object[]{
                            rs.getInt("payment_id"),
                            rs.getInt("rental_id"),
                            rs.getString("full_name"),
                            rs.getBigDecimal("amount"),
                            rs.getString("payment_method"),
                            rs.getTimestamp("payment_date"),
                            rs.getString("payment_status"),
                            rs.getString("reference_number")
                        });
                    }
                }
            }

        } catch (SQLException e) {
            showError("Search failed: " + e.getMessage());
        }
    }

    // SELECT PAYMENT RECORD
    private void loadSelectedPayment() {

        int row = paymentTable.getSelectedRow();

        if (row == -1) {
            return;
        }

        String status = paymentModel.getValueAt(row, 6).toString();
        statusCombo.setSelectedItem(status);
    }

    // REFRESH
    private void refreshData() {
        searchField.setText("");
        loadRentals();
        loadPayments();
        clearForm();
    }

    // CLEAR FORM
    private void clearForm() {

        if (rentalCombo.getItemCount() > 0) {
            rentalCombo.setSelectedIndex(0);
        }

        amountField.setText("");
        methodCombo.setSelectedIndex(0);
        statusCombo.setSelectedItem("PENDING");
        referenceField.setText("");
        paymentTable.clearSelection();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "RentEase",
                JOptionPane.ERROR_MESSAGE
        );
    }

    // RENTAL COMBO ITEM
    private static class RentalOption {

        int id;
        String customerName;
        BigDecimal totalAmount;

        RentalOption(int id, String customerName, BigDecimal totalAmount) {
            this.id = id;
            this.customerName = customerName;
            this.totalAmount = totalAmount;
        }

        @Override
        public String toString() {
            return "Rental #" + id + " - " + customerName
                    + " (₱" + totalAmount + ")";
        }
    }
}
