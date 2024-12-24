package student_1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseImportApp {
    public static void main() {
        SwingUtilities.invokeLater(DatabaseImportApp::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Database Importer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 250);

        // Center the frame on screen
        frame.setLocationRelativeTo(null);

        // Panel for layout
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1, 10, 10));

        // Label
        JLabel label = new JLabel("Import database from file:");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label);

        // Format selection dropdown
        String[] formats = {"SQL", "CSV"};
        JComboBox<String> formatComboBox = new JComboBox<>(formats);
        panel.add(formatComboBox);

        // Import button
        JButton importButton = new JButton("Import");
        panel.add(importButton);

        // Cancel button
        JButton cancelButton = new JButton("Cancel");
        panel.add(cancelButton);

        frame.add(panel);
        frame.setVisible(true);

        // Import button action listener
        importButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedFormat = (String) formatComboBox.getSelectedItem();
                importDatabase(selectedFormat);
            }
        });

        // Cancel button action listener
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
            }
        });
    }

    private static void importDatabase(String format) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select " + format + " File to Import");

        int userSelection = fileChooser.showOpenDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToImport = fileChooser.getSelectedFile();
            try {
                if ("SQL".equals(format)) {
                    executeSQLFromFile(fileToImport);
                } else if ("CSV".equals(format)) {
                    importCSV(fileToImport);
                }
                JOptionPane.showMessageDialog(null, "Import successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error importing file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void executeSQLFromFile(File file) throws Exception {
        String url = "jdbc:mysql://localhost:3306/student";
        String user = "root";
        String password = "";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             BufferedReader br = new BufferedReader(new FileReader(file))) {

            StringBuilder sql = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sql.append(line);
                if (line.trim().endsWith(";")) { // Execute SQL command when a semicolon is found
                    stmt.execute(sql.toString());
                    sql.setLength(0); // Clear the StringBuilder for the next command
                }
            }
        }
    }

    private static void importCSV(File file) throws Exception {
        String url = "jdbc:mysql://localhost:3306/student";
        String user = "root";
        String password = "";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             BufferedReader br = new BufferedReader(new FileReader(file))) {

            String tableName = JOptionPane.showInputDialog(null, "Enter the table name:", "Table Name", JOptionPane.QUESTION_MESSAGE);
            if (tableName == null || tableName.trim().isEmpty()) {
                throw new IllegalArgumentException("Table name cannot be empty.");
            }

            String line;
            List<String> rows = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                rows.add(line);
            }

            if (!rows.isEmpty()) {
                String[] headers = rows.get(0).split(",");
                String createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " (";
                for (String header : headers) {
                    createTableSQL += header.trim() + " VARCHAR(255),";
                }
                createTableSQL = createTableSQL.replaceAll(",+$", ")");

                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(createTableSQL);

                    for (int i = 1; i < rows.size(); i++) {
                        String[] values = rows.get(i).split(",");
                        String insertSQL = "INSERT INTO " + tableName + " VALUES (";
                        for (String value : values) {
                            insertSQL += "'" + value.trim().replace("'", "''") + "',";
                        }
                        insertSQL = insertSQL.replaceAll(",+$", ")");
                        stmt.execute(insertSQL);
                    }
                }
            }
        }
    }
}
