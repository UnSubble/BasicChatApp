package commands;

import server.ClientHandler;
import server.Server;

public class NicknameCommand extends Command {

    public NicknameCommand(Server server) {
        super(server);
    }

    @Override
    public void perform(ClientHandler handler, String... args) {
        handler.getUser().setUsername(String.join(" ", args));
    }

    @Override
    public String commandName() {
        return "nickname";
    }
}
