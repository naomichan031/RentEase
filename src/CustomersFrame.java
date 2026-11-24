
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class CustomersFrame extends JFrame {

    private final Color NAVY = new Color(25, 45, 75);
    private final Color BACKGROUND = new Color(243, 246, 250);

    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextArea addressArea;
    private JTextField searchField;

    private JTable customerTable;
    private DefaultTableModel tableModel;

    private int selectedCustomerId = -1;

    public CustomersFrame(String adminName) {

        setTitle("RentEase - Customers Management");
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND);

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(NAVY);
        header.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("Customers Management");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(Color.WHITE);

        JLabel adminLabel = new JLabel("Administrator: " + adminName);
        adminLabel.setForeground(Color.WHITE);

        header.add(title, BorderLayout.WEST);
        header.add(adminLabel, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // MAIN CONTENT
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));

        // CUSTOMER FORM
        JPanel formPanel = new JPanel(null);
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createLineBorder(
                new Color(215, 220, 230)));

        JLabel formTitle = new JLabel("Customer Details");
        formTitle.setFont(new Font("Arial", Font.BOLD, 23));
        formTitle.setBounds(25, 20, 300, 35);
        formPanel.add(formTitle);

        JLabel nameLabel = new JLabel("Full Name");
        nameLabel.setBounds(25, 75, 300, 25);
        formPanel.add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(25, 105, 350, 35);
        formPanel.add(nameField);

        JLabel phoneLabel = new JLabel("Phone Number");
        phoneLabel.setBounds(25, 155, 300, 25);
        formPanel.add(phoneLabel);

        phoneField = new JTextField();
        phoneField.setBounds(25, 185, 350, 35);
        formPanel.add(phoneField);

        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setBounds(25, 235, 300, 25);
        formPanel.add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(25, 265, 350, 35);
        formPanel.add(emailField);

        JLabel addressLabel = new JLabel("Address");
        addressLabel.setBounds(25, 315, 300, 25);
        formPanel.add(addressLabel);

        addressArea = new JTextArea();
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);

        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressScroll.setBounds(25, 345, 350, 100);
        formPanel.add(addressScroll);

        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");

        addButton.setBounds(25, 490, 165, 35);
        updateButton.setBounds(205, 490, 170, 35);
        deleteButton.setBounds(25, 540, 165, 35);
        clearButton.setBounds(205, 540, 170, 35);

        formPanel.add(addButton);
        formPanel.add(updateButton);
        formPanel.add(deleteButton);
        formPanel.add(clearButton);

        // CUSTOMER TABLE
        JPanel tablePanel = new JPanel(new BorderLayout(0, 10));
        tablePanel.setBackground(BACKGROUND);

        JLabel tableTitle = new JLabel("Customer List");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 23));

        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(BACKGROUND);

        searchField = new JTextField();

        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");

        searchPanel.add(searchField, BorderLayout.CENTER);

        JPanel searchButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchButtons.setBackground(BACKGROUND);
        searchButtons.add(searchButton);
        searchButtons.add(refreshButton);

        searchPanel.add(searchButtons, BorderLayout.EAST);

        JPanel tableTop = new JPanel(new BorderLayout(0, 10));
        tableTop.setBackground(BACKGROUND);
        tableTop.add(tableTitle, BorderLayout.NORTH);
        tableTop.add(searchPanel, BorderLayout.CENTER);

        tablePanel.add(tableTop, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new String[]{"Customer ID", "Full Name", "Phone", "Email", "Address"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        customerTable = new JTable(tableModel);
        customerTable.setRowHeight(28);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customerTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        customerTable.getColumnModel().getColumn(0).setPreferredWidth(85);
        customerTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        customerTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        customerTable.getColumnModel().getColumn(3).setPreferredWidth(180);
        customerTable.getColumnModel().getColumn(4).setPreferredWidth(200);

        JScrollPane tableScroll = new JScrollPane(customerTable);
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        mainPanel.add(formPanel);
        mainPanel.add(tablePanel);

        add(mainPanel, BorderLayout.CENTER);

        // BUTTON ACTIONS
        addButton.addActionListener(e -> addCustomer());
        updateButton.addActionListener(e -> updateCustomer());
        deleteButton.addActionListener(e -> deleteCustomer());
        clearButton.addActionListener(e -> clearFields());
        searchButton.addActionListener(e -> searchCustomers());
        refreshButton.addActionListener(e -> loadCustomers());

        // TABLE SELECTION
        customerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = customerTable.getSelectedRow();

                if (row >= 0) {
                    selectedCustomerId = Integer.parseInt(
                            tableModel.getValueAt(row, 0).toString());

                    nameField.setText(tableModel.getValueAt(row, 1).toString());
                    phoneField.setText(tableModel.getValueAt(row, 2).toString());
                    emailField.setText(tableModel.getValueAt(row, 3).toString());

                    Object address = tableModel.getValueAt(row, 4);
                    addressArea.setText(address == null ? "" : address.toString());
                }
            }
        });

        loadCustomers();
        setVisible(true);
    }

    // ADD CUSTOMER
    private void addCustomer() {

        if (!validateFields()) return;

        String sql = "INSERT INTO customers "
                + "(full_name, phone, email, address) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) {
                showError("Database connection failed.");
                return;
            }

            try (PreparedStatement pst = conn.prepareStatement(sql)) {

                pst.setString(1, nameField.getText().trim());
                pst.setString(2, phoneField.getText().trim());
                pst.setString(3, emailField.getText().trim());
                pst.setString(4, addressArea.getText().trim());

                pst.executeUpdate();

                JOptionPane.showMessageDialog(this,
                        "Customer added successfully!");

                loadCustomers();
                clearFields();
            }

        } catch (SQLException e) {
            showError("Unable to add customer: " + e.getMessage());
        }
    }

    // UPDATE CUSTOMER
    private void updateCustomer() {

        if (selectedCustomerId == -1) {
            showError("Please select a customer from the table.");
            return;
        }

        if (!validateFields()) return;

        String sql = "UPDATE customers SET full_name=?, phone=?, "
                + "email=?, address=? WHERE customer_id=?";

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) {
                showError("Database connection failed.");
                return;
            }

            try (PreparedStatement pst = conn.prepareStatement(sql)) {

                pst.setString(1, nameField.getText().trim());
                pst.setString(2, phoneField.getText().trim());
                pst.setString(3, emailField.getText().trim());
                pst.setString(4, addressArea.getText().trim());
                pst.setInt(5, selectedCustomerId);

                pst.executeUpdate();

                JOptionPane.showMessageDialog(this,
                        "Customer updated successfully!");

                loadCustomers();
                clearFields();
            }

        } catch (SQLException e) {
            showError("Unable to update customer: " + e.getMessage());
        }
    }

    // DELETE CUSTOMER
    private void deleteCustomer() {

        if (selectedCustomerId == -1) {
            showError("Please select a customer from the table.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this customer?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        String sql = "DELETE FROM customers WHERE customer_id=?";

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) {
                showError("Database connection failed.");
                return;
            }

            try (PreparedStatement pst = conn.prepareStatement(sql)) {

                pst.setInt(1, selectedCustomerId);
                pst.executeUpdate();

                JOptionPane.showMessageDialog(this,
                        "Customer deleted successfully!");

                loadCustomers();
                clearFields();
            }

        } catch (SQLException e) {
            showError("Unable to delete customer. This customer may have existing rental records.\n"
                    + e.getMessage());
        }
    }

    // LOAD CUSTOMERS
    private void loadCustomers() {

        tableModel.setRowCount(0);

        String sql = "SELECT customer_id, full_name, phone, email, address "
                + "FROM customers ORDER BY customer_id DESC";

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) {
                showError("Database connection failed.");
                return;
            }

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {

                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                            rs.getInt("customer_id"),
                            rs.getString("full_name"),
                            rs.getString("phone"),
                            rs.getString("email"),
                            rs.getString("address")
                    });
                }
            }

        } catch (SQLException e) {
            showError("Unable to load customers: " + e.getMessage());
        }
    }

    // SEARCH CUSTOMERS
    private void searchCustomers() {

        tableModel.setRowCount(0);

        String keyword = searchField.getText().trim();

        String sql = "SELECT customer_id, full_name, phone, email, address "
                + "FROM customers WHERE full_name LIKE ? "
                + "OR phone LIKE ? OR email LIKE ? "
                + "ORDER BY customer_id DESC";

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) {
                showError("Database connection failed.");
                return;
            }

            try (PreparedStatement pst = conn.prepareStatement(sql)) {

                String search = "%" + keyword + "%";

                pst.setString(1, search);
                pst.setString(2, search);
                pst.setString(3, search);

                try (ResultSet rs = pst.executeQuery()) {

                    while (rs.next()) {
                        tableModel.addRow(new Object[]{
                                rs.getInt("customer_id"),
                                rs.getString("full_name"),
                                rs.getString("phone"),
                                rs.getString("email"),
                                rs.getString("address")
                        });
                    }
                }
            }

        } catch (SQLException e) {
            showError("Search failed: " + e.getMessage());
        }
    }

    // VALIDATE FIELDS
    private boolean validateFields() {

        if (nameField.getText().trim().isEmpty()) {
            showError("Please enter the customer's full name.");
            nameField.requestFocus();
            return false;
        }

        if (phoneField.getText().trim().isEmpty()) {
            showError("Please enter the customer's phone number.");
            phoneField.requestFocus();
            return false;
        }

        if (emailField.getText().trim().isEmpty()) {
            showError("Please enter the customer's email address.");
            emailField.requestFocus();
            return false;
        }

        if (!emailField.getText().trim().matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            showError("Please enter a valid email address.");
            emailField.requestFocus();
            return false;
        }

        return true;
    }

    // CLEAR FIELDS
    private void clearFields() {

        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressArea.setText("");
        searchField.setText("");

        selectedCustomerId = -1;
        customerTable.clearSelection();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message, "RentEase",
                JOptionPane.ERROR_MESSAGE);
    }
}
