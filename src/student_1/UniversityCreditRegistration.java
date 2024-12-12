
/*
 * This JPanel is designed for NetBeans design mode compatibility.
 */
package student_1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class UniversityCreditRegistration {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create frame
            JFrame frame = new JFrame("University Credit Registration");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 400);
            frame.setLocationRelativeTo(null);

            // Create main panel with padding
            JPanel mainPanel = new JPanel(new GridBagLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            frame.add(mainPanel);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;

            // Title
            JLabel titleLabel = new JLabel("University Credit Registration");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            mainPanel.add(titleLabel, gbc);

            // Student Name
            gbc.gridy++;
            gbc.gridwidth = 1;
            JLabel nameLabel = new JLabel("Student Name:");
            nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            mainPanel.add(nameLabel, gbc);

            gbc.gridx = 1;
            JTextField nameField = new JTextField(20);
            mainPanel.add(nameField, gbc);

            // Course Selection
            gbc.gridy++;
            gbc.gridx = 0;
            gbc.gridwidth = 2;
            JLabel courseLabel = new JLabel("Select Courses:");
            courseLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            mainPanel.add(courseLabel, gbc);

            // Course Checkboxes
            JPanel coursePanel = new JPanel(new GridLayout(4, 1, 5, 5));
            JCheckBox course1 = new JCheckBox("Math 101 (3 Credits)");
            JCheckBox course2 = new JCheckBox("Physics 201 (4 Credits)");
            JCheckBox course3 = new JCheckBox("History 101 (2 Credits)");
            JCheckBox course4 = new JCheckBox("Computer Science 101 (5 Credits)");

            course1.setFont(new Font("Arial", Font.PLAIN, 12));
            course2.setFont(new Font("Arial", Font.PLAIN, 12));
            course3.setFont(new Font("Arial", Font.PLAIN, 12));
            course4.setFont(new Font("Arial", Font.PLAIN, 12));

            coursePanel.add(course1);
            coursePanel.add(course2);
            coursePanel.add(course3);
            coursePanel.add(course4);

            gbc.gridy++;
            mainPanel.add(coursePanel, gbc);

            // Total Credits Display
            gbc.gridy++;
            JLabel totalCreditsLabel = new JLabel("Total Credits: 0");
            totalCreditsLabel.setFont(new Font("Arial", Font.BOLD, 14));
            mainPanel.add(totalCreditsLabel, gbc);

            // Register Button
            gbc.gridy++;
            JButton registerButton = new JButton("Register");
            registerButton.setFont(new Font("Arial", Font.BOLD, 14));
            registerButton.setBackground(new Color(0, 123, 255));
            registerButton.setForeground(Color.WHITE);
            mainPanel.add(registerButton, gbc);

            // Real-Time Credit Calculation
            ItemListener updateCredits = e -> {
                int totalCredits = 0;
                if (course1.isSelected()) totalCredits += 3;
                if (course2.isSelected()) totalCredits += 4;
                if (course3.isSelected()) totalCredits += 2;
                if (course4.isSelected()) totalCredits += 5;
                totalCreditsLabel.setText("Total Credits: " + totalCredits);
            };

            course1.addItemListener(updateCredits);
            course2.addItemListener(updateCredits);
            course3.addItemListener(updateCredits);
            course4.addItemListener(updateCredits);

            // Register Button Action
            registerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String studentName = nameField.getText().trim();
                    int totalCredits = Integer.parseInt(totalCreditsLabel.getText().split(": ")[1]);

                    if (studentName.isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "Please enter the student's name.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else if (totalCredits == 0) {
                        JOptionPane.showMessageDialog(frame, "Please select at least one course.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Registration successful for " + studentName + " with " + totalCredits + " credits.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        nameField.setText("");
                        course1.setSelected(false);
                        course2.setSelected(false);
                        course3.setSelected(false);
                        course4.setSelected(false);
                    }
                }
            });

            // Show frame
            frame.setVisible(true);
        });
    }
}
