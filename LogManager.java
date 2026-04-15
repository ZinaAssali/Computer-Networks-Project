import java.util.logging.*;
import java.io.IOException;

public class LogManager {
    private static Logger logger;

    public static Logger getLogger()   { return logger;}

    /*
    setup()
    Initializes the logger for logging server activity
     */
    public static synchronized void setup() {
        // Do nothing if the logger is already set up
        if (logger != null) { return; }

        // Set up
        FileHandler fh = null;
        try {
            fh = new FileHandler("server_log.log", true);

            // Set format for logs (Date - Level - Message)
            fh.setFormatter(new SimpleFormatter());

            logger.addHandler(fh);

            // Stop logs from being printed to the consol
            logger.setUseParentHandlers(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
