package ca.uwaterloo.redynisdaemon.threads;

import ca.uwaterloo.redynisdaemon.beans.PlacementInstruction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        log.info("Placement Thread executed");
        log.debug("placementInstructions: " + placementInstructions);

//        TODO: Write a function to execute the copy

//        TODO: Write a function to delete copies

    }
}
