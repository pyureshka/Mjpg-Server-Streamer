package main.java.org.example;

import java.io.IOException;

public class Main {
    public static void main (String[] args) throws IOException {
        TcpConnecting tcp = new TcpConnecting();
        tcp.start();
    }
}
