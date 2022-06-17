package main.java.org.example;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class ImageSource {
    private File fileImage;
    private ByteArrayOutputStream streamByteArray;
    private BufferedImage bufferedImage;

    public ImageSource() {}

    public ArrayList<byte[]> getImage() throws IOException {
        fileImage = new File("./image");
        ArrayList<byte[]> listByteImg = new ArrayList<>();
        for (File img : fileImage.listFiles()){
            bufferedImage = ImageIO.read(img);
            streamByteArray = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", streamByteArray);
            streamByteArray.flush();
            listByteImg.add(streamByteArray.toByteArray());
        }
        streamByteArray.close();
        return listByteImg;
    }
}
