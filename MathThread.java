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
                    out.writeBytes("Closing connection");
                    clientSocket.close();
                }
                if (message.equalsIgnoreCase("INIT"))   {
                    // Expecting the next line to be the username
                    String userArg = bufferedIn.readLine();
                    if (userArg == null)    {
                        out.writeBytes("Missing argument: User Name");
                    }
                    else {
                        this.userName = userArg;
                        out.writeBytes("User initialized");
                    }
                }
                if (message.equalsIgnoreCase("CALC"))   {
                    // Expecting the next line to be the equation
                    String equation = bufferedIn.readLine();
                }
                // TODO Log things
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
