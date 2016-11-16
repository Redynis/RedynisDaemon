package ca.uwaterloo.redynisdaemon.threads;

import ca.uwaterloo.redynisdaemon.beans.PlacementInstruction;
import ca.uwaterloo.redynisdaemon.beans.UsageMetric;
import ca.uwaterloo.redynisdaemon.exceptions.InternalAppError;
import ca.uwaterloo.redynisdaemon.utils.Constants;
import ca.uwaterloo.redynisdaemon.utils.RedisHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;

public class AnalyzerThread implements Runnable
{
    private static Logger log = LogManager.getLogger(AnalyzerThread.class);
    private ScheduledExecutorService placementScheduler;

    public AnalyzerThread(ScheduledExecutorService placementScheduler)
    {
        this.placementScheduler = placementScheduler;
    }

    @Override
    public void run()
    {
        try
        {
            log.info("Analyzer Thread begun");
            Set<PlacementInstruction> placementInstructions = new HashSet<>();

            Map<String, UsageMetric> allUsageMetrics = getAllUsageMetrics();

            PlacementInstruction instruction = null;
            for (Map.Entry<String, UsageMetric> entry : allUsageMetrics.entrySet())
            {
                instruction = analyzeKeyMetrics(entry);
                if(null != instruction)
                {
                    placementInstructions.add(instruction);
                }
            }

            if (placementInstructions.size() > 0)
            {
                placementScheduler.execute(new PlacementThread(placementInstructions));
            }
        }
        catch (Exception e)
        {
            log.error("Encountered exception while executing Analyzer Thread. Terminating thread. ", e);
        }

        log.info("Analyzer Thread concluded");
    }

    private PlacementInstruction analyzeKeyMetrics(Map.Entry<String, UsageMetric>keyMetric)
    {
        PlacementInstruction instruction = null;

        String redisKey = keyMetric.getKey();
        UsageMetric usageMetric = keyMetric.getValue();

        Map<String, Integer> hostAccesses = usageMetric.getHostAccesses();
        Set<String> oldHosts = usageMetric.getHosts();
        Set<String> currentHosts = new HashSet<>();

        String hostname = null;
        Integer hostAccessCount = null;
        for (Map.Entry<String, Integer> hostAccessEntry : hostAccesses.entrySet())
        {
            hostname = hostAccessEntry.getKey();
            hostAccessCount = hostAccessEntry.getValue();

            log.debug("hostAccessEntry: " + hostAccessEntry);
            if (exceedsAccessThreshold(hostAccessCount, usageMetric.getTotalAccessCount()))
            {
                currentHosts.add(hostname);
            }
        }

        // Set difference gives use the new hosts to replicate on
        Set<String> newHosts = new HashSet<>(currentHosts);
        newHosts.removeAll(oldHosts);

        // Set difference gives use the hosts to delete from
        Set<String> deleteHosts = new HashSet<>(oldHosts);
        deleteHosts.removeAll(currentHosts);

        log.debug("oldHosts: " + oldHosts);
        log.debug("currentHosts: " + currentHosts);
        log.debug("newHosts: " + newHosts);
        log.debug("deleteHosts: " + deleteHosts);

        String sourceHost = oldHosts.iterator().next();
        if (newHosts.size() != 0 && deleteHosts.size() != 0)
        {
            instruction = new PlacementInstruction(redisKey, sourceHost, newHosts, deleteHosts);
        }

        return instruction;
    }

    private Boolean exceedsAccessThreshold(Integer accesses, Integer totalAccessCount)
    {
        Double factor = (double) accesses / (double) totalAccessCount;
        log.debug("factor: " + factor);
        return factor >= Constants.ACCESS_THRESHOLD_FACTOR;
    }

    private Map<String, UsageMetric> getAllUsageMetrics()
        throws InternalAppError, IOException
    {
        RedisHelper redisHelper = RedisHelper.getInstance();
        List<String> keys = new ArrayList<>(redisHelper.getAllKeys());
        Map<String, UsageMetric> usageMetricMap = new HashMap<>();

        List<String> usageMetrics = redisHelper.multiGet(keys);

        for (int i = 0; i < keys.size(); i++)
        {
            usageMetricMap.put(keys.get(i), Constants.MAPPER.readValue(usageMetrics.get(i), UsageMetric.class));
        }

        return usageMetricMap;
    }
}
