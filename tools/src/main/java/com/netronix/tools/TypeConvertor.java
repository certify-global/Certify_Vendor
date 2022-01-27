package com.netronix.tools;

import android.util.Log;

public class TypeConvertor {
	
	public static final String TAG = TypeConvertor.class.getSimpleName();

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}	

	public static String bytesToHexWithSpace(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 3];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 3] = hexArray[v >>> 4];
	        hexChars[j * 3 + 1] = hexArray[v & 0x0F];
	        hexChars[j * 3 + 2] = 0x20;
	    }
	    return new String(hexChars);
	}	
	
	public static byte[] intToLittleEndianByteArray(int a)
	{
	    byte[] ret = new byte[4];
	    ret[3] = (byte) (a & 0xFF);   
	    ret[2] = (byte) ((a >> 8) & 0xFF);   
	    ret[1] = (byte) ((a >> 16) & 0xFF);   
	    ret[0] = (byte) ((a >> 24) & 0xFF);
	    return ret;
	}
	
	
	public static Integer LittleEndianBytesToInt (byte[] src, int offset, int size) {
		Integer ret = null;
		int r=0;
		
		if (src.length<offset+size) {
			Log.i(TAG, "LittleEndianBytesToInt - arry out of bound !");
			return null;
		}
		
		if (2==size) {
			r = ((src[offset+1]&0xff)<<8)|(src[offset]&0xff);
			ret = r;
		}
		else if (4==size) {
			r = ((src[offset+3]&0xff)<<24)|((src[offset+2]&0xff)<<16)|((src[offset+1]&0xff)<<8)|(src[offset]&0xff);
			ret = r;
		}
		else {
			Log.i(TAG, "LittleEndianBytesToInt - not support !");
		}
		return ret;
	}
	
	public static void intToLittleEndianDWORD(int src, byte[] des, int desStartIndex)
	{
		des[desStartIndex+0] = (byte) (src & 0xFF);
		des[desStartIndex+1] = (byte) ((src >> 8) & 0xFF);   
		des[desStartIndex+2] = (byte) ((src >> 16) & 0xFF);
		des[desStartIndex+3] = (byte) ((src >> 24) & 0xFF);
	}
	
	public static void shortToLittleEndianWORD(int src, byte[] des, int desStartIndex)
	{
		des[desStartIndex+0] = (byte) (src & 0xFF);
		des[desStartIndex+1] = (byte) ((src >> 8) & 0xFF);
	}	
	
	public static void intToBigEndianDWORD(int src, byte[] des, int desStartIndex) 
	{
		des[desStartIndex+3] = (byte) (src & 0xFF);
		des[desStartIndex+2] = (byte) ((src >> 8) & 0xFF);   
		des[desStartIndex+1] = (byte) ((src >> 16) & 0xFF);
		des[desStartIndex+0] = (byte) ((src >> 24) & 0xFF);
	}
	
}


/*-------------------------------
Test Convert 2 byte to short
bytes : 01 02 03 04 
SHORT - CurrentPlatform : 0x102
DWORD - CurrentPlatform : 0x1020304
SHORT - BigEndian : 0x102
DWORD - BigEndian : 0x1020304
SHORT - LittleEndian : 0x201
DWORD - LittleEndian : 0x4030201
-------------------------------*/
 