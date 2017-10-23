package com.siterwell.sdk.util;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
@class DownLoadH5
@autor Administrator
@time 2017/10/16 13:58
@email xuejunju_4595@qq.com
*/
public class DownLoadH5 {
    private static final String TAG="DownLoadH5";
    private Context context;
    private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

    public DownLoadH5(Context context) {
        this.context = context;
    }

    public void startDownLoadH5(final String url, final String pageFileName){
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG,"启动下载线程!");
                downloadAllAssets(url,pageFileName);
            }
        });
    }

    private void downloadAllAssets( String url,String pageFileName) {
        // Temp folder for holding asset during download
        Log.i(TAG,"download");
        File zipDir =  ExternalStorage.getSDCacheDir(context, "tmp");
        //File zipDir =  new File(context.getApplicationContext().getFilesDir().getAbsolutePath(),"tmp");
        // File path to store .zip file before unzipping
        File zipFile = new File( zipDir.getPath() ,"temp.zip" );
        // Folder to hold unzipped output
        File outputDir = ExternalStorage.getSDCacheDir( context, pageFileName );
        //File outputDir = new File(context.getApplicationContext().getFilesDir().getAbsolutePath(),pageFileName);
        try {
            DownloadFile.download( url, zipFile, zipDir );
            unzipFile( zipFile, outputDir );
        } finally {
            zipFile.delete();
        }
    }

    /**
     * Unpack .zip file.
     *
     * @param zipFile
     * @param destination
     */
    protected void unzipFile( File zipFile, File destination ) {
        Log.i(TAG,"unzip");
        DecompressZip decomp = new DecompressZip( zipFile.getPath(),
                destination.getPath() + File.separator );
        decomp.unzip();
    }
}
