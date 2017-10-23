package com.siterwell.sdk.util;

/**
 * ZipDownloader
 *
 * A simple app to demonstrate downloading and unpacking a .zip file
 * as a background task.
 *
 */


import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;


public class DownloadFile {

    private static final String TAG="DownloadFile";
    private static final int BUFFER_SIZE = 8192;

    /**
     * Download a file from a URL somewhere.  The download is atomic;
     * that is, it downloads to a temporary file, then renames it to
     * the requested file name only if the download successfully
     * completes.
     *
     * Returns TRUE if download succeeds, FALSE otherwise.
     *
     * @param url				Source URL
     * @param output			Path to output file
     * @param tmpDir			Place to put file download in progress
     */
    public static void download( String url, File output, File tmpDir ) {
        InputStream is = null;
        OutputStream os = null;
        File tmp = null;
        Log.i(TAG,"url:"+url);
        Log.i(TAG,"下载中...");
        try {
            tmp = File.createTempFile( "download", ".tmp", tmpDir );
            if(url.contains("/")) {
                int i = url.lastIndexOf("/");
                String s = url.substring(0, i);
                String ss = url.substring(i, url.length());
                String reallyUrl = (String) TextUtils.concat(s, URLEncoder.encode(ss, "utf-8"));
                Log.i("SSSSS", reallyUrl);
                is = new URL(reallyUrl).openStream();
                os = new BufferedOutputStream(new FileOutputStream(tmp));
                copyStream(is, os);
                tmp.renameTo(output);
                tmp = null;
            }
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        } finally {
            if ( tmp != null ) { try { tmp.delete(); tmp = null; } catch (Exception ignore) {;} }
            if ( is != null  ) { try { is.close();   is = null;  } catch (Exception ignore) {;} }
            if ( os != null  ) { try { os.close();   os = null;  } catch (Exception ignore) {;} }
        }
    }

    /**
     * Copy from one stream to another.  Throws IOException in the event of error
     * (for example, SD card is full)
     *
     * @param is		Input stream.
     * @param os		Output stream.
     */
    public static void copyStream( InputStream is, OutputStream os ) throws IOException {
        byte[] buffer = new byte[ BUFFER_SIZE ];
        copyStream( is, os, buffer, BUFFER_SIZE );
    }

    /**
     * Copy from one stream to another.  Throws IOException in the event of error
     * (for example, SD card is full)
     *
     * @param is			Input stream.
     * @param os			Output stream.
     * @param buffer		Temporary buffer to use for copy.
     * @param bufferSize	Size of temporary buffer, in bytes.
     */
    public static void copyStream( InputStream is, OutputStream os,
                                   byte[] buffer, int bufferSize ) throws IOException {
        try {
            for (;;) {
                int count = is.read( buffer, 0, bufferSize );
                if ( count == -1 ) { break; }
                os.write( buffer, 0, count );
            }
        } catch ( IOException e ) {
            throw e;
        }
    }
}