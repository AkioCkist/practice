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
public class DrawScoreDistribution {
    public static void aaaa() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/student", "root", "");
            Statement stmt = conn.createStatement();

            String sql = "SELECT scoreEnglish, scoreMath, scoreScience, scoreHistory FROM studentscore";
            ResultSet rs = stmt.executeQuery(sql);

            HistogramDataset dataset = new HistogramDataset();
            double[] englishScores = new double[100];
            double[] mathScores = new double[100];
            double[] scienceScores = new double[100];
            double[] historyScores = new double[100];
            
            int index = 0;
            while (rs.next()) {
                englishScores[index] = rs.getDouble("scoreEnglish");
                mathScores[index] = rs.getDouble("scoreMath");
                scienceScores[index] = rs.getDouble("scoreScience");
                historyScores[index] = rs.getDouble("scoreHistory");
                index++;
            }

            englishScores = java.util.Arrays.copyOf(englishScores, index);
            mathScores = java.util.Arrays.copyOf(mathScores, index);
            scienceScores = java.util.Arrays.copyOf(scienceScores, index);
            historyScores = java.util.Arrays.copyOf(historyScores, index);
            
            dataset.addSeries("English Scores", englishScores, 10); // 10 bins
            dataset.addSeries("Math Scores", mathScores, 10);
            dataset.addSeries("Science Scores", scienceScores, 10);
            dataset.addSeries("History Scores", historyScores, 10);

            JFreeChart chart = ChartFactory.createHistogram(
                    "Score Distribution by Subject",
                    "Score Range",
                    "Frequency",
                    (IntervalXYDataset) dataset,
                    org.jfree.chart.plot.PlotOrientation.VERTICAL,
                    true, 
                    true, 
                    false 
            );

            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
            JFrame frame = new JFrame("Score Distribution Chart");
            frame.setContentPane(chartPanel);
            frame.pack();
            frame.setVisible(true);

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
