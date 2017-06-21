import javax.swing.*;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

import static java.lang.System.out;

public class mtCoreMain extends JFrame {
    private JTextArea logArea = new JTextArea("", 32, 64);
    PrintStream printStream = new PrintStream(new LogOutputStream(logArea));
    private JPanel panel = new JPanel();
    public static void main (String[] args) throws SocketException {
        new mtCoreMain();
    }
    public mtCoreMain() {
        super("TEST FORM!");
        System.setOut(printStream);
        System.setErr(printStream);

        setSize(800,600);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        logArea.setEditable(false);

        panel.add(new JScrollPane(logArea));
        add(panel);
        setLocationByPlatform(true);
        setVisible(true);

        //(new IncomingSocketThread()).start();
    }

}
