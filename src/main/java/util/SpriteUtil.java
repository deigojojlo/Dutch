package main.java.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.swing.ImageIcon;

import main.java.game.view.GameView;

/**
 * Utility class for loading sprite images.
 */
public class SpriteUtil {

    public static String pathToSprites = Path.of("src", "main", "resources", "sprites").toString();

    /**
     * Loads an image (can be an animated .gif) from the resources directory and
     * returns it as an
     * ImageIcon.
     *
     * @param filePath the path to the file as a succesion of strings (optional
     *                 sub-folders followed by the name of the file)
     * @return an ImageIcon containing the image
     */
    public static ImageIcon loadIcon(String... filePath) {
        File file = Path.of(pathToSprites, filePath).toFile();
        if (file.exists()) {
            return new ImageIcon(file.getPath());
        } else {
            System.out.println(
                    "The file named '" + file.getAbsolutePath() + "' did not load properly or does not exist!");
            return null;
        }
    }

    /**
     * Loads an image from the resources directory and returns it as a
     * BufferedImage.
     *
     * @param filePath the path to the file as a succesion of strings (optional
     *                 sub-folders followed by the name of the file)
     * @return a BufferedImage containing the PNG image, or null if the image could
     *         not be loaded
     */
    public static BufferedImage loadBufferedImage(String... filePath) {
        File file = Path.of(pathToSprites, filePath).toFile();
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            System.out.println(
                    "The file named '" + file.getAbsolutePath() + "' did not load properly or does not exist!");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 
     * @param icon the ImageIcon to rotate
     * @param angle the angle at which the ImageIcon should be rotated
     * @return the rotated ImageIcon
     */
    public static ImageIcon rotateFrame(ImageIcon icon, int angle) {
        Image image = icon.getImage();
        BufferedImage rotated = rotateFrame(image, angle);
        return new ImageIcon(rotated);
    }

    /**
     * 
     * @param img the Image to rotate
     * @param angle the angle at which the Image should be rotated
     * @return the rotated BufferedImage
     */
    private static BufferedImage rotateFrame(Image img, int angle) {
        int w = img.getWidth(null);
        int h = img.getHeight(null);
        int type = BufferedImage.TYPE_INT_ARGB;

        BufferedImage rotated;
        Graphics2D g;

        switch (angle) {
            case 90:
                rotated = new BufferedImage(h, w, type);
                g = rotated.createGraphics();
                g.translate(h, 0);
                g.rotate(Math.toRadians(90));
                break;
            case 180:
                rotated = new BufferedImage(w, h, type);
                g = rotated.createGraphics();
                g.translate(w, h);
                g.rotate(Math.toRadians(180));
                break;
            case 270:
                rotated = new BufferedImage(h, w, type);
                g = rotated.createGraphics();
                g.translate(0, w);
                g.rotate(Math.toRadians(270));
                break;
            default:
                rotated = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                g = rotated.createGraphics();
                break;
        }

        g.drawImage(img, 0, 0, null);
        g.dispose();
        return rotated;
    }

    /**
     * 
     * @param newWidth the new wanted width
     * @param newHeight the new wanted height
     * @param rotation the angle at which the ImageIcon should be rotated
     * @param filePath the path to the ImageIcon .GIF file
     * @return the resized and/or rotated .GIF file
     */
    public static ImageIcon resizeGif(int newWidth, int newHeight, int rotation, String... filePath) {
        File file = Path.of(pathToSprites, filePath).toFile();

        if (!file.exists()) {
            System.out.println("The file named '" + file.getAbsolutePath() + "' does not exist!");
            return null;
        }

        ImageReader reader = null;
        try (InputStream gifInputStream = new FileInputStream(file);
                ImageInputStream imageStream = ImageIO.createImageInputStream(gifInputStream)) {

            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("gif");
            if (!readers.hasNext()) {
                System.out.println("No GIF reader available.");
                return null;
            }

            reader = readers.next();
            reader.setInput(imageStream, false);
            GifToArray.ImageFrame[] frames = GifToArray.readGIF(reader);

            for (GifToArray.ImageFrame frame : frames) {
                BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = resized.createGraphics();

                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                g.drawImage(frame.getImage(), 0, 0, newWidth, newHeight, null);
                g.dispose();

                resized = rotateFrame(resized, rotation);
                frame.setImage(resized);
            }

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageOutputStream output = new MemoryCacheImageOutputStream(baos)) {

                GifSequenceWriter writer = new GifSequenceWriter(
                    output,
                    BufferedImage.TYPE_INT_ARGB,
                    (int) Math.round(frames[0].getDelay() / GameView.getAnimSpeed()),
                    true
                );

                for (GifToArray.ImageFrame frame : frames) {
                    writer.writeToSequence(frame.getImage(),
                        (int) Math.round(frame.getDelay() / GameView.getAnimSpeed())
                    );
                }

                writer.close();
                output.flush();

                return new ImageIcon(baos.toByteArray());

            } catch (IOException e) {
                System.out.println("Failed to write resized GIF.");
                e.printStackTrace();
                return null;
            }

        } catch (IOException e) {
            System.out.println("Failed to read or process the GIF file: " + file.getAbsolutePath());
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                reader.dispose();
            }
        }
    }
}
