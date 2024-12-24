package student_1;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;

/**
 * @author AkioCkist
 */
public class DrawPerformance {
    public static void bbbb() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/student", "root", "");
            Statement stmt = conn.createStatement();

            // Dynamically fetch numeric columns from the 'coursescore' table
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, "coursescore", null);

            ArrayList<String> numericColumns = new ArrayList<>();
            System.out.println("Fetching columns and their types from the 'coursescore' table:");

            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String columnType = columns.getString("TYPE_NAME");

                System.out.println("Column: " + columnName + ", Type: " + columnType);

                // Detect numeric columns dynamically
                if (!columnName.equals("courseMail") && (columnType.contains("DOUBLE") || columnType.contains("FLOAT") || columnType.contains("INT"))) {
                    numericColumns.add(columnName);
                }
            }

            if (numericColumns.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No numeric columns found!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Generate Pie Charts for each numeric column
            for (String numericColumn : numericColumns) {
                String sql = "SELECT `" + numericColumn + "` FROM coursescore";
                ResultSet rs = stmt.executeQuery(sql);

                // Aggregate data into ranges (e.g., 0.00-1.00, 1.00-2.00, ..., 9.00-10.00)
                int[] ranges = new int[10]; // 10 intervals: [0.00-1.00, 1.00-2.00, ..., 9.00-10.00]
                while (rs.next()) {
                    double value = rs.getDouble(numericColumn);
                    if (!rs.wasNull() && value >= 0 && value <= 10.0) {
                        int rangeIndex = Math.min((int) value, 9); // Each range represents 1.0
                        ranges[rangeIndex]++;
                    }
                }

                // Create dataset for pie chart
                DefaultPieDataset dataset = new DefaultPieDataset();
                for (int i = 0; i < ranges.length; i++) {
                    String rangeLabel = String.format("%.2f-%.2f", i * 1.0, (i + 1) * 1.0);
                    dataset.setValue(rangeLabel, ranges[i]);
                }

                // Create Pie Chart
                JFreeChart pieChart = ChartFactory.createPieChart(
                        "Distribution for " + numericColumn,
                        dataset,
                        true, true, false
                );

                // Display Chart in JFrame
                ChartPanel chartPanel = new ChartPanel(pieChart);
                chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
                JFrame frame = new JFrame("Performance Distribution Chart - " + numericColumn);
                frame.setContentPane(chartPanel);
                frame.pack();

                // Center the JFrame on the screen
                frame.setLocationRelativeTo(null);

                frame.setVisible(true);
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
