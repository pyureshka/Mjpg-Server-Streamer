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
    private ByteArrayOutputStream streamByteArray;

    public ImageSource() {}

    public ArrayList<byte[]> getImage(String gifName) throws IOException {
        ArrayList<byte[]> gifFrames = new ArrayList<>();
        BufferedImage master;
        try {
            String[] imageatt = new String[]{
                    "imageLeftPosition",
                    "imageTopPosition",
                    "imageWidth",
                    "imageHeight"
            };

            ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
            ImageInputStream ciis = ImageIO.createImageInputStream(new File("./resources"+"/"+gifName+".gif"));
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
                streamByteArray = new ByteArrayOutputStream();
                ImageIO.write(master, "GIF", streamByteArray);
                gifFrames.add(streamByteArray.toByteArray());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        streamByteArray.close();
        return gifFrames;
    }
}
