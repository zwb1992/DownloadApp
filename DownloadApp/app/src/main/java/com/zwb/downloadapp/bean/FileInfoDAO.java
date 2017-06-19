package com.zwb.downloadapp.bean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zwb.downloadapp.C;
import com.zwb.downloadapp.db.DbHelper;

/**
 * Created by zwb
 * Description
 * Date 2017/6/19.
 */

public class FileInfoDAO {
    private DbHelper dbHelper;
    private Context context;

    public FileInfoDAO(Context context) {
        this.context = context;
        dbHelper = new DbHelper(context);
    }

    /**
     * 插入下载信息
     *
     * @param fileInfo 下载文件信息
     */
    public void insert(FileInfo fileInfo) {
        if (exists(fileInfo)) {
            update(fileInfo);
        } else {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("url", fileInfo.getUrl());
            values.put("fileName", fileInfo.getFileName());
            values.put("length", fileInfo.getLength());
            values.put("completed", fileInfo.getCompleted());
            db.insert(C.DB.FILE_INFO, null, values);
            db.close();
        }
    }

    public void delete(FileInfo fileInfo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(C.DB.FILE_INFO, "fileName=? and url=?", new String[]{fileInfo.getFileName(), fileInfo.getUrl()});
    }

    /**
     * 更新下载信息
     *
     * @param fileInfo 下载文件信息
     */
    public void update(FileInfo fileInfo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("length", fileInfo.getLength());
        values.put("completed", fileInfo.getCompleted());
        db.update(C.DB.FILE_INFO, values, "fileName=? and url=?", new String[]{fileInfo.getFileName(), fileInfo.getUrl()});
        db.close();
    }

    /**
     * 获取下载信息
     *
     * @param fileInfo 下载文件信息
     * @return 下载文件信息
     */
    public FileInfo get(FileInfo fileInfo) {
        if (exists(fileInfo)) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from " + C.DB.FILE_INFO + " where fileName=? and url=?",
                    new String[]{fileInfo.getFileName(), fileInfo.getUrl()});
            if (cursor.moveToNext()) {
                fileInfo.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                fileInfo.setCompleted(cursor.getLong(cursor.getColumnIndex("completed")));
                fileInfo.setLength(cursor.getLong(cursor.getColumnIndex("length")));
                db.close();
                cursor.close();
                return fileInfo;
            } else {
                return fileInfo;
            }
        } else {
            return fileInfo;
        }
    }


    /**
     * 是否插入过下载信息
     *
     * @param fileInfo 下载文件信息
     * @return
     */
    public boolean exists(FileInfo fileInfo) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + C.DB.FILE_INFO + " where fileName=? and url=?",
                new String[]{fileInfo.getFileName(), fileInfo.getUrl()});
        boolean exist = cursor.moveToNext();
        db.close();
        cursor.close();
        return exist;
    }
}
