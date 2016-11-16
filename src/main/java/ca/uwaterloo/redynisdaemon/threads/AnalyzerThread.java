package ca.uwaterloo.redynisdaemon.threads;

import ca.uwaterloo.redynisdaemon.beans.PlacementInstruction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

public class AnalyzerThread implements Runnable
{
    private static Logger log = LogManager.getLogger(AnalyzerThread.class);
    ScheduledExecutorService placementScheduler;

    public AnalyzerThread(ScheduledExecutorService placementScheduler)
    {
        this.placementScheduler = placementScheduler;
    }

    @Override
    public void run()
    {
        log.info("Analyzer Thread begun");
        Set<PlacementInstruction> placementInstructions = null;

//        TODO: Get all keys from metadata and get usage stats

//        TODO: Write function to analyze single key stats and emit a PlacementInstruction

//        TODO: Batch all the placement instructions and invole Placement Thread

        placementScheduler.execute(new PlacementThread(placementInstructions));
    }
}
