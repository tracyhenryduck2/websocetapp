package com.siterwell.sdk.http;

import java.util.Arrays;

/**
 * Created by TracyHenry on 2018/3/21.
 */

public class AirKissEncoder {

    //32768
    private int mEncodedData[] = new int[2 << 14];
    private int mLength = 0;

    // Random char should be in range [0, 127).
    private static final String TAG = "AirKissEncoder";

    public AirKissEncoder(String ssid, String password, String pinCode) {
        int times = 5;
        while (times-- > 0) {
            leadingPart();
            magicCode(ssid, password, pinCode);

            for (int i = 0; i < 15; ++i) {
                prefixCode(password);
                String data = password+pinCode;
                int index;
                byte content[] = new byte[4];

                for (index = 0; index < data.length() / 4; ++index) {
                    System.arraycopy(data.getBytes(), index * 4, content, 0, content.length);
                    sequence(index, content);
                }

                if (data.length() % 4 != 0) {
                    byte less[] = new byte[data.length() % 4];
                    System.arraycopy(data.getBytes(), index * 4, less, 0, less.length);
                    for (int j = 0; j < less.length; j++) {
                        content[j] = less[j];
                    }
                    int length = data.length() % 4 ;
                    for (int j = 0; j < 4 - length; j++) {
                        byte supple[] = intToByteArray(0);
                        int seat = length+j;
                        //content[seat] = supple[3];
                        content[seat] = 0;
                    }
                    sequence(index, content);
                }
            }
        }
    }

    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        //由高位到低位
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);
        return result;
    }

    public int[] getEncodedData() {
        return Arrays.copyOf(mEncodedData, mLength);
    }

    private void appendEncodedData(int length) {
        mEncodedData[mLength++] = length;
    }

    private int CRC8(byte data[]) {
        int len = data.length;
        int i = 0;
        byte crc = 0x00;
        while (len-- > 0) {
            byte extract = data[i++];
            for (byte tempI = 8; tempI != 0; tempI--) {
                byte sum = (byte) ((crc & 0xFF) ^ (extract & 0xFF));
                sum = (byte) ((sum & 0xFF) & 0x01);
                crc = (byte) ((crc & 0xFF) >>> 1);
                if (sum != 0) {
                    crc = (byte)((crc & 0xFF) ^ 0x8C);
                }
                extract = (byte) ((extract & 0xFF) >>> 1);
            }
        }
        return (crc & 0xFF);
    }

    private int CRC8(String stringData) {
        return CRC8(stringData.getBytes());
    }

    private void leadingPart() {
        int [] a=new int[]{1,11,21,31};
        for (int i = 0; i < 50; ++i) {
            for (int j = 0; j <4; ++j)
                appendEncodedData(a[j]);
        }
    }

    private void magicCode(String ssid, String password, String pin) {
        //Log.i(TAG,"ssid:"+ssid);
        int length = password.length() + pin.length() + ssid.length();
        //Log.i(TAG,"length:"+length);
        int magicCode[] = new int[4];
        magicCode[0] = 0x00 | (length >>> 4 & 0xF);
        if (magicCode[0] == 0)
            magicCode[0] = 0x08;
        magicCode[1] = 0x10 | (length & 0xF);
        int crc8 = CRC8(ssid);
        magicCode[2] = 0x20 | (crc8 >>> 4 & 0xF);
        magicCode[3] = 0x30 | (crc8 & 0xF);
        for (int i = 0; i < 20; ++i) {
            for (int j = 0; j < 4; ++j)
                appendEncodedData(magicCode[j]);
        }
    }

    private void prefixCode(String password) {
        int length = password.length();
        int prefixCode[] = new int[4];
        prefixCode[0] = 0x40 | (length >>> 4 & 0xF);
        prefixCode[1] = 0x50 | (length & 0xF);
        int crc8 = CRC8(new byte[] {(byte)length});
        //int crc8=CRC8(String.valueOf(length));
        prefixCode[2] = 0x60 | (crc8 >>> 4 & 0xF);
        prefixCode[3] = 0x70 | (crc8 & 0xF);
        for (int j = 0; j < 4; ++j)
            appendEncodedData(prefixCode[j]);
    }

    private void sequence(int index, byte data[]) {
        byte content[] = new byte[data.length + 1];
        content[0] = (byte)(index & 0xFF);
        System.arraycopy(data, 0, content, 1, data.length);
        int crc8 = CRC8(content);
        appendEncodedData(0x80 | crc8);
        appendEncodedData(0x80 | index);
        for (byte aData : data)
            appendEncodedData(aData | 0x100);
    }

}
