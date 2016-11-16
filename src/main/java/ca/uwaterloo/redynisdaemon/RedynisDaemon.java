package ca.uwaterloo.redynisdaemon;

import ca.uwaterloo.redynisdaemon.processors.Processor;
import ca.uwaterloo.redynisdaemon.processors.RedynisAnalysisProcessor;
import ca.uwaterloo.redynisdaemon.utils.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RedynisDaemon
{
    private static Logger log = LogManager.getLogger(RedynisDaemon.class);

    public static void main(String[] args)
    {
        try
        {
            log.info("Commencing RedynisDaemon run");
            Options.initializeInstance(args);

            Processor processor = new RedynisAnalysisProcessor();
            processor.process();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
