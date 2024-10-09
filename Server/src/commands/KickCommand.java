package commands;

import server.ClientHandler;
import server.Server;

public class KickCommand extends Command {
    private final Server server;

    public KickCommand(Server server) {
        super(server);
        this.server = server;
    }

    @Override
    public void perform(ClientHandler handler, String... args) {
        if (handler.getUser().getId() == 0) {
            for (ClientHandler clientHandler : server.getOutputsOfUsers()) {
                if (clientHandler.getUser() != null && args[0].equals(clientHandler.getUser().getUsername())) {
                    clientHandler.shutdown();
                    handler.getWriter().println("[SERVER]: Successfully kicked!");
                    return;
                }
            }
        }
    }

    @Override
    public String commandName() {
        return "kick";
    }
}
