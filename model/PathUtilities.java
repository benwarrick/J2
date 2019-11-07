package model;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;

import org.opencv.core.Mat;

public class PathUtilities {
    /*
     * A static class for doing things to images. 
     * --Ben
     */
    public static class ImageUtils {
    	public static BufferedImage toBufferedImage(Mat m) {
    		int type = BufferedImage.TYPE_BYTE_GRAY;
    		if (m.channels() > 1) {
    			type = BufferedImage.TYPE_3BYTE_BGR;
    		}
    		int bufferSize = m.channels()*m.cols()*m.rows(); 
    		byte [] b = new byte[bufferSize]; 
    		m.get(0, 0, b);
    		BufferedImage image = new BufferedImage(m.cols(), m.rows(), type); 
    		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    		System.arraycopy(b,  0,  targetPixels, 0, b.length);
    		return image;
    	}
    }
    public static boolean createFolder(File folderPath) {
    	boolean result = false;
    	if (!folderPath.exists()) {
			System.out.println("Creating directory: " + folderPath.getAbsolutePath().toString()); 
			
			try {
				folderPath.mkdir(); 
				result = true; 
			}
			catch(SecurityException se) {
				System.out.println("You'll have to manyally create: " + folderPath.getAbsolutePath().toString());
			} 
		}
    	return result;
    }
}