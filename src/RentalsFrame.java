
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class RentalsFrame extends JFrame {

    private final Color NAVY = new Color(25, 45, 75);
    private final Color BACKGROUND = new Color(243, 246, 250);

    private JComboBox<Customer> customerCombo;
    private JComboBox<Equipment> equipmentCombo;

    private JTextField quantityField;
    private JTextField rentalDateField;
    private JTextField returnDateField;

    private JTable cartTable;
    private DefaultTableModel cartModel;

    private JTable rentalTable;
    private DefaultTableModel rentalModel;

    private ArrayList<RentalItem> cart = new ArrayList<>();

    private String adminName;

    public RentalsFrame(String adminName) {

        this.adminName = adminName;

        setTitle("RentEase - Rentals Management");
        setSize(1150, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND);

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(NAVY);
        header.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("Rentals Management");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(Color.WHITE);

        JLabel adminLabel = new JLabel("Administrator: " + adminName);
        adminLabel.setForeground(Color.WHITE);

        header.add(title, BorderLayout.WEST);
        header.add(adminLabel, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // MAIN PANEL
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // RENTAL DETAILS FORM
        JPanel formPanel = new JPanel(new GridLayout(2, 4, 12, 10));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Rental Information"));

        customerCombo = new JComboBox<>();
        equipmentCombo = new JComboBox<>();

        quantityField = new JTextField("1");
        rentalDateField = new JTextField(LocalDate.now().toString());
        returnDateField = new JTextField(LocalDate.now().plusDays(1).toString());

        formPanel.add(createFieldPanel("Customer", customerCombo));
        formPanel.add(createFieldPanel("Equipment", equipmentCombo));
        formPanel.add(createFieldPanel("Quantity", quantityField));
        formPanel.add(createFieldPanel("Rental Date (YYYY-MM-DD)", rentalDateField));
        formPanel.add(createFieldPanel("Expected Return (YYYY-MM-DD)", returnDateField));

        JButton addItemButton = new JButton("Add Equipment");
        formPanel.add(addItemButton);

        JButton removeItemButton = new JButton("Remove Selected Item");
        formPanel.add(removeItemButton);

        JButton clearCartButton = new JButton("Clear Cart");
        formPanel.add(clearCartButton);

        mainPanel.add(formPanel, BorderLayout.NORTH);

        // RENTAL CART
        JPanel cartPanel = new JPanel(new BorderLayout(0, 8));
        cartPanel.setBackground(BACKGROUND);

        JLabel cartTitle = new JLabel("Rental Equipment Items");
        cartTitle.setFont(new Font("Arial", Font.BOLD, 21));

        cartPanel.add(cartTitle, BorderLayout.NORTH);

        cartModel = new DefaultTableModel(
                new String[]{"Equipment ID", "Equipment", "Quantity",
                        "Daily Rate", "Days", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        cartTable = new JTable(cartModel);
        cartTable.setRowHeight(27);

        cartPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        //mainPanel.add(cartPanel, BorderLayout.CENTER);

        // BOTTOM ACTIONS AND RENTAL HISTORY
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 10));
        bottomPanel.setBackground(BACKGROUND);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(BACKGROUND);

        JButton saveButton = new JButton("Save Rental");
        JButton refreshButton = new JButton("Refresh");
        JButton clearButton = new JButton("Clear");

        actionPanel.add(saveButton);
        actionPanel.add(refreshButton);
        actionPanel.add(clearButton);

        bottomPanel.add(actionPanel, BorderLayout.NORTH);

        rentalModel = new DefaultTableModel(
                new String[]{"Rental ID", "Customer", "Rental Date",
                        "Expected Return", "Status", "Total Amount"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        rentalTable = new JTable(rentalModel);
        rentalTable.setRowHeight(25);

        JPanel historyPanel = new JPanel(new BorderLayout(0, 5));
        historyPanel.setBackground(BACKGROUND);

        JLabel historyTitle = new JLabel("Rental Records");
        historyTitle.setFont(new Font("Arial", Font.BOLD, 19));

        historyPanel.add(historyTitle, BorderLayout.NORTH);
        historyPanel.add(new JScrollPane(rentalTable), BorderLayout.CENTER);

        bottomPanel.add(historyPanel, BorderLayout.CENTER);

        //mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        JSplitPane verticalSplit = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            cartPanel,
            bottomPanel
        );

        verticalSplit.setResizeWeight(0.40);
        verticalSplit.setDividerLocation(230);
        verticalSplit.setOneTouchExpandable(true);
        verticalSplit.setBorder(null);

        mainPanel.add(verticalSplit, BorderLayout.CENTER);  
        
        add(mainPanel, BorderLayout.CENTER);

        // BUTTON ACTIONS
        addItemButton.addActionListener(e -> addEquipmentToCart());
        removeItemButton.addActionListener(e -> removeSelectedItem());
        clearCartButton.addActionListener(e -> clearCart());
        saveButton.addActionListener(e -> saveRental());
        refreshButton.addActionListener(e -> refreshData());
        clearButton.addActionListener(e -> clearCart());

        loadCustomers();
        loadEquipment();
        loadRentals();

        setVisible(true);
    }

    private JPanel createFieldPanel(String label, JComponent component) {

        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(Color.WHITE);

        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("Arial", Font.BOLD, 12));

        panel.add(fieldLabel, BorderLayout.NORTH);
        panel.add(component, BorderLayout.CENTER);

        return panel;
    }

    // LOAD CUSTOMERS
    private void loadCustomers() {

        customerCombo.removeAllItems();

        String sql = "SELECT customer_id, full_name FROM customers ORDER BY full_name";

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) return;

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {

                while (rs.next()) {
                    customerCombo.addItem(new Customer(
                            rs.getInt("customer_id"),
                            rs.getString("full_name")
                    ));
                }
            }

        } catch (SQLException e) {
            showError("Unable to load customers: " + e.getMessage());
        }
    }

    // LOAD AVAILABLE EQUIPMENT
    private void loadEquipment() {

        equipmentCombo.removeAllItems();

        String sql = "SELECT equipment_id, equipment_name, daily_rate, "
                + "quantity_available FROM equipment "
                + "WHERE quantity_available > 0 "
                + "ORDER BY equipment_name";

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) return;

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {

                while (rs.next()) {
                    equipmentCombo.addItem(new Equipment(
                            rs.getInt("equipment_id"),
                            rs.getString("equipment_name"),
                            rs.getBigDecimal("daily_rate"),
                            rs.getInt("quantity_available")
                    ));
                }
            }

        } catch (SQLException e) {
            showError("Unable to load equipment: " + e.getMessage());
        }
    }

    // ADD EQUIPMENT TO CART
    private void addEquipmentToCart() {

        Customer customer = (Customer) customerCombo.getSelectedItem();
        Equipment equipment = (Equipment) equipmentCombo.getSelectedItem();

        if (customer == null) {
            showError("Please add a customer first.");
            return;
        }

        if (equipment == null) {
            showError("No available equipment found.");
            return;
        }

        int quantity;

        try {
            quantity = Integer.parseInt(quantityField.getText().trim());

            if (quantity <= 0) {
                showError("Quantity must be greater than zero.");
                return;
            }

        } catch (NumberFormatException e) {
            showError("Please enter a valid quantity.");
            return;
        }

        try {
            LocalDate rentalDate = LocalDate.parse(rentalDateField.getText().trim());
            LocalDate returnDate = LocalDate.parse(returnDateField.getText().trim());

            if (returnDate.isBefore(rentalDate) || returnDate.equals(rentalDate)) {
                showError("Expected return date must be after rental date.");
                return;
            }

            int days = (int) ChronoUnit.DAYS.between(rentalDate, returnDate);

            int alreadyAdded = 0;

            for (RentalItem item : cart) {
                if (item.equipmentId == equipment.id) {
                    alreadyAdded += item.quantity;
                }
            }

            if (quantity + alreadyAdded > equipment.available) {
                showError("Requested quantity exceeds available stock.\n"
                        + "Available: " + equipment.available
                        + "\nAlready in cart: " + alreadyAdded);
                return;
            }

            BigDecimal subtotal = equipment.dailyRate
                    .multiply(BigDecimal.valueOf(quantity))
                    .multiply(BigDecimal.valueOf(days));

            RentalItem item = new RentalItem(
                    equipment.id,
                    equipment.name,
                    quantity,
                    equipment.dailyRate,
                    days,
                    subtotal
            );

            cart.add(item);

            cartModel.addRow(new Object[]{
                    item.equipmentId,
                    item.name,
                    item.quantity,
                    item.dailyRate,
                    item.days,
                    item.subtotal
            });

            quantityField.setText("1");

        } catch (Exception e) {
            showError("Please enter valid rental dates in YYYY-MM-DD format.");
        }
    }

    // REMOVE SELECTED ITEM
    private void removeSelectedItem() {

        int row = cartTable.getSelectedRow();

        if (row == -1) {
            showError("Please select an item from the rental list.");
            return;
        }

        cart.remove(row);
        cartModel.removeRow(row);
    }

    // SAVE RENTAL TRANSACTION
    private void saveRental() {

        Customer customer = (Customer) customerCombo.getSelectedItem();

        if (customer == null) {
            showError("Please select a customer.");
            return;
        }

        if (cart.isEmpty()) {
            showError("Please add at least one equipment item.");
            return;
        }

        LocalDate rentalDate;
        LocalDate returnDate;

        try {
            rentalDate = LocalDate.parse(rentalDateField.getText().trim());
            returnDate = LocalDate.parse(returnDateField.getText().trim());

            if (!returnDate.isAfter(rentalDate)) {
                showError("Expected return date must be after rental date.");
                return;
            }

        } catch (Exception e) {
            showError("Invalid rental dates. Use YYYY-MM-DD.");
            return;
        }

        BigDecimal total = BigDecimal.ZERO;

        for (RentalItem item : cart) {
            total = total.add(item.subtotal);
        }

        String userSql = "SELECT user_id FROM users "
                + "WHERE full_name = ? ORDER BY user_id LIMIT 1";

        String rentalSql = "INSERT INTO rentals "
                + "(customer_id, user_id, rental_date, expected_return_date, "
                + "rental_status, total_amount) VALUES (?, ?, ?, ?, ?, ?)";

        String detailSql = "INSERT INTO rental_details "
                + "(rental_id, equipment_id, quantity, daily_rate, "
                + "rental_days, subtotal) VALUES (?, ?, ?, ?, ?, ?)";

        String stockSql = "UPDATE equipment SET quantity_available = "
                + "quantity_available - ? "
                + "WHERE equipment_id = ? AND quantity_available >= ?";

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) {
                showError("Database connection failed.");
                return;
            }

            conn.setAutoCommit(false);

            try {

                int userId;

                try (PreparedStatement userStmt = conn.prepareStatement(userSql)) {

                    userStmt.setString(1, adminName);

                    try (ResultSet rs = userStmt.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException(
                                    "Administrator account not found in users table.");
                        }

                        userId = rs.getInt("user_id");
                    }
                }

                int rentalId;
                String customerEmail = null;

                try (PreparedStatement rentalStmt = conn.prepareStatement(
                        rentalSql, Statement.RETURN_GENERATED_KEYS)) {

                    rentalStmt.setInt(1, customer.id);
                    rentalStmt.setInt(2, userId);
                    rentalStmt.setDate(3, Date.valueOf(rentalDate));
                    rentalStmt.setDate(4, Date.valueOf(returnDate));
                    rentalStmt.setString(5, "ONGOING");
                    rentalStmt.setBigDecimal(6, total);

                    rentalStmt.executeUpdate();

                    try (ResultSet keys = rentalStmt.getGeneratedKeys()) {

                        if (!keys.next()) {
                            throw new SQLException("Unable to retrieve rental ID.");
                        }

                        rentalId = keys.getInt(1);
                        // Retrieve the customer's registered email
                        String emailSql = "SELECT email FROM customers WHERE customer_id = ?";

                        try (PreparedStatement emailStmt = conn.prepareStatement(emailSql)) {
                            emailStmt.setInt(1, customer.id);

                            try (ResultSet emailRs = emailStmt.executeQuery()) {
                                if (emailRs.next()) {
                                    customerEmail = emailRs.getString("email");
                                 }
                            }
                        }
                    }
                }

                try (PreparedStatement detailStmt = conn.prepareStatement(detailSql);
                     PreparedStatement stockStmt = conn.prepareStatement(stockSql)) {

                    for (RentalItem item : cart) {

                        stockStmt.setInt(1, item.quantity);
                        stockStmt.setInt(2, item.equipmentId);
                        stockStmt.setInt(3, item.quantity);

                        int updated = stockStmt.executeUpdate();

                        if (updated == 0) {
                            throw new SQLException(
                                    "Not enough available stock for " + item.name);
                        }

                        detailStmt.setInt(1, rentalId);
                        detailStmt.setInt(2, item.equipmentId);
                        detailStmt.setInt(3, item.quantity);
                        detailStmt.setBigDecimal(4, item.dailyRate);
                        detailStmt.setInt(5, item.days);
                        detailStmt.setBigDecimal(6, item.subtotal);

                        detailStmt.executeUpdate();
                    }
                }

                
conn.commit();

// Send confirmation email after the rental is saved
String emailStatus;

try {
    if (customerEmail == null || customerEmail.isBlank()) {
        emailStatus = "Email not sent: Customer has no registered email.";
    } else {

        String emailMessage =
                "<h2>RentEase Rental Confirmation</h2>"
                + "<p>Dear " + customer.name + ",</p>"
                + "<p>Your equipment rental has been successfully registered.</p>"
                + "<p><b>Rental ID:</b> " + rentalId + "</p>"
                + "<p><b>Rental Date:</b> " + rentalDate + "</p>"
                + "<p><b>Expected Return:</b> " + returnDate + "</p>"
                + "<p><b>Total Amount:</b> ₱" + total + "</p>"
                + "<p>Thank you for choosing RentEase!</p>";

        ResendEmailService.sendEmail(
                customerEmail,
                "RentEase Rental Confirmation - #" + rentalId,
                emailMessage
        );

        emailStatus = "Confirmation email sent successfully!";
    }

} catch (Exception emailError) {
    emailStatus = "Rental saved, but email failed: "
            + emailError.getMessage();
}

JOptionPane.showMessageDialog(this,
        "Rental saved successfully!\n"
                + "Rental ID: " + rentalId
                + "\nTotal Amount: ₱" + total
                + "\n\n" + emailStatus);

clearCart();
loadEquipment();
loadRentals();

            } catch (Exception e) {

                conn.rollback();
                showError("Rental was not saved. Changes were rolled back.\n"
                        + e.getMessage());

            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        }
    
    }

    // LOAD RENTAL RECORDS
    private void loadRentals() {

        rentalModel.setRowCount(0);

        String sql = "SELECT r.rental_id, c.full_name, r.rental_date, "
                + "r.expected_return_date, r.rental_status, r.total_amount "
                + "FROM rentals r "
                + "INNER JOIN customers c ON r.customer_id = c.customer_id "
                + "ORDER BY r.rental_id DESC";

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) return;

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {

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
            showError("Unable to load rental records: " + e.getMessage());
        }
    }

    // REFRESH DATA
    private void refreshData() {
        loadCustomers();
        loadEquipment();
        loadRentals();
    }

    // CLEAR CART
    private void clearCart() {
        cart.clear();
        cartModel.setRowCount(0);
        quantityField.setText("1");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "RentEase",
                JOptionPane.ERROR_MESSAGE);
    }

    // CUSTOMER CLASS
    private static class Customer {

        int id;
        String name;

        Customer(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    // EQUIPMENT CLASS
    private static class Equipment {

        int id;
        String name;
        BigDecimal dailyRate;
        int available;

        Equipment(int id, String name, BigDecimal dailyRate, int available) {
            this.id = id;
            this.name = name;
            this.dailyRate = dailyRate;
            this.available = available;
        }

        @Override
        public String toString() {
            return name + " (Available: " + available
                    + ", ₱" + dailyRate + "/day)";
        }
    }

    // RENTAL ITEM CLASS
    private static class RentalItem {

        int equipmentId;
        String name;
        int quantity;
        BigDecimal dailyRate;
        int days;
        BigDecimal subtotal;

        RentalItem(int equipmentId, String name, int quantity,
                   BigDecimal dailyRate, int days, BigDecimal subtotal) {

            this.equipmentId = equipmentId;
            this.name = name;
            this.quantity = quantity;
            this.dailyRate = dailyRate;
            this.days = days;
            this.subtotal = subtotal;
        }
    }
}
