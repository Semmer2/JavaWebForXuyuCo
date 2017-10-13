package com.jl.util;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import com.jspsmart.upload.Files;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.JPEGEncodeParam;

public class TifConvern {
	 
    public static int getImageHeight(String filename) throws IOException
    {
    	File _file = new File(filename); //读入文件  
        Image src = javax.imageio.ImageIO.read(_file); //构造Image对象 
        int height=src.getHeight(null); //得到源图长         
        return height;
    }
	public static boolean Tif2JPG4(String sourcepath,String shortfilename,String targetpath) {
		try {
			String filename = sourcepath+"\\"+shortfilename+".tif";
			String jpegFileName = targetpath+"\\"+shortfilename+".jpg";
			FileOutputStream stream = null;			
			File file=new File(filename);
			File targetfile=new File(jpegFileName);
			//if(targetfile.exists())
			//{
			//	targetfile.delete();
			//}
	        if(file.exists() && targetfile.exists()==false)
	        {
	        	stream = new FileOutputStream(jpegFileName);
	        	byte[] b = getBytesFromFile(file);
				InputStream bais = new ByteArrayInputStream(b);
				ImageDecoder decoder = ImageCodec.createImageDecoder("tiff",bais,null);
				RenderedImage ri = decoder.decodeAsRenderedImage();
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				ImageIO.write(ri, "JPEG", outputStream);			
				stream.write(outputStream.toByteArray());
				stream.close();
				//file.delete();
				return true;
	        }
	        else
	        {
	        	if(targetfile.exists())
	        		return true;
	        	else
	        		return false;
	        }
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	
	
	private static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);
		long length = file.length();
		if (length > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("File is too big, can't support.");
		}
		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];
		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}
		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "
					+ file.getName());
		}
		// Close the input stream and return bytes
		is.close();
		return bytes;
	}
	public static void main(String[] args)
	{
		/*final File folder = new File("L:\\jlcar\\hotimage\\");
		for (final File fileEntry : folder.listFiles()) {	       
	       String s=fileEntry.getName();
	       s=StringUtils.replaceString(s,".tif","");
	       TifConvern.Tif2JPG4("L:\\jlcar\\hotimage\\",s,"L:\\jlcar\\jpghotimage\\");
	        
	    }*/
		//TifConvern.Tif2JPG4("L:\\jlcar\\hotimage\\","553","L:\\jlcar\\jpghotimage\\");
		
	}

}