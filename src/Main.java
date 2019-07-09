import java.io.IOException;
import java.net.UnknownHostException;

public class Main {
    public static int SERVER_PORT = 5000;
    public static String SERVER_HOST = "localhost";

    public static void main(String[] args) throws InterruptedException, UnknownHostException, IOException {
        ChatServer server = new ChatServer(SERVER_HOST, SERVER_PORT);
        Thread tserver = new Thread(server);
        tserver.start();
        Thread.sleep(1000);

        new Thread(new ClienteGUI("User 1", server)).start();
        new Thread(new ClienteGUI("User 2", server)).start();
        new Thread(new ClienteGUI("User 3", server)).start();
    }
}
