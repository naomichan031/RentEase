
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.math.BigDecimal;

public class EquipmentFrame extends JFrame {

    private JTextField nameField;
    private JTextField brandField;
    private JTextField rateField;
    private JTextField totalField;
    private JTextField availableField;
    private JTextField searchField;

    private JTextArea descriptionArea;
    private JComboBox<Category> categoryBox;
    private JComboBox<String> statusBox;

    private JTable equipmentTable;
    private DefaultTableModel tableModel;

    private int selectedEquipmentId = -1;

    private final Color NAVY = new Color(25, 45, 75);
    private final Color BACKGROUND = new Color(243, 246, 250);

    public EquipmentFrame(String adminName) {

        setTitle("RentEase - Equipment Management");
        setSize(1150, 720);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(BACKGROUND);

        add(createHeader(adminName), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);

        loadCategories();
        loadEquipment("");

        setVisible(true);
    }

    // =====================================
    // HEADER
    // =====================================

    private JPanel createHeader(String adminName) {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(NAVY);
        panel.setBorder(
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        );

        JLabel title = new JLabel("Equipment Management");
        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setForeground(Color.WHITE);

        JLabel user = new JLabel("Administrator: " + adminName);
        user.setForeground(Color.WHITE);

        panel.add(title, BorderLayout.WEST);
        panel.add(user, BorderLayout.EAST);

        return panel;
    }

    // =====================================
    // MAIN PANEL
    // =====================================

    private JPanel createMainPanel() {

        JPanel main = new JPanel(new BorderLayout(15, 15));
        main.setBackground(BACKGROUND);
        main.setBorder(
            BorderFactory.createEmptyBorder(10, 20, 20, 20)
        );

        main.add(createForm(), BorderLayout.WEST);
        main.add(createTablePanel(), BorderLayout.CENTER);

        return main;
    }

    // =====================================
    // EQUIPMENT FORM
    // =====================================

    private JPanel createForm() {

        JPanel form = new JPanel(new GridBagLayout());
        form.setPreferredSize(new Dimension(310, 0));
        form.setBackground(Color.WHITE);
        form.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(220, 225, 232)
                ),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            )
        );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);

        JLabel heading = new JLabel("Equipment Details");
        heading.setFont(new Font("Arial", Font.BOLD, 19));

        nameField = new JTextField();
        brandField = new JTextField();
        rateField = new JTextField();
        totalField = new JTextField();
        availableField = new JTextField();

        descriptionArea = new JTextArea(3, 15);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        categoryBox = new JComboBox<>();

        statusBox = new JComboBox<>(
            new String[] {
                "AVAILABLE",
                "UNAVAILABLE",
                "MAINTENANCE"
            }
        );

        int row = 0;

        addField(form, gbc, row++, heading);
        addLabel(form, gbc, row++, "Equipment Name");
        addField(form, gbc, row++, nameField);

        addLabel(form, gbc, row++, "Category");
        addField(form, gbc, row++, categoryBox);

        addLabel(form, gbc, row++, "Brand");
        addField(form, gbc, row++, brandField);

        addLabel(form, gbc, row++, "Description");
        addField(form, gbc, row++, new JScrollPane(descriptionArea));

        addLabel(form, gbc, row++, "Daily Rental Rate");
        addField(form, gbc, row++, rateField);

        addLabel(form, gbc, row++, "Total Quantity");
        addField(form, gbc, row++, totalField);

        addLabel(form, gbc, row++, "Available Quantity");
        addField(form, gbc, row++, availableField);

        addLabel(form, gbc, row++, "Equipment Status");
        addField(form, gbc, row++, statusBox);

        JPanel buttons = new JPanel(new GridLayout(2, 2, 5, 5));
        buttons.setBackground(Color.WHITE);

        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");

        addButton.addActionListener(e -> addEquipment());
        updateButton.addActionListener(e -> updateEquipment());
        deleteButton.addActionListener(e -> deleteEquipment());
        clearButton.addActionListener(e -> clearForm());

        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(clearButton);

        addField(form, gbc, row++, buttons);

        gbc.weighty = 1;
        form.add(new JPanel() {{
            setOpaque(false);
        }}, gbc);

        return form;
    }

    private void addLabel(
        JPanel panel,
        GridBagConstraints gbc,
        int row,
        String text
    ) {

        gbc.gridy = row;
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(label, gbc);
    }

    private void addField(
        JPanel panel,
        GridBagConstraints gbc,
        int row,
        Component component
    ) {

        gbc.gridy = row;
        panel.add(component, gbc);
    }

    // =====================================
    // TABLE
    // =====================================

    private JPanel createTablePanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND);

        JPanel searchPanel = new JPanel(new BorderLayout(8, 8));
        searchPanel.setBackground(BACKGROUND);

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 35));

        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");

        searchButton.addActionListener(
            e -> loadEquipment(searchField.getText().trim())
        );

        refreshButton.addActionListener(e -> {
            searchField.setText("");
            loadEquipment("");
        });

        JPanel searchButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchButtons.setBackground(BACKGROUND);
        searchButtons.add(searchButton);
        searchButtons.add(refreshButton);

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButtons, BorderLayout.EAST);

        String[] columns = {
            "ID",
            "Equipment",
            "Category",
            "Brand",
            "Daily Rate",
            "Total",
            "Available",
            "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        equipmentTable = new JTable(tableModel);
        equipmentTable.setRowHeight(28);
        equipmentTable.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );

        equipmentTable.getSelectionModel()
            .addListSelectionListener(e -> {

                if (!e.getValueIsAdjusting()
                    && equipmentTable.getSelectedRow() != -1) {

                    selectEquipment();
                }
            });

        JScrollPane scrollPane = new JScrollPane(equipmentTable);
        scrollPane.setBackground(Color.WHITE);

        JLabel tableTitle = new JLabel("Equipment Inventory");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 18));

        panel.add(tableTitle, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setBackground(BACKGROUND);
        center.add(searchPanel, BorderLayout.NORTH);
        center.add(scrollPane, BorderLayout.CENTER);

        panel.add(center, BorderLayout.CENTER);

        return panel;
    }

    // =====================================
    // CATEGORY DROPDOWN
    // =====================================

    private static class Category {

        int id;
        String name;

        Category(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private void loadCategories() {

        categoryBox.removeAllItems();

        String sql =
            "SELECT category_id, category_name FROM categories ORDER BY category_name";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                categoryBox.addItem(
                    new Category(
                        rs.getInt("category_id"),
                        rs.getString("category_name")
                    )
                );
            }

        } catch (SQLException e) {
            showError("Unable to load categories: " + e.getMessage());
        }
    }

    // =====================================
    // LOAD EQUIPMENT
    // =====================================

    private void loadEquipment(String search) {

        tableModel.setRowCount(0);

        String sql = """
            SELECT e.equipment_id,
                   e.category_id,
                   e.equipment_name,
                   c.category_name,
                   e.description,
                   e.brand,
                   e.daily_rate,
                   e.quantity_total,
                   e.quantity_available,
                   e.equipment_status
            FROM equipment e
            LEFT JOIN categories c
                ON e.category_id = c.category_id
            WHERE e.equipment_name LIKE ?
               OR e.brand LIKE ?
               OR c.category_name LIKE ?
            ORDER BY e.equipment_id DESC
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String keyword = "%" + search + "%";

            stmt.setString(1, keyword);
            stmt.setString(2, keyword);
            stmt.setString(3, keyword);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    tableModel.addRow(new Object[] {
                        rs.getInt("equipment_id"),
                        rs.getString("equipment_name"),
                        rs.getString("category_name"),
                        rs.getString("brand"),
                        rs.getBigDecimal("daily_rate"),
                        rs.getInt("quantity_total"),
                        rs.getInt("quantity_available"),
                        rs.getString("equipment_status")
                    });
                }
            }

        } catch (SQLException e) {
            showError("Unable to load equipment: " + e.getMessage());
        }
    }

    // =====================================
    // VALIDATE FORM
    // =====================================

    private boolean validateForm() {

        try {

            if (nameField.getText().trim().isEmpty()) {
                showError("Equipment name is required.");
                return false;
            }

            if (categoryBox.getSelectedItem() == null) {
                showError("Please add a category first.");
                return false;
            }

            BigDecimal rate =
                new BigDecimal(rateField.getText().trim());

            int total = Integer.parseInt(
                totalField.getText().trim()
            );

            int available = Integer.parseInt(
                availableField.getText().trim()
            );

            if (rate.compareTo(BigDecimal.ZERO) < 0) {
                showError("Rental rate cannot be negative.");
                return false;
            }

            if (total < 0 || available < 0) {
                showError("Quantities cannot be negative.");
                return false;
            }

            if (available > total) {
                showError(
                    "Available quantity cannot exceed total quantity."
                );
                return false;
            }

            return true;

        } catch (NumberFormatException e) {
            showError("Enter valid numeric values for rate and quantity.");
            return false;
        }
    }

    // =====================================
    // ADD EQUIPMENT
    // =====================================

    private void addEquipment() {

        if (!validateForm()) return;

        String sql = """
            INSERT INTO equipment
            (category_id, equipment_name, description,
             brand, daily_rate, quantity_total,
             quantity_available, equipment_status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        Category category =
            (Category) categoryBox.getSelectedItem();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, category.id);
            stmt.setString(2, nameField.getText().trim());
            stmt.setString(3, descriptionArea.getText().trim());
            stmt.setString(4, brandField.getText().trim());
            stmt.setBigDecimal(
                5, new BigDecimal(rateField.getText().trim())
            );
            stmt.setInt(
                6, Integer.parseInt(totalField.getText().trim())
            );
            stmt.setInt(
                7, Integer.parseInt(availableField.getText().trim())
            );
            stmt.setString(
                8, (String) statusBox.getSelectedItem()
            );

            stmt.executeUpdate();

            JOptionPane.showMessageDialog(
                this, "Equipment added successfully!"
            );

            clearForm();
            loadEquipment("");

        } catch (SQLException e) {
            showError("Unable to add equipment: " + e.getMessage());
        }
    }

    // =====================================
    // SELECT TABLE ROW
    // =====================================

    private void selectEquipment() {

        int row = equipmentTable.getSelectedRow();

        if (row == -1) return;

        selectedEquipmentId = Integer.parseInt(
            tableModel.getValueAt(row, 0).toString()
        );

        String sql = """
            SELECT * FROM equipment WHERE equipment_id = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, selectedEquipmentId);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    nameField.setText(
                        rs.getString("equipment_name")
                    );

                    brandField.setText(
                        rs.getString("brand") == null
                            ? "" : rs.getString("brand")
                    );

                    descriptionArea.setText(
                        rs.getString("description") == null
                            ? "" : rs.getString("description")
                    );

                    rateField.setText(
                        rs.getBigDecimal("daily_rate").toString()
                    );

                    totalField.setText(
                        String.valueOf(rs.getInt("quantity_total"))
                    );

                    availableField.setText(
                        String.valueOf(rs.getInt("quantity_available"))
                    );

                    statusBox.setSelectedItem(
                        rs.getString("equipment_status")
                    );

                    int categoryId = rs.getInt("category_id");

                    for (int i = 0; i < categoryBox.getItemCount(); i++) {

                        Category category = categoryBox.getItemAt(i);

                        if (category.id == categoryId) {
                            categoryBox.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }

        } catch (SQLException e) {
            showError("Unable to select equipment: " + e.getMessage());
        }
    }

    // =====================================
    // UPDATE EQUIPMENT
    // =====================================

    private void updateEquipment() {

        if (selectedEquipmentId == -1) {
            showError("Please select equipment from the table.");
            return;
        }

        if (!validateForm()) return;

        String sql = """
            UPDATE equipment
            SET category_id = ?,
                equipment_name = ?,
                description = ?,
                brand = ?,
                daily_rate = ?,
                quantity_total = ?,
                quantity_available = ?,
                equipment_status = ?
            WHERE equipment_id = ?
            """;

        Category category =
            (Category) categoryBox.getSelectedItem();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, category.id);
            stmt.setString(2, nameField.getText().trim());
            stmt.setString(3, descriptionArea.getText().trim());
            stmt.setString(4, brandField.getText().trim());
            stmt.setBigDecimal(
                5, new BigDecimal(rateField.getText().trim())
            );
            stmt.setInt(
                6, Integer.parseInt(totalField.getText().trim())
            );
            stmt.setInt(
                7, Integer.parseInt(availableField.getText().trim())
            );
            stmt.setString(
                8, (String) statusBox.getSelectedItem()
            );
            stmt.setInt(9, selectedEquipmentId);

            stmt.executeUpdate();

            JOptionPane.showMessageDialog(
                this, "Equipment updated successfully!"
            );

            clearForm();
            loadEquipment("");

        } catch (SQLException e) {
            showError("Unable to update equipment: " + e.getMessage());
        }
    }

    // =====================================
    // DELETE EQUIPMENT
    // =====================================

    private void deleteEquipment() {

        if (selectedEquipmentId == -1) {
            showError("Please select equipment to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this equipment?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        String sql =
            "DELETE FROM equipment WHERE equipment_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, selectedEquipmentId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(
                this, "Equipment deleted successfully!"
            );

            clearForm();
            loadEquipment("");

        } catch (SQLException e) {
            showError(
                "Unable to delete equipment. It may be referenced by rental records.\n"
                + e.getMessage()
            );
        }
    }

    // =====================================
    // CLEAR FORM
    // =====================================

    private void clearForm() {

        selectedEquipmentId = -1;

        nameField.setText("");
        brandField.setText("");
        rateField.setText("");
        totalField.setText("");
        availableField.setText("");
        descriptionArea.setText("");

        if (categoryBox.getItemCount() > 0) {
            categoryBox.setSelectedIndex(0);
        }

        statusBox.setSelectedIndex(0);

        equipmentTable.clearSelection();
    }

    // =====================================
    // ERROR MESSAGE
    // =====================================

    private void showError(String message) {

        JOptionPane.showMessageDialog(
            this,
            message,
            "RentEase",
            JOptionPane.ERROR_MESSAGE
        );
    }

}
