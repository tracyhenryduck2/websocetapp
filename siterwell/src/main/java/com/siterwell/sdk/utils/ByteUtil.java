package com.siterwell.sdk.utils;


import com.litesuits.android.log.Log;

import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * In Java, it don't support unsigned int, so we use char to replace uint8.
 * The range of byte is [-128,127], and the range of char is [0,65535].
 * So the byte could used to store the uint8.
 * (We assume that the String could be mapped to assic)
 * @author afunx
 *
 */
public class ByteUtil {
	/* CRC 高位字节值表 */
	private static char[] auchCRCHi = {
		0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0,
		0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
		0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0,
		0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40,
		0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1,
		0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41,
		0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1,
		0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
		0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0,
		0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40,
		0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1,
		0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40,
		0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0,
		0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40,
		0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0,
		0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40,
		0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0,
		0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
		0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0,
		0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
		0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0,
		0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40,
		0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1,
		0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
		0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0,
		0x80, 0x41, 0x00, 0xC1, 0x81, 0x40
	} ;
/* CRC低位字节值表*/
	private static char[] auchCRCLo = {
		0x00, 0xC0, 0xC1, 0x01, 0xC3, 0x03, 0x02, 0xC2, 0xC6, 0x06,
		0x07, 0xC7, 0x05, 0xC5, 0xC4, 0x04, 0xCC, 0x0C, 0x0D, 0xCD,
		0x0F, 0xCF, 0xCE, 0x0E, 0x0A, 0xCA, 0xCB, 0x0B, 0xC9, 0x09,
		0x08, 0xC8, 0xD8, 0x18, 0x19, 0xD9, 0x1B, 0xDB, 0xDA, 0x1A,
		0x1E, 0xDE, 0xDF, 0x1F, 0xDD, 0x1D, 0x1C, 0xDC, 0x14, 0xD4,
		0xD5, 0x15, 0xD7, 0x17, 0x16, 0xD6, 0xD2, 0x12, 0x13, 0xD3,
		0x11, 0xD1, 0xD0, 0x10, 0xF0, 0x30, 0x31, 0xF1, 0x33, 0xF3,
		0xF2, 0x32, 0x36, 0xF6, 0xF7, 0x37, 0xF5, 0x35, 0x34, 0xF4,
		0x3C, 0xFC, 0xFD, 0x3D, 0xFF, 0x3F, 0x3E, 0xFE, 0xFA, 0x3A,
		0x3B, 0xFB, 0x39, 0xF9, 0xF8, 0x38, 0x28, 0xE8, 0xE9, 0x29,
		0xEB, 0x2B, 0x2A, 0xEA, 0xEE, 0x2E, 0x2F, 0xEF, 0x2D, 0xED,
		0xEC, 0x2C, 0xE4, 0x24, 0x25, 0xE5, 0x27, 0xE7, 0xE6, 0x26,
		0x22, 0xE2, 0xE3, 0x23, 0xE1, 0x21, 0x20, 0xE0, 0xA0, 0x60,
		0x61, 0xA1, 0x63, 0xA3, 0xA2, 0x62, 0x66, 0xA6, 0xA7, 0x67,
		0xA5, 0x65, 0x64, 0xA4, 0x6C, 0xAC, 0xAD, 0x6D, 0xAF, 0x6F,
		0x6E, 0xAE, 0xAA, 0x6A, 0x6B, 0xAB, 0x69, 0xA9, 0xA8, 0x68,
		0x78, 0xB8, 0xB9, 0x79, 0xBB, 0x7B, 0x7A, 0xBA, 0xBE, 0x7E,
		0x7F, 0xBF, 0x7D, 0xBD, 0xBC, 0x7C, 0xB4, 0x74, 0x75, 0xB5,
		0x77, 0xB7, 0xB6, 0x76, 0x72, 0xB2, 0xB3, 0x73, 0xB1, 0x71,
		0x70, 0xB0, 0x50, 0x90, 0x91, 0x51, 0x93, 0x53, 0x52, 0x92,
		0x96, 0x56, 0x57, 0x97, 0x55, 0x95, 0x94, 0x54, 0x9C, 0x5C,
		0x5D, 0x9D, 0x5F, 0x9F, 0x9E, 0x5E, 0x5A, 0x9A, 0x9B, 0x5B,
		0x99, 0x59, 0x58, 0x98, 0x88, 0x48, 0x49, 0x89, 0x4B, 0x8B,
		0x8A, 0x4A, 0x4E, 0x8E, 0x8F, 0x4F, 0x8D, 0x4D, 0x4C, 0x8C,
		0x44, 0x84, 0x85, 0x45, 0x87, 0x47, 0x46, 0x86, 0x82, 0x42,
		0x43, 0x83, 0x41, 0x81, 0x80, 0x40
	} ;

	/**
	 * 用于情景的CRC校验
	 * crc code generator  3
	 * @param msg
	 * @return
     */
	public  static String CRCmakerCharAndCode(String msg){

		try {
			int totalLength = Integer.parseInt(msg.substring(0,4),16);
			char[] complain = new char[totalLength];
//		complain[0] = (char) Integer.parseInt(msg.substring(0,2),16);

			complain[0] = (char) Integer.parseInt(msg.substring(0,2),16);
			complain[1] = (char) Integer.parseInt(msg.substring(2,4),16);
			complain[2] = (char) Integer.parseInt(msg.substring(4,6),16);
//		complain[3] = (char) Integer.parseInt(msg.substring(6,8),16);
//		complain[4] = (char) Integer.parseInt(msg.substring(8,10),16);
//		complain[5] = msg.charAt(10);
//		complain[6] = msg.charAt(11);
//		complain[7] = msg.charAt(12);
// 		complain[5] = 4;
//		complain[6] = 5;
//		complain[7] = 6;


			String name = msg.substring(6,38);
			for(int i = 0; i< name.length();i++){

//			complain[2+i+1] = (char) Integer.parseInt(Integer.toHexString((int)name.charAt(i)));
				complain[2+i+1] = name.charAt(i);
			}
			String status = msg.substring(32+6,msg.length());
			int statusLength = status.length()/2;
			for (int i = 0 ; i <statusLength ; i++){
//			complain[35+i] = (char)Integer.parseInt(status.substring(0,2),16);
				String num =  status.substring(0,2);
				complain[35+i] = (char) Integer.parseInt(num,16);
				status = status.substring(2);
			}

			char CRCHi = 0;
			char CRCLo = 0;
//		int msgLength = msg.length()/2;
//		char[] content = new char[msgLength];
//		for(int i = 0; i< code.size() ; i ++){
//			content[i] = (char) Integer.parseInt(msg.substring(0,2),16);
//			msg = msg.substring(2);
//		}
//		String[] charMsg = msg.split("");
//		Log.i("ceshi1", "msg length"+msg.length()+" +++"+charMsg.length);
			int index = 0,CRCIndex;
			char uchCRCHi = 0xff;
			char uchCRCLo = 0xff;

			while (index<totalLength){
//			byte[] uniCode = charMsg[index].getBytes();
				char charCode =  complain[index];

				CRCIndex = uchCRCHi ^charCode;
				uchCRCHi = (char) (uchCRCLo ^auchCRCHi[CRCIndex]);
				uchCRCLo = auchCRCLo[CRCIndex];

				index++;
			}

			CRCLo = uchCRCHi;
			int crcLo = (int)CRCLo;
			String crcLo1 = Integer.toHexString(crcLo);
			if(crcLo1.length()<2){
				crcLo1 = "0" + crcLo1;
			}
			CRCHi = uchCRCLo;
			int crcHi = (int)CRCHi;
			String crcHi1 = Integer.toHexString(crcHi);
			if (crcHi1.length()<2){
				crcHi1 = "0" + crcHi1;
			}

			return "" + crcHi1 + crcLo1;
		}catch (Exception e){
			return "0000";
		}

	}
	/**
	 * 用于非名称的16进制字符串的的CRC校验
	 * @param msg
	 * @return
     */
	public  static String CRCmakerChar(String msg){
		char CRCHi = 0;
		char CRCLo = 0;
		int msgLength = msg.length()/2;
		char[] content = new char[msgLength];
		for(int i = 0; i< msgLength ; i ++){
			content[i] = (char) Integer.parseInt(msg.substring(0,2),16);
			msg = msg.substring(2);
		}
//		String[] charMsg = msg.split("");
//		Log.i("ceshi1", "msg length"+msg.length()+" +++"+charMsg.length);
		int index = 0,CRCIndex;
		char uchCRCHi = 0xff;
		char uchCRCLo = 0xff;

		while (index<msgLength){
//			byte[] uniCode = charMsg[index].getBytes();
			char charCode =  content[index];

			CRCIndex = uchCRCHi ^charCode;
			uchCRCHi = (char) (uchCRCLo ^auchCRCHi[CRCIndex]);
			uchCRCLo = auchCRCLo[CRCIndex];

			index++;
		}

		CRCLo = uchCRCHi;
		int crcLo = (int)CRCLo;
		String crcLo1 = Integer.toHexString(crcLo);
		if(crcLo1.length()<2){
			crcLo1 = "0" + crcLo1;
		}
		CRCHi = uchCRCLo;
		int crcHi = (int)CRCHi;
		String crcHi1 = Integer.toHexString(crcHi);
		if (crcHi1.length()<2){
			crcHi1 = "0" + crcHi1;
		}

		return "" + crcHi1 + crcLo1;
	}
	/**
	 * crc code generator  1
	 * 用于名称的CRC校验
	 * @param msg
	 * @return
     */
	public  static String CRCmaker(String msg){
		char CRCHi = 0;
		char CRCLo = 0;
		int msgLength = msg.length();
//		String[] charMsg = msg.split("");
//		Log.i("ceshi1", "msg length"+msg.length()+" +++"+charMsg.length);
		int index = 0,CRCIndex;
		char uchCRCHi = 0xff;
		char uchCRCLo = 0xff;

		while (index<msgLength){
//			byte[] uniCode = charMsg[index].getBytes();
			char charCode =  msg.charAt(index);

			CRCIndex = uchCRCHi ^charCode;
			uchCRCHi = (char) (uchCRCLo ^auchCRCHi[CRCIndex]);
			uchCRCLo = auchCRCLo[CRCIndex];

			index++;
		}

		CRCLo = uchCRCHi;
		int crcLo = (int)CRCLo;
		String crcLo1 = Integer.toHexString(crcLo);
		if(crcLo1.length()<2){
			crcLo1 = "0" + crcLo1;
		}
		CRCHi = uchCRCLo;
		int crcHi = (int)CRCHi;
		String crcHi1 = Integer.toHexString(crcHi);
		if (crcHi1.length()<2){
			crcHi1 = "0" + crcHi1;
		}

		return "" + crcHi1 + crcLo1;
	}

	public static final String ESPTOUCH_ENCODING_CHARSET = "ISO-8859-1";
	
	/**
	 * Put String to byte[]
	 * 
	 * @param destbytes
	 *            the byte[] of dest
	 * @param srcString
	 *            the String of src
	 * @param destOffset
	 *            the offset of byte[]
	 * @param srcOffset
	 *            the offset of String
	 * @param count
	 *            the count of dest, and the count of src as well
	 */
	public static void putString2bytes(byte[] destbytes, String srcString,
			int destOffset, int srcOffset, int count) {
		for (int i = 0; i < count; i++) {
			destbytes[count + i] = srcString.getBytes()[i];
		}
	}

	/**
	 * Convert uint8 into char( we treat char as uint8)
	 * 
	 * @param uint8
	 *            the unit8 to be converted
	 * @return the byte of the unint8
	 */
	public static byte convertUint8toByte(char uint8) {
		if (uint8 > Byte.MAX_VALUE - Byte.MIN_VALUE) {
			throw new RuntimeException("Out of Boundary");
		}
		return (byte) uint8;
	}

	/**
	 * Convert char into uint8( we treat char as uint8 )
	 * 
	 * @param b
	 *            the byte to be converted
	 * @return the char(uint8)
	 */
	public static char convertByte2Uint8(byte b) {
		// char will be promoted to int for char don't support & operator
		// & 0xff could make negatvie value to positive
		return (char) (b & 0xff);
	}

	/**
	 * Convert byte[] into char[]( we treat char[] as uint8[])
	 * 
	 * @param bytes
	 *            the byte[] to be converted
	 * @return the char[](uint8[])
	 */
	public static char[] convertBytes2Uint8s(byte[] bytes) {
		int len = bytes.length;
		char[] uint8s = new char[len];
		for (int i = 0; i < len; i++) {
			uint8s[i] = convertByte2Uint8(bytes[i]);
		}
		return uint8s;
	}

	/**
	 * Put byte[] into char[]( we treat char[] as uint8[])
	 *
	 * @param destOffset
	 *            the offset of char[](uint8[])
	 * @param srcOffset
	 *            the offset of byte[]
	 * @param count
	 *            the count of dest, and the count of src as well
	 */
	public static void putbytes2Uint8s(char[] destUint8s, byte[] srcBytes,
			int destOffset, int srcOffset, int count) {
		for (int i = 0; i < count; i++) {
			destUint8s[destOffset + i] = convertByte2Uint8(srcBytes[srcOffset
					+ i]);
		}
	}

	/**
	 * Convert byte to Hex String
	 * 
	 * @param b
	 *            the byte to be converted
	 * @return the Hex String
	 */
	public static String convertByte2HexString(byte b) {
		char u8 = convertByte2Uint8(b);
		String buf = Integer.toHexString(u8);
		if (buf.length() == 1) {
			buf = '0' + buf;
		}
		return buf;
	}

	/**
	 * Convert char(uint8) to Hex String
	 * 
	 * @param u8
	 *            the char(uint8) to be converted
	 * @return the Hex String
	 */
	public static String convertU8ToHexString(char u8) {
		return Integer.toHexString(u8);
	}

	/**
	 * Split uint8 to 2 bytes of high byte and low byte. e.g. 20 = 0x14 should
	 * be split to [0x01,0x04] 0x01 is high byte and 0x04 is low byte
	 * 
	 * @param uint8
	 *            the char(uint8)
	 * @return the high and low bytes be split, byte[0] is high and byte[1] is
	 *         low
	 */
	public static byte[] splitUint8To2bytes(char uint8) {
		if (uint8 < 0 || uint8 > 0xff) {
			throw new RuntimeException("Out of Boundary");
		}
		String hexString = Integer.toHexString(uint8);
		byte low;
		byte high;
		if (hexString.length() > 1) {
			high = (byte) Integer.parseInt(hexString.substring(0, 1), 16);
			low = (byte) Integer.parseInt(hexString.substring(1, 2), 16);
		} else {
			high = 0;
			low = (byte) Integer.parseInt(hexString.substring(0, 1), 16);
		}
		byte[] result = new byte[] { high, low };
		return result;
	}

	/**
	 * Combine 2 bytes (high byte and low byte) to one whole byte
	 * 
	 * @param high
	 *            the high byte
	 * @param low
	 *            the low byte
	 * @return the whole byte
	 */
	public static byte combine2bytesToOne(byte high, byte low) {
		if (high < 0 || high > 0xf || low < 0 || low > 0xf) {
			throw new RuntimeException("Out of Boundary");
		}
		return (byte) (high << 4 | low);
	}

	/**
	 * Combine 2 bytes (high byte and low byte) to
	 * 
	 * @param high
	 *            the high byte
	 * @param low
	 *            the low byte
	 * @return the char(u8)
	 */
	public static char combine2bytesToU16(byte high, byte low) {
		char highU8 = convertByte2Uint8(high);
		char lowU8 = convertByte2Uint8(low);
		return (char) (highU8 << 8 | lowU8);
	}

	/**
	 * Generate the random byte to be sent
	 * 
	 * @return the random byte
	 */
	private static byte randomByte() {
		return (byte) (127 - new Random().nextInt(256));
	}

	/**
	 * Generate the random byte to be sent
	 * 
	 * @param len
	 *            the len presented by u8
	 * @return the byte[] to be sent
	 */
	public static byte[] randomBytes(char len) {
		byte[] data = new byte[len];
		for (int i = 0; i < len; i++) {
			data[i] = randomByte();
		}
		return data;
	}

	public static byte[] genSpecBytes(char len) {
		byte[] data = new byte[len];
		for (int i = 0; i < len; i++) {
			data[i] = '1';
		}
		return data;
	}
	
	/**
	 * Generate the random byte to be sent
	 * 
	 * @param len
	 *            the len presented by byte
	 * @return the byte[] to be sent
	 */
	public static byte[] randomBytes(byte len) {
		char u8 = convertByte2Uint8(len);
		return randomBytes(u8);
	}
	
	/**
	 * Generate the specific byte to be sent
	 * @param len
	 *            the len presented by byte
	 * @return the byte[] 
	 */
	public static byte[] genSpecBytes(byte len) {
		char u8 = convertByte2Uint8(len);
		return genSpecBytes(u8);
	}
	
	public static String parseBssid(byte[] bssidBytes, int offset, int count) {
		byte[] bytes = new byte[count];
		for (int i = 0; i < count; i++) {
			bytes[i] = bssidBytes[i + offset];
		}
		return parseBssid(bytes);
	}
	
    /**
     * parse "24,-2,52,-102,-93,-60" to "18,fe,34,9a,a3,c4"
     * parse the bssid from hex to String
     * @param bssidBytes the hex bytes bssid, e.g. {24,-2,52,-102,-93,-60}
     * @return the String of bssid, e.g. 18fe349aa3c4
     */
    public static String parseBssid(byte[] bssidBytes)
    {
        StringBuilder sb = new StringBuilder();
        int k;
        String hexK;
        String str;
        for (int i = 0; i < bssidBytes.length; i++)
        {
            k = 0xff & bssidBytes[i];
            hexK = Integer.toHexString(k);
            str = ((k < 16) ? ("0" + hexK) : (hexK));
            System.out.println(str);
            sb.append(str);
        }
        return sb.toString();
    }
    
    /**
     * @param string the string to be used
     * @return the byte[] of String according to {@link #ESPTOUCH_ENCODING_CHARSET}
     */
	public static byte[] getBytesByString(String string) {
		try {
			return string.getBytes(ESPTOUCH_ENCODING_CHARSET);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("the charset is invalid");
		}
	}

	private static void test_splitUint8To2bytes() {
		// 20 = 0x14
		byte[] result = splitUint8To2bytes((char) 20);
		if (result[0] == 1 && result[1] == 4) {
			System.out.println("test_splitUint8To2bytes(): pass");
		} else {
			System.out.println("test_splitUint8To2bytes(): fail");
		}
	}

	private static void test_combine2bytesToOne() {
		byte high = 0x01;
		byte low = 0x04;
		if (combine2bytesToOne(high, low) == 20) {
			System.out.println("test_combine2bytesToOne(): pass");
		} else {
			System.out.println("test_combine2bytesToOne(): fail");
		}
	}

	private static void test_convertChar2Uint8() {
		byte b1 = 'a';
		// -128: 1000 0000 should be 128 in unsigned char
		// -1: 1111 1111 should be 255 in unsigned char
		byte b2 = (byte) -128;
		byte b3 = (byte) -1;
		if (convertByte2Uint8(b1) == 97 && convertByte2Uint8(b2) == 128
				&& convertByte2Uint8(b3) == 255) {
			System.out.println("test_convertChar2Uint8(): pass");
		} else {
			System.out.println("test_convertChar2Uint8(): fail");
		}
	}

	private static void test_convertUint8toByte() {
		char c1 = 'a';
		// 128: 1000 0000 should be -128 in byte
		// 255: 1111 1111 should be -1 in byte
		char c2 = 128;
		char c3 = 255;
		if (convertUint8toByte(c1) == 97 && convertUint8toByte(c2) == -128
				&& convertUint8toByte(c3) == -1) {
			System.out.println("test_convertUint8toByte(): pass");
		} else {
			System.out.println("test_convertUint8toByte(): fail");
		}
	}
	
    private static void test_parseBssid() {
        byte b[] = {15, -2, 52, -102, -93, -60};
        if("0ffe349aa3c4".equals(parseBssid(b)))
        {
            System.out.println("test_parseBssid(): pass");
        }
        else
        {
            System.out.println("test_parseBssid(): fail");
        }
    }


	private static byte uniteBytes(String src0, String src1) {
		byte b0 = Byte.decode("0x" + src0).byteValue();
		b0 = (byte) (b0 << 4);
		byte b1 = Byte.decode("0x" + src1).byteValue();
		byte ret = (byte) (b0 | b1);
		return ret;
	}

	/**
	 * bytes转换成十六进制字符串
	 */
	public static byte[] hexStr2Bytes(String src) {
		int m = 0, n = 0;
		int l = src.length() / 2;
		System.out.println(l);
		byte[] ret = new byte[l];
		try {
			for (int i = 0; i < l; i++) {
				m = i * 2 + 1;
				n = m + 1;
				ret[i] = uniteBytes(src.substring(i * 2, m), src.substring(m, n));
			}
		}catch (Exception e){
               Log.i("ceshi","格式 err");
		}

		return ret;
	}


	public static void main(String args[]) {
//		test_convertUint8toByte();
//		test_convertChar2Uint8();
//		test_splitUint8To2bytes();
//		test_combine2bytesToOne();
//		test_parseBssid();
		String abc = "01020101010100033B0521010001010100010100033BFE4318";
		System.out.print(""+ByteUtil.CRCmakerChar(abc));
//		int[] arrayData = {1,2,4,5,6,7,5,6,7,3,8,9,10,12,11,20,30,40};
//		Arrays.sort(arrayData);
//		for (int a : arrayData){
//			System.out.print("" + a + ";");
//		}


	}

}
