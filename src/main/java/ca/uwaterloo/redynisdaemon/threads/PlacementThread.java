package ca.uwaterloo.redynisdaemon.threads;

import ca.uwaterloo.redynisdaemon.beans.PlacementInstruction;
import ca.uwaterloo.redynisdaemon.beans.UsageMetric;
import ca.uwaterloo.redynisdaemon.exceptions.InternalAppError;
import ca.uwaterloo.redynisdaemon.utils.Constants;
import ca.uwaterloo.redynisdaemon.utils.Options;
import ca.uwaterloo.redynisdaemon.utils.RedisHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.Set;

class PlacementThread implements Runnable
{
    private static Logger log = LogManager.getLogger(PlacementThread.class);
    private Set<PlacementInstruction> placementInstructions;

    PlacementThread(Set<PlacementInstruction> placementInstructions)
    {
        this.placementInstructions = placementInstructions;
    }

    @Override
    public void run()
    {
        log.info("Placement Thread begun");
        try
        {
            log.debug("placementInstructions: " + placementInstructions);

            for (PlacementInstruction instruction: placementInstructions)
            {
                executeCopy(
                    instruction.getRedisKey(),
                    instruction.getSourceHost(),
                    instruction.getReplicateOnHosts()
                );

                executeDelete(
                    instruction.getRedisKey(),
                    instruction.getDeleteFromHosts()
                );

                updateOwnerHosts(
                    instruction.getRedisKey(), instruction.getReplicateOnHosts(), instruction.getDeleteFromHosts()
                );
                log.debug("executed copy");
            }
        }
        catch (Exception e)
        {
            log.error("Encountered exception while executing Analyzer Thread. Terminating thread. ", e);
        }

        log.info("Placement Thread concluded");
    }

    private void executeDelete(String redisKey, Set<String> deleteFromHosts)
        throws InternalAppError
    {
        Jedis jedis = null;
        Integer dataPort = Options.getInstance().getAppConfig().getDataPort();
        for (String host: deleteFromHosts)
        {
            jedis = new Jedis(host, dataPort);
            jedis.del(redisKey);
        }
    }

    private void executeCopy(String redisKey, String sourceHost, Set<String> replicateOnHosts)
        throws InternalAppError, IOException
    {
        Jedis jedis = null;
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

        UsageMetric newUsageMetric =
            new UsageMetric(
                usageMetric.getTotalAccessCount(),
                hosts,
                usageMetric.getHostAccesses()
            );

        RedisHelper.getInstance().setValue(redisKey, Constants.MAPPER.writeValueAsString(newUsageMetric));
    }

}
