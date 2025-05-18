import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ChartExporter {
  private static final String OUTPUT_FOLDER = "data/figure/";

  private static void ensureOutputFolderExists() {
    File directory = new File(OUTPUT_FOLDER);
    if (!directory.exists()) { directory.mkdirs(); }
  }

  public static void generateBarChart(Map<String, Integer> data, String title, String filename) {
    ensureOutputFolderExists();

    CategoryAxis xAxis = new CategoryAxis();
    NumberAxis yAxis = new NumberAxis();
    BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
    chart.setTitle(title);

    XYChart.Series<String, Number> series = new XYChart.Series<>();
    series.setName("Values");
    for (Map.Entry<String, Integer> entry : data.entrySet()) {
      series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
    }
    chart.getData().add(series);

    saveChartAsImage(chart, filename);
  }

  public static void generatePieChart(Map<String, Integer> data, String title, String filename) {
    ensureOutputFolderExists();

    PieChart pieChart = new PieChart();
    pieChart.setTitle(title);

    for (Map.Entry<String, Integer> entry : data.entrySet()) {
      pieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
    }

    saveChartAsImage(pieChart, filename);
  }

  public static void generateScatterChart(Map<Double, Double> data, String title, String filename) {
    ensureOutputFolderExists();

    NumberAxis xAxis = new NumberAxis();
    xAxis.setLabel("Discount Percentage");
    NumberAxis yAxis = new NumberAxis();
    yAxis.setLabel("Likes Count");

    ScatterChart<Number, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);
    scatterChart.setTitle(title);

    XYChart.Series<Number, Number> series = new XYChart.Series<>();
    series.setName("Likes vs. Discount");
    for (Map.Entry<Double, Double> entry : data.entrySet()) {
      series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
    }
    scatterChart.getData().add(series);

    saveChartAsImage(scatterChart, filename);
  }

  private static void saveChartAsImage(Chart chart, String filename) {
    Scene scene = new Scene(chart, 800, 600);
    WritableImage image = scene.snapshot(null);
    File outputFile = new File(OUTPUT_FOLDER + filename);

    try {
      ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", outputFile);
      System.out.println("Chart saved as: " + outputFile.getAbsolutePath());
    } catch (IOException e) {
        System.err.println("Failed to save chart: " + e.getMessage());
      }
  }
}