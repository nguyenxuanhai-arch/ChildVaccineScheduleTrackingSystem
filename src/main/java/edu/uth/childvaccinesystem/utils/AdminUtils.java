package edu.uth.childvaccinesystem.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Utility class containing common functionality used across admin controllers
 */
public class AdminUtils {

    private static final Logger logger = LoggerFactory.getLogger(AdminUtils.class);

    /**
     * Compresses an image for storage. Scales the image if it exceeds max dimensions
     * and compresses it to JPEG format.
     *
     * @param imageData Original image data as byte array
     * @return Compressed image as byte array
     * @throws IOException If image processing fails
     */
    public static byte[] compressImage(byte[] imageData) throws IOException {
        if (imageData == null || imageData.length == 0) {
            logger.warn("Empty image data provided for compression");
            return new byte[0];
        }

        try {
            BufferedImage sourceImage = ImageIO.read(new ByteArrayInputStream(imageData));
            
            if (sourceImage == null) {
                logger.warn("Could not read image data for compression");
                return imageData; // Return original if cannot be processed
            }
            
            // Scale the image if it's too large
            int maxWidth = 800;
            int maxHeight = 800;
            int width = sourceImage.getWidth();
            int height = sourceImage.getHeight();
            
            logger.debug("Original image dimensions: {}x{}", width, height);
            
            if (width > maxWidth || height > maxHeight) {
                float ratio = (float) width / (float) height;
                if (ratio > 1) {
                    width = maxWidth;
                    height = (int) (width / ratio);
                } else {
                    height = maxHeight;
                    width = (int) (height * ratio);
                }
                
                logger.debug("Resizing image to: {}x{}", width, height);
                
                BufferedImage resizedImage = new BufferedImage(width, height, sourceImage.getType());
                java.awt.Graphics2D g = resizedImage.createGraphics();
                g.drawImage(sourceImage, 0, 0, width, height, null);
                g.dispose();
                sourceImage = resizedImage;
            }
            
            // Compress the image
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(sourceImage, "jpeg", outputStream);
            byte[] compressedData = outputStream.toByteArray();
            
            logger.debug("Image compressed from {} to {} bytes ({}% reduction)", 
                imageData.length, compressedData.length,
                String.format("%.1f", (1 - (float)compressedData.length / imageData.length) * 100));
            
            return compressedData;
        } catch (IOException e) {
            logger.error("Error compressing image: {}", e.getMessage());
            throw e;
        }
    }
} 