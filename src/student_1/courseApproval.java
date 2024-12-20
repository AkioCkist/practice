/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package student_1;

import java.awt.Font;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.sql.Statement;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author AkioCkist
 */
public class courseApproval extends javax.swing.JFrame {

    /**
     * Creates new form showStudent
     */
    
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    public courseApproval() {
        super("courseApproval");
        initComponents();
        conn = databaseConnection.connection();
        connectToDatabase();
        loadPendingRequests();
    }
    private void connectToDatabase() {
    try {
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/student", "root", "");
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(null, "Database Connection Failed: " + ex.getMessage());
    }
}

private void setupTable() {
    DefaultTableModel model = new DefaultTableModel(new Object[]{"Select", "ID", "CourseMail", "Column", "New Value", "Status"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            // Only make the "Select" column editable
            return column == 0;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) { // The "Select" column
                return Boolean.class; // Checkbox requires Boolean type
            }
            return super.getColumnClass(columnIndex);
        }
    };
    jTable1.setModel(model);
}

private void loadPendingRequests() {
    try {
        ((DefaultTableModel) jTable1.getModel()).setRowCount(0); // Clear existing rows
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM approval WHERE status = 'Pending'");
        while (rs.next()) {
            ((DefaultTableModel) jTable1.getModel()).addRow(new Object[]{
                    Boolean.FALSE, // Checkbox is unchecked by default
                    rs.getInt("id"),
                    rs.getString("courseMail"),
                    rs.getString("columnName"),
                    rs.getBoolean("newValue"),
                    rs.getString("status")
            });
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Error loading requests: " + ex.getMessage());
    }
}

private void approveSelected() {
    try {
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            boolean isChecked = (Boolean) jTable1.getValueAt(i, 0); // Check if checkbox is selected
            if (isChecked) {
                int id = (int) jTable1.getValueAt(i, 1);
                String courseMail = (String) jTable1.getValueAt(i, 2);
                String column = (String) jTable1.getValueAt(i, 3);
                boolean value = (Boolean) jTable1.getValueAt(i, 4);

                // Update the `course` table
                String updateQuery = "UPDATE course SET " + column + " = ? WHERE courseMail = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setBoolean(1, value);
                updateStmt.setString(2, courseMail);
                updateStmt.executeUpdate();

                // Check if the `courseMail` exists in `coursescore`
                String checkRowQuery = "SELECT COUNT(*) FROM coursescore WHERE courseMail = ?";
                PreparedStatement checkRowStmt = conn.prepareStatement(checkRowQuery);
                checkRowStmt.setString(1, courseMail);
                ResultSet rs = checkRowStmt.executeQuery();
                boolean rowExists = false;
                if (rs.next() && rs.getInt(1) > 0) {
                    rowExists = true;
                }

                if (!rowExists) {
                    // Insert a new row with default NULL values for the courseMail
                    String insertRowQuery = "INSERT INTO coursescore (courseMail) VALUES (?)";
                    PreparedStatement insertRowStmt = conn.prepareStatement(insertRowQuery);
                    insertRowStmt.setString(1, courseMail);
                    insertRowStmt.executeUpdate();
                }

                // Ensure column exists in `coursescore` table
                String addColumnQuery = "ALTER TABLE coursescore ADD COLUMN IF NOT EXISTS " + column + " DOUBLE DEFAULT NULL";
                PreparedStatement addColumnStmt = conn.prepareStatement(addColumnQuery);
                addColumnStmt.executeUpdate();

                // Update the column value in `coursescore`
                String scoreUpdateQuery = "UPDATE coursescore SET " + column + " = ? WHERE courseMail = ?";
                PreparedStatement scoreUpdateStmt = conn.prepareStatement(scoreUpdateQuery);
                if (value) {
                    scoreUpdateStmt.setDouble(1, 0.0); // Set to 0.0 if updated to 1
                } else {
                    scoreUpdateStmt.setNull(1, java.sql.Types.DOUBLE); // Set to NULL if updated to 0
                }
                scoreUpdateStmt.setString(2, courseMail);
                scoreUpdateStmt.executeUpdate();

                // Delete row from approval table
                deleteApprovalRow(id);
            }
        }

        JOptionPane.showMessageDialog(null, "Selected requests approved successfully!");
        loadPendingRequests(); // Refresh the table
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Error approving requests: " + ex.getMessage());
    }
}


private void rejectSelected() {
    try {
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            boolean isChecked = (Boolean) jTable1.getValueAt(i, 0);
            if (isChecked) {
                int id = (int) jTable1.getValueAt(i, 1);

                // Update the approval table's status to 'Rejected' and delete the row
                String updateQuery = "UPDATE approval SET status = 'Rejected' WHERE id = ?";
                PreparedStatement statusStmt = conn.prepareStatement(updateQuery);
                statusStmt.setInt(1, id);
                statusStmt.executeUpdate();

                // Delete row from approval table
                deleteApprovalRow(id);
            }
        }

        JOptionPane.showMessageDialog(null, "Selected requests rejected successfully!");
        loadPendingRequests(); // Refresh the table
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Error rejecting requests: " + ex.getMessage());
    }
}

private void deleteApprovalRow(int id) {
    try {
        String deleteQuery = "DELETE FROM approval WHERE id = ?";
        PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
        deleteStmt.setInt(1, id);
        deleteStmt.executeUpdate();
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Error deleting row: " + ex.getMessage());
    }
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Course Approval", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 24))); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Select", "ID", "Course Mail", "Courses", "Value", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jButton1.setIcon(new javax.swing.ImageIcon("C:\\Users\\AkioCkist\\Downloads\\project Image\\back.png")); // NOI18N
        jButton1.setText("Back");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton3.setIcon(new javax.swing.ImageIcon("C:\\Users\\AkioCkist\\Downloads\\project Image\\approve.png")); // NOI18N
        jButton3.setText("Approve");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton5.setIcon(new javax.swing.ImageIcon("C:\\Users\\AkioCkist\\Downloads\\project Image\\reject.png")); // NOI18N
        jButton5.setText("Reject ");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1083, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(126, 126, 126)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(134, 134, 134)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(137, 137, 137))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 63, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18))
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
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setSize(new java.awt.Dimension(1121, 566));
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

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        setVisible(false);
        dashboardStaff object = new dashboardStaff();
        object.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        approveSelected();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        rejectSelected();
    }//GEN-LAST:event_jButton5ActionPerformed

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
            java.util.logging.Logger.getLogger(courseApproval.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(courseApproval.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(courseApproval.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(courseApproval.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new courseApproval().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
