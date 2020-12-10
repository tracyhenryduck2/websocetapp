package com.siterwell.demo.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;


public class BitmapCompressor {
	/** 
     * Get bitmap from specified image path 
     *  
     * @param imgPath 
     * @return 
     */  
    public Bitmap getBitmap(String imgPath) {  
        // Get bitmap through image path  
        BitmapFactory.Options newOpts = new BitmapFactory.Options();  
        newOpts.inJustDecodeBounds = false;  
        newOpts.inPurgeable = true;  
        newOpts.inInputShareable = true;  
        // Do not compress  
        newOpts.inSampleSize = 1;  
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeFile(imgPath, newOpts);  
    }  
      
    /** 
     * Store bitmap into specified image path 
     *  
     * @param bitmap 
     * @param outPath 
     * @throws FileNotFoundException  
     */  
    public void storeImage(Bitmap bitmap, String outPath) throws FileNotFoundException {  
        FileOutputStream os = new FileOutputStream(outPath);  
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);  
    }  
      
    /** 
     * Compress image by pixel, this will modify image width/height.  
     * Used to get thumbnail 
     *  
     * @param imgPath image path 
     * @param pixelW target pixel of width 
     * @param pixelH target pixel of height 
     * @return 
     */  
    public Bitmap ratio(String imgPath, float pixelW, float pixelH) {  
        BitmapFactory.Options newOpts = new BitmapFactory.Options();    
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true，即只读边不读内容  
        newOpts.inJustDecodeBounds = true;  
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        // Get bitmap info, but notice that bitmap is null now    
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath,newOpts);  
            
        newOpts.inJustDecodeBounds = false;    
        int w = newOpts.outWidth;    
        int h = newOpts.outHeight;    
        // 想要缩放的目标尺寸  
        float hh = pixelH;// 设置高度为240f时，可以明显看到图片缩小了  
        float ww = pixelW;// 设置宽度为120f，可以明显看到图片缩小了  
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可    
        int be = 1;//be=1表示不缩放    
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放    
            be = (int) (newOpts.outWidth / ww);    
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放    
            be = (int) (newOpts.outHeight / hh);    
        }    
        if (be <= 0) be = 1;    
        newOpts.inSampleSize = be;//设置缩放比例  
        // 开始压缩图片，注意此时已经把options.inJustDecodeBounds 设回false了  
        bitmap = BitmapFactory.decodeFile(imgPath, newOpts);  
        // 压缩好比例大小后再进行质量压缩  
//        return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除  
        return bitmap;  
    }  
      
    /** 
     * Compress image by size, this will modify image width/height.  
     * Used to get thumbnail 
     *  
     * @param image 
     * @param pixelW target pixel of width 
     * @param pixelH target pixel of height 
     * @return 
     */  
    public Bitmap ratio(Bitmap image, float pixelW, float pixelH) {  
        ByteArrayOutputStream os = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 100, os);  
        if( os.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出      
            os.reset();//重置baos即清空baos    
            image.compress(Bitmap.CompressFormat.JPEG, 50, os);//这里压缩50%，把压缩后的数据存放到baos中    
        }    
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());    
        BitmapFactory.Options newOpts = new BitmapFactory.Options();    
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了    
        newOpts.inJustDecodeBounds = true;  
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, newOpts);    
        newOpts.inJustDecodeBounds = false;    
        int w = newOpts.outWidth;    
        int h = newOpts.outHeight;    
        float hh = pixelH;// 设置高度为240f时，可以明显看到图片缩小了  
        float ww = pixelW;// 设置宽度为120f，可以明显看到图片缩小了  
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可    
        int be = 1;//be=1表示不缩放    
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放    
            be = (int) (newOpts.outWidth / ww);    
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放    
            be = (int) (newOpts.outHeight / hh);    
        }    
        if (be <= 0) be = 1;    
        newOpts.inSampleSize = be;//设置缩放比例    
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了    
        is = new ByteArrayInputStream(os.toByteArray());    
        bitmap = BitmapFactory.decodeStream(is, null, newOpts);  
        //压缩好比例大小后再进行质量压缩  
//      return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除  
        return bitmap;  
    }  
      
    /** 
     * Compress by quality,  and generate image to the path specified 
     *  
     * @param image 
     * @param outPath 
     * @param maxSize target will be compressed to be smaller than this size.(kb) 
     * @throws IOException  
     */  
    public static void compressAndGenImage(Bitmap image, String outPath, int maxSize) throws IOException {  
        ByteArrayOutputStream os = new ByteArrayOutputStream();  
        // scale  
        int options = 100;  
        // Store the bitmap into output stream(no compress)  
        image.compress(Bitmap.CompressFormat.JPEG, options, os);    
        // Compress by loop  
        while ( os.toByteArray().length / 1024 > maxSize) {  
            // Clean up os  
            os.reset();  
            // interval 10  
            options -= 10;  
            image.compress(Bitmap.CompressFormat.JPEG, options, os);  
        }  
          
        // Generate compressed image file  
        FileOutputStream fos = new FileOutputStream(outPath);    
        fos.write(os.toByteArray());    
        fos.flush();    
        fos.close();    
    }  
      
    /** 
     * Compress by quality,  and generate image to the path specified 
     *  
     * @param imgPath 
     * @param outPath 
     * @param maxSize target will be compressed to be smaller than this size.(kb) 
     * @param needsDelete Whether delete original file after compress 
     * @throws IOException  
     */  
    public void compressAndGenImage(String imgPath, String outPath, int maxSize, boolean needsDelete) throws IOException {  
        compressAndGenImage(getBitmap(imgPath), outPath, maxSize);  
          
        // Delete original file  
        if (needsDelete) {  
            File file = new File (imgPath);  
            if (file.exists()) {  
                file.delete();  
            }  
        }  
    }  
      
    /** 
     * Ratio and generate thumb to the path specified 
     *  
     * @param image 
     * @param outPath 
     * @param pixelW target pixel of width 
     * @param pixelH target pixel of height 
     * @throws FileNotFoundException 
     */  
    public void ratioAndGenThumb(Bitmap image, String outPath, float pixelW, float pixelH) throws FileNotFoundException {  
        Bitmap bitmap = ratio(image, pixelW, pixelH);  
        storeImage( bitmap, outPath);  
    }  
      
    /** 
     * Ratio and generate thumb to the path specified 
     *  
     * @param image 
     * @param outPath 
     * @param pixelW target pixel of width 
     * @param pixelH target pixel of height 
     * @param needsDelete Whether delete original file after compress 
     * @throws FileNotFoundException 
     */  
    public void ratioAndGenThumb(String imgPath, String outPath, float pixelW, float pixelH, boolean needsDelete) throws FileNotFoundException {  
        Bitmap bitmap = ratio(imgPath, pixelW, pixelH);  
        storeImage( bitmap, outPath);  
          
        // Delete original file  
                if (needsDelete) {  
                    File file = new File (imgPath);  
                    if (file.exists()) {  
                        file.delete();  
                    }  
                }  
    } 
    
    public static int getOrientation(Context context, Uri photoUri) {
        int orientation = 0;
        Cursor cursor = context.getContentResolver().query(photoUri,
                    new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() != 1) {
                return -1;
            }
            cursor.moveToFirst();
            orientation = cursor.getInt(0);
            cursor.close();
        }
        return orientation;
    }
    
    public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
                 final int heightRatio = Math.round((float) height/ (float) reqHeight);
                 final int widthRatio = Math.round((float) width / (float) reqWidth);
                 inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
            return inSampleSize;
    }
    
 // 根据路径获得图片并压缩，返回bitmap用于显示
    public static Bitmap getSmallBitmap(String filePath) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);

            // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options,300, 350);

            // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
        }
    
    public static Bitmap compressImage(Bitmap image) {  
    	  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
        int options = 90;  
        while ( baos.toByteArray().length / 1024>400) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩         
            baos.reset();//重置baos即清空baos  
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
            Log.i("ceshi","正在压缩中");
            //options -= 10;//每次都减少10  
        }  
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片  
        return bitmap;  
    } 
    
}
