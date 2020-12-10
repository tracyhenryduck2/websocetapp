package com.siterwell.demo.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.litesuits.android.log.Log;
import com.siterwell.demo.folder.bean.LocalFolderBean;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by gc-0001 on 2017/4/26.
 */

public class FolderDao {
    private final String TAG = "FolderDao";
    private SysDB sys;
    private SQLiteDatabase db;
    Context context;
    public FolderDao(Context context){
        this.context = context;
        this.sys = new SysDB(context);
        this.db = this.sys.getWritableDatabase();
    }



    /**
     * 插入分组数列到数据库
     *
     * @param folderlist
     * @return
     */
    public ArrayList<Long> insertFolderList(List<LocalFolderBean> folderlist) {

        ArrayList<Long> rows = new ArrayList<Long>();
        try {

            db.beginTransaction();
            for (LocalFolderBean c : folderlist) {
                long rowId = insertFolder(c);
                if (rowId != -1L) {
                    rows.add(rowId);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return rows;
    }

    /**
     * 插入分组
     * @param folderBean
     * @return
     */
    public long insertFolder(LocalFolderBean folderBean) {

        if(folderBean == null || TextUtils.isEmpty(folderBean.getFolderId())) {
            return -1L;
        }
        ContentValues values = null;
        try {
            values = new ContentValues();
            values.put("folderid", folderBean.getFolderId());
            values.put("name", folderBean.getFolderName());
            values.put("url",folderBean.getImage());
            if(!isHasFolder(folderBean.getFolderId())) {
                return db.insert("folder", null, values);
            } else {
                return db.update("folder",values , "folderid ='" + folderBean.getFolderId()+"'",null);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (values != null) {
                values.clear();
            }
        }
        return -1L;
    }


    //判断是否有该folderid的分组
    public boolean isHasFolder(String folderid){

        LocalFolderBean folderBean = findFolderBySid(folderid);
        try {
            if(TextUtils.isEmpty(folderBean.getFolderId())){
                return false;
            }else{
                return true;
            }

        }catch (NullPointerException e){
            return false;
        }

    }



    /**
     * @return FolderListBean
     */
    public LocalFolderBean findFolderBySid(String folderid){
        LocalFolderBean folderBean = null;
        try {

            Cursor cursor = db.rawQuery("select * from folder where folderid = '"+folderid+"'",null);
            if(cursor.moveToFirst()) {
                folderBean = new LocalFolderBean();
                folderBean.setFolderId(cursor.getString(cursor.getColumnIndex("folderid")));
                folderBean.setFolderName(cursor.getString(cursor.getColumnIndex("name")));
                folderBean.setImage(cursor.getString(cursor.getColumnIndex("url")));
            }
            cursor.close();
        }catch (NullPointerException e){
            Log.i(TAG,"no folder");
        }finally {
            return folderBean;
        }
    }

     /*
     @method findFoldersByNameLike
     @autor Administrator
     @time 2017/8/4 13:47
     @email xuejunju_4595@qq.com
     */
    public int  findFoldersByNameLike(String namelike){
        int count = 0;
        try {

            Cursor cursor = db.rawQuery("select count(*) from folder where name like '"+namelike+"%'",null);
            cursor.moveToFirst();
            count = cursor.getInt(0);
            cursor.close();
        }catch (NullPointerException e){
            Log.i(TAG,"no folder");
        }finally {
            return count;
        }
    }


    /**
     * methodname:findAllFolders
     * 作者：Henry on 2017/4/27 8:45
     * 邮箱：xuejunju_4595@qq.com
     * 参数:
     * 返回:所有分组
     */
    public List<LocalFolderBean> findAllFolders(){
        List<LocalFolderBean> sys2 = new ArrayList<LocalFolderBean>();
        try {
            Cursor cursor = db.rawQuery("select * from folder where 1 = 1",null);
            while (cursor.moveToNext()){
                String foldid = cursor.getString(cursor.getColumnIndex("folderid"));
                if(!"0".equals(foldid)){
                    LocalFolderBean sb = new LocalFolderBean();
                    sb.setFolderId(cursor.getString(cursor.getColumnIndex("folderid")));
                    sb.setFolderName(cursor.getString(cursor.getColumnIndex("name")));
                    sb.setImage(cursor.getString(cursor.getColumnIndex("url")));
                    sys2.add(sb);
                }

            }
            cursor.close();
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed device");
        }finally {
            return sys2;
        }

    }

    public void deleteByFolderId(String deviceid){
        String where = "folderid = ?";
        String[] whereValue ={ deviceid };
        db.delete("folder", where, whereValue);
    }

    //删除所有操作
    public void deleteAll()
    {
        String where = "1 = 1";
        db.delete("folder", where, null);
    }

    /**
     * update Mid
     * @param name,folderid
     */
    public void updateFolderName(String name,String folderid){
        String where = "folderid = ?";
        String[] whereValue = {folderid};
        ContentValues cv = new ContentValues();
        cv.put("name",name);
        db.update("folder", cv, where, whereValue);
    }

    public void reset(){

        db.close();
        sys.close();
    }

}
