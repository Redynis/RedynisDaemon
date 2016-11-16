package ca.uwaterloo.redynisdaemon.utils;

import ca.uwaterloo.redynisdaemon.exceptions.InternalAppError;
import ca.uwaterloo.redynisdaemon.exceptions.InvalidConfigurationError;
import lombok.Getter;
import lombok.ToString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

@ToString
@Getter
public class Options
{
    private static Options instance;
    private static Logger log = LogManager.getLogger(Options.class);

    @Option(name = "-help", usage = "help", metaVar = "HELP")
    private Boolean help = false;

    @Option(name = "-configFilePath", usage = "App Config file path", metaVar = "CONFIG_FILEPATH", required = true)
    private String configFilePath;

    public static void initializeInstance(String[] args)
        throws InvalidConfigurationError
    {
        if (null == instance)
        {
            instance = new Options(args);
        }
    }

    public static Options getInstance()
        throws InternalAppError
    {
        if (null == instance)
        {
            throw new InternalAppError("Tried accessing options without initializing it first.");
        }
        return instance;
    }

    private Options(String[] args)
        throws InvalidConfigurationError
    {
        CmdLineParser parser = new CmdLineParser(this);

        if (help)
        {
            parser.printUsage(System.out);
            System.exit(0);
        }

        try
        {
            parser.parseArgument(args);
        }
        catch (CmdLineException e)
        {
            log.error("CmdLineException while reading options", e);
            throw new InvalidConfigurationError(
                "CmdLineException while reading options", e
            );
        }

        log.debug("configFilePath: " + getConfigFilePath());
        log.info("Options successfully read");
    }
}