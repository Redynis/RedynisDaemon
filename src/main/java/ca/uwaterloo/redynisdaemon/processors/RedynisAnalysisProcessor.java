package ca.uwaterloo.redynisdaemon.processors;

import ca.uwaterloo.redynisdaemon.threads.AnalyzerThread;
import ca.uwaterloo.redynisdaemon.utils.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RedynisAnalysisProcessor extends Processor
{
    private static Logger log = LogManager.getLogger(RedynisAnalysisProcessor.class);

    @Override
    public void process()
        throws Exception
    {
        final ScheduledExecutorService analysisScheduler =
            Executors.newScheduledThreadPool(Options.getInstance().getAppConfig().getAnalysisThreadPoolSize());
        final ScheduledExecutorService placementScheduler =
            Executors.newScheduledThreadPool(Options.getInstance().getAppConfig().getPlacementThreadPoolSize());

        log.info("RedynisAnalysis Processing initiated");
        analysisScheduler.scheduleWithFixedDelay(
            new AnalyzerThread(placementScheduler), 0, 5, TimeUnit.SECONDS
        );

    }
}
