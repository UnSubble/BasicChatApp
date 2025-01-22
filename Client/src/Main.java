import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("Chat App");
        Client client = new Client();
        frame.add(client);
        frame.setBounds(200, 200, 800, 560);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        client.start();
    }
}