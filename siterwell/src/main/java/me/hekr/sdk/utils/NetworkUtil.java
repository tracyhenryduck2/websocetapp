package me.hekr.sdk.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import me.hekr.sdk.monitor.NetType;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: NetWork in lite-common.jar
 */

public class NetworkUtil {
    private static final String TAG = NetworkUtil.class.getSimpleName();

    public NetworkUtil() {
    }

    public static ConnectivityManager getConnectivityManager(Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static TelephonyManager getTelephonyManager(Context context) {
        return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public static boolean isConnected(Context context) {
        NetworkInfo net = getConnectivityManager(context).getActiveNetworkInfo();
        return net != null && net.isConnected();
    }

    public static boolean isConnectedOrConnecting(Context context) {
        NetworkInfo[] nets = getConnectivityManager(context).getAllNetworkInfo();
        if (nets != null) {
            NetworkInfo[] arr$ = nets;
            int len$ = nets.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                NetworkInfo net = arr$[i$];
                if (net.isConnectedOrConnecting()) {
                    return true;
                }
            }
        }

        return false;
    }

    public static NetType getConnectedType(Context context) {
        NetworkInfo net = getConnectivityManager(context).getActiveNetworkInfo();
        if (net != null) {
            switch (net.getType()) {
                case 0:
                    return NetType.MOBILE;
                case 1:
                    return NetType.WIFI;
                default:
                    return NetType.OTHER;
            }
        } else {
            return NetType.NONE;
        }
    }

    public static boolean isWifiConnected(Context context) {
        NetworkInfo net = getConnectivityManager(context).getActiveNetworkInfo();
        return net != null && net.getType() == 1 && net.isConnected();
    }

    public static boolean isMobileConnected(Context context) {
        NetworkInfo net = getConnectivityManager(context).getActiveNetworkInfo();
        return net != null && net.getType() == 0 && net.isConnected();
    }

    public static boolean isAvailable(Context context) {
        return isWifiAvailable(context) || isMobileAvailable(context) && isMobileEnabled(context);
    }

    public static boolean isWifiAvailable(Context context) {
        NetworkInfo[] nets = getConnectivityManager(context).getAllNetworkInfo();
        if (nets != null) {
            NetworkInfo[] arr$ = nets;
            int len$ = nets.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                NetworkInfo net = arr$[i$];
                if (net.getType() == 1) {
                    return net.isAvailable();
                }
            }
        }

        return false;
    }

    public static boolean isMobileAvailable(Context context) {
        NetworkInfo[] nets = getConnectivityManager(context).getAllNetworkInfo();
        if (nets != null) {
            NetworkInfo[] arr$ = nets;
            int len$ = nets.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                NetworkInfo net = arr$[i$];
                if (net.getType() == 0) {
                    return net.isAvailable();
                }
            }
        }

        return false;
    }

    public static boolean isMobileEnabled(Context context) {
        try {
            Method e = ConnectivityManager.class.getDeclaredMethod("getMobileDataEnabled", new Class[0]);
            e.setAccessible(true);
            return ((Boolean) e.invoke(getConnectivityManager(context), new Object[0])).booleanValue();
        } catch (Exception var2) {
            var2.printStackTrace();
            return true;
        }
    }

    public static boolean printNetworkInfo(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo in = connectivity.getActiveNetworkInfo();
            LogUtil.i(TAG, "getActiveNetworkInfo: " + in);
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; ++i) {
                    LogUtil.i(TAG, "NetworkInfo[" + i + "]isAvailable : " + info[i].isAvailable());
                    LogUtil.i(TAG, "NetworkInfo[" + i + "]isConnected : " + info[i].isConnected());
                    LogUtil.i(TAG, "NetworkInfo[" + i + "]isConnectedOrConnecting : " + info[i].isConnectedOrConnecting());
                    LogUtil.i(TAG, "NetworkInfo[" + i + "]: " + info[i]);
                }

                LogUtil.i(TAG, "\n");
            } else {
                LogUtil.i(TAG, "getAllNetworkInfo is null");
            }
        }

        return false;
    }

    public static int getConnectedTypeINT(Context context) {
        NetworkInfo net = getConnectivityManager(context).getActiveNetworkInfo();
        if (net != null) {
            if (LogUtil.getDebugEnable()) {
                LogUtil.i(TAG, "NetworkInfo: " + net.toString());
            }

            return net.getType();
        } else {
            return -1;
        }
    }

    public static int getTelNetworkTypeINT(Context context) {
        return getTelephonyManager(context).getNetworkType();
    }

    public static NetworkUtil.NetWorkType getNetworkType(Context context) {
        int type = getConnectedTypeINT(context);
        switch (type) {
            case 0:
            case 2:
            case 3:
            case 4:
            case 5:
                int teleType = getTelephonyManager(context).getNetworkType();
                switch (teleType) {
                    case 1:
                    case 2:
                    case 4:
                    case 7:
                    case 11:
                        return NetworkUtil.NetWorkType.Net2G;
                    case 3:
                    case 5:
                    case 6:
                    case 8:
                    case 9:
                    case 10:
                    case 12:
                    case 14:
                    case 15:
                        return NetworkUtil.NetWorkType.Net3G;
                    case 13:
                        return NetworkUtil.NetWorkType.Net4G;
                    default:
                        return NetworkUtil.NetWorkType.UnKnown;
                }
            case 1:
                return NetworkUtil.NetWorkType.Wifi;
            default:
                return NetworkUtil.NetWorkType.UnKnown;
        }
    }

    public static enum NetWorkType {
        UnKnown(-1),
        Wifi(1),
        Net2G(2),
        Net3G(3),
        Net4G(4);

        public int value;

        private NetWorkType(int value) {
            this.value = value;
        }
    }

    public static String getHostIP() {

        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return hostIp;
    }

    public static InetAddress getDeviceIpAddress(Context context) {
        InetAddress result = null;
        try {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            // default to Android localhost
            result = InetAddress.getByName("127.0.0.1");
            // figure out our wifi address, otherwise bail
            WifiInfo wifiinfo = wifiManager.getConnectionInfo();
            int intaddr = wifiinfo.getIpAddress();
            byte[] byteaddr = new byte[]{(byte) (intaddr & 0xff), (byte) (intaddr >> 8 & 0xff), (byte) (intaddr >> 16 & 0xff), (byte) (intaddr >> 24 & 0xff)};
            result = InetAddress.getByAddress(byteaddr);
        } catch (UnknownHostException ex) {
            LogUtil.w(TAG, String.format("getDeviceIpAddress Error: %s", ex.getMessage()));
        }
        return result;
    }
}
