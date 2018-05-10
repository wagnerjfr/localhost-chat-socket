package entity;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.JTextArea;

public class ChatClient {

    private Socket socket;
    private PrintWriter writer;
    private JTextArea textArea;
    private ChatServer server;
    private ListenerServer listener;

    public ChatClient(JTextArea textArea, ChatServer server) throws UnknownHostException, IOException {
        this.textArea = textArea;
        this.server = server;
        connectToServer();
    }

    private void connectToServer() throws UnknownHostException, IOException {
        socket = new Socket(server.getHost(), server.getPort());
        writer = new PrintWriter(socket.getOutputStream());
        listener = new ListenerServer();
        new Thread(listener).start();
    }

    public void sendToServer(String text) {
        writer.println(text);
        writer.flush();
    }

    public void disconnect() {
        try {
            writer.close();
            listener.stop();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ListenerServer implements Runnable {
        Scanner reader;

        public ListenerServer() throws IOException {
            reader = new Scanner(socket.getInputStream());
        }

        @Override
        public void run() {
            String text;
            while (reader.hasNextLine()) {
                text = reader.nextLine();
                textArea.append(text + "\n");
            }
        }

        public void stop() {
            reader.close();
        }
    }
}
