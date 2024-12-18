/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student_1;

/**
 *
 * @author AkioCkist
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StaffApprovalApp {
    private JFrame frame;
    private JTable table;
    private DefaultTableModel model;
    private JButton approveButton, rejectButton;
    private Connection connection;

    public StaffApprovalApp() {
        initialize();
        connectToDatabase();
        loadPendingRequests();
    }

    private void initialize() {
        frame = new JFrame("Staff Approval");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);

        model = new DefaultTableModel(new String[]{"Select", "ID", "courseMail", "Column", "Value", "Status"}, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) return Boolean.class; // Column 0 for checkboxes
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Only the checkbox column is editable
            }
        };

        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        approveButton = new JButton("Approve Selected");
        rejectButton = new JButton("Reject Selected");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        approveButton.addActionListener(e -> approveSelected());
        rejectButton.addActionListener(e -> rejectSelected());

        frame.setVisible(true);
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/student", "root", "");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Database Connection Failed: " + ex.getMessage());
        }
    }

    private void loadPendingRequests() {
        try {
            model.setRowCount(0);
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM approval WHERE status = 'Pending'");
            while (rs.next()) {
                model.addRow(new Object[]{
                        true, // Checkbox is selected by default
                        rs.getInt("id"),
                        rs.getString("courseMail"),
                        rs.getString("columnName"),
                        rs.getBoolean("newValue"),
                        rs.getString("status")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error loading requests: " + ex.getMessage());
        }
    }

    private void approveSelected() {
        try {
            for (int i = 0; i < table.getRowCount(); i++) {
                boolean isChecked = (Boolean) table.getValueAt(i, 0); // Check if checkbox is selected
                if (isChecked) {
                    int id = (int) table.getValueAt(i, 1);
                    String courseMail = (String) table.getValueAt(i, 2);
                    String column = (String) table.getValueAt(i, 3);
                    boolean value = (Boolean) table.getValueAt(i, 4);

                    // Update main table
                    String updateQuery = "UPDATE course SET " + column + " = ? WHERE courseMail = ?";
                    PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                    updateStmt.setBoolean(1, value);
                    updateStmt.setString(2, courseMail);
                    updateStmt.executeUpdate();

                    // Delete row from approval table
                    deleteApprovalRow(id);
                }
            }

            JOptionPane.showMessageDialog(frame, "Selected requests approved successfully!");
            loadPendingRequests(); // Refresh the table
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error approving requests: " + ex.getMessage());
        }
    }

    private void rejectSelected() {
        try {
            for (int i = 0; i < table.getRowCount(); i++) {
                boolean isChecked = (Boolean) table.getValueAt(i, 0);
                if (isChecked) {
                    int id = (int) table.getValueAt(i, 1);

                    // Update the approval table's status to 'Rejected' and delete the row
                    String updateQuery = "UPDATE approval SET status = 'Rejected' WHERE id = ?";
                    PreparedStatement statusStmt = connection.prepareStatement(updateQuery);
                    statusStmt.setInt(1, id);
                    statusStmt.executeUpdate();

                    // Delete row from approval table
                    deleteApprovalRow(id);
                }
            }

            JOptionPane.showMessageDialog(frame, "Selected requests rejected successfully!");
            loadPendingRequests(); // Refresh the table
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error rejecting requests: " + ex.getMessage());
        }
    }

    private void deleteApprovalRow(int id) {
        try {
            String deleteQuery = "DELETE FROM approval WHERE id = ?";
            PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery);
            deleteStmt.setInt(1, id);
            deleteStmt.executeUpdate();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error deleting row: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StaffApprovalApp::new);
    }
}



