package com.siterwell.demo.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.siterwell.demo.common.CCPAppManager;

/**
 * Created by jishu0001 on 2016/8/19.
 */
public class  SysDB extends SQLiteOpenHelper {
    //    创建表格
    private String folderTable = "create table if not exists folder(id integer primary key autoincrement,folderid varchar(100),name varchar(200) NOT NULL,url text)";
    private String deviceTable = "create table if not exists device(id integer primary key autoincrement,model varchar(30),name varchar(200) NOT NULL,online integer,folderid varchar(100),status integer default(-1),percent integer default(-1),signal integer default(-1),bindkey varchar(100),ctrlkey varchar(100),deviceid varchar(100),ppkey varchar(100),socketmodel integer default(0),socketstatus integer default(0),circleon varchar(4),circleoff varchar(4),circlenumber integer,circlueenable integer,countdowntime varchar(4),action integer,notice integer,countdownenable integer , noticecircle integer,connecthost varchar(50))";
    private String socketTimerTable = "create table if not exists sockettimer(id integer primary key autoincrement,timerid varchar(20),name varchar(50),zone integer,hour integer,min integer,week varchar(3),enable integer default(0),deviceid varchar(100),tostatus integer)";
    //  防止重复
    private String dropFolderTable ="drop table if exists folder";
    private String dropDeviceTable ="drop table if exists device";
    private String dropsocketTimerTable ="drop table if exists sockettimer";

    public SysDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public SysDB(Context context){
        super(context, CCPAppManager.getUserId()+"_linkdb.db",null,6);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(folderTable);
        db.execSQL(deviceTable);
        db.execSQL(socketTimerTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


//        if(oldVersion == 1){
//            String addconlumn = "alter table device add socketmodel integer default(1),socketstatus integer default(0),circleon varchar(4),circleoff varchar(4),circlenumber integer,circlueenable integer,countdowntime varchar(4),action integer,notice integer,countdownenable integer";
//            db.execSQL(addconlumn);
//
//        }
//        else if(oldVersion < 1){
            db.execSQL(dropFolderTable);
            db.execSQL(dropDeviceTable);
            db.execSQL(dropsocketTimerTable);
//        }
        onCreate(db);


    }

    // 删除字段或则修改字段的方法
    public void alterCloumn(SQLiteDatabase db, String alterTableName,
                            String create_Table_Sql, String copy_Sql) {

        final String DROP_TEMP_TABLE = "drop table if exists tempTable";
        // 重新命名修改的表
        db.execSQL("alter table " + alterTableName + " rename to tempTable");
        // 重新创建修改的表
        db.execSQL(create_Table_Sql);
        // 将临时表里的数据copy到新的数据库中
        db.execSQL(copy_Sql);
        // 最后删掉临时表
        db.execSQL(DROP_TEMP_TABLE);
        Log.i("update", "--------");
    }



}
