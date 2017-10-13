package com.jl.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class StringHelper
{

    public StringHelper()
    {
    }

    public static String GBK(String strConst)
    {
        try
        {
            String s = new String(strConst.getBytes("ISO-8859-1"), "GBK");
            return s;
        }
        catch(UnsupportedEncodingException e)
        {
            String s1 = strConst;
            return s1;
        }
    }

    public static String Base64(byte bytes[])
    {
        return (new BASE64Encoder()).encode(bytes).replaceAll("\n", "").replaceAll("\r", "");
        //String tmp=new String(bytes);
        //return StringUtils.escape(tmp);
    }

    public static byte[] decodeBase64(String buff)
        throws IOException
    {
        return (new BASE64Decoder()).decodeBuffer(buff);
        //return StringUtils.unescape(buff).getBytes();        
        
    }
}
