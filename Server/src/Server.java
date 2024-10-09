import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {

    private final AtomicBoolean running;
    private final ServerSocket socket;
    private Thread serverThread;
    private final List<ClientHandler> outputsOfUsers;

    public Server(int port) {
        try {
            this.running = new AtomicBoolean(false);
            this.socket = new ServerSocket(port);
            this.outputsOfUsers = new ArrayList<>();
            this.outputsOfUsers.add(new Logger());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Server() {
        this(8080);
    }

    public List<ClientHandler> getOutputsOfUsers() {
        return Collections.unmodifiableList(outputsOfUsers);
    }

    public boolean isRunning() {
        return running.get();
    }

    public void execute() {
        if (running.get())
            throw new RuntimeException("Server is already running!");
        running.set(true);
        serverThread = new Thread(() -> {
            while (running.get()) {
                try {
                    Socket newLogin = socket.accept();
                    ClientHandler handler = new ClientHandler(newLogin, outputsOfUsers.size() + 1, this);
                    Thread t = new Thread(handler::start);
                    t.start();
                    outputsOfUsers.add(handler);
                } catch (IOException ignored) {
                }
            }
        });
        serverThread.start();
    }

    public void awaitTermination(long time) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                running.set(false);
                shutdown();
            }
        }, time);
    }

    public void shutdown() {
        running.set(false);
        serverThread.interrupt();
        System.exit(0);
    }

    public synchronized void remove(ClientHandler handler, String username) {
        outputsOfUsers.forEach(x -> x.getWriter().println(username + " has left the chat!"));
        outputsOfUsers.remove(handler);
    }

    class Logger extends ClientHandler {

        protected Logger() {
            super(new PrintWriter(System.out, true));
        }

        @Override
        public void start() {

        }
    }
}
