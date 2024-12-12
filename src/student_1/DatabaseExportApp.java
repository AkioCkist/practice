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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseExportApp {
    public static void main() {
        SwingUtilities.invokeLater(DatabaseExportApp::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Database Exporter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 10, 10));

        JLabel label = new JLabel("Select export format:");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label);

        String[] formats = {"CSV", "SQL", "JSON", "XML"};
        JComboBox<String> formatComboBox = new JComboBox<>(formats);
        panel.add(formatComboBox);

        JButton exportButton = new JButton("Export");
        panel.add(exportButton);

        frame.add(panel);
        frame.setVisible(true);

        // Button action listener
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedFormat = (String) formatComboBox.getSelectedItem();
                if (selectedFormat != null) {
                    saveExport(selectedFormat);
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a format.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private static void saveExport(String format) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save as " + format);
        fileChooser.setSelectedFile(new File("export_all_tables." + getExtension(format)));

        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(fileToSave)) {
                // Fetch and write database data
                String data = fetchAllTablesData(format);
                writer.write(data);
                JOptionPane.showMessageDialog(null, "Export successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static String fetchAllTablesData(String format) {
        StringBuilder data = new StringBuilder();
        String url = "jdbc:mysql://localhost:3306/student"; 
        String user = "root"; 
        String password = ""; 

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement tableStmt = conn.createStatement();
             ResultSet tables = tableStmt.executeQuery("SHOW TABLES")) {

            while (tables.next()) {
                String tableName = tables.getString(1);
                data.append("-- Data for table: ").append(tableName).append("\n");

                try (Statement dataStmt = conn.createStatement();
                     ResultSet rs = dataStmt.executeQuery("SELECT * FROM " + tableName)) {

                    int columnCount = rs.getMetaData().getColumnCount();

                    // Format data based on selected format
                    switch (format) {
                        case "CSV":
                            // Add headers
                            for (int i = 1; i <= columnCount; i++) {
                                data.append(rs.getMetaData().getColumnName(i)).append(i == columnCount ? "\n" : ",");
                            }
                            // Add rows
                            while (rs.next()) {
                                for (int i = 1; i <= columnCount; i++) {
                                    data.append(rs.getString(i)).append(i == columnCount ? "\n" : ",");
                                }
                            }
                            break;
                        case "SQL":
                            while (rs.next()) {
                                data.append("INSERT INTO ").append(tableName).append(" VALUES (");
                                for (int i = 1; i <= columnCount; i++) {
                                    data.append("'").append(rs.getString(i)).append("'").append(i == columnCount ? ");\n" : ", ");
                                }
                            }
                            break;
                        case "JSON":
                            data.append("[");
                            while (rs.next()) {
                                data.append("{");
                                for (int i = 1; i <= columnCount; i++) {
                                    data.append("\"").append(rs.getMetaData().getColumnName(i)).append("\": \"").append(rs.getString(i)).append("\"");
                                    if (i < columnCount) data.append(", ");
                                }
                                data.append("},");
                            }
                            if (data.charAt(data.length() - 1) == ',') {
                                data.deleteCharAt(data.length() - 1);
                            }
                            data.append("]");
                            break;
                        case "XML":
                            data.append("<rows>\n");
                            while (rs.next()) {
                                data.append("  <row>\n");
                                for (int i = 1; i <= columnCount; i++) {
                                    data.append("    <").append(rs.getMetaData().getColumnName(i)).append(">")
                                        .append(rs.getString(i)).append("</").append(rs.getMetaData().getColumnName(i)).append(">\n");
                                }
                                data.append("  </row>\n");
                            }
                            data.append("</rows>");
                            break;
                    }
                    data.append("\n\n"); // Add spacing between tables
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error fetching data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return data.toString();
    }

    private static String getExtension(String format) {
        switch (format) {
            case "CSV": return "csv";
            case "SQL": return "sql";
            case "JSON": return "json";
            case "XML": return "xml";
            default: return "txt";
        }
    }
}
