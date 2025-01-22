import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client extends JPanel {
    private final AtomicBoolean running;
    private final Socket socket;
    private final JTextArea history;
    private final JButton chatButton;
    private final JTextArea textArea;
    private final PrintWriter writer;

    public Client(int port) throws IOException {
        this.socket = new Socket("localhost", port);
        this.running = new AtomicBoolean(true);
        this.history = new JTextArea();
        this.textArea = new JTextArea();
        this.chatButton = new JButton();
        this.writer = new PrintWriter(socket.getOutputStream(), true);
        this.init();
    }

    private void init() {
        history.setEditable(false);
        history.setLineWrap(true);
        history.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(history);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBounds(20, 20, 760, 220);

        textArea.setBounds(20, 400, 580, 100);

        chatButton.setBounds(640, 425, 120, 50);
        chatButton.setText("Chat");
        chatButton.addActionListener(new ButtonListener());

        this.setLayout(null);
        this.add(scrollPane);
        this.add(chatButton);
        this.add(textArea);
    }

    public Client() throws IOException {
        this(8080);
    }

    public void start() throws IOException {
        BufferedReader s = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        new Thread(() -> {
            while (running.get()) {
                try {
                    String input = s.readLine();
                    if (input != null) {
                        SwingUtilities.invokeLater(() -> {
                            history.append(input + "\n");
                            history.setCaretPosition(history.getDocument().getLength());
                        });
                    }
                } catch (IOException e) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                            "Disconnected: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
                    running.set(false);
                }
            }
        }).start();
    }

    public void shutdown() throws IOException {
        running.set(false);
        writer.close();
        socket.close();
    }

    private class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String message = textArea.getText().trim();
            if (message.isEmpty()) return;
            writer.println(message);
            textArea.setText("");
            if (message.equals("/quit")) {
                try {
                    shutdown();
                } catch (IOException ignored) {
                }
                System.exit(0);
            }
        }
    }
}
