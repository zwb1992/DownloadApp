package com.zwb.downloadapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zwb.downloadapp.C;

/**
 * Created by zwb
 * Description
 * Date 2017/6/19.
 */

public class DbHelper extends SQLiteOpenHelper {
    private static DbHelper dbHelper;

    public static DbHelper getInstance(Context context) {
        if (dbHelper == null) {
            synchronized (DbHelper.class) {
                if (dbHelper == null) {
                    dbHelper = new DbHelper(context);
                }
            }
        }
        return dbHelper;
    }

    private DbHelper(Context context) {
        this(context, C.DB.DB_NAME, null, C.DB.VERSION);
    }

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + C.DB.FILE_INFO + "(_id INTEGER PRIMARY KEY ,url varchar(500),fileName varchar(50),length long,completed long)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + C.DB.FILE_INFO);
    }
}
