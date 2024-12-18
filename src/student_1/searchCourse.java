/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package student_1;
import com.mysql.cj.xdevapi.Statement;
import java.awt.Component;
import java.sql.ResultSet;
import java.sql.Connection;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.security.SecureRandom;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author AkioCkist
 */
public class searchCourse extends javax.swing.JFrame {

    /**
     * Creates new form searchAdmin
     */
    Connection conn = null;
    java.sql.Statement stmt = null;
    ResultSet rs = null;
    private List<String> booleanColumns;
    static String loginMail;
    public searchCourse() {
        super("Search Staff");
        booleanColumns = new ArrayList<>();
        initComponents();
        conn = databaseConnection.connection(); 
        connectToDatabase();
        fetchBooleanColumns();
        setupTable();  // Set up table with boolean columns
        mail.setText(loginMail);       
        
    }
    public void setloginMail(String loginMail) {
        this.loginMail = loginMail;      
    }
    private void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/student", "root", ""); // Adjust for your setup
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Database Connection Failed: " + ex.getMessage());
            System.exit(1);
        }
    }

    private void fetchBooleanColumns() {
        try {
            booleanColumns.clear();
            String query = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'student' AND TABLE_NAME = 'course' AND DATA_TYPE = 'tinyint'";
            java.sql.Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                booleanColumns.add(rs.getString("COLUMN_NAME"));
            }

            // Update Table Columns Dynamically
            ((DefaultTableModel) jTable1.getModel()).setColumnIdentifiers(booleanColumns.toArray());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error fetching column metadata: " + ex.getMessage());
        }
    }

    private void loadData(String courseMail) {
        ((DefaultTableModel) jTable1.getModel()).setRowCount(0); // Clear the table

        if (booleanColumns.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No boolean columns found in the table.");
            return;
        }

        StringBuilder queryBuilder = new StringBuilder("SELECT ");
        for (String col : booleanColumns) {
            queryBuilder.append(col).append(", ");
        }
        queryBuilder.setLength(queryBuilder.length() - 2); // Remove last comma
        queryBuilder.append(" FROM course WHERE courseMail = ?");

        try (PreparedStatement pstmt = conn.prepareStatement(queryBuilder.toString())) {
            pstmt.setString(1, courseMail);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Object[] rowData = new Object[booleanColumns.size()];
                    for (int i = 0; i < booleanColumns.size(); i++) {
                        rowData[i] = rs.getBoolean(booleanColumns.get(i));  // Ensure this is a boolean value
                    }
                    ((DefaultTableModel) jTable1.getModel()).addRow(rowData);
                } else {
                    JOptionPane.showMessageDialog(null, "No record found for courseMail: " + courseMail);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error loading data: " + ex.getMessage());
        }
    }

    private void updateDatabase(String courseMail) {
        if (((DefaultTableModel) jTable1.getModel()).getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "No data to update.");
            return;
        }

        try {
            // Dynamically construct UPDATE query
            StringBuilder queryBuilder = new StringBuilder("UPDATE course SET ");
            for (String col : booleanColumns) {
                queryBuilder.append(col).append(" = ?, ");
            }
            queryBuilder.setLength(queryBuilder.length() - 2); // Remove last comma
            queryBuilder.append(" WHERE courseMail = ?");

            PreparedStatement pstmt = conn.prepareStatement(queryBuilder.toString());

            // Set boolean values
            for (int i = 0; i < jTable1.getColumnCount(); i++) {
                Boolean value = (Boolean) jTable1.getValueAt(0, i);  // Ensure this is a Boolean
                pstmt.setBoolean(i + 1, value != null && value);  // Convert null to false if needed
            }

            pstmt.setString(booleanColumns.size() + 1, courseMail);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(null, "Record Updated Successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to Update Record.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error updating database: " + ex.getMessage());
        }
    }

    // Method to setup table for boolean columns
    private void setupTable() {
        // Create a table model with columns corresponding to boolean columns
        DefaultTableModel model = new DefaultTableModel(new Object[0][booleanColumns.size()], booleanColumns.toArray()) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return Boolean.class;  // Ensure column is treated as Boolean
            }
        };
        jTable1.setModel(model);

        // Set up a checkbox editor for boolean columns
        for (int i = 0; i < booleanColumns.size(); i++) {
            jTable1.getColumnModel().getColumn(i).setCellEditor(new DefaultCellEditor(new JCheckBox()));
            jTable1.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    JCheckBox checkBox = new JCheckBox();
                    checkBox.setSelected(value != null && (Boolean) value);
                    return checkBox;
                }
            });
        }
    }
    private void deleteRow() {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a row to delete.");
            return;
        }

        // Assuming courseMail is the first column (adjust if necessary)
        String courseMail = (String) jTable1.getValueAt(selectedRow, 0); // Get the courseMail of the selected row

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this record?", "Delete Record", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Remove row from JTable
            ((DefaultTableModel) jTable1.getModel()).removeRow(selectedRow);

            // Now delete from the database
            try {
                String deleteQuery = "DELETE FROM course WHERE courseMail = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
                    pstmt.setString(1, courseMail);
                    int rowsDeleted = pstmt.executeUpdate();
                    if (rowsDeleted > 0) {
                        JOptionPane.showMessageDialog(null, "Record deleted successfully!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to delete the record.");
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error deleting record: " + ex.getMessage());
            }
        }
    }
    // Initialize your components here, including jTable1 (JTable)


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        update = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        mail = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        search = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Search & Add, Remove Course", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 24))); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(653, 800));

        update.setIcon(new javax.swing.ImageIcon("C:\\Users\\AkioCkist\\Downloads\\project Image\\submit.png")); // NOI18N
        update.setText("Update");
        update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateActionPerformed(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon("C:\\Users\\AkioCkist\\Downloads\\project Image\\back.png")); // NOI18N
        jButton2.setText("Back");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton4.setIcon(new javax.swing.ImageIcon("C:\\Users\\AkioCkist\\Downloads\\project Image\\cancel1.png")); // NOI18N
        jButton4.setText("Delete");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jPanel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        jLabel7.setFont(new java.awt.Font("Arial", 0, 16)); // NOI18N
        jLabel7.setText("Mail:");

        mail.setEditable(false);
        mail.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        mail.setEnabled(false);
        mail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mailActionPerformed(evt);
            }
        });
        mail.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                mailKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                mailKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                    .addContainerGap(85, Short.MAX_VALUE)
                    .addComponent(mail, javax.swing.GroupLayout.PREFERRED_SIZE, 603, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(30, 30, 30)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(36, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(32, 32, 32)
                    .addComponent(mail, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(32, Short.MAX_VALUE)))
        );

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        search.setIcon(new javax.swing.ImageIcon("C:\\Users\\AkioCkist\\Downloads\\project Image\\loupe.png")); // NOI18N
        search.setText("Search");
        search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 722, Short.MAX_VALUE))
                .addGap(0, 25, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(66, 66, 66)
                .addComponent(search, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(80, 80, 80)
                .addComponent(update, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton4)
                .addGap(83, 83, 83)
                .addComponent(jButton2)
                .addGap(70, 70, 70))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(79, 79, 79)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(update, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(search, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jMenu1.setText("File");
        jMenu1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jMenuItem1.setIcon(new javax.swing.ImageIcon("C:\\Users\\AkioCkist\\Downloads\\project Image\\home.png")); // NOI18N
        jMenuItem1.setText("Home");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenuItem2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jMenuItem2.setIcon(new javax.swing.ImageIcon("C:\\Users\\AkioCkist\\Downloads\\project Image\\logout.png")); // NOI18N
        jMenuItem2.setText("Log out");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 794, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 408, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(399, 399, 399))
        );

        setSize(new java.awt.Dimension(810, 483));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        setVisible(false);
        home object = new home();
        object.setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        setVisible(false);
        login object = new login();
        object.setVisible(true);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void updateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateActionPerformed
        // TODO add your handling code here:  
        updateDatabase(mail.getText());
    }//GEN-LAST:event_updateActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        setVisible(false);
        dashboardStudent object = new dashboardStudent();
        object.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        //Warning table
	int attempts = 0; 
        boolean isConfirmed = false;

        while (attempts < 4 && !isConfirmed) {
            // Generate random string
            String confirmationCode = generateRandomString(8);

            JPanel panel = new JPanel();
            JLabel label = new JLabel(
                "Type the following code to confirm deletion: " + confirmationCode +
                " (Attempts left: " + (4 - attempts) + ")"
            );
            JTextField textField = new JTextField(10);

            panel.add(label);
            panel.add(textField);

            int response = JOptionPane.showConfirmDialog(
                this, 
                panel, 
                "Confirm Deletion", 
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.WARNING_MESSAGE
            );

            if (response == JOptionPane.OK_OPTION) {
                String userInput = textField.getText().trim();
                if (confirmationCode.equals(userInput)) {
                    isConfirmed = true;
                    break;
                } else {
                    attempts++;
                    if (attempts < 4) {
                        JOptionPane.showMessageDialog(
                            this, 
                            "Incorrect code. Please try again.", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Deletion canceled.", "Canceled", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }

        if (isConfirmed) {
            // Perform the delete operation
            try{
                deleteRow();
            }
            catch(Exception e){
                JOptionPane.showMessageDialog(null,e);
            }
        } else {
            // User failed 
            JOptionPane.showMessageDialog(
                this, 
                "You have exceeded the maximum number of attempts. Deletion canceled.", 
                "Failed", 
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

// Random Method
    private String generateRandomString(int length) {
        final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void mailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mailActionPerformed

    private void mailKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mailKeyReleased
        // TODO add your handling code here:       
    }//GEN-LAST:event_mailKeyReleased

    private void mailKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_mailKeyPressed
        // TODO add your handling code here:      
    }//GEN-LAST:event_mailKeyPressed

    private void searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchActionPerformed
        // TODO add your handling code here:   
        loadData(loginMail);
    }//GEN-LAST:event_searchActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(searchCourse.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(searchCourse.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(searchCourse.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(searchCourse.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new searchCourse().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField mail;
    private javax.swing.JButton search;
    private javax.swing.JButton update;
    // End of variables declaration//GEN-END:variables
}
