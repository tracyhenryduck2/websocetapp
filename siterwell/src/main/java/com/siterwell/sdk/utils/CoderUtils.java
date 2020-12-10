package com.siterwell.sdk.utils;


import java.io.UnsupportedEncodingException;

/**
 * Created by jishu0001 on 2017/2/9.
 */

public class CoderUtils {
    private static final String TAG = CoderUtils.class.getName();


    /*
    @method getStringFromAscii
    @autor Administrator
    @time 2017/6/30 9:51
    @email xuejunju_4595@qq.com
    从ascii 码转成GBK编码的String类型变量，若格式不对则返回NUll
    */
    public static String getStringFromAscii(String input){



        try {
            if(input.length()!=32){
                return "";
            }
            byte[]a = ByteUtil.hexStr2Bytes(input);
            String name  = new String(a,"GBK");
            if(name.indexOf("$")==-1){
                return "";
            }
            int index = -1;
            if(name.indexOf("@")!=-1){
                index = name.lastIndexOf("@");
            }

            return  name.substring(index + 1, name.indexOf("$"));

        }catch (Exception e){
            e.printStackTrace();
            return "";
        }


    }


    /**
     * the scene name to code
     * @param input
     * @return
     */

    public static String getAscii(String input){
        int countf = 0;

        try {
            countf = 15 - input.getBytes("GBK").length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        StringBuffer buffer = new StringBuffer();
        for(int i=0;i<countf;i++){
            buffer.append("@");
        }
        String newname = buffer.toString()+input+"$";

        byte[] nameBt = new byte[16];
        try {
            nameBt = newname.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String ds = "";
        for(int i=0;i<nameBt.length;i++){
            String str = ByteUtil.convertByte2HexString(nameBt[i]);
            ds+=str;
        }

        return ds;
    }




    public static void main(String args[]) {

        String abc = "404040404040404040ced2b5c4b2e524";

        byte ds = (byte) Integer.parseInt("11",16);
        System.out.print("点对点"+ds);
    }

}
