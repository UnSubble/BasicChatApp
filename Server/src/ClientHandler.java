import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

public class ClientHandler {

    private Socket socket;
    private User user;
    private Server server;
    private PrintWriter writer;

    protected ClientHandler(PrintWriter writer) {
        this.writer = writer;
    }

    public ClientHandler(Socket socket, long id, Server server) throws IOException {
        this.socket = socket;
        UUID uuid = UUID.nameUUIDFromBytes(String.format("user%d", id).getBytes());
        user = new User(id, "user" + uuid);
        this.writer = new PrintWriter(socket.getOutputStream(), true);
        this.server = server;
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
            while (server.isRunning()) {
                if ((line = reader.readLine()) != null) {
                    LocalDateTime time = LocalDateTime.now();
                    String finalLine = line;
                    if (line.startsWith("/nickname "))
                        user.setUsername(line.substring("/nickname ".length()));
                    else if (line.equals("/quit"))
                        break;
                    else
                        server.getOutputsOfUsers().forEach(x ->
                            x.writer.println(String.format("[%s] %s: %s", time, user.getUsername(), finalLine)));
                }
            }
            server.remove(this, user.getUsername());
        } catch (IOException ignored) {
        }
    }
}
