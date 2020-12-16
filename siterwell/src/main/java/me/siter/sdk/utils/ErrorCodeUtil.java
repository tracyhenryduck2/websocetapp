package me.siter.sdk.utils;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
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
