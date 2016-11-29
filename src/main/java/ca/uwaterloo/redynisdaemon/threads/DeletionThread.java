package ca.uwaterloo.redynisdaemon.threads;

import ca.uwaterloo.redynisdaemon.exceptions.InternalAppError;
import ca.uwaterloo.redynisdaemon.instructions.DeletionInstruction;
import ca.uwaterloo.redynisdaemon.utils.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.util.Set;

class DeletionThread implements Runnable
{
    private static Logger log = LogManager.getLogger(PlacementThread.class);
    private Set<DeletionInstruction> deletionInstructions;

    DeletionThread(Set<DeletionInstruction> deletionInstructions)
    {
        this.deletionInstructions = deletionInstructions;
    }

    @Override
    public void run()
    {
        log.info("Deletion Thread begun");
        try
        {
            for (DeletionInstruction deletionInstruction : deletionInstructions)
            {
                executeDelete(deletionInstruction.getRedisKey(), deletionInstruction.getDeleteFromHosts());
            }

            log.info("Deletion Thread concluded");
        }
        catch (Exception e)
        {
            log.error("Encountered exception while executing Analyzer Thread. Terminating thread. ", e);
        }
    }

    private void executeDelete(String redisKey, Set<String> deleteFromHosts)
        throws InternalAppError
    {
        Jedis jedis;
        Integer dataPort = Options.getInstance().getAppConfig().getDataPort();
        for (String host: deleteFromHosts)
        {
            jedis = new Jedis(host, dataPort);
            jedis.del(redisKey);
        }
    }
}
