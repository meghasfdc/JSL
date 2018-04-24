/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.utilities.welch;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/** Use JavaFX to display a WelchChart.  It must have two arguments when
 *  launched: first is a string that represents the path to a wpdf file
 *  made by WelchDataFileAnalyzer.  It is best to launch this via 
 *  the WelchDataFileAnalyzer class
 *
 * @author rossetti
 */
public class WelchChart extends Application {

    private File myFile;
    private FileInputStream myFIn;
    private DataInputStream myDIn;
    private long myNumObs;

    @Override
    public void start(Stage primaryStage) {
        final Parameters params = getParameters();
        final List<String> parameters = params.getRaw();
        if (parameters.size() != 2) {
            throw new IllegalArgumentException("The number of parameters must be 2");
        }
        Path path = Paths.get(parameters.get(0));
        myFile = path.toFile();
        myNumObs = Long.parseLong(parameters.get(1));
        try {
            myFIn = new FileInputStream(myFile);
            myDIn = new DataInputStream(myFIn);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WelchChart.class.getName()).log(Level.SEVERE, null, ex);
        }

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        ScatterChart scatterChart = new ScatterChart(xAxis, yAxis);
        xAxis.setLabel("Observation Number");
        yAxis.setLabel("Observation");
        scatterChart.setTitle("Welch Plot Chart");
        scatterChart.setAnimated(false);

        scatterChart.setData(getChartData());

        StackPane root = new StackPane();
        root.getChildren().add(scatterChart);
        Scene scene = new Scene(root, 600, 550);
        primaryStage.setTitle("Welch Plot Analysis");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private ObservableList<XYChart.Series<Long, Double>> getChartData() {
        ObservableList<XYChart.Series<Long, Double>> data = FXCollections.observableArrayList();
        XYChart.Series<Long, Double> myWelchAvg = new XYChart.Series();
        XYChart.Series<Long, Double> myCumAvg = new XYChart.Series();;
        myWelchAvg.setName("avg");
        myCumAvg.setName("cum avg");
        for (long i = 1; i <= myNumObs; i++) {
            try {
                double avg = myDIn.readDouble();
                double cavg = myDIn.readDouble();
                myWelchAvg.getData().add(new XYChart.Data(i, avg));
                myCumAvg.getData().add(new XYChart.Data(i, cavg));
            } catch (IOException ex) {
                Logger.getLogger(WelchChart.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        data.addAll(myWelchAvg, myCumAvg);
        return data;
    }

    public static void launchWelchChart(String[] args) {
        WelchChart.launch(WelchChart.class, args);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
