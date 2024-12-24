package student_1;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.IntervalXYDataset;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;

/**
 * @author AkioCkist
 */
public class DrawScoreDistribution {
    public static void aaaa() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/student", "root", "");
            Statement stmt = conn.createStatement();

            // Dynamically fetch column names from the 'coursescore' table
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, "coursescore", null);

            ArrayList<String> scoreColumns = new ArrayList<>();
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                if (!columnName.equals("courseMail")) { // Exclude the primary key column
                    scoreColumns.add(columnName);
                }
            }

            // Build the SQL query dynamically
            StringBuilder sql = new StringBuilder("SELECT ");
            for (int i = 0; i < scoreColumns.size(); i++) {
                sql.append(scoreColumns.get(i));
                if (i < scoreColumns.size() - 1) {
                    sql.append(", ");
                }
            }
            sql.append(" FROM coursescore");

            ResultSet rs = stmt.executeQuery(sql.toString());

            // Create a dataset for the histogram
            HistogramDataset dataset = new HistogramDataset();

            // Collect scores dynamically for each column
            ArrayList<double[]> scoresList = new ArrayList<>();
            for (int i = 0; i < scoreColumns.size(); i++) {
                scoresList.add(new double[100]); // Initialize arrays with maximum size
            }

            int index = 0;
            while (rs.next()) {
                for (int i = 0; i < scoreColumns.size(); i++) {
                    scoresList.get(i)[index] = rs.getDouble(scoreColumns.get(i));
                }
                index++;
            }

            // Trim arrays to actual size
            for (int i = 0; i < scoresList.size(); i++) {
                scoresList.set(i, java.util.Arrays.copyOf(scoresList.get(i), index));
            }

            // Add series to the dataset
            for (int i = 0; i < scoreColumns.size(); i++) {
                dataset.addSeries(scoreColumns.get(i) + " Scores", scoresList.get(i), 10); // 10 bins
            }

            // Create the chart
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

            // Display the chart in a JFrame
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
            JFrame frame = new JFrame("Score Distribution Chart");
            frame.setContentPane(chartPanel);
            frame.pack();

            // Center the JFrame on the screen
            frame.setLocationRelativeTo(null);

            frame.setVisible(true);

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
