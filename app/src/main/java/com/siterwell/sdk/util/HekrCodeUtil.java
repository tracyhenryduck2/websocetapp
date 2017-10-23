package com.siterwell.sdk.util;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.siterwell.sdk.bean.ErrorMsgBean;

import org.json.JSONException;

import java.util.Locale;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
@class HekrCodeUtil
@autor Administrator
@time 2017/10/16 13:49
@email xuejunju_4595@qq.com
*/
public class HekrCodeUtil {
    //中文简体
    public static final int LANGUAGE_zh_Hans = 1;
    //中文繁体
    public static final int LANGUAGE_zh_Hant = 2;
    //中文英文
    public static final int LANGUAGE_en = 3;


    public HekrCodeUtil() {
    }

    /**
     * 从网址 中拿到路径
     */
    public static String url2Folder(String url) {
        Pattern p = Pattern.compile("h5page/(.*?)/");
        Matcher m = p.matcher(url);
        while (m.find()) {
            MatchResult mr = m.toMatchResult();
            return mr.group(1);
        }
        return "";
    }

    public static String zip2Folder(String url) {
        Pattern p = Pattern.compile("h5package/(.*?)/");
        Matcher m = p.matcher(url);
        while (m.find()) {
            MatchResult mr = m.toMatchResult();
            return mr.group(1);
        }
        return "";
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
        return (getLanguage(context) == HekrCodeUtil.LANGUAGE_zh_Hans) ? "zh-CN" : "en-US";
    }

    /**
     * 获取语言tag（下划线）
     *
     * @param context context
     * @return zh-CN/en-US
     */
    public static String getLanguageTag2(Context context) {
        return (getLanguage(context) == HekrCodeUtil.LANGUAGE_zh_Hans) ? "zh_CN" : "en_US";
    }


    /**
     * 错误码转换到错误信息！
     *
     * @param errorCode 错误码
     * @return 错误信息
     */
    public static String errorCode2Msg(int errorCode) {
        switch (errorCode) {
            case 3400001:
                return "手机号码无效";
            case 3400002:
                return "验证码错误";
            case 3400003:
                return "验证码过期";
            case 3400005:
                return "发送验证码次数过多";
            case 3400006:
                return "无效的请求类型";
            case 3400007:
                return "无效的旧密码";
            case 3400008:
                return "用户已注册";
            case 3400009:
                return "用户尚未验证";
            case 3400010:
                return "账号或密码错误";
            case 3400011:
                return "用户不存在";
            case 3400012:
                return "无效的邮件token";
            case 3400013:
                return "账户已认证";
            case 3400014:
                return "账户已经关联了某个三方账号";
            case 500:
            case 5200000:
                return "服务内部错误";
            case 5400001:
                return "内部错误";
            case 5400002:
                return "app重复登录";
            case 5400003:
                return "appTid不能为空";
            case 5400004:
                return "授权关系已存在";
            case 5400005:
                return "授权关系不存在";
            case 5400006:
                return "因网络原因绑定失败";
            case 5400007:
                return "因超时原因绑定失败";
            case 5400009:
                return "修改用户档案失败";
            case 5400010:
                return "校验code失败";
            case 5400011:
                return "设备授权次数达到上限";
            case 5400012:
                return "因内部错误绑定失败";
            case 5400013:
                return "因重复绑定绑定失败";
            case 5400014:
                return "设备不属于用户";
            case 5400015:
                return "没有这样的指令";
            case 5400016:
                return "设备无法重复登录";
            case 5400017:
                return "devTid不能为空";
            case 5400018:
                return "创建定时预约次数达到上限";
            case 5400019:
                return "授权的指令已过期";
            case 5400020:
                return "不支持该指令";
            case 5400021:
                return "不合法的邮件token";
            case 5400022:
                return "不合法的旧密码";
            case 5400023:
                return "不合法的校验code";
            case 5400024:
                return "由于内部错误设备无法找到，请重连";
            case 5400025:
                return "不存在该pid";
            case 5400026:
                return "没有对该指令的权限";
            case 5400027:
                return "指定模板不存在";
            case 5400028:
                return "由于内部不正确的状态导致设备无法被找到";
            case 5400035:
                return "指定任务不存在";
            case 5400036:
                return "无法创建重复模板";
            case 5400037:
                return "设备id 不匹配";
            case 5400039:
                return "用户不存在";
            case 5400040:
                return "校验code过期";
            case 5400041:
                return "校验code发送失败";
            case 5400042:
                return "校验code类型不合法";
            case 5400043:
                return "设备无法强制绑定";
            case 5500000:
                return "内部服务错误";
            case 6400001:
                return "指定id的反向注册申请不存在";
            case 6400002:
                return "不合法的反向授权请求";
            case 6400003:
                return "只有属主可以授权设备给其他人";
            case 6400004:
                return "指定devTid的设备不存在";
            case 6400005:
                return "达到文件夹所能容纳设备数量的上限";
            case 6400006:
                return "无法创建同名文件夹";
            case 6400007:
                return "指定id的文件夹不存在";
            case 6400008:
                return "达到创建文件夹数量上限";
            case 6400009:
                return "无法删除根目录";
            case 6400010:
                return "无法给根目录改名";
            case 6400011:
                return "指定的规则不存在";
            case 6400012:
                return "指定的定时预约任务不存在";
            case 6400013:
                return "无法创建相同的规则";
            case 6400014:
                return "无法创建相同的定时预约";
            case 6400015:
                return "不合法的prodPubKey";
            case 6400016:
                return "没有权限这样做";
            case 6400017:
                return "请求参数错误";
            case 6400018:
                return "指定的网盘文件不存在";
            case 6400020:
                return "找不到这个红外码";
            case 6400021:
                return "红外服务请求出错";
            case 6400022:
                return "无法找到指令集";
            case 6400023:
                return "参数不支持";
            case 6400024:
                return "解析json失败";
            case 6500001:
                return "删除网盘文件失败";
            case 6500002:
                return "上传网盘文件失败";
            case 6500003:
                return "http网络调用失败";
            case 8200000:
                return "成功";
            case 8400000:
                return "产品不存在";
            case 8400001:
                return "协议模板不存在";
            case 8400002:
                return "非法参数";
            case 8400003:
                return "平台参数错误";
            case 8400004:
                return "指定pid不存在";
            case 9200000:
                return "成功";
            case 9400000:
                return "错误";
            case 9400001:
                return "非法参数";
            case 9400002:
                return "action 不存在";
            case 9400003:
                return "产品不存在";
            case 9400004:
                return "超时";
            case 9400005:
                return "方法不支持";
            case 9500000:
                return "服务错误";
            case 9500001:
                return "服务应答错误";
            case 0:
                return "网络超时";
            case 1:
                return "登录信息过期，请重新登录！";
            case 2:
                return "未知错误！";
            case 400016:
                return "操作过于频繁,请稍候再试";
            case 400017:
                return "今日操作已达上限";
          /*  case 400014:
                return "密码重置失败";*/
            default:
                // return String.valueOf(errorCode);
                return "服务器异常，请重新再试！" + errorCode;
        }
    }


    /**
     * 定义错误
     *
     * @param code  httpCode
     * @param bytes body
     * @return 错误码
     */
    public static int getErrorCode(int code, byte[] bytes) {
        switch (code) {
            case ConstantsUtil.ErrorCode.NETWORK_TIME_OUT:
                Log.e(ConstantsUtil.HEKR_SDK_ERROR, ConstantsUtil.NETWORK_ERROR);
                return code;
            case ConstantsUtil.ErrorCode.TOKEN_TIME_OUT:
                Log.e(ConstantsUtil.HEKR_SDK_ERROR, ConstantsUtil.TOKEN_OUT_ERROR);
                return code;
            case ConstantsUtil.ErrorCode.SERVER_ERROR:
                if (bytes != null && bytes.length > 0) {
                    Log.e(ConstantsUtil.HEKR_SDK_ERROR, new String(bytes));
                }
                return code;
            default:
                Log.e(ConstantsUtil.HEKR_SDK_ERROR, "HTTP-" + code + new String(bytes));
                ErrorMsgBean errorMsgBean = msg2Bean(bytes);
                return errorMsgBean.getCode();
        }
    }


    /**
     * 转换错误信息
     *
     * @param url   网址
     * @param code  httpCode
     * @param bytes body
     * @return 错误码
     */
    public static int getErrorCode(String url, int code, byte[] bytes) {
        String errorMsg;
        switch (code) {
            case ConstantsUtil.ErrorCode.NETWORK_TIME_OUT:
                errorMsg = ConstantsUtil.NETWORK_ERROR;
                Log.e(ConstantsUtil.HEKR_SDK_ERROR, url + "\n" + "HTTP-" + code + "\n" + errorMsg);
                return code;
            case ConstantsUtil.ErrorCode.TOKEN_TIME_OUT:
                errorMsg = ConstantsUtil.TOKEN_OUT_ERROR;
                Log.e(ConstantsUtil.HEKR_SDK_ERROR, url + "\n" + "HTTP-" + code + "\n" + errorMsg);
                return code;
            case ConstantsUtil.ErrorCode.SERVER_ERROR:
                if (bytes != null && bytes.length > 0) {
                    errorMsg = new String(bytes);
                    Log.e(ConstantsUtil.HEKR_SDK_ERROR, url + "\n" + "HTTP-" + code + "\n" + errorMsg);
                }
                return code;
            default:
                ErrorMsgBean errorMsgBean = msg2Bean(bytes);
                Log.e(ConstantsUtil.HEKR_SDK_ERROR, url + "\n" + "HTTP-" + code + "\n" + new String(bytes));
                return errorMsgBean.getCode();
        }
    }


    /**
     * 转换错误信息
     *
     * @return ErrorMsgBean
     */
    public static ErrorMsgBean msg2Bean(byte[] bytes) {
        ErrorMsgBean errorMsgBean = new ErrorMsgBean(ConstantsUtil.ErrorCode.UNKNOWN_ERROR, ConstantsUtil.UNKNOWN_ERROR, System.currentTimeMillis());
        try {
            if (bytes == null || bytes.length == 0) {
                return errorMsgBean;
            } else {
                String errorMsg = new String(bytes);
                errorMsgBean = JSON.parseObject(errorMsg, ErrorMsgBean.class);
                return errorMsgBean;
            }
        } catch (Exception e) {
            return new ErrorMsgBean(ConstantsUtil.ErrorCode.UNKNOWN_ERROR, ConstantsUtil.UNKNOWN_ERROR, System.currentTimeMillis());
        }
    }


    /**
     * 判断是否token过期
     */
    public static boolean getTimeOutFlag(int i, byte[] bytes) {
        if (i == 403) {
            if (bytes != null && bytes.length > 0) {
                String str = new String(bytes);
                try {
                    org.json.JSONObject object = new org.json.JSONObject(str);
                    if (object.has("status")) {
                        return object.getInt("status") == 403;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return false;
        }
        return false;
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
