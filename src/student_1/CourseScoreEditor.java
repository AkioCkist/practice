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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class CourseScoreEditor {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/student";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private Connection connection;
    private JFrame frame;
    private JComboBox<String> studentDropdown;
    private JPanel scorePanel;
    private Map<String, JTextField> scoreFields;

    public CourseScoreEditor() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            initializeUI();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to connect to the database: " + e.getMessage());
            System.exit(1);
        }
    }

    private void initializeUI() {
        frame = new JFrame("Course Score Editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout());
        studentDropdown = new JComboBox<>();
        JButton loadButton = new JButton("Load Scores");
        topPanel.add(new JLabel("Select Student:"));
        topPanel.add(studentDropdown);
        topPanel.add(loadButton);

        scorePanel = new JPanel();
        scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.Y_AXIS));

        JButton saveButton = new JButton("Save Scores");

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(scorePanel), BorderLayout.CENTER);
        frame.add(saveButton, BorderLayout.SOUTH);

        loadButton.addActionListener(e -> loadScores());
        saveButton.addActionListener(e -> saveScores());

        loadStudentList();

        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void loadStudentList() {
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery("SELECT courseMail FROM courseScore")) {
            studentDropdown.removeAllItems();
            while (rs.next()) {
                studentDropdown.addItem(rs.getString("courseMail"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Failed to load student list: " + e.getMessage());
        }
    }

    private void loadScores() {
        String selectedStudent = (String) studentDropdown.getSelectedItem();
        if (selectedStudent == null) {
            JOptionPane.showMessageDialog(frame, "Please select a student.");
            return;
        }

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM courseScore WHERE courseMail = '" + selectedStudent + "'")) {
            if (rs.next()) {
                ResultSetMetaData metaData = rs.getMetaData();
                scoreFields = new HashMap<>();
                scorePanel.removeAll();

                for (int i = 2; i <= metaData.getColumnCount(); i++) {
                    String columnName = metaData.getColumnName(i);
                    if (!rs.wasNull() && rs.getObject(columnName) != null) {
                        double value = rs.getDouble(columnName);

                        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                        JLabel label = new JLabel(columnName + ":");
                        JTextField textField = new JTextField(String.valueOf(value), 10);

                        scoreFields.put(columnName, textField);

                        rowPanel.add(label);
                        rowPanel.add(textField);
                        scorePanel.add(rowPanel);
                    }
                }

                scorePanel.revalidate();
                scorePanel.repaint();
            } else {
                JOptionPane.showMessageDialog(frame, "No scores found for the selected student.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Failed to load scores: " + e.getMessage());
        }
    }

    private void saveScores() {
        String selectedStudent = (String) studentDropdown.getSelectedItem();
        if (selectedStudent == null) {
            JOptionPane.showMessageDialog(frame, "Please select a student.");
            return;
        }

        try {
            StringBuilder updateQuery = new StringBuilder("UPDATE courseScore SET ");
            for (Map.Entry<String, JTextField> entry : scoreFields.entrySet()) {
                String column = entry.getKey();
                String value = entry.getValue().getText();
                updateQuery.append(column).append(" = ").append(value).append(", ");
            }

            updateQuery.setLength(updateQuery.length() - 2); // Remove last comma
            updateQuery.append(" WHERE courseMail = '").append(selectedStudent).append("'");

            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate(updateQuery.toString());
                JOptionPane.showMessageDialog(frame, "Scores updated successfully.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Failed to save scores: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CourseScoreEditor::new);
    }
}


