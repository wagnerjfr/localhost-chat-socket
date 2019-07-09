import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class ChatServer implements Runnable {

    public static final String SERVER_EXIT_MESG = ":q";
    private List<PrintWriter> writers = new ArrayList<>();
    private List<ListenerClient> listeners = new ArrayList<>();
    private int port;
    private String host;
    private ServerSocket serverSocket;
    private int numClients;
    private SimpleDateFormat ft;

    public ChatServer(String host, int port) {
        this.host = host;
        this.port = port;
        ft = new SimpleDateFormat ("yyyy-MM-dd <HH:mm:ss>");
    }

    @Override
    public void run() {
        try {
            startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startServer() throws IOException {
        serverSocket = new ServerSocket(port);
        while (true) {
            Socket socket = serverSocket.accept();

            ListenerClient listener = new ListenerClient(socket);
            listeners.add(listener);
            new Thread(listener).start();

            writers.add(new PrintWriter(socket.getOutputStream()));

            numClients++;
        }
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    private void answerToAll(String text) {
        Date dNow = new Date( );
        String date = ft.format(dNow);
        for (PrintWriter writer : writers) {
            writer.println(date + ": " + text);
            writer.flush();
        }
    }

    private void closeServer() {
        for (PrintWriter writer : writers) {
            writer.close();
        }
        writers.clear();
        System.exit(0);
    }

    private class ListenerClient implements Runnable {
        private Scanner reader;
        private Socket socket;

        public ListenerClient(Socket socket) throws IOException {
            this.socket = socket;
            reader = new Scanner(socket.getInputStream());
        }

        @Override
        public void run() {
            String text;
            while (reader.hasNextLine()) {
                text = reader.nextLine();
                if (text.equals(SERVER_EXIT_MESG)) {
                    stop();
                    break;
                }
                System.out.println(text);
                answerToAll(text);
            }

            if (numClients == 0) {
                closeServer();
            }
        }

        private void stop() {
            try {
                numClients--;
                reader.close();
                socket.close();
                listeners.remove(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
