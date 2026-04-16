import java.io.*;
import java.net.*;
import java.util.Scanner;

public class MathClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 6789;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        try {
            Socket socket = new Socket(SERVER_HOST, SERVER_PORT);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()));

            System.out.println("Connected to server");

            out.write("INIT\n");
            out.write(username + "\n");
            out.flush();
            System.out.println("Server: " + in.readLine());

            for (int i = 0; i < 3; i++) {
                System.out.print("Enter a math expression: ");
                String equation = scanner.nextLine();

                out.write("CALC\n");
                out.write(equation + "\n");
                out.flush();

                System.out.println("Server: " + in.readLine());
            }

            out.write("CLOSE\n");
            out.flush();
            System.out.println("Server: " + in.readLine());

            socket.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        scanner.close();
    }
}