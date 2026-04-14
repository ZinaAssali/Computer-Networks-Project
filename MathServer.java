/*
MathServer.Java
This class handles accepting client connections and gives each client its own thread to handle requests
 */

import java.io.*;
import java.net.*;

public class MathServer {
    static final int PORT = 6789;

    public static void main(String args[]) {
        ServerSocket serverSocket = null;
        Socket socket = null;

        // Create welcoming socket
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();

        }
        // Every time a new client connects to the server, start a thread to handle communication with it
        while (true) {
            try {
                // Create a connection socket for the new client
                socket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            // new thread for a client, passing the connection socket into it
            new MathThread(socket).start();
        }
    }
}
