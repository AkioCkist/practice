/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package student_1;

import java.awt.Font;
import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author AkioCkist
 */
public class login extends javax.swing.JFrame {

    /**
     * Creates new form login
     */
    
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    
    public login() {
        super("Login");
        initComponents();
        conn = databaseConnection.connection();
        loadData();
    }
//Load mail and password information from created file if login successfully     
    private void loadData(){
        try{
	FileReader out = new FileReader("info.txt");
	BufferedReader reader = new BufferedReader(out);
	String line = reader.readLine();
        if(line != null){
            mail.setText(line);
            line = reader.readLine();
            password.setText(line);
            line = reader.readLine();
            if(line.equals("true")){
                RememberPassword.setSelected(true);
            }
            else{
                RememberPassword.setSelected(false);
            }
        }
	reader.close();
        }catch(IOException e){
	System.out.println("Error:" + e.toString());
        }
    }
    private void loginIn(){
        try{
            stmt = conn.createStatement();
            String userEmail = mail.getText();
            String userPassword = password.getText();
            
            String sql = "SELECT 'admin' AS role FROM admin WHERE mail = '"+userEmail+"' AND password = '"+userPassword+"' " +
             "UNION " +
             "SELECT 'student' AS role FROM student WHERE stdMail = '"+userEmail+"' AND stdPassword = '"+userPassword+"' " +
             "UNION " +
             "SELECT 'staff' AS role FROM staff WHERE staffMail = '"+userEmail+"' AND staffPassword = '"+userPassword+"'";
           
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
            String role = rs.getString("role");
            if ("admin".equals(role)) {
                setVisible(false);
                home object = new home();
                object.setVisible(true);
            } 
            else if ("student".equals(role)) {
                String loginInfo = mail.getText();
                viewStudentProfile info = new viewStudentProfile();  
                info.setloginMail(loginInfo);
                
                courseRegister cr = new courseRegister();  
                cr.setloginMail(loginInfo);
                setVisible(false);
                
                dashboardStudent object = new dashboardStudent();
                object.setVisible(true);
            }
            else if ("staff".equals(role)){
                setVisible(false);
                dashboardStaff object = new dashboardStaff();
                object.setVisible(true);
            }
            } else {
                // Invalid login
                JOptionPane.showMessageDialog(this, 
        "<html><font face='Calibri' size='24' color='red'>What's wrong, bro? Can't even remember your own identity? |.___.| Ask yo GREAT GRANDMA again");
            }
            
        }
        catch(HeadlessException | SQLException e){
           JOptionPane.showMessageDialog(null,e);
        }      
    }
    private void rememberPassword(){
        if(RememberPassword.isSelected()){
        try{
            FileWriter writer = new FileWriter("info.txt");
            writer.write(mail.getText() + "\n");           
            writer.write(password.getText() + "\n");
            writer.write(RememberPassword.isSelected() + "\n");           
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred while saving credentials.");
            e.printStackTrace();
            }
        }
        else{
            try{
            FileWriter writer = new FileWriter("info.txt");
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred while saving credentials.");
            e.printStackTrace();
        }
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

        jLabel1 = new javax.swing.JLabel();
        mail = new javax.swing.JTextField();
        password = new javax.swing.JPasswordField();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        logo = new javax.swing.JLabel();
        RememberPassword = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setText("Email:");

        mail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mailActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel2.setText("Password:");

        jButton1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon("C:\\Users\\AkioCkist\\Downloads\\project Image\\login1.png")); // NOI18N
        jButton1.setText("Login");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jButton2.setIcon(new javax.swing.ImageIcon("C:\\Users\\AkioCkist\\Downloads\\project Image\\cancel1.png")); // NOI18N
        jButton2.setText("Cancel");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        logo.setIcon(new javax.swing.ImageIcon("C:\\Users\\AkioCkist\\Downloads\\project Image\\logo.png")); // NOI18N

        RememberPassword.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        RememberPassword.setText("Remember Password");
        RememberPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RememberPasswordActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(225, 225, 225)
                        .addComponent(logo))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(129, 129, 129)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel2)
                                        .addComponent(jLabel1))
                                    .addGap(18, 18, 18)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(mail)
                                        .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, 385, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(RememberPassword)
                                    .addGap(142, 142, 142)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(141, 141, 141)
                                .addComponent(jButton1)
                                .addGap(58, 58, 58)
                                .addComponent(jButton2)))))
                .addGap(140, 140, 140))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(58, Short.MAX_VALUE)
                .addComponent(logo, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(49, 49, 49)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mail, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(RememberPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(96, 96, 96))
        );

        setSize(new java.awt.Dimension(760, 526));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void mailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mailActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        loginIn();
        rememberPassword();          
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void RememberPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RememberPasswordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_RememberPasswordActionPerformed

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
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new login().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox RememberPassword;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel logo;
    private javax.swing.JTextField mail;
    private javax.swing.JPasswordField password;
    // End of variables declaration//GEN-END:variables
}
