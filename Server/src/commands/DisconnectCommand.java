package commands;

import server.ClientHandler;
import server.Server;

public class DisconnectCommand extends Command {

    public DisconnectCommand(Server server) {
        super(server);
    }

    @Override
    public void perform(ClientHandler handler, String... args) {
        handler.shutdown();
    }

    @Override
    public String commandName() {
        return "disconnect";
    }
}
