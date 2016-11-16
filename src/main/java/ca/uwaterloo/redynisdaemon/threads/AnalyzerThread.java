package ca.uwaterloo.redynisdaemon.threads;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        placementScheduler.execute(new PlacementThread());
    }
}
