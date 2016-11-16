package ca.uwaterloo.redynisdaemon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RedynisDaemon
{
    private static Logger log = LogManager.getLogger(RedynisDaemon.class);

    public static void main(String[] args)
    {
        log.info("Commencing RedynisDaemon run");
    }
}
