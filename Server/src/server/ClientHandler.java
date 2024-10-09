package server;

import commands.Command;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientHandler {

    private Socket socket;
    private User user;
    private Server server;
    private final PrintWriter writer;
    private AtomicBoolean running;

    protected ClientHandler(PrintWriter writer, Server server, long id) {
        this.writer = writer;
        init(server, id);
    }

    public ClientHandler(Socket socket, long id, Server server) throws IOException {
        this.socket = socket;
        this.writer = new PrintWriter(socket.getOutputStream(), true);
        init(server, id);
    }

    private void init(Server server, long id) {
        UUID uuid = UUID.nameUUIDFromBytes(String.format("user%d", id).getBytes());
        user = new User(id, "user" + uuid);
        this.server = server;
        this.running = new AtomicBoolean(true);
    }

    private void sendOnJoining() {
        writer.println(String.format("Hi there, your current nickname: %s, " +
                        "if you want to change your nickname you can write: /nickname NAME", user.getUsername()));
    }

    public User getUser() {
        return user;
    }

    public PrintWriter getWriter() {
        return writer;
    }

    public void start() {
        sendOnJoining();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while (server.isRunning() && running.get()) {
                if ((line = reader.readLine()) != null) {
                    if (performInput(line))
                        break;
                }
            }
            server.remove(this, user.getUsername());
        } catch (IOException ignored) {
        }
    }

    protected boolean performInput(String line) {
        if (line == null || line.isBlank())
            return false;
        LocalDateTime time = LocalDateTime.now();
        String[] split = line.split(" ");
        Command command;
        if (line.startsWith("/") &&
                (command = server.getCommand(split[0].substring(1))) != null) {
            command.perform(this, Arrays.copyOfRange(split, 1, split.length));
        } else if (line.equals("/quit"))
            return true;
        else
            server.getOutputsOfUsers().forEach(x ->
                    x.writer.println(String.format("[%s] %s: %s", time, user.getUsername(), line)));
        return false;
    }

    public void shutdown() {
        running.set(false);
    }
}
