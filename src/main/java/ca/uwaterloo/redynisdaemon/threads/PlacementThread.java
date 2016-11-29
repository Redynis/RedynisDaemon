package ca.uwaterloo.redynisdaemon.threads;

import ca.uwaterloo.redynisdaemon.instructions.DeletionInstruction;
import ca.uwaterloo.redynisdaemon.instructions.PlacementInstruction;
import ca.uwaterloo.redynisdaemon.beans.UsageMetric;
import ca.uwaterloo.redynisdaemon.exceptions.InternalAppError;
import ca.uwaterloo.redynisdaemon.utils.Constants;
import ca.uwaterloo.redynisdaemon.utils.Options;
import ca.uwaterloo.redynisdaemon.utils.RedisHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class PlacementThread implements Runnable
{
    private static Logger log = LogManager.getLogger(PlacementThread.class);
    private Set<PlacementInstruction> placementInstructions;
    private ScheduledExecutorService deletionScheduler;

    PlacementThread(Set<PlacementInstruction> placementInstructions, ScheduledExecutorService deletionScheduler)
    {
        this.placementInstructions = placementInstructions;
        this.deletionScheduler = deletionScheduler;
    }

    @Override
    public void run()
    {
        log.info("Placement Thread begun");
        try
        {
            log.debug("placementInstructions: " + placementInstructions);
            Set<DeletionInstruction> deletionInstructions = new HashSet<>();

            for (PlacementInstruction instruction: placementInstructions)
            {

                if (instruction.getSourceHost() != null && !instruction.getSourceHost().isEmpty() &&
                    null != instruction.getReplicateOnHosts() && !instruction.getReplicateOnHosts().isEmpty())
                {
                    executeCopy(
                        instruction.getRedisKey(),
                        instruction.getSourceHost(),
                        instruction.getReplicateOnHosts()
                    );
                }

                if (null != instruction.getDeleteFromHosts() && !instruction.getDeleteFromHosts().isEmpty())
                {
                    deletionInstructions.add(
                        new DeletionInstruction(instruction.getRedisKey(), instruction.getDeleteFromHosts())
                    );
                }

                updateOwnerHosts(
                    instruction.getRedisKey(), instruction.getReplicateOnHosts(), instruction.getDeleteFromHosts()
                );
                log.debug("executed copy");
            }

            if (deletionInstructions.size() > 0)
            {
                deletionScheduler.schedule(
                    new DeletionThread(deletionInstructions),
                    Options.getInstance().getAppConfig().getSecondsBetweenAnalysis(),
                    TimeUnit.SECONDS
                );
            }

            log.info("Placement Thread concluded");
        }
        catch (Exception e)
        {
            log.error("Encountered exception while executing Analyzer Thread. Terminating thread. ", e);
        }
    }

    private void executeCopy(String redisKey, String sourceHost, Set<String> replicateOnHosts)
        throws InternalAppError, IOException
    {
        Jedis jedis;
        Integer dataPort = Options.getInstance().getAppConfig().getDataPort();

        String redisValue;

        jedis = new Jedis(sourceHost, dataPort);
        redisValue = jedis.get(redisKey);

        for (String host: replicateOnHosts)
        {
            jedis = new Jedis(host, dataPort);
            jedis.set(redisKey, redisValue);
        }
    }

    private void updateOwnerHosts(String redisKey, Set<String> newHosts, Set<String> obsoleteHosts)
        throws InternalAppError, IOException
    {
        UsageMetric usageMetric =
            Constants.MAPPER.readValue(RedisHelper.getInstance().getValue(redisKey), UsageMetric.class);

        Set<String> hosts = usageMetric.getHosts();
        hosts.addAll(newHosts);
        hosts.removeAll(obsoleteHosts);

        log.debug("redisKey: " + redisKey);
        log.debug("hosts: " + hosts);

        if (hosts.size() > 0)
        {
            UsageMetric newUsageMetric =
                new UsageMetric(
                    usageMetric.getTotalAccessCount(),
                    hosts,
                    usageMetric.getHostAccesses(),
                    usageMetric.getLastAccessedDate()
                );

            RedisHelper.getInstance().setValue(redisKey, Constants.MAPPER.writeValueAsString(newUsageMetric));
        }
        else
        {
            RedisHelper.getInstance().deleteKey(redisKey);
        }

    }

}
