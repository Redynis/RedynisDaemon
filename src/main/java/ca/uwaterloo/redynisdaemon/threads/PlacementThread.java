package ca.uwaterloo.redynisdaemon.threads;

import ca.uwaterloo.redynisdaemon.beans.PlacementInstruction;
import ca.uwaterloo.redynisdaemon.exceptions.InternalAppError;
import ca.uwaterloo.redynisdaemon.utils.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;

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
        try
        {
            log.info("Placement Thread executed");
            log.debug("placementInstructions: " + placementInstructions);

            for (PlacementInstruction instruction: placementInstructions)
            {
                executeCopy(
                    instruction.getRedisKey(),
                    instruction.getSourceHost(),
                    instruction.getReplicateOnHosts()
                );
                log.debug("executed copy");
            }
        }
        catch (Exception e)
        {
            log.error("Encountered exception while executing Analyzer Thread. Terminating thread. ", e);
        }

    }

    private void executeCopy(String redisKey, String sourceHost, Set<String> replicateOnHosts)
        throws InternalAppError
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
}
