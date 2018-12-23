package luecx.data.image;


import luecx.ai.neuralnetwork.tools.ArrayTools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Loader {


    public static BufferedImage loadImage(String file) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }

    public static double[][][] loadImage_3d(String file) {
        BufferedImage img = loadImage(file);

        if(img == null) return null;
        ArrayTools.printDimensions(convertToArray(img));
        return convertToArray(img);
    }

    public static double[][][] loadImage_3d_bw(String file) {
        BufferedImage img = loadImage(file);
        if(img == null) return null;
        return new double[][][]{convertToArray_bw(img)};
    }

    public static double[][] loadImage_2d_bw(String file) {
        BufferedImage img = loadImage(file);
        if(img == null) return null;
        return convertToArray_bw(img);
    }

    public static BufferedImage convertToBufferedImage(double[][][] data) {
        ArrayTools.rescale_values(data);

        BufferedImage image = new BufferedImage(data[0].length, data[0][0].length, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < data[0].length; x++) {
            for (int y = 0; y < data[0][0].length; y++) {

                float val = (float)data[0][x][y];

                Color c = new Color(
                        val,
                        data.length >= 2 ? (float)data[1][x][y]:val,
                        data.length >= 3 ? (float)data[2][x][y]:val
                );
                image.setRGB(x, y, c.getRGB());
            }
        }

        return image;
    }


    public static double[][][] convertToArray(BufferedImage image) {
        double[][][] ar = new double[3][image.getWidth()][image.getHeight()];
        for (int x = 0; x < ar[0].length; x++) {
            for (int y = 0; y < ar[0][0].length; y++) {
                Color c = new Color(image.getRGB(x, y));
                ar[0][x][y] = c.getRed() / 255d;
                ar[1][x][y] = c.getGreen() / 255d;
                ar[2][x][y] = c.getBlue() / 255d;
            }
        }
        return ar;
    }

    public static double[][] convertToArray_bw(BufferedImage image) {
        double[][] ar = new double[image.getWidth()][image.getHeight()];
        for (int x = 0; x < ar.length; x++) {
            for (int y = 0; y < ar[0].length; y++) {
                Color c = new Color(image.getRGB(x, y));
                ar[x][y] = (c.getRed() + c.getGreen() + c.getBlue()) / (3 * 255d);

            }
        }
        return ar;
    }

    public static void writeImage(String name, BufferedImage image) {
        String path = "res/" + name + ".png ";
        File ImageFile = new File(path);
        ImageFile.mkdirs();
        try {
            ImageIO.write(image, "png", ImageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeImage(String name, double[][][] data) {
        writeImage(name, convertToBufferedImage(data));
    }

    public static void writeImage(String name, double[][] data) {
        writeImage(name, convertToBufferedImage(new double[][][]{data}));
    }

    public static void main(String[] args){
        Loader.writeImage("test", ArrayTools.createRandomArray(1,10,10,-2,1));
    }

}
