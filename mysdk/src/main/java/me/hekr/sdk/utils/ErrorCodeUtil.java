package me.hekr.sdk.utils;

/**
 * Created by hucn on 2017/10/17.
 * Author:
 * Description:
 */

public class ErrorCodeUtil {

    public static String getErrorDesc(int code) {
        if (code >= 100000) {
            return CloudErrorMap.code2Desc(code);
        } else if (code >= 10000 && code <= 99999) {
            return AndroidErrorMap.code2Desc(code);
        } else {
            return null;
        }
    }
}
