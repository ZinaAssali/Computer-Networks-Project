/*
MathThread.java
This class handles requests from each client connecting to the server

 */
import java.io.*;
import java.net.*;
import java.util.logging.Logger;

public class MathThread extends Thread {
    protected Socket clientSocket;
    protected String userName;

    public MathThread(Socket clientSocket) {
        // Set up the connection socket for this thread so it can talk with the client
        this.clientSocket = clientSocket;
        this.userName = clientSocket.getRemoteSocketAddress().toString();
    }

    /*
    String calculate
    Takes in an equation as a string, parses it and performs the appropriate calculations to obtain the result
    Parameters:     String equation - The equation to be parsed and evaluated
    Output:         String - Message that indicates the value of the evaluated equation or an error message
     */
    private String calculate(String equation)    {
        // Split the equation into pieces eliminated by white space
        String[] tokens = equation.trim().split("\\s+");

        // Check if the equation passed in is actually an equation for basic math
        if ((tokens.length < 3) || (tokens.length % 2 == 0))    {
            return "Improper format: Needs to be in form of a + b";
        }

        double result;
        try {
            // Initialize result to first number
            result = Double.parseDouble(tokens[0]);

            // Iterate through each part of the equation by 2 (Looking at next operator and operand
            for (int i = 1; i < tokens.length; i += 2) {
                String operator = tokens[i];
                Double operand = Double.parseDouble(tokens[i+1]);

                // Do the math (Only supports addition, subtraction, multiplication, and division)
                switch (operator)   {
                    case "+":
                        result += operand;
                        break;
                    case "-":
                        result -= operand;
                        break;
                    case "*":
                        result *= operand;
                        break;
                    case "/":
                        if (operand == 0)   {
                            return "Cannot divide by zero";
                        }
                        else {
                            result /= operand;
                        }
                        break;
                    default:
                        return "Encountered unexpected operator";
                }
            }
        } catch(NumberFormatException e) {
            return "Could not parse a number";
        }

        return "Result: " + String.valueOf(result);
    }

    public void run()   {
        // Initialize I/O streams for client and a buffer for them
        InputStream in = null;
        BufferedReader bufferedIn = null;
        DataOutputStream out = null;
        Logger log = LogManager.getLogger();

        try {
            bufferedIn = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            out = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e)   {
            // Terminate the thread if making either stream and/or buffer fail
            return;
        }

        String message;
        // Start the timer for how long the user has been connected
        long startTime = System.currentTimeMillis();
        while (true)    {
            try {
                message = bufferedIn.readLine();
                if (message == null) {
                    break;
                }
                if (message.equalsIgnoreCase("INIT")) {
                    String userArg = bufferedIn.readLine();

                    if (userArg == null) {
                        out.writeBytes("Missing argument: User Name\n");
                        out.flush();
                    } else {
                        this.userName = userArg;
                        out.writeBytes("User initialized\n");
                        out.flush();
                        log.info(userName + "(" + clientSocket.getRemoteSocketAddress() + ") has connected");
                    }
                } else if (message.equalsIgnoreCase("CALC")) {
                    String equation = bufferedIn.readLine();

                    if (equation == null) {
                        out.writeBytes("Missing argument: equation\n");
                        out.flush();
                        continue;
                    }

                    log.info(userName + "(" + clientSocket.getRemoteSocketAddress()
                            + ") requested calculation of [" + equation + "]");

                    out.writeBytes(calculate(equation) + "\n");
                    out.flush();
                } else if (message.equalsIgnoreCase("CLOSE")) {
                    out.writeBytes("Closing connection\n");
                    out.flush();

                    long duration = System.currentTimeMillis() - startTime;
                    if (userName.equals(clientSocket.getRemoteSocketAddress().toString())) {
                        log.warning("Unknown user(" + userName + ") disconnected. Duration: " + duration + " ms");
                    } else {
                        log.info(userName + "(" + clientSocket.getRemoteSocketAddress()
                                + ") disconnected. Duration: " + duration + " ms");
                    }

                    clientSocket.close();
                    break;
                } else {
                    out.writeBytes("Unknown command\n");
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
