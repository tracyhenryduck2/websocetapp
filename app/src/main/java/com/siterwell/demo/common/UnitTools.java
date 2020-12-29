package com.siterwell.demo.common;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;

import com.siterwell.demo.R;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by jishu0001 on 2016/8/30.
 */
public class UnitTools {
    private final Locale SPANISH = new Locale("es", "ES");;
    private Context context;
    private static MediaPlayer mediaPlayer = null;
    private static String imagePath=null;
    public UnitTools(Context context){
        this.context = context;
    }


    /**
     * methodname:
     * 作者：Henry on 2017/3/6 8:53
     * 邮箱：xuejunju_4595@qq.com
     * 参数:context
     * 返回:当前运行的activity的名称
     */
    public static String getRunningActivityName(Context context){
        ActivityManager activityManager=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        String runningActivity=activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        return runningActivity;
    }

    /**
     * methodname:
     * 作者：Henry on 2017/3/6 8:54
     * 邮箱：xuejunju_4595@qq.com
     * 参数:设置容器第一次打开标志
     * 返回:
     */
    public static void writeFirstOpen(Context context, String activityName, boolean falg){
        SharedPreferences user = context.getSharedPreferences("user_info",0);
        SharedPreferences.Editor mydata = user.edit();
        mydata.putBoolean(activityName ,falg);
        mydata.commit();
    }

    /**
     * methodname:
     * 作者：Henry on 2017/3/6 8:56
     * 邮箱：xuejunju_4595@qq.com
     * 参数:读取是否第一次打开标志
     * 返回:
     */
    public static boolean readFirstOpen(Context context, String activityName) {
        SharedPreferences wode = context.getSharedPreferences("user_info",0);
        boolean flag = wode.getBoolean(activityName,false);

        return flag;
    }

    public static List<String> decode1(List<String> mlist) {
        String temp="";
        for(int i = 0;i<mlist.size();i++){
            for(int j=i+1;j<mlist.size();j++){
                if( Integer.parseInt(mlist.get(i))> Integer.parseInt( mlist.get(j))){
                    temp = mlist.get(i);
                    mlist.set(i,mlist.get(j));
                    mlist.set(j,temp);
                }
            }
        }
        return mlist;
    }

    public static String timeDecode(String timeIn, int tag) {
        String time  ="";
        switch (tag){
            case 6:
                if(timeIn != null && timeIn.length() == tag && tag==6){
                    String d = timeIn.substring(0,2);
                    String h = timeIn.substring(2,4);
                    String m = timeIn.substring(4,6);
                    String d1 =d;
//                    if(Integer.toHexString(Integer.parseInt(h)).length()<2){
//                        h1 = "0"+Integer.toHexString(Integer.parseInt(h));
//                    }else {
//                        h1 = Integer.toHexString(Integer.parseInt(h));
//                    }
                    String h1 ="";
                    if(Integer.toHexString(Integer.parseInt(h)).length()<2){
                        h1 = "0"+ Integer.toHexString(Integer.parseInt(h));
                    }else {
                        h1 = Integer.toHexString(Integer.parseInt(h));
                    }
                    String m1 ="";
                    if(Integer.toHexString(Integer.parseInt(m)).length()<2){
                        m1 = "0"+ Integer.toHexString(Integer.parseInt(m));
                    }else {
                        m1 = Integer.toHexString(Integer.parseInt(m));
                    }
                    time = d1 +h1+m1;
                }else {
                    time ="000000";
                }
                break;
            case 4:
                if(timeIn!= null && timeIn.length() == 4 && tag ==4){
                    String m = timeIn.substring(0,2);
                    String s = timeIn.substring(2,4);
                    String m1 ="";
                    if(Integer.toHexString(Integer.parseInt(m)).length()<2){
                        m1 = "0"+ Integer.toHexString(Integer.parseInt(m));
                    }else {
                        m1 = Integer.toHexString(Integer.parseInt(m));
                    }
                    String s1 ="";
                    if(Integer.toHexString(Integer.parseInt(s)).length()<2){
                        s1 = "0"+ Integer.toHexString(Integer.parseInt(s));
                    }else {
                        s1 = Integer.toHexString(Integer.parseInt(s));
                    }
                    time= m1+s1;
                }else{
                    time = "0000";
                }
                break;
            default:
                break;
        }
        return time;
    }


    /**
     *判断当前应用程序处于前台还是后台
     */
    public static boolean isApplicationBroughtToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public static void playNotifycationMusic(Context context)
            throws IOException {
        // paly music ...

        if (mediaPlayer!=null && mediaPlayer.isPlaying()) {
            return;
        }

            mediaPlayer = MediaPlayer.create(context, R.raw.phonering);

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
//        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    public static void stopMusic(Context context)
            throws IOException {
        // paly music ...
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.phonering);
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        mediaPlayer = null;
    }

    public static int getStatusBarHeight(Context context){
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }


    //获取屏幕的宽度
    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }


    //获取屏幕的宽度
    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }

    /**
     * write ssid code
     * @param name
     * @param key
     */
    public void writeSSidcode(String name, String key){
        SharedPreferences user = context.getSharedPreferences("user_info",0);
        SharedPreferences.Editor mydata = user.edit();
        mydata.putString(name ,key);
        mydata.commit();
    }

    public String readSSidcode(String key) {
        SharedPreferences wode = context.getSharedPreferences("user_info",0);
        String psd = wode.getString(key,null);

        return psd;
    }

    /**
     * 设置语言
     * @param Language
     */
    public void writeLanguage(String Language){
        SharedPreferences user = context.getSharedPreferences("user_info",0);
        SharedPreferences.Editor mydata = user.edit();
        mydata.putString("Language" ,Language);
        mydata.commit();
    }


    public String readLanguage() {
        SharedPreferences wode = context.getSharedPreferences("user_info",0);
        String name = wode.getString("Language","");

        return name;
    }

    public String shiftLanguage(Context context,String sta){

        Resources resource = context.getResources();
        Configuration config = resource.getConfiguration();
        Locale locale = context.getResources().getConfiguration().locale;//获得local对象
        String lan = locale.getLanguage();

        if(TextUtils.isEmpty(sta)){
            if("zh".equals(lan)){
                config.locale = Locale.CHINA;
                writeLanguage("zh");
            }else if("fr".equals(lan)){
                config.locale = Locale.FRENCH;
                writeLanguage("fr");
            }else if("de".equals(lan)){
                config.locale = Locale.GERMANY;
                writeLanguage("de");
            }else if("es".equals(lan)){
                config.locale = SPANISH;
                writeLanguage("es");
            }else{
                config.locale = Locale.ENGLISH;
                writeLanguage("en");
            }

            context.getResources().updateConfiguration(config, null);
            return lan;
        }else{
            if("zh".equals(sta)){
                config.locale = Locale.CHINA;
                writeLanguage("zh");
            }else if("fr".equals(sta)){
                config.locale = Locale.FRENCH;
                writeLanguage("fr");
            }else if("de".equals(sta)){
                config.locale = Locale.GERMANY;
                writeLanguage("de");
            }else if("es".equals(sta)){
                config.locale = SPANISH;
                writeLanguage("es");
            }else{
                config.locale = Locale.ENGLISH;
                writeLanguage("en");
            }

            context.getResources().updateConfiguration(config, null);
            return sta;
        }



    }

    /**
     * get usr Login infor
     * @param name
     * @param name
     */
    public void writeUserLog(String name){
        SharedPreferences user = context.getSharedPreferences("user_info",0);
        SharedPreferences.Editor mydata = user.edit();
        mydata.putString("userlist" ,name);
        mydata.commit();
    }
    public String readUserLog() {
        SharedPreferences wode = context.getSharedPreferences("user_info",0);
        String list = wode.getString("userlist","[]");
        return list;
    }

    public static String getImagePath(Context context) {
        if (imagePath == null) {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                // 判断sd卡是否存在
                File sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
                File file = new File(sdDir.getAbsolutePath() + "/siterlink");
                if (file.isDirectory()) {
                    imagePath = file.getAbsolutePath();
                } else {
                    if (file.mkdirs()) {
                        imagePath = file.getAbsolutePath();
                    } else {
                        imagePath = context.getFilesDir()
                                .getAbsolutePath();
                    }
                }
            } else {
                imagePath = context.getFilesDir().getAbsolutePath();
            }
        }
        return imagePath;
    }
    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    public static boolean isLocServiceEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    public static void main(String args[]) {
//		test_convertUint8toByte();
//		test_convertChar2Uint8();
//		test_splitUint8To2bytes();
//		test_combine2bytesToOne();
//		test_parseBssid();
        String abc = "40404040404040404040404061616124";
       // System.out.print(""+getAsciiFromString(abc));
//		int[] arrayData = {1,2,4,5,6,7,5,6,7,3,8,9,10,12,11,20,30,40};
//		Arrays.sort(arrayData);
//		for (int a : arrayData){
//			System.out.print("" + a + ";");
//		}


    }

}
