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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseImportApp {
    public static void main() {
        SwingUtilities.invokeLater(DatabaseImportApp::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Database Importer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);

        // Panel for layout
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 10, 10));

        // Label
        JLabel label = new JLabel("Import database from file:");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label);

        // Import button
        JButton importButton = new JButton("Import");
        panel.add(importButton);

        frame.add(panel);
        frame.setVisible(true);

        // Button action listener
        importButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                importDatabase();
            }
        });
    }

    private static void importDatabase() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select SQL File to Import");

        int userSelection = fileChooser.showOpenDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToImport = fileChooser.getSelectedFile();
            try {
                executeSQLFromFile(fileToImport);
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
}

