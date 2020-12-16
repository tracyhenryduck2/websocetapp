package me.siter.sdk.utils;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description:
 */

public class ResourceUtil {
    /**
     * 读取R.raw.hekr.json
     */
    public static String convertStreamToString(Context mContext, int id) {
        InputStream is = mContext.getResources().openRawResource(id);
        String s = "";
        try {
            Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
            if (scanner.hasNext()) s = scanner.next();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }
}
