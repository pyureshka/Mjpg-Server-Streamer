package main.java.org.example;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Properties;

public class TcpConnecting extends Thread{
    private int port;
    public String name;
    public InetAddress ipTcpSocket;
    private ServerSocket serverSocket;


    public TcpConnecting() throws IOException {
        Properties prop = new Properties();
        File propFile = new File("config.properties");
        if(propFile.exists()) {
            prop.load(new FileReader("config.properties"));
        }
        port = Integer.parseInt(prop.getProperty("port","3000"));
        ipTcpSocket = InetAddress.getByName(prop.getProperty("host","localhost"));
        name = prop.getProperty("name", System.getProperty("user.name"));

        serverSocket = new ServerSocket(port, 2, ipTcpSocket);
    }

    public void run() {
        while (true) {
            try {
                new WebSocketWorker(serverSocket.accept()).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
