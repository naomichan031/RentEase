
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ReturnsFrame extends JFrame {

    private final String adminName;
    private final int adminId;

    private final Color NAVY = new Color(25, 45, 75);
    private final Color BACKGROUND = new Color(243, 246, 250);
    private final Color TEXT = new Color(45, 55, 72);

    private JTable rentalTable;
    private JTable returnTable;

    private DefaultTableModel rentalModel;
    private DefaultTableModel returnModel;

    private JTextField returnDateField;
    private JTextField lateFeeField;
    private JTextField searchField;
    private JTextArea conditionNotesArea;

    public ReturnsFrame(String adminName, int adminId) {

        this.adminName = adminName;
        this.adminId = adminId;

        setTitle("RentEase - Returns Management | Administrator: " + this.adminName);
        setSize(1150, 750);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Returns Management");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(Color.WHITE);

        JLabel administrator = new JLabel(
                "Administrator: " + adminName
        );
        administrator.setForeground(Color.WHITE);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(NAVY);
        header.setBorder(
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        );
        header.add(title, BorderLayout.WEST);
        header.add(administrator, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        JPanel main = new JPanel(new BorderLayout(15, 15));
        main.setBackground(BACKGROUND);
        main.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        // ONGOING RENTALS
        JPanel rentalPanel = new JPanel(new BorderLayout(5, 5));
        rentalPanel.setBackground(BACKGROUND);

        JLabel rentalTitle = new JLabel("Ongoing Rentals");
        rentalTitle.setFont(new Font("Arial", Font.BOLD, 20));
        rentalTitle.setForeground(TEXT);

        rentalModel = new DefaultTableModel(
                new String[]{
                    "Rental ID", "Customer", "Rental Date",
                    "Expected Return", "Status", "Total Amount"
                }, 0
        ) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        rentalTable = new JTable(rentalModel);
        rentalTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        rentalTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                calculateLateFee();
            }
        });

        rentalPanel.add(rentalTitle, BorderLayout.NORTH);
        rentalPanel.add(new JScrollPane(rentalTable), BorderLayout.CENTER);

        // RETURN FORM
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(
                BorderFactory.createTitledBorder("Return Details")
        );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 7, 7, 7);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        returnDateField = new JTextField(
                LocalDate.now().toString(), 12
        );

        lateFeeField = new JTextField("0.00", 12);
        lateFeeField.setEditable(false);

        conditionNotesArea = new JTextArea(3, 25);
        conditionNotesArea.setLineWrap(true);
        conditionNotesArea.setWrapStyleWord(true);

        addFormRow(form, gbc, 0, "Actual Return Date (YYYY-MM-DD):",
                returnDateField);

        addFormRow(form, gbc, 1, "Calculated Late Fee:",
                lateFeeField);

        addFormRow(form, gbc, 2, "Equipment Condition Notes:",
                new JScrollPane(conditionNotesArea));

        JButton processButton = new JButton("Process Return");
        JButton refreshButton = new JButton("Refresh");
        JButton clearButton = new JButton("Clear");

        processButton.addActionListener(e -> processReturn());
        refreshButton.addActionListener(e -> refreshData());
        clearButton.addActionListener(e -> clearForm());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setBackground(Color.WHITE);
        buttons.add(processButton);
        buttons.add(refreshButton);
        buttons.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        form.add(buttons, gbc);

        // RETURN HISTORY
        JPanel historyPanel = new JPanel(new BorderLayout(5, 5));
        historyPanel.setBackground(BACKGROUND);

        JLabel historyTitle = new JLabel("Return Records");
        historyTitle.setFont(new Font("Arial", Font.BOLD, 20));
        historyTitle.setForeground(TEXT);

        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton historyRefreshButton = new JButton("Refresh");

        searchButton.addActionListener(e -> searchReturns());
        historyRefreshButton.addActionListener(e -> loadReturnRecords());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(BACKGROUND);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(historyRefreshButton);

        returnModel = new DefaultTableModel(
                new String[]{
                    "Return ID", "Rental ID", "Customer",
                    "Return Date", "Late Fee", "Condition Notes",
                    "Received By"
                }, 0
        ) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        returnTable = new JTable(returnModel);

        JPanel historyTop = new JPanel(new BorderLayout());
        historyTop.setBackground(BACKGROUND);
        historyTop.add(historyTitle, BorderLayout.WEST);
        historyTop.add(searchPanel, BorderLayout.EAST);

        historyPanel.add(historyTop, BorderLayout.NORTH);
        historyPanel.add(new JScrollPane(returnTable), BorderLayout.CENTER);

        JPanel topSection = new JPanel(new GridLayout(2, 1, 10, 10));
        topSection.setBackground(BACKGROUND);
        topSection.add(rentalPanel);
        topSection.add(form);

        JSplitPane split = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                topSection,
                historyPanel
        );
        split.setResizeWeight(0.60);
        split.setDividerLocation(390);

        main.add(split, BorderLayout.CENTER);
        add(main, BorderLayout.CENTER);

        loadOngoingRentals();
        loadReturnRecords();

        setVisible(true);
    }

    private void addFormRow(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String label,
            Component component
    ) {
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        panel.add(component, gbc);
    }

    private void loadOngoingRentals() {

        rentalModel.setRowCount(0);

        String sql = """
            SELECT r.rental_id, c.full_name,
                   r.rental_date, r.expected_return_date,
                   r.rental_status, r.total_amount
            FROM rentals r
            JOIN customers c ON r.customer_id = c.customer_id
            WHERE r.rental_status = 'ONGOING'
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
                    rentalModel.addRow(new Object[]{
                        rs.getInt("rental_id"),
                        rs.getString("full_name"),
                        rs.getDate("rental_date"),
                        rs.getDate("expected_return_date"),
                        rs.getString("rental_status"),
                        rs.getBigDecimal("total_amount")
                    });
                }
            }

        } catch (SQLException e) {
            showError("Unable to load ongoing rentals: " + e.getMessage());
        }
    }

    private BigDecimal calculateLateFeeValue(
            Connection conn,
            int rentalId,
            LocalDate returnDate
    ) throws SQLException {

        String dateSql = """
            SELECT expected_return_date
            FROM rentals
            WHERE rental_id = ?
              AND rental_status = 'ONGOING'
            """;

        LocalDate expectedDate;

        try (PreparedStatement stmt = conn.prepareStatement(dateSql)) {
            stmt.setInt(1, rentalId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException(
                            "Rental is no longer ongoing."
                    );
                }

                expectedDate = rs.getDate(
                        "expected_return_date"
                ).toLocalDate();
            }
        }

        long overdueDays = Math.max(
                0,
                ChronoUnit.DAYS.between(expectedDate, returnDate)
        );

        if (overdueDays == 0) {
            return BigDecimal.ZERO;
        }

        String detailSql = """
            SELECT daily_rate, quantity
            FROM rental_details
            WHERE rental_id = ?
            """;

        BigDecimal lateFee = BigDecimal.ZERO;

        try (PreparedStatement stmt = conn.prepareStatement(detailSql)) {
            stmt.setInt(1, rentalId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BigDecimal dailyRate =
                            rs.getBigDecimal("daily_rate");

                    int quantity = rs.getInt("quantity");

                    BigDecimal itemFee = dailyRate
                            .multiply(BigDecimal.valueOf(quantity))
                            .multiply(BigDecimal.valueOf(overdueDays));

                    lateFee = lateFee.add(itemFee);
                }
            }
        }

        return lateFee;
    }

    private void calculateLateFee() {

        int row = rentalTable.getSelectedRow();

        if (row < 0) {
            lateFeeField.setText("0.00");
            return;
        }

        try {
            int rentalId = Integer.parseInt(
                    rentalModel.getValueAt(row, 0).toString()
            );

            LocalDate returnDate = LocalDate.parse(
                    returnDateField.getText().trim()
            );

            try (Connection conn = DBConnection.getConnection()) {

                if (conn == null) {
                    return;
                }

                BigDecimal fee = calculateLateFeeValue(
                        conn, rentalId, returnDate
                );

                lateFeeField.setText(fee.toPlainString());
            }

        } catch (Exception e) {
            lateFeeField.setText("Invalid date");
        }
    }

    private void processReturn() {

        int row = rentalTable.getSelectedRow();

        if (row < 0) {
            showError("Please select an ongoing rental.");
            return;
        }

        int rentalId = Integer.parseInt(
                rentalModel.getValueAt(row, 0).toString()
        );

        LocalDate returnDate;

        try {
            returnDate = LocalDate.parse(
                    returnDateField.getText().trim()
            );
        } catch (Exception e) {
            showError("Invalid return date. Use YYYY-MM-DD.");
            return;
        }

        if (returnDate.isAfter(LocalDate.now())) {
            showError("Return date cannot be in the future.");
            return;
        }

        String notes = conditionNotesArea.getText().trim();

        String confirm = "Process return for Rental ID "
                + rentalId + "?\nLate Fee: ₱"
                + lateFeeField.getText();

        int choice = JOptionPane.showConfirmDialog(
                this,
                confirm,
                "Confirm Return",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        String insertReturnSql = """
            INSERT INTO returns
            (rental_id, return_date, late_fee,
             condition_notes, received_by)
            VALUES (?, ?, ?, ?, ?)
            """;

        String detailsSql = """
            SELECT equipment_id, quantity
            FROM rental_details
            WHERE rental_id = ?
            """;

        String restoreStockSql = """
            UPDATE equipment
            SET quantity_available =
                LEAST(quantity_total, quantity_available + ?)
            WHERE equipment_id = ?
            """;

        String updateRentalSql = """
            UPDATE rentals
            SET rental_status = 'COMPLETED'
            WHERE rental_id = ?
              AND rental_status = 'ONGOING'
            """;

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) {
                showError("Database connection failed.");
                return;
            }

            conn.setAutoCommit(false);

            try {

                BigDecimal lateFee = calculateLateFeeValue(
                        conn, rentalId, returnDate
                );

                // Record the return
                try (PreparedStatement stmt =
                        conn.prepareStatement(insertReturnSql)) {

                    stmt.setInt(1, rentalId);
                    stmt.setDate(2, Date.valueOf(returnDate));
                    stmt.setBigDecimal(3, lateFee);
                    stmt.setString(4, notes);
                    stmt.setInt(5, adminId);

                    stmt.executeUpdate();
                }

                // Restore equipment stock
                try (PreparedStatement detailStmt =
                        conn.prepareStatement(detailsSql);
                     PreparedStatement stockStmt =
                        conn.prepareStatement(restoreStockSql)) {

                    detailStmt.setInt(1, rentalId);

                    try (ResultSet rs = detailStmt.executeQuery()) {

                        while (rs.next()) {

                            stockStmt.setInt(
                                    1, rs.getInt("quantity")
                            );

                            stockStmt.setInt(
                                    2, rs.getInt("equipment_id")
                            );

                            int updated = stockStmt.executeUpdate();

                            if (updated != 1) {
                                throw new SQLException(
                                        "Unable to restore equipment stock."
                                );
                            }
                        }
                    }
                }

                // Mark rental completed
                try (PreparedStatement stmt =
                        conn.prepareStatement(updateRentalSql)) {

                    stmt.setInt(1, rentalId);

                    if (stmt.executeUpdate() != 1) {
                        throw new SQLException(
                                "Unable to update rental status."
                        );
                    }
                }

                conn.commit();

                JOptionPane.showMessageDialog(
                        this,
                        "Return processed successfully!\n"
                        + "Rental ID: " + rentalId
                        + "\nLate Fee: ₱" + lateFee
                );

                clearForm();
                refreshData();

            } catch (Exception e) {
                conn.rollback();

                showError(
                        "Return was not processed. "
                        + "Changes were rolled back.\n"
                        + e.getMessage()
                );

            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        }
    }

    private void loadReturnRecords() {

        returnModel.setRowCount(0);

        String sql = """
            SELECT ret.return_id, ret.rental_id,
                   c.full_name, ret.return_date,
                   ret.late_fee, ret.condition_notes,
                   u.full_name AS received_by_name
            FROM returns ret
            JOIN rentals r ON ret.rental_id = r.rental_id
            JOIN customers c ON r.customer_id = c.customer_id
            JOIN users u ON ret.received_by = u.user_id
            ORDER BY ret.return_id DESC
            """;

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) {
                showError("Database connection failed.");
                return;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    returnModel.addRow(new Object[]{
                        rs.getInt("return_id"),
                        rs.getInt("rental_id"),
                        rs.getString("full_name"),
                        rs.getDate("return_date"),
                        rs.getBigDecimal("late_fee"),
                        rs.getString("condition_notes"),
                        rs.getString("received_by_name")
                    });
                }
            }

        } catch (SQLException e) {
            showError("Unable to load return records: " + e.getMessage());
        }
    }

    private void searchReturns() {

        String keyword = searchField.getText().trim();

        returnModel.setRowCount(0);

        String sql = """
            SELECT ret.return_id, ret.rental_id,
                   c.full_name, ret.return_date,
                   ret.late_fee, ret.condition_notes,
                   u.full_name AS received_by_name
            FROM returns ret
            JOIN rentals r ON ret.rental_id = r.rental_id
            JOIN customers c ON r.customer_id = c.customer_id
            JOIN users u ON ret.received_by = u.user_id
            WHERE CAST(ret.return_id AS CHAR) LIKE ?
               OR CAST(ret.rental_id AS CHAR) LIKE ?
               OR c.full_name LIKE ?
            ORDER BY ret.return_id DESC
            """;

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) {
                showError("Database connection failed.");
                return;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                String search = "%" + keyword + "%";

                stmt.setString(1, search);
                stmt.setString(2, search);
                stmt.setString(3, search);

                try (ResultSet rs = stmt.executeQuery()) {

                    while (rs.next()) {
                        returnModel.addRow(new Object[]{
                            rs.getInt("return_id"),
                            rs.getInt("rental_id"),
                            rs.getString("full_name"),
                            rs.getDate("return_date"),
                            rs.getBigDecimal("late_fee"),
                            rs.getString("condition_notes"),
                            rs.getString("received_by_name")
                        });
                    }
                }
            }

        } catch (SQLException e) {
            showError("Search failed: " + e.getMessage());
        }
    }

    private void refreshData() {
        loadOngoingRentals();
        loadReturnRecords();
        lateFeeField.setText("0.00");
    }

    private void clearForm() {
        rentalTable.clearSelection();
        returnDateField.setText(LocalDate.now().toString());
        lateFeeField.setText("0.00");
        conditionNotesArea.setText("");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "RentEase",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
