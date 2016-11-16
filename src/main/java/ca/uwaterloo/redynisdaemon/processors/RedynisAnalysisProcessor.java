package ca.uwaterloo.redynisdaemon.processors;

import ca.uwaterloo.redynisdaemon.utils.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RedynisAnalysisProcessor extends Processor
{
    private static Logger log = LogManager.getLogger(RedynisAnalysisProcessor.class);

    @Override
    public void process()
        throws Exception
    {
        log.info("RedynisAnalysis Processing initiated");
    }
}
