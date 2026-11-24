
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class CategoriesFrame extends JFrame {

    private JTextField nameField;
    private JTextField searchField;
    private JTextArea descriptionArea;

    private JTable categoryTable;
    private DefaultTableModel tableModel;

    private int selectedCategoryId = -1;

    private final Color NAVY = new Color(25, 45, 75);
    private final Color BACKGROUND = new Color(243, 246, 250);

    public CategoriesFrame(String adminName) {

        setTitle("RentEase - Categories Management");
        setSize(1050, 680);
        setMinimumSize(new Dimension(850, 550));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(BACKGROUND);

        add(createHeader(adminName), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);

        loadCategories("");

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

        JLabel title = new JLabel("Categories Management");
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
            BorderFactory.createEmptyBorder(15, 20, 20, 20)
        );

        main.add(createForm(), BorderLayout.WEST);
        main.add(createTablePanel(), BorderLayout.CENTER);

        return main;
    }

    // =====================================
    // CATEGORY FORM
    // =====================================

    private JPanel createForm() {

        JPanel form = new JPanel(new GridBagLayout());
        form.setPreferredSize(new Dimension(300, 0));
        form.setBackground(Color.WHITE);

        form.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(220, 225, 232)
                ),
                BorderFactory.createEmptyBorder(20, 18, 20, 18)
            )
        );

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(7, 0, 7, 0);

        JLabel heading = new JLabel("Category Details");
        heading.setFont(new Font("Arial", Font.BOLD, 20));

        nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(250, 35));

        descriptionArea = new JTextArea(6, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        int row = 0;

        addField(form, gbc, row++, heading);

        addLabel(form, gbc, row++, "Category Name");
        addField(form, gbc, row++, nameField);

        addLabel(form, gbc, row++, "Description");
        addField(
            form,
            gbc,
            row++,
            new JScrollPane(descriptionArea)
        );

        JPanel buttons = new JPanel(new GridLayout(2, 2, 8, 8));
        buttons.setBackground(Color.WHITE);

        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");

        addButton.addActionListener(e -> addCategory());
        updateButton.addActionListener(e -> updateCategory());
        deleteButton.addActionListener(e -> deleteCategory());
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
    // CATEGORY TABLE
    // =====================================

    private JPanel createTablePanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND);

        JLabel heading = new JLabel("Category List");
        heading.setFont(new Font("Arial", Font.BOLD, 19));

        JPanel searchPanel = new JPanel(new BorderLayout(8, 8));
        searchPanel.setBackground(BACKGROUND);

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 35));

        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");

        searchButton.addActionListener(
            e -> loadCategories(searchField.getText().trim())
        );

        refreshButton.addActionListener(e -> {
            searchField.setText("");
            loadCategories("");
        });

        JPanel searchButtons = new JPanel(
            new FlowLayout(FlowLayout.LEFT)
        );

        searchButtons.setBackground(BACKGROUND);
        searchButtons.add(searchButton);
        searchButtons.add(refreshButton);

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButtons, BorderLayout.EAST);

        String[] columns = {
            "Category ID",
            "Category Name",
            "Description"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        categoryTable = new JTable(tableModel);
        categoryTable.setRowHeight(30);

        categoryTable.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );

        categoryTable.getSelectionModel()
            .addListSelectionListener(e -> {

                if (!e.getValueIsAdjusting()
                    && categoryTable.getSelectedRow() != -1) {

                    selectCategory();
                }
            });

        JScrollPane scrollPane = new JScrollPane(categoryTable);

        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setBackground(BACKGROUND);

        top.add(heading, BorderLayout.NORTH);
        top.add(searchPanel, BorderLayout.CENTER);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // =====================================
    // LOAD CATEGORIES
    // =====================================

    private void loadCategories(String search) {

        tableModel.setRowCount(0);

        String sql = """
            SELECT category_id, category_name, description
            FROM categories
            WHERE category_name LIKE ?
            ORDER BY category_id DESC
            """;

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) {
                showError("Database connection failed.");
                return;
            }

            try (PreparedStatement stmt =
                    conn.prepareStatement(sql)) {

                stmt.setString(1, "%" + search + "%");

                try (ResultSet rs = stmt.executeQuery()) {

                    while (rs.next()) {

                        tableModel.addRow(new Object[] {
                            rs.getInt("category_id"),
                            rs.getString("category_name"),
                            rs.getString("description")
                        });
                    }
                }
            }

        } catch (SQLException e) {
            showError(
                "Unable to load categories: " + e.getMessage()
            );
        }
    }

    // =====================================
    // VALIDATE FORM
    // =====================================

    private boolean validateForm() {

        String name = nameField.getText().trim();

        if (name.isEmpty()) {
            showError("Category name is required.");
            return false;
        }

        if (name.length() > 100) {
            showError("Category name must not exceed 100 characters.");
            return false;
        }

        return true;
    }

    // =====================================
    // ADD CATEGORY
    // =====================================

    private void addCategory() {

        if (!validateForm()) return;

        String sql = """
            INSERT INTO categories
            (category_name, description)
            VALUES (?, ?)
            """;

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) {
                showError("Database connection failed.");
                return;
            }

            try (PreparedStatement stmt =
                    conn.prepareStatement(sql)) {

                stmt.setString(1, nameField.getText().trim());
                stmt.setString(
                    2, descriptionArea.getText().trim()
                );

                stmt.executeUpdate();

                JOptionPane.showMessageDialog(
                    this,
                    "Category added successfully!"
                );

                clearForm();
                loadCategories("");

            }

        } catch (SQLException e) {

            if (e.getErrorCode() == 1062) {
                showError("This category name already exists.");
            } else {
                showError(
                    "Unable to add category: " + e.getMessage()
                );
            }
        }
    }

    // =====================================
    // SELECT CATEGORY
    // =====================================

    private void selectCategory() {

        int row = categoryTable.getSelectedRow();

        if (row == -1) return;

        selectedCategoryId = Integer.parseInt(
            tableModel.getValueAt(row, 0).toString()
        );

        nameField.setText(
            tableModel.getValueAt(row, 1).toString()
        );

        Object description = tableModel.getValueAt(row, 2);

        descriptionArea.setText(
            description == null ? "" : description.toString()
        );
    }

    // =====================================
    // UPDATE CATEGORY
    // =====================================

    private void updateCategory() {

        if (selectedCategoryId == -1) {
            showError("Please select a category from the table.");
            return;
        }

        if (!validateForm()) return;

        String sql = """
            UPDATE categories
            SET category_name = ?, description = ?
            WHERE category_id = ?
            """;

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) {
                showError("Database connection failed.");
                return;
            }

            try (PreparedStatement stmt =
                    conn.prepareStatement(sql)) {

                stmt.setString(1, nameField.getText().trim());
                stmt.setString(
                    2, descriptionArea.getText().trim()
                );
                stmt.setInt(3, selectedCategoryId);

                int result = stmt.executeUpdate();

                if (result > 0) {

                    JOptionPane.showMessageDialog(
                        this,
                        "Category updated successfully!"
                    );

                    clearForm();
                    loadCategories("");

                } else {
                    showError("Category not found.");
                }
            }

        } catch (SQLException e) {

            if (e.getErrorCode() == 1062) {
                showError("This category name already exists.");
            } else {
                showError(
                    "Unable to update category: " + e.getMessage()
                );
            }
        }
    }

    // =====================================
    // DELETE CATEGORY
    // =====================================

    private void deleteCategory() {

        if (selectedCategoryId == -1) {
            showError("Please select a category to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this category?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        String sql =
            "DELETE FROM categories WHERE category_id = ?";

        try (Connection conn = DBConnection.getConnection()) {

            if (conn == null) {
                showError("Database connection failed.");
                return;
            }

            try (PreparedStatement stmt =
                    conn.prepareStatement(sql)) {

                stmt.setInt(1, selectedCategoryId);

                int result = stmt.executeUpdate();

                if (result > 0) {

                    JOptionPane.showMessageDialog(
                        this,
                        "Category deleted successfully!"
                    );

                    clearForm();
                    loadCategories("");

                } else {
                    showError("Category not found.");
                }
            }

        } catch (SQLException e) {

            if (e.getErrorCode() == 1451) {
                showError(
                    "Cannot delete this category because it is "
                    + "currently assigned to existing equipment."
                );
            } else {
                showError(
                    "Unable to delete category: " + e.getMessage()
                );
            }
        }
    }

    // =====================================
    // CLEAR FORM
    // =====================================

    private void clearForm() {

        selectedCategoryId = -1;

        nameField.setText("");
        descriptionArea.setText("");

        categoryTable.clearSelection();
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
