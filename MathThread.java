/*
MathThread.java
This class handles requests from each client connecting to the server

 */
import java.io.*;
import java.net.*;

public class MathThread extends Thread {
    protected Socket clientSocket;
    protected String userName;

    public MathThread(Socket clientSocket) {
        // Set up the connection socket for this thread so it can talk with the client
        this.clientSocket = clientSocket;
    }

    public void run()   {
        // Initialize I/O streams for client and a buffer for them
        InputStream in = null;
        BufferedReader bufferedIn = null;
        DataOutputStream out = null;

        try {
            in = clientSocket.getInputStream();
            bufferedIn = new BufferedReader(new InputStreamReader(in));
            out = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e)   {
            // Terminate the thread if making either stream and/or buffer fail
            return;
        }

        String message;
        while (true)    {
            try {
                message = bufferedIn.readLine();
                if (message.equalsIgnoreCase("CLOSE"))  {
                    // When the client sends terminate message, terminate the connection
                    clientSocket.close();
                }
                // TODO Handle other messages (init, calc, etc.)
                // TODO Log things
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
