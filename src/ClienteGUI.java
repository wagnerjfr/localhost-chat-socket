import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class ClienteGUI extends JFrame implements Runnable {

    private JPanel contentPane;
    private JTextField textField;
    private ChatClient chatClient;
    private static int yPostion = 100;
    private static int height = 200;

    /**
     * Create the frame.
     * @throws IOException 
     * @throws UnknownHostException 
     */
    public ClienteGUI(final String name, ChatServer server) throws UnknownHostException, IOException {

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, yPostion, 450, height);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        yPostion += height+50;

        JPanel panel = new JPanel();
        contentPane.add(panel, BorderLayout.NORTH);

        textField = new JTextField();
        panel.add(textField);
        textField.setColumns(10);

        JButton btnNewButton = new JButton("Send");
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                chatClient.sendToServer(name + " says " + textField.getText());
                textField.setText("");
            }
        });
        panel.add(btnNewButton);

        JScrollPane scrollPane = new JScrollPane();
        contentPane.add(scrollPane, BorderLayout.CENTER);

        JTextArea textArea = new JTextArea();
        scrollPane.setViewportView(textArea);

        chatClient = new ChatClient(textArea, server);
        setTitle(name + " (online)");

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                chatClient.sendToServer(name + " left");
                chatClient.sendToServer(ChatServer.SERVER_EXIT_MESG);
                chatClient.disconnect();
            }
        });
    }

    @Override
    public void run() {
        this.setVisible(true);
    }
}
