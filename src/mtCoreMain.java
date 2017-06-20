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
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets))
            displayInterfaceInformation(netint);
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

    static void displayInterfaceInformation(NetworkInterface netint) throws SocketException {
        out.printf("Display name: %s\n", netint.getDisplayName());
        out.printf("Name: %s\n", netint.getName());
        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
            out.printf("InetAddress: %s\n", inetAddress);
        }
        out.printf("\n");
    }
}
