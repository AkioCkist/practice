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
import java.util.ArrayList;
import java.util.List;

public class StudentApp {
    private JFrame frame;
    private JTextField searchField;
    private JButton searchButton, submitButton;
    private JTable table;
    private DefaultTableModel model;
    private Connection connection;
    private List<String> booleanColumns;

    public StudentApp() {
        booleanColumns = new ArrayList<>();
        initialize();
        connectToDatabase();
        fetchBooleanColumns();
    }

    private void initialize() {
        frame = new JFrame("Student Course Update Request");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 400);
        frame.setLayout(new BorderLayout());

        // Search Panel
        JPanel searchPanel = new JPanel();
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        searchPanel.add(new JLabel("Enter CourseMail:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Table Panel
        model = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int column) {
                return Boolean.class;
            }
        };
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Submit Button
        submitButton = new JButton("Submit Changes for Approval");

        frame.add(searchPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(submitButton, BorderLayout.SOUTH);

        // Event Listeners
        searchButton.addActionListener(e -> loadData(searchField.getText()));
        submitButton.addActionListener(e -> submitForApproval(searchField.getText()));

        frame.setVisible(true);
    }

    private void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/student", "root", "");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Database Connection Failed: " + ex.getMessage());
            System.exit(1);
        }
    }

    private void fetchBooleanColumns() {
        try {
            booleanColumns.clear();
            String query = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS " +
                    "WHERE TABLE_SCHEMA = 'student' AND TABLE_NAME = 'course' AND DATA_TYPE = 'tinyint'";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                booleanColumns.add(rs.getString("COLUMN_NAME"));
            }

            model.setColumnIdentifiers(booleanColumns.toArray());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error fetching columns: " + ex.getMessage());
        }
    }

    private void loadData(String courseMail) {
        model.setRowCount(0);
        if (booleanColumns.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No boolean columns found.");
            return;
        }

        try {
            StringBuilder query = new StringBuilder("SELECT ");
            for (String col : booleanColumns) query.append(col).append(", ");
            query.setLength(query.length() - 2); 
            query.append(" FROM course WHERE courseMail = ?");

            PreparedStatement pstmt = connection.prepareStatement(query.toString());
            pstmt.setString(1, courseMail);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Object[] rowData = new Object[booleanColumns.size()];
                for (int i = 0; i < booleanColumns.size(); i++) {
                    rowData[i] = rs.getBoolean(booleanColumns.get(i));
                }
                model.addRow(rowData);
            } else {
                JOptionPane.showMessageDialog(frame, "No record found.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error loading data: " + ex.getMessage());
        }
    }

    private void submitForApproval(String courseMail) {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(frame, "No data to submit.");
            return;
        }

        try {
            String query = "INSERT INTO approval (courseMail, columnName, newValue) VALUES (?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(query);

            for (int i = 0; i < booleanColumns.size(); i++) {
                boolean value = (Boolean) table.getValueAt(0, i);
                pstmt.setString(1, courseMail);
                pstmt.setString(2, booleanColumns.get(i));
                pstmt.setBoolean(3, value);
                pstmt.addBatch();
            }

            pstmt.executeBatch();
            JOptionPane.showMessageDialog(frame, "Changes submitted for approval!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error submitting changes: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudentApp::new);
    }
}






