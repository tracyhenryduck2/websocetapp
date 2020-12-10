package com.siterwell.demo.folder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.siterwell.demo.R;
import com.siterwell.demo.common.Errcode;
import com.siterwell.demo.common.TopbarSuperActivity;
import com.siterwell.demo.common.UnitTools;
import com.siterwell.demo.folder.bean.LocalFolderBean;
import com.siterwell.demo.storage.FolderDao;
import com.siterwell.sdk.http.HekrUser;
import com.siterwell.sdk.http.HekrUserAction;
import com.siterwell.sdk.http.bean.FolderBean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by gc-0001 on 2017/4/25.
 */

public class AddFolderActivity extends TopbarSuperActivity implements View.OnClickListener{
    private final String TAG ="AddFolderActivity";
    private Button btn_add;
    private EditText editText;
    private final static int REQUEST_ADD_PIC = 1;
    private final static int REQUEST_IMAGE_CUT = 2;
    private String path;
    private GridView gridView_icos;
    private IcosAdpater icosAdpater;
    private List<LocalFolderBean> folderlist;
    private int ico_index = -1;
    private String[] foldernames;
    private FolderDao folderDao;

    @Override
    protected void onCreateInit() {
        initdata();
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_addfolder;
    }

    private void initView(){
        folderDao = new FolderDao(this);
        gridView_icos = (GridView)findViewById(R.id.icos);
        btn_add = (Button)findViewById(R.id.addFolder);
        editText = (EditText)findViewById(R.id.foldername);
        btn_add.setOnClickListener(this);
        getTopBarView().setTopBarStatus(R.drawable.back, -1, getResources().getString(R.string.addfolder), 1, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   finish();
            }
        },null,R.color.bar_bg);
        icosAdpater = new IcosAdpater(this,folderlist);
        gridView_icos.setAdapter(icosAdpater);
        gridView_icos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(AddFolderActivity.this,"点击了",Toast.LENGTH_LONG).show();
                for(int m=0;m<folderlist.size();m++){
                    folderlist.get(m).setSelect(false);
                }
                folderlist.get(i).setSelect(true);
                icosAdpater.refreshLists(folderlist);
                ico_index = i;
                int t = folderDao.findFoldersByNameLike(foldernames[i]);
                editText.setText(foldernames[i]+(t+1));
                //editText.requestFocus();
                //InputMethodManager inputMethodManager =
                //    (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                //inputMethodManager.toggleSoftInputFromWindow(
                 //   editText.getApplicationWindowToken(),
                 //   InputMethodManager.SHOW_FORCED, 0);
            }
        });

    }

    private void initdata(){
        foldernames = getResources().getStringArray(R.array.folder_name);
        folderlist = new ArrayList<LocalFolderBean>();
        for(int i=0;i<6;i++){
            LocalFolderBean localFolderBean = new LocalFolderBean();
            localFolderBean.setSelect(false);
            localFolderBean.setFolderName(foldernames[i]);
            folderlist.add(localFolderBean);
        }

    }

    @Override
    public void onClick(View view) {
         switch (view.getId()){
             case R.id.addFolder:
                final String name = editText.getText().toString().trim();

                 if(TextUtils.isEmpty(name)){
                     Toast.makeText(AddFolderActivity.this,getResources().getString(R.string.name_is_null),Toast.LENGTH_SHORT).show();
                 }
                 else if(name.length()>=20){
                     Toast.makeText(AddFolderActivity.this,getResources().getString(R.string.name_is_too_long),Toast.LENGTH_SHORT).show();
                 }
                 else if(ico_index == -1){
                     Toast.makeText(AddFolderActivity.this,getResources().getString(R.string.please_choose_ico),Toast.LENGTH_SHORT).show();
                 }
                 else
                   {
                    showProgressDialog(getResources().getString(R.string.wait));
                           HekrUserAction.getInstance(AddFolderActivity.this).addFolder(name+"ufile"+ico_index, new HekrUser.AddFolderListener() {
                               @Override
                               public void addFolderSuccess(FolderBean folderBean) {
                                   hideProgressDialog();
                                   Log.i(TAG,folderBean.getFolderName());
                                   Toast.makeText(AddFolderActivity.this,getResources().getString(R.string.success_add),Toast.LENGTH_SHORT).show();
                                   finish();
                               }

                               @Override
                               public void addFolderFail(int errorCode) {
                                   hideProgressDialog();
                                   Toast.makeText(AddFolderActivity.this, Errcode.errorCode2Msg(AddFolderActivity.this,errorCode),Toast.LENGTH_SHORT).show();
                               }
                           });

                 }
                 break;

         }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(resultCode!=RESULT_OK) return;


        if(requestCode==REQUEST_ADD_PIC){
            ArrayList<String> resulturls = data.getExtras().getStringArrayList("urls");
            File tempFile = new File(resulturls.get(0));
            startPhotoZoom(Uri.fromFile(tempFile));

        }else if(requestCode == REQUEST_IMAGE_CUT){


            path  = UnitTools.getImagePath(this)
                    +"/"+ new Date().getTime() + ".jpg";
            Bundle bundle = data.getExtras();
            Bitmap bitmap =(Bitmap) bundle.get("data");;

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
        }

    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUEST_IMAGE_CUT);
    }

}
