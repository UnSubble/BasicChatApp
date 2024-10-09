package commands;

import server.ClientHandler;
import server.Server;

public abstract class Command {

    public Command(Server server) {
        server.addCommand(this);
    }

    public abstract void perform(ClientHandler handler, String... args);

    public abstract String commandName();
}
