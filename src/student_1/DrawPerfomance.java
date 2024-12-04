/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student_1;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.IntervalXYDataset;

/**
 *
 * @author AkioCkist
 */
public class DrawPerfomance {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/student", "root", "");
            Statement stmt = conn.createStatement();
            String sql = "SELECT performanceStatus, COUNT(*) AS count FROM studentscore GROUP BY performanceStatus";
            ResultSet rs = stmt.executeQuery(sql);

            DefaultPieDataset dataset = new DefaultPieDataset();

            while (rs.next()) {
                String status = rs.getString("performanceStatus");
                int count = rs.getInt("count");
                dataset.setValue(status, count);
            }

            JFreeChart pieChart = ChartFactory.createPieChart(
                    "Performance Status Distribution",
                    dataset,
                    true, true, false
            );

            ChartPanel chartPanel = new ChartPanel(pieChart);
            chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
            JFrame frame = new JFrame();
            frame.setContentPane(chartPanel);
            frame.pack();
            frame.setVisible(true);

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
