package com.siterwell.sdk.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.litesuits.common.io.FileUtils;
import com.siterwell.sdk.bean.DeviceBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/16.
 */

public class DevicesCacheUtil {


    //将用户数据写入到sd卡filename中
    private static void saveDeviceDataSD(String customData, String fileName) {
        //Log.i(TAG,"写入sd卡内容:"+userInfo);
        FileOutputStream fos;
        //String info = customData;
        try {
            fos = new FileOutputStream(Environment.getDataDirectory() + "/" + fileName, false);
            OutputStreamWriter writer = new OutputStreamWriter(fos, "utf-8");
            writer.write(customData);
            writer.flush();
            writer.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //从sd卡中获取存储数据
    private static String getDeviceDataSD(String fileName) {
        StringBuilder strsBuffer = new StringBuilder();
        try {
            // 判断是否存在SD
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                File file = new File(Environment.getExternalStorageDirectory(), fileName);
                // 判断是否存在该文件
                if (file.exists()) {
                    // 打开文件输入流
                    FileInputStream fileR = new FileInputStream(file);
                    BufferedReader reads = new BufferedReader(
                            new InputStreamReader(fileR));
                    String st;
                    while ((st = reads.readLine()) != null) {
                        strsBuffer.append(st);
                    }
                    fileR.close();
                    return strsBuffer.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


    /**
     * 保存String到本地
     */
    private static void save(Context context, String devicesJsonStr) {
        if (!TextUtils.isEmpty(devicesJsonStr)) {

            File file = getFile(context);
            try {
                FileUtils.writeStringToFile(file, devicesJsonStr);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存设备列表到本地
     *
     * @param allDevicesLists 设备列表
     */
    public static void saveDevices(Context context, List<DeviceBean> allDevicesLists) {
        if (!allDevicesLists.isEmpty()) {
            save(context, JSONArray.toJSONString(allDevicesLists));
        }
    }


    /**
     * 读取本地数据
     */
    private static String read(Context context) {
        File file = getFile(context);
        try {
            return FileUtils.readFileToString(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取本地的设备列表
     *
     * @return 设备列表
     */
    public static List<DeviceBean> readDeviceLists(Context context) {
        String str = read(context);
        List<DeviceBean> list = new ArrayList<>();
        if (!TextUtils.isEmpty(str)) {
            list = JSON.parseArray(str, DeviceBean.class);
        }
        return list;
    }


    /**
     * 清除本地数据
     */
    public static void deleteDeviceLists(Context context) {
        try {
            FileUtils.forceDelete(getFile(context));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File getFile(Context context) {
        String folder = context.getApplicationContext().getFilesDir().getAbsolutePath();
        return new File(folder, ConstantsUtil.DEVICES_JSON_NAME);
    }


}
