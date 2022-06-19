package main.java.org.example;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ImageSource {
    private File fileImage;
    private ByteArrayOutputStream streamByteArray;
    private BufferedImage bufferedImage;

    public ImageSource() {}

    public ArrayList<byte[]> getImage(String gifName) throws IOException, InterruptedException {
        gifToJpg(gifName);
        fileImage = new File("./"+gifName);
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

    public void gifToJpg(String gifName) {
        BufferedImage master = null;
        try {
            String[] imageatt = new String[]{
                    "imageLeftPosition",
                    "imageTopPosition",
                    "imageWidth",
                    "imageHeight"
            };

            ImageReader reader = (ImageReader) ImageIO.getImageReadersByFormatName("gif").next();
            ImageInputStream ciis = ImageIO.createImageInputStream(new File("./"+gifName+"/"+gifName+".gif"));
            reader.setInput(ciis, false);

            int noi = reader.getNumImages(true);
            master = null;

            for (int i = 0; i < noi; i++) {
                BufferedImage image = reader.read(i);
                IIOMetadata metadata = reader.getImageMetadata(i);

                Node tree = metadata.getAsTree("javax_imageio_gif_image_1.0");
                NodeList children = tree.getChildNodes();

                for (int j = 0; j < children.getLength(); j++) {
                    Node nodeItem = children.item(j);

                    if (nodeItem.getNodeName().equals("ImageDescriptor")) {
                        Map<String, Integer> imageAttr = new HashMap<String, Integer>();

                        for (int k = 0; k < imageatt.length; k++) {
                            NamedNodeMap attr = nodeItem.getAttributes();
                            Node attnode = attr.getNamedItem(imageatt[k]);
                            imageAttr.put(imageatt[k], Integer.valueOf(attnode.getNodeValue()));
                        }
                        if (i == 0) {
                            master = new BufferedImage(imageAttr.get("imageWidth"), imageAttr.get("imageHeight"), BufferedImage.TYPE_INT_ARGB);
                        }
                        master.getGraphics().drawImage(image, imageAttr.get("imageLeftPosition"), imageAttr.get("imageTopPosition"), null);
                    }
                }
                ImageIO.write(master, "GIF", new File("./"+gifName+"/"+gifName + i + ".gif"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
       // return master;
    }
}
