import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {
    private final AtomicBoolean running;
    private final Socket socket;
    private final Scanner scanner;

    public Client(int port) throws IOException {
        this.socket = new Socket("localhost", port);
        this.running = new AtomicBoolean(true);
        this.scanner = new Scanner(System.in);
    }

    public Client() throws IOException {
        this(8080);
    }

    public void start() throws IOException {
        BufferedReader s = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        new Thread(() -> {
            while (running.get()) {
                String input;
                try {
                    if ((input = s.readLine()) != null)
                        System.out.println(input);
                } catch (IOException e) {
                }
            }
            }).start();
        while (running.get()) {
            String line = scanner.nextLine();
            writer.println(line);
            if (line.equals("/quit")) {
                running.set(false);
                System.exit(0);
            }
        }
    }
}
