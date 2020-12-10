package com.siterwell.demo.imagealumutils;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

public class IOHelper { 

/** 从给定路径加载图片*/ 
		public static Bitmap loadBitmap(String imgpath,BitmapFactory.Options options) { 
		   return BitmapFactory.decodeFile(imgpath,options); 
		} 
/** 从给定的路径加载图片，并指定是否自动旋转方向*/ 
public static Bitmap loadBitmap(String imgpath, boolean adjustOritation,BitmapFactory.Options options) { 
		if (!adjustOritation) { 
		return loadBitmap(imgpath,options); 
		} else { 
		Bitmap bm = loadBitmap(imgpath,options); 
		int digree = 0; 
		ExifInterface exif = null; 
		try { 
		exif = new ExifInterface(imgpath); 
		} catch (IOException e) { 
		e.printStackTrace(); 
		exif = null; 
		} 
		if (exif != null) { 
				// 读取图片中相机方向信息 
				int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 
				ExifInterface.ORIENTATION_UNDEFINED); 
				// 计算旋转角度 
				switch (ori) { 
				case ExifInterface.ORIENTATION_ROTATE_90: 
				digree = 90; 
				break; 
				case ExifInterface.ORIENTATION_ROTATE_180: 
				digree = 180; 
				break; 
				case ExifInterface.ORIENTATION_ROTATE_270: 
				digree = 270; 
				break; 
				default: 
				digree = 0; 
				break; 
				} 
		} 
			if (digree != 0) { 
			// 旋转图片 
			Matrix m = new Matrix(); 
			m.postRotate(digree);
			if(bm!=null)
			{
				bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), 
				bm.getHeight(), m, true); 
			}

			} 
		return bm; 
		} 
} 

} 