package server;

import commands.*;

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
    private final Map<String, Command> commandMap;

    public Server(int port) {
        try {
            this.running = new AtomicBoolean(false);
            this.socket = new ServerSocket(port);
            this.outputsOfUsers = new ArrayList<>();
            this.commandMap = new HashMap<>();
            initializeCommands();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeCommands() {
        new NicknameCommand(this);
        new DisconnectCommand(this);
        new KickCommand(this);
        new ShutdownCommand(this);
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
            throw new RuntimeException("server.Server is already running!");
        running.set(true);
        Logger logger = new Logger();
        Thread loggerThread = new Thread(logger::start);
        loggerThread.start();
        outputsOfUsers.add(logger);
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

    public void addCommand(Command command) {
        this.commandMap.putIfAbsent(command.commandName(), command);
    }

    public Command getCommand(String name) {
        return commandMap.get(name);
    }

    public synchronized void remove(ClientHandler handler, String username) {
        outputsOfUsers.forEach(x -> x.getWriter().println(username + " has left the chat!"));
        outputsOfUsers.remove(handler);
    }

    class Logger extends ClientHandler {

        protected Logger() {
            super(new PrintWriter(System.out, true), Server.this, 0);
        }

        @Override
        public void start() {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line;
            try {
                while (running.get()) {
                    if ((line = reader.readLine()) != null) {
                        this.performInput(line);
                    }
                }
            } catch (IOException e) {
            }
        }
    }
}
