package main.java.org.example;

import com.sun.net.httpserver.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;

public class Main {
    public static void main (String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress("localhost", 3000),1);
        httpServer.createContext("/", new MjpgHandler());
        httpServer.setExecutor(null); // creates a default executor
        httpServer.start();
    }

    static class MjpgHandler implements HttpHandler{
        private ImageSource img;
        private OutputStream out;

        @Override
        public void handle(HttpExchange exchange) {
            new Thread(()->{
                img = new ImageSource();

                try {
                    Headers h = exchange.getResponseHeaders();
                    h.set("Cache-Control", "no-cache, private");
                    h.set("Content-Type", "multipart/x-mixed-replace;boundary=--boundary");
                    exchange.sendResponseHeaders(200, 0);
                    out = exchange.getResponseBody();
                    int i = 0;
                    ArrayList<byte[]> images = img.getImage();
                    while(true) {
                        if(i < images.size()) {
                            out.write((
                                    "--boundary\r\n" +
                                    "Content-type: image/jpeg\r\n" +
                                    "Content-Length: " + images.get(i).length + "\r\n\r\n").getBytes());
                            out.write(images.get(i));
                            out.write("\r\n".getBytes());
                            out.flush(); //
                            i++;
                        } else {
                            i = 0;
                        }

                        Thread.sleep(100);
                    }
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }
}
