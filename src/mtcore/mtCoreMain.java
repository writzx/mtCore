package mtcore;

import mtcore.parser.metadata.CFileMetadata;
import mtcore.parser.metadata.CMetadata;
import mtcore.parser.metadata.CSoundMetadata;
import mtcore.security.Decrypt;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import sun.misc.BASE64Encoder;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.swing.*;
import java.awt.color.CMMException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.security.*;

public class mtCoreMain extends JFrame {
    private JTextArea logArea = new JTextArea("", 32, 64);
    PrintStream printStream = new PrintStream(new LogOutputStream(logArea));

    private JPanel panel = new JPanel();

    JTextField input = new JTextField("",24);
    JTextField output = new JTextField("", 32);
    JButton button = new JButton("GENERATE");

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

        panel.add(input);
        panel.add(output);
        panel.add(button);
        panel.add(new JScrollPane(logArea));
        add(panel);
        setLocationByPlatform(true);
        setVisible(true);

        System.out.println(new AESEngine().getBlockSize());
        //(new mtcore.IncomingSocketThread()).start();

        // generate("rsa_pub","rsa_priv");
//        KeyGenerator kg = null;
//        try {
//            kg = KeyGenerator.getInstance("AES");
//            kg.init(256);
//            SecretKey sk = kg.generateKey();
//
//            System.out.println(Hex.toHexString(sk.getEncoded()));
//
//            AES abc = new AES();
//            abc.setPadding(new PKCS7Padding());
//            abc.setKey(sk.getEncoded());
//            button.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    byte[] ba = new byte[0];
//                    try {
//                        ba = input.getText().getBytes("UTF-8");
//                        byte[] encr = abc.encrypt(ba);
//                        output.setText(Hex.toHexString(encr));
//                        System.out.println(ba.length + " -> " + encr.length);
//                    } catch (UnsupportedEncodingException | InvalidCipherTextException e1) {
//                        e1.printStackTrace();
//                    }
//                }
//            });
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
    }

    private void generate (String publicKeyFilename, String privateFilename) {
        try {

            Security.addProvider(new BouncyCastleProvider());

            // Create the public and private keys
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
            BASE64Encoder b64 = new BASE64Encoder();

            SecureRandom random = createFixedRandom();
            generator.initialize(3072, random);

            KeyPair pair = generator.generateKeyPair();
            Key pubKey = pair.getPublic();
            Key privKey = pair.getPrivate();

            System.out.println("publicKey : " + b64.encode(pubKey.getEncoded()));
            System.out.println("privateKey : " + b64.encode(privKey.getEncoded()));

            BufferedWriter out = new BufferedWriter(new FileWriter(publicKeyFilename));
            out.write(b64.encode(pubKey.getEncoded()));
            out.close();

            out = new BufferedWriter(new FileWriter(privateFilename));
            out.write(b64.encode(privKey.getEncoded()));
            out.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static SecureRandom createFixedRandom() {
        return new FixedRand();
    }

    private static class FixedRand extends SecureRandom {
        MessageDigest sha;
        byte[] state;

        FixedRand() {
            try {
                this.sha = MessageDigest.getInstance("SHA-256");
                this.state = sha.digest();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("can't find SHA-256!");
            }
        }

        public void nextBytes(byte[] bytes) {
            int    off = 0;

            sha.update(state);

            while (off < bytes.length) {
                state = sha.digest();

                if (bytes.length - off > state.length) {
                    System.arraycopy(state, 0, bytes, off, state.length);
                } else {
                    System.arraycopy(state, 0, bytes, off, bytes.length - off);
                }

                off += state.length;

                sha.update(state);
            }
        }
    }
}
