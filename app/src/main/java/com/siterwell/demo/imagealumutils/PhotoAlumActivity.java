package com.siterwell.demo.imagealumutils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.siterwell.demo.R;
import com.siterwell.demo.common.BitmapCompressor;
import com.siterwell.demo.common.UnitTools;


public class PhotoAlumActivity extends Activity implements ListImageDirPopupWindow.OnImageDirSelected,OnClickListener
{
	private ProgressDialog mProgressDialog;

	/**
	 * 存储文件夹中的图片数量
	 */
	private int mPicsSize;
	/**
	 * 图片数量最多的文件夹
	 */
	private File mImgDir;
	/**
	 * 所有的图片
	 */
	private List<String> mImgs = new ArrayList<String>();

	private GridView mGirdView;
	private MyAdapter mAdapter;
	/**
	 * 临时的辅助类，用于防止同一个文件夹的多次扫描
	 */
	private HashSet<String> mDirPaths = new HashSet<String>();

	/**
	 * 扫描拿到所有的图片文件夹
	 */
	private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();

	private RelativeLayout mBottomLy;

	private TextView mChooseDir;
	private TextView mImageCount;
	int totalCount = 0;

	private int mScreenHeight;

	private ListImageDirPopupWindow mListImageDirPopupWindow;
	
	private ImageButton backbtn;
    private TextView    titleTextview;
    private TextView    confirmTextview;

	private Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			
			switch (msg.what) {
			case 0x110:
				mProgressDialog.dismiss();
				// 为View绑定数据
				data2View();
				// 初始化展示文件夹的popupWindw
				initListDirPopupWindw();
				break;
			case 0x111:
				mProgressDialog.dismiss();
				mAdapter = new MyAdapter(getApplicationContext(), mImgs,
						R.layout.grid_item, mImgDir.getAbsolutePath());
				mGirdView.setAdapter(mAdapter);
				break;
			default:
				break;
			}
			
		}
	};

	/**
	 * 为View绑定数据
	 */
	private void data2View()
	{
		if (mImgDir == null)
		{
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_one_pics),
					Toast.LENGTH_SHORT).show();
			return;
		}

		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		mAdapter = new MyAdapter(getApplicationContext(), mImgs,
				R.layout.grid_item, mImgDir.getAbsolutePath());
		mGirdView.setAdapter(mAdapter);
		String count = String.format(getResources().getString(R.string.pic),totalCount);
		mImageCount.setText(count);
	};

	/**
	 * 初始化展示文件夹的popupWindw
	 */
	private void initListDirPopupWindw()
	{
		mListImageDirPopupWindow = new ListImageDirPopupWindow(
				LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
				mImageFloders, LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.list_dir, null));

		mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener()
		{

			@Override
			public void onDismiss()
			{
				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
		// 设置选择文件夹的回调
		mListImageDirPopupWindow.setOnImageDirSelected(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_album);

		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		mScreenHeight = outMetrics.heightPixels;
		MyAdapter.mSelectedImage.removeAll(MyAdapter.mSelectedImage);
		initView();
		getImages();
		initEvent();

	}

	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
	 */
	private void getImages()
	{
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
		{
			Toast.makeText(this, getResources().getString(R.string.no_extern_storage), Toast.LENGTH_SHORT).show();
			return;
		}
		// 显示进度条
		mProgressDialog = ProgressDialog.show(this, null, getResources().getString(R.string.loading_press));

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{

				String firstImage = null;

				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = PhotoAlumActivity.this
						.getContentResolver();

				// 只查询jpeg和png的图片
				Cursor mCursor = mContentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png" },
						MediaStore.Images.Media.DATE_MODIFIED);
	
				Log.e("TAG", mCursor.getCount() + "");
				while (mCursor.moveToNext())
				{
					// 获取图片的路径
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));

					Log.e("TAG", path);
					// 拿到第一张图片的路径
					if (firstImage == null)
						firstImage = path;
					// 获取该图片的父路径名
					File parentFile = new File(path).getParentFile();
					if (parentFile == null)
						continue;
					String dirPath = parentFile.getAbsolutePath();
					ImageFloder imageFloder = null;
					// 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
					if (mDirPaths.contains(dirPath))
					{
						continue;
					} else
					{
						mDirPaths.add(dirPath);
						// 初始化imageFloder
						imageFloder = new ImageFloder();
						imageFloder.setDir(dirPath);
						imageFloder.setFirstImagePath(path);
					}

					int picSize = parentFile.list(new FilenameFilter()
					{
						@Override
						public boolean accept(File dir, String filename)
						{
							if (filename.endsWith(".jpg")
									|| filename.endsWith(".png")
									|| filename.endsWith(".jpeg"))
								return true;
							return false;
						}
					}).length;
					totalCount += picSize;

					imageFloder.setCount(picSize);
					mImageFloders.add(imageFloder);

					if (picSize > mPicsSize)
					{
						mPicsSize = picSize;
						mImgDir = parentFile;
					}
				}
				// 扫描完成，辅助的HashSet也就可以释放内存了
				mDirPaths = null;

               if(mImgDir==null){
   				mHandler.sendEmptyMessage(0x110);
   				return;
               }
						// 只查询jpeg和png的图片
				mCursor = mContentResolver.query(mImageUri, null,
								"("+MediaStore.Images.Media.MIME_TYPE + "=? or "
										+ MediaStore.Images.Media.MIME_TYPE + "=?"+ ") and "
										+ MediaStore.Images.Media.DATA +" like ?",
								new String[] { "image/jpeg", "image/png" ,"%" +mImgDir.getAbsolutePath() + "%"},
								MediaStore.Images.Media.DATE_MODIFIED  + " DESC");
			
						Log.e("TAG", mCursor.getCount() + "");
						while (mCursor.moveToNext())
						{
							// 获取图片的路径
							String path = mCursor.getString(mCursor
									.getColumnIndex(MediaStore.Images.Media.DATA));
		                    File flie = new File(path);
							mImgs.add(flie.getName());
							// 拿到第一张图片的路径

						}
						mCursor.close();

				
				
				// 通知Handler扫描图片完成
				mHandler.sendEmptyMessage(0x110);

			}
		}).start();

	}

	/**
	 * 初始化View
	 */
	private void initView()
	{
		backbtn   = (ImageButton)findViewById(R.id.back);
		titleTextview = (TextView)findViewById(R.id.title);
		titleTextview.setText(getResources().getString(R.string.pic_album));
		confirmTextview = (TextView)findViewById(R.id.queding);
		backbtn.setOnClickListener(this);
		confirmTextview.setOnClickListener(this);
		mGirdView = (GridView) findViewById(R.id.id_gridView);
		mChooseDir = (TextView) findViewById(R.id.id_choose_dir);
		mImageCount = (TextView) findViewById(R.id.id_total_count);

		mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);

	}

	private void initEvent()
	{
		/**
		 * 为底部的布局设置点击事件，弹出popupWindow
		 */
		mBottomLy.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mListImageDirPopupWindow
						.setAnimationStyle(R.style.anim_popup_dir);
				mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = .3f;
				getWindow().setAttributes(lp);
			}
		});
	}

	@Override
	public void selected(ImageFloder floder)
	{

		mImgDir = new File(floder.getDir());
		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		String count = String.format(getResources().getString(R.string.pic),floder.getCount());
		mImageCount.setText(count);
		mChooseDir.setText(floder.getName());
		mListImageDirPopupWindow.dismiss();
		
		
		mProgressDialog = ProgressDialog.show(this, null, getResources().getString(R.string.loading_press));
		mImgs.removeAll(mImgs);
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{


				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = PhotoAlumActivity.this
						.getContentResolver();

				// 只查询jpeg和png的图片
				Cursor mCursor = mContentResolver.query(mImageUri, null,
						"("+MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=?"+ ") and "
								+ MediaStore.Images.Media.DATA +" like ?",
						new String[] { "image/jpeg", "image/png" ,"%" +mImgDir.getAbsolutePath() + "%"},
						MediaStore.Images.Media.DATE_MODIFIED + " DESC");
	
				Log.e("TAG", mCursor.getCount() + "");
				while (mCursor.moveToNext())
				{
					// 获取图片的路径
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					Log.i("ceshi","path"+path);
                    File flie = new File(path);
					mImgs.add(flie.getName());
					// 拿到第一张图片的路径

				}
				mCursor.close();

				// 通知Handler扫描图片完成
				mHandler.sendEmptyMessage(0x111);

			}
		}).start();
		

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.queding)
		{
//			for(String s : MyAdapter.mSelectedImage){
//				Log.i("ceshi",s);
//				}
			//submit();
			submit();
		
		}
		else if(v.getId()==R.id.back)
		{
			finish();
		}
	}

	private void submit() {
		
		
		Intent intent = new Intent();
		intent.putStringArrayListExtra("urls",  MyAdapter.mSelectedImage);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	
	private void mds(){
		ArrayList<String> imageurl_compress = new ArrayList<String>();
		for(int i=0;i<MyAdapter.mSelectedImage.size();i++)
		{
		
			
			String path  = UnitTools.getImagePath(this)
					+"/"+ new Date().getTime() + ".jpg";
			
			Bitmap bitmap = BitmapCompressor.getSmallBitmap(MyAdapter.mSelectedImage.get(i));
			
			FileOutputStream b = null;
			try {
				b = new FileOutputStream(path);
				bitmap.compress(Bitmap.CompressFormat.JPEG,100, b);// 把数据写入文件
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					bitmap.recycle();
					 bitmap=null;
					b.flush();
					b.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			imageurl_compress.add(path);
		}
		
		
		Intent intent = new Intent();
		intent.putStringArrayListExtra("urls",  imageurl_compress);
		setResult(RESULT_OK, intent);
		finish();

	}
	

}
