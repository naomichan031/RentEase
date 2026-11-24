
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ReportsFrame extends JFrame {

    private String adminName;
    private JComboBox<String> reportType;
    private JTable table;
    private DefaultTableModel model;
    private JLabel summaryLabel;

    public ReportsFrame(String adminName) {
        this.adminName = adminName;

        setTitle("RentEase - Reports Management | " + adminName);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel heading = new JLabel("Reports Management | Admin: " + adminName);
        heading.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        topPanel.add(heading, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Select Report:"));

        reportType = new JComboBox<>(new String[]{
            "Rental Transactions",
            "Payment Records",
            "Equipment Inventory",
            "Customer List",
            "Returns and Late Fees"
        });

        JButton refreshButton = new JButton("Refresh");
        filterPanel.add(reportType);
        filterPanel.add(refreshButton);

        topPanel.add(filterPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        model = new DefaultTableModel();
        table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        add(scrollPane, BorderLayout.CENTER);

        summaryLabel = new JLabel("Select a report to view its records.");
        summaryLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        add(summaryLabel, BorderLayout.SOUTH);

        reportType.addActionListener(e -> loadReport());
        refreshButton.addActionListener(e -> loadReport());

        loadReport();
        setVisible(true);
    }

    private void loadReport() {
        String selected = (String) reportType.getSelectedItem();

        switch (selected) {
            case "Rental Transactions":
                loadRentalReport();
                break;

            case "Payment Records":
                loadPaymentReport();
                break;

            case "Equipment Inventory":
                loadEquipmentReport();
                break;

            case "Customer List":
                loadCustomerReport();
                break;

            case "Returns and Late Fees":
                loadReturnsReport();
                break;

            default:
                break;
        }
    }

    private void loadRentalReport() {
        String sql =
            "SELECT r.rental_id, c.full_name, u.full_name AS processed_by, " +
            "r.rental_date, r.expected_return_date, r.rental_status, r.total_amount " +
            "FROM rentals r " +
            "JOIN customers c ON r.customer_id = c.customer_id " +
            "JOIN users u ON r.user_id = u.user_id " +
            "ORDER BY r.rental_id DESC";

        loadTable(sql, new String[]{
            "Rental ID", "Customer", "Processed By",
            "Rental Date", "Expected Return", "Status", "Total Amount"
        }, "Rental transactions");
    }

    private void loadPaymentReport() {
        String sql =
            "SELECT p.payment_id, p.rental_id, c.full_name, p.amount, " +
            "p.payment_method, p.payment_date, p.payment_status, p.reference_number " +
            "FROM payments p " +
            "JOIN rentals r ON p.rental_id = r.rental_id " +
            "JOIN customers c ON r.customer_id = c.customer_id " +
            "ORDER BY p.payment_id DESC";

        loadTable(sql, new String[]{
            "Payment ID", "Rental ID", "Customer", "Amount",
            "Method", "Payment Date", "Status", "Reference"
        }, "Payment records");
    }

    private void loadEquipmentReport() {
        String sql =
            "SELECT e.equipment_id, e.equipment_name, c.category_name, " +
            "e.brand, e.daily_rate, e.quantity_total, " +
            "e.quantity_available, e.equipment_status " +
            "FROM equipment e " +
            "JOIN categories c ON e.category_id = c.category_id " +
            "ORDER BY e.equipment_id";

        loadTable(sql, new String[]{
            "Equipment ID", "Equipment", "Category", "Brand",
            "Daily Rate", "Total Quantity", "Available", "Status"
        }, "Equipment inventory");
    }

    private void loadCustomerReport() {
        String sql =
            "SELECT customer_id, full_name, phone, email, address, created_at " +
            "FROM customers ORDER BY customer_id DESC";

        loadTable(sql, new String[]{
            "Customer ID", "Full Name", "Phone",
            "Email", "Address", "Date Created"
        }, "Customer records");
    }

    private void loadReturnsReport() {
        String sql =
            "SELECT ret.return_id, ret.rental_id, c.full_name, " +
            "ret.return_date, ret.late_fee, ret.condition_notes, u.full_name " +
            "FROM returns ret " +
            "JOIN rentals r ON ret.rental_id = r.rental_id " +
            "JOIN customers c ON r.customer_id = c.customer_id " +
            "JOIN users u ON ret.received_by = u.user_id " +
            "ORDER BY ret.return_id DESC";

        loadTable(sql, new String[]{
            "Return ID", "Rental ID", "Customer", "Return Date",
            "Late Fee", "Condition Notes", "Received By"
        }, "Return and late fee records");
    }

    private void loadTable(String sql, String[] columns, String reportName) {
        model.setRowCount(0);
        model.setColumnCount(0);

        for (String column : columns) {
            model.addColumn(column);
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            int count = 0;
            int columnCount = columns.length;

            while (rs.next()) {
                Object[] row = new Object[columnCount];

                for (int i = 0; i < columnCount; i++) {
                    row[i] = rs.getObject(i + 1);
                }

                model.addRow(row);
                count++;
            }

            summaryLabel.setText(
                reportName + " | Total Records: " + count
            );

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Unable to load report:\n" + ex.getMessage(),
                "Report Error",
                JOptionPane.ERROR_MESSAGE
            );

            summaryLabel.setText("Unable to load " + reportName + ".");
        }
    }
}
