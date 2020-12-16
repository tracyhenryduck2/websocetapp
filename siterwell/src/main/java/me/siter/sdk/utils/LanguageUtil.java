package me.siter.sdk.utils;

import android.content.Context;
import android.os.Build;

import java.util.Locale;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 **/
public class LanguageUtil {
    //中文简体
    public static final int LANGUAGE_zh_Hans = 1;
    //中文繁体
    public static final int LANGUAGE_zh_Hant = 2;
    //中文英文
    public static final int LANGUAGE_en = 3;


    public LanguageUtil() {

    }

    /**
     * 获取语言
     */
    public static int getLanguage(Context context) {
        String language = context.getResources().getConfiguration().locale.getCountry();
        switch (language) {
            case "CN":
                return LANGUAGE_zh_Hans;
            case "TW":
                return LANGUAGE_zh_Hant;
            case "US":
                return LANGUAGE_en;
            default:
                return LANGUAGE_en;
        }
    }

    /**
     * 获取语言tag
     *
     * @param context context
     * @return zh-CN/en-US
     */
    public static String getLanguageTag(Context context) {
        return (getLanguage(context) == LanguageUtil.LANGUAGE_zh_Hans) ? "zh-CN" : "en-US";
    }

    /**
     * 获取语言tag（下划线）
     *
     * @param context context
     * @return zh-CN/en-US
     */
    public static String getLanguageTag2(Context context) {
        return (getLanguage(context) == LanguageUtil.LANGUAGE_zh_Hans) ? "zh_CN" : "en_US";
    }

    public static String getAcceptLnaguage() {
        String acceptLanguage;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            acceptLanguage = Locale.getDefault().toLanguageTag();
        } else {
            acceptLanguage = toBcp47Language(Locale.getDefault());
        }
        return acceptLanguage;
    }

    public static String toBcp47Language(Locale loc) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return loc.toLanguageTag();
        }

        // we will use a dash as per BCP 47
        final char SEP = '-';
        String language = loc.getLanguage();
        String region = loc.getCountry();
        String variant = loc.getVariant();

        // special case for Norwegian Nynorsk since "NY" cannot be a variant as per BCP 47
        // this goes before the string matching since "NY" wont pass the variant checks
        if (language.equals("no") && region.equals("NO") && variant.equals("NY")) {
            language = "nn";
            region = "NO";
            variant = "";
        }

        if (language.isEmpty() || !language.matches("\\p{Alpha}{2,8}")) {
            language = "und";       // Follow the Locale#toLanguageTag() implementation
            // which says to return "und" for Undetermined
        } else if (language.equals("iw")) {
            language = "he";        // correct deprecated "Hebrew"
        } else if (language.equals("in")) {
            language = "id";        // correct deprecated "Indonesian"
        } else if (language.equals("ji")) {
            language = "yi";        // correct deprecated "Yiddish"
        }

        // ensure valid country code, if not well formed, it's omitted
        if (!region.matches("\\p{Alpha}{2}|\\p{Digit}{3}")) {
            region = "";
        }

        // variant subtags that begin with a letter must be at least 5 characters long
        if (!variant.matches("\\p{Alnum}{5,8}|\\p{Digit}\\p{Alnum}{3}")) {
            variant = "";
        }

        StringBuilder bcp47Tag = new StringBuilder(language);
        if (!region.isEmpty()) {
            bcp47Tag.append(SEP).append(region);
        }
        if (!variant.isEmpty()) {
            bcp47Tag.append(SEP).append(variant);
        }

        return bcp47Tag.toString();
    }
}
