package ca.uwaterloo.redynisdaemon.threads;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlacementThread implements Runnable
{
    public static Logger log = LogManager.getLogger(PlacementThread.class);

    @Override
    public void run()
    {
        log.info("Placement Thread executed");
    }
}
