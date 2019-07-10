package com.github.sheld5.CernyRytir_PriceCheck.java.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Resources {

    public static BufferedImage windowIcon;

    public static void load() {
        windowIcon = loadImage("mtgManaSymbols.jpg");
    }

    private static BufferedImage loadImage(String fileName) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(Resources.class.getResourceAsStream("/" + fileName));
        } catch (IOException e) {
            System.out.println("Error while loading " + fileName);
            e.printStackTrace();
        }
        return image;
    }

}
