package com.jl.util;

public class Base64 {
	  public Base64() {
	  }

	  /**
	   *  ��string �Խ���Base64����
	   *
	   * @param encodeStr
	   */
	  
	  public static String Encoder(byte[] encodeStr) {
	    String ret;
	    sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
	    ret = encoder.encodeBuffer(encodeStr);
	    return ret;

	  }

	  /**
	   *  ��string �Խ���Base64����
	   *
	   * @param encodeStr
	   */

	  public static byte[] Decoder(String decodeStr) {
	    byte[] ret=null;
	    sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
	    try {
	      ret = decoder.decodeBuffer(decodeStr);
	    }
	    catch (Exception e) {
	      ret = null;
	    }

	    return ret;
	  }

	  public static void main(String[] args) {
	    
	  }
	}

