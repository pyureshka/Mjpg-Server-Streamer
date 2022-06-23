package main.java.org.example;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class WebSocketWorker extends Thread{
    private Socket socket;
    private BufferedReader bufReader;
    private ImageSource img;
    private OutputStream out;
    private boolean run = true;
    public WebSocketWorker(Socket socket) {this.socket = socket;}

    @Override
    public void run() {
        try {
            String header = readRequest();
            sendHttpResponse(header);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String readRequest() throws IOException {
        bufReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String header = bufReader.readLine();
        System.out.println(header);
        return header;
    }

    public void sendHttpResponse (String header) throws IOException, InterruptedException {
        String path = header.split(" ")[1];
        out = socket.getOutputStream();
        if (new File("." + path).exists()) {
            img = new ImageSource();

            // http response
            out.write((header.split(" ") [2] + "200 OK"+"\r\n").getBytes());
            out.write(("Content-Type: multipart/x-mixed-replace;boundary=--boundary"+"\r\n\r\n").getBytes());
            out.flush();

            ArrayList<byte[]> images = img.getImage(path.replaceFirst("/", ""));
            int i = 0;
            while (run) {
                if (i < images.size()) {
                    out.write((
                            "--boundary\r\n" +
                                    "Content-Type: image/jpeg\r\n" +
                                    "Content-Length: " + images.get(i).length + "\r\n\r\n").getBytes());
                    out.write(images.get(i));
                    out.write("\r\n".getBytes());
                    out.flush();
                    i++;
                } else {
                    i = 0;
                }

                Thread.sleep(100);
            }
            out.close();
        } else {
            String errorMsg = "File not found";
            out.write((header.split(" ")[2] + "404"+"\r\n").getBytes());
            out.write(("Content-Length: " + errorMsg.length()+"\r\n\r\n").getBytes());
            out.write(errorMsg.getBytes());
            out.flush();
            out.close();
        }
    }
}
