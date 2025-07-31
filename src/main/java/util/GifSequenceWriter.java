package main.java.util;

import java.awt.image.*;
import java.io.*;
import java.util.Iterator;
import javax.imageio.*;
import javax.imageio.metadata.*;
import javax.imageio.stream.*;

public class GifSequenceWriter {
        protected ImageWriter gifWriter;
        protected ImageWriteParam imageWriteParam;
        protected IIOMetadata imageMetaData;

        /**
         * Creates a new GifSequenceWriter
         *
         * @param outputStream        the ImageOutputStream to be written to
         * @param imageType           one of the imageTypes specified in BufferedImage
         * @param timeBetweenFramesMS the time between frames in miliseconds
         * @param loopContinuously    wether the gif should loop repeatedly
         * @throws IIOException if no gif ImageWriters are found
         */
        public GifSequenceWriter(
                        ImageOutputStream outputStream,
                        int imageType,
                        int timeBetweenFramesMS,
                        boolean loopContinuously) throws IIOException, IOException {

                ImageTypeSpecifier imageTypeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(imageType);

                gifWriter = getWriter();
                imageWriteParam = gifWriter.getDefaultWriteParam();

                imageMetaData = gifWriter.getDefaultImageMetadata(imageTypeSpecifier,
                                imageWriteParam);

                String metaFormatName = imageMetaData.getNativeMetadataFormatName();

                IIOMetadataNode root = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);

                IIOMetadataNode graphicsControlExtensionNode = getNode(
                                root,
                                "GraphicControlExtension");

                graphicsControlExtensionNode.setAttribute("disposalMethod", "restoreToBackgroundColor");
                graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
                graphicsControlExtensionNode.setAttribute(
                                "transparentColorFlag",
                                "TRUE");
                graphicsControlExtensionNode.setAttribute(
                                "delayTime",
                                Integer.toString(timeBetweenFramesMS / 10));
                graphicsControlExtensionNode.setAttribute(
                                "transparentColorIndex",
                                "0");

                IIOMetadataNode commentsNode = getNode(root, "CommentExtensions");
                commentsNode.setAttribute("CommentExtension", "Created by MAH");

                IIOMetadataNode appEntensionsNode = getNode(
                                root,
                                "ApplicationExtensions");

                IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");

                child.setAttribute("applicationID", "NETSCAPE");
                child.setAttribute("authenticationCode", "2.0");

                int loop = loopContinuously ? 0 : 1;

                child.setUserObject(new byte[] { 0x1, (byte) (loop & 0xFF), (byte) ((loop >> 8) & 0xFF) });
                appEntensionsNode.appendChild(child);

                imageMetaData.setFromTree(metaFormatName, root);

                gifWriter.setOutput(outputStream);

                gifWriter.prepareWriteSequence(null);
        }

        /**
         * Adds the frame to the GifSequenceWriter
         * 
         * @param img the RenderedImage to add
         * @param delayTime the time before the next frame in miliseconds 
         * @throws IOException if an error occurs during writing
         */
        public void writeToSequence(RenderedImage img, int delayTime) throws IOException {
                String metaFormatName = imageMetaData.getNativeMetadataFormatName();
                IIOMetadataNode root = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);
                IIOMetadataNode gce = getNode(root, "GraphicControlExtension");
                gce.setAttribute("delayTime", Integer.toString(delayTime));
                imageMetaData.setFromTree(metaFormatName, root);

                gifWriter.writeToSequence(
                                new IIOImage(
                                                img,
                                                null,
                                                imageMetaData),
                                imageWriteParam);
        }

        /**
         * Close this GifSequenceWriter object. This does not close the underlying
         * stream, just finishes off the GIF.
         *
         * @throws IOException if there is a problem writing the last bytes.
         */
        public void close() throws IOException {
                gifWriter.endWriteSequence();
        }

        /**
         * Returns the first available GIF ImageWriter using
         * ImageIO.getImageWritersBySuffix("gif").
         *
         * @return a GIF ImageWriter object
         * @throws IIOException if no GIF image writers are returned
         */
        private static ImageWriter getWriter() throws IIOException {
                Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix("gif");
                if (!iter.hasNext()) {
                        throw new IIOException("No GIF Image Writers Exist");
                } else {
                        return iter.next();
                }
        }

        /**
         * Returns an existing child node, or creates and returns a new child node (if
         * the requested node does not exist).
         *
         * @param rootNode the <tt>IIOMetadataNode</tt> to search for the child node.
         * @param nodeName the name of the child node.
         *
         * @return the child node, if found or a new node created with the given name.
         */
        private static IIOMetadataNode getNode(
                        IIOMetadataNode rootNode,
                        String nodeName) {
                int nNodes = rootNode.getLength();
                for (int i = 0; i < nNodes; i++) {
                        if (rootNode.item(i).getNodeName().compareToIgnoreCase(nodeName) == 0) {
                                return ((IIOMetadataNode) rootNode.item(i));
                        }
                }
                IIOMetadataNode node = new IIOMetadataNode(nodeName);
                rootNode.appendChild(node);
                return (node);
        }
}