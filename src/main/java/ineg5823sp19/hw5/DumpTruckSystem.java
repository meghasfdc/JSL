/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ineg5823sp19.hw5;

import jsl.modeling.EventActionIfc;
import jsl.modeling.Experiment;
import jsl.modeling.JSLEvent;
import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;
import jsl.modeling.Simulation;
import jsl.modeling.SimulationReporter;
import jsl.modeling.elements.EventGenerator;
import jsl.modeling.elements.EventGeneratorActionIfc;
import jsl.modeling.queue.QObject;
import jsl.modeling.elements.station.DelayStation;
import jsl.modeling.elements.station.SResource;
import jsl.modeling.elements.station.SendQObjectIfc;
import jsl.modeling.elements.station.SingleQueueStation;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.observers.variable.MultipleComparisonDataCollector;
import jsl.utilities.random.RandomIfc;
import jsl.utilities.random.rvariable.ConstantRV;
import jsl.utilities.random.rvariable.ExponentialRV;
import jsl.utilities.random.rvariable.UniformRV;
import jsl.utilities.statistic.MultipleComparisonAnalyzer;
import jsl.utilities.statistic.Statistic;

/**
 *
 * @author rossetti
 */
public class DumpTruckSystem extends SchedulingElement {

    protected int myNumTrucks;

    protected final ResponseVariable myResponseTime;

    protected final ArriveAtLoadersAction myArriveAtLoadersAction;

    protected final SingleQueueStation myLoadingStation;

    protected final SingleQueueStation myWeighingStation;

    protected final DelayStation myTruckTravel;

    protected final SendToLoading mySendToLoading;

    protected final SResource myLoader;

    protected final SResource myScale;

    private final EventGenerator myTruckGenerator;

    public DumpTruckSystem(ModelElement parent) {
        this(parent, 2, 8, 1, new UniformRV(12.0, 24.0), new ExponentialRV(85.0),
                new UniformRV(1.0, 9.0), null);
    }

    public DumpTruckSystem(ModelElement parent, String name) {
        this(parent, 2, 8, 1, new UniformRV(12.0, 24.0), new ExponentialRV(85.0),
                new UniformRV(1.0, 9.0), name);
    }

    public DumpTruckSystem(ModelElement parent, int numLoaders, int numTrucks, int numScales,
            RandomIfc loadingTime, RandomIfc travelTime, RandomIfc weighingTime, String name) {
        super(parent, name);
        setNumberOfTrucks(numTrucks);
        myArriveAtLoadersAction = new ArriveAtLoadersAction();
        myTruckGenerator = new EventGenerator(this, myArriveAtLoadersAction, ConstantRV.ZERO,
                ConstantRV.ONE, 1);
        mySendToLoading = new SendToLoading();
        myLoader = new SResource(this, numLoaders, "Loader");
        myLoadingStation = new SingleQueueStation(this, myLoader, loadingTime, "Loading Station");
        myScale = new SResource(this, numScales, "Scale");
        myWeighingStation = new SingleQueueStation(this, myScale, weighingTime,
                new SendToTravelling(), "Scale Station");
        myLoadingStation.setNextReceiver(myWeighingStation);
        myTruckTravel = new DelayStation(this, travelTime, mySendToLoading, "Traveling");
        myResponseTime = new ResponseVariable(this, "Response Time");

    }

    public final void setNumLoaders(int loaders) {
        myLoader.setInitialCapacity(loaders);
    }

    public final void setNumScales(int numScales) {
        myScale.setInitialCapacity(numScales);
    }

    public final void setNumberOfTrucks(int numTrucks) {
        if (numTrucks <= 0) {
            throw new IllegalArgumentException("# trucks must be > 0");
        }
        myNumTrucks = numTrucks;

    }

    public final void setLoadingTimeRV(RandomIfc d) {
        myLoadingStation.setServiceTime(d);
    }

    public final void setWeighingTimeRV(RandomIfc d) {
        myWeighingStation.setServiceTime(d);
    }

    public final void setTravelTimeRV(RandomIfc d) {
        myTruckTravel.setDelayTime(d);
    }

    protected class ArriveAtLoadersAction implements EventGeneratorActionIfc {

        @Override
        public void generate(EventGenerator generator, JSLEvent event) {
            for (int i = 0; i < myNumTrucks; i++) {
                mySendToLoading.send(new QObject(getTime(), "Truck" + i));
            }
        }

    }

    protected class SendToTravelling implements SendQObjectIfc {

        @Override
        public void send(QObject qo) {
            myResponseTime.setValue(getTime() - qo.getTimeStamp());
            myTruckTravel.receive(qo);
        }

    }

    protected class SendToLoading implements SendQObjectIfc {

        @Override
        public void send(QObject truck) {
            truck.setTimeStamp(getTime());
            myLoadingStation.receive(truck);
        }

    }

    public final ResponseVariable getResponseTimeVariable() {
        return myResponseTime;
    }

    public static void main(String[] args) {

        //runSlowLoaderModel();
        compareSystems();
    }

    public static void runSlowLoaderModel() {
        UniformRV slowLoader = new UniformRV(12.0, 24.0);
        Simulation s = new Simulation("Dump Truck Simulation");
        SimulationReporter r = s.makeSimulationReporter();

        // set the parameters of the experiment
        s.setNumberOfReplications(10);
        s.setLengthOfReplication(200000.0);
        s.setLengthOfWarmUp(50000.0);

        DumpTruckSystem dts = new DumpTruckSystem(s.getModel());
        dts.setNumLoaders(2);
        dts.setNumberOfTrucks(8);
        dts.setLoadingTimeRV(slowLoader);

        // run the simulation
        System.out.println("Running: 2 loaders, with loading time = UNIF(12, 24)");
        s.run();

        // write out results
        r.printAcrossReplicationSummaryStatistics();
    }

    public static void compareSystems() {
        UniformRV fastLoader = new UniformRV(6.0, 12.0);
        // have them share the same stream
        UniformRV slowLoader = new UniformRV(12.0, 24.0);

        Simulation s = new Simulation("Dump Truck Simulation");
        SimulationReporter r = s.makeSimulationReporter();

        // set the parameters of the experiment
        s.setNumberOfReplications(10);
        s.setLengthOfReplication(200000.0);
        s.setLengthOfWarmUp(50000.0);

        DumpTruckSystem dts = new DumpTruckSystem(s.getModel(), "DTS");
        dts.setNumLoaders(2);
        dts.setLoadingTimeRV(slowLoader);

        ResponseVariable rst = dts.getResponseTimeVariable();
        MultipleComparisonDataCollector mcadc = new MultipleComparisonDataCollector(rst);

        // run the simulation
        System.out.println("Running: 2 loaders, with loading time = UNIF(12,24)");
        s.setExperimentName("SlowLoaders");
        s.run();

        // write out results
        r.writeAcrossReplicationCSVStatistics("DTS-Slow");
        r.writeAcrossReplicationStatistics("DTS-Slow");
        r.printAcrossReplicationSummaryStatistics();

        // change the model
        dts.setNumLoaders(1);
        dts.setLoadingTimeRV(fastLoader);

        // run the simulation
        System.out.println();
        System.out.println("Running: 1 loader, with loading time = UNIF(6,12)");
        s.setExperimentName("FastLoader");
        s.run();

        // write out results
        r.writeAcrossReplicationCSVStatistics("DTS-Fast");
        r.writeAcrossReplicationStatistics("DTS-Fast");
        r.printAcrossReplicationSummaryStatistics();

        MultipleComparisonAnalyzer mca = mcadc.getMultipleComparisonAnalyzer();
        System.out.println("");
        
        System.out.println(mca.getHalfWidthDifferenceSummaryStatistics("Response Time", 0.99));

        // get a statistic on the differences
        Statistic statistic = mca.getPairedDifferenceStatistic("SlowLoaders", "FastLoader");
        statistic.setConfidenceLevel(0.99);
        long sampleSize = statistic.estimateSampleSize(0.02);
        System.out.println("Estimated sample size = " + sampleSize);
        System.out.println("Done! Writing results to file.");
    }

}
