package com.jl.util;

import java.util.*;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.io.InputStream;

public final class Constants {
	public static final short JDKVERSION = 15;
	public static final Random RANDOM = new Random();
	public static final Base64 BASE64 = new Base64();
	public static Properties constantsProperties;
	static {
		constantsProperties = new Properties();
		InputStream is = null;
		try {
			is = Constants.class
					.getResourceAsStream("/config/constants.properties");
			constantsProperties.load(is);
		} catch (Exception exception) {
			System.out.println("Can't read the properties file. ");
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
	}

	public Constants() {

	}

	public synchronized static String getValue(String key) {
		String value = "";
		value = constantsProperties.getProperty(key);
		return value;
	}
}