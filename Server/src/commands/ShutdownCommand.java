package commands;

import server.ClientHandler;
import server.Server;

public class ShutdownCommand extends Command {

    private final Server server;

    public ShutdownCommand(Server server) {
        super(server);
        this.server = server;
    }

    @Override
    public void perform(ClientHandler handler, String... args) {
        server.shutdown();
    }

    @Override
    public String commandName() {
        return "shutdown";
    }
}
