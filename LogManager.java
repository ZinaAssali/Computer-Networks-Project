import java.io.IOException;
import java.util.logging.*;

public class LogManager {
    private static Logger logger = Logger.getLogger("ServerLogger");

    public static Logger getLogger()   { return logger;}

    /*
    setup()
    Initializes the logger for logging server activity
     */
    public static synchronized void setup() {
        // Set up
        FileHandler fh = null;
        try {
            fh = new FileHandler("server_log.log", true);

            // Set format for logs (Date - Level - Message)
            fh.setFormatter(new SimpleFormatter());

            fh.setLevel(Level.ALL);

            logger.addHandler(fh);
            logger.setLevel(Level.ALL);

            // Stop logs from being printed to the consol
            logger.setUseParentHandlers(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}