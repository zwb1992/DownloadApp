package com.zwb.downloadapp.bean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zwb.downloadapp.C;
import com.zwb.downloadapp.db.DbHelper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zwb
 * Description
 * Date 2017/6/19.
 */

public class FileInfoDAO {
    //提供原子操作来进行Integer的使用，因此十分适合高并发情况下的使用。可以解决多线程同时访问数据库的情况
    private AtomicInteger mOpenCounter = new AtomicInteger();
    private DbHelper dbHelper;
    private Context context;
    private SQLiteDatabase mDatabase;
    private static FileInfoDAO fileInfoDAO;

    private FileInfoDAO(Context context) {
        this.context = context;
        dbHelper = DbHelper.getInstance(context);
    }

    public static FileInfoDAO getInstance(Context context) {
        if (fileInfoDAO == null) {
            synchronized (FileInfoDAO.class) {
                if (fileInfoDAO == null) {
                    fileInfoDAO = new FileInfoDAO(context);
                }
            }
        }
        return fileInfoDAO;
    }

    public synchronized SQLiteDatabase getWritableDatabase() {
        if (mOpenCounter.incrementAndGet() == 1) {
            // Opening new database
            mDatabase = dbHelper.getWritableDatabase();
        }
        return mDatabase;
    }

    public synchronized SQLiteDatabase getReadableDatabase() {
        if (mOpenCounter.incrementAndGet() == 1) {
            // Opening new database
            mDatabase = dbHelper.getReadableDatabase();
        }
        return mDatabase;
    }

    public synchronized void closeDatabase() {
        if (mOpenCounter.decrementAndGet() == 0) {
            // Closing database
            mDatabase.close();
        }
    }

    /**
     * 插入下载信息
     *
     * @param fileInfo 下载文件信息
     */
    public synchronized void insert(FileInfo fileInfo) {
        if (exists(fileInfo)) {
            update(fileInfo);
        } else {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("url", fileInfo.getUrl());
            values.put("fileName", fileInfo.getFileName());
            values.put("length", fileInfo.getLength());
            values.put("completed", fileInfo.getCompleted());
            db.insert(C.DB.FILE_INFO, null, values);
            closeDatabase();
        }
    }

    public synchronized void delete(FileInfo fileInfo) {
        SQLiteDatabase db = getReadableDatabase();
        db.delete(C.DB.FILE_INFO, "fileName=? and url=?", new String[]{fileInfo.getFileName(), fileInfo.getUrl()});
        closeDatabase();
    }

    /**
     * 更新下载信息
     *
     * @param fileInfo 下载文件信息
     */
    public synchronized void update(FileInfo fileInfo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("length", fileInfo.getLength());
        values.put("completed", fileInfo.getCompleted());
        db.update(C.DB.FILE_INFO, values, "fileName=? and url=?", new String[]{fileInfo.getFileName(), fileInfo.getUrl()});
        closeDatabase();
    }

    /**
     * 获取下载信息
     *
     * @param fileInfo 下载文件信息
     * @return 下载文件信息
     */
    public FileInfo get(FileInfo fileInfo) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + C.DB.FILE_INFO + " where fileName=? and url=?",
                new String[]{fileInfo.getFileName(), fileInfo.getUrl()});
        if (cursor.moveToNext()) {
            fileInfo.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
            fileInfo.setCompleted(cursor.getLong(cursor.getColumnIndex("completed")));
            fileInfo.setLength(cursor.getLong(cursor.getColumnIndex("length")));
            cursor.close();
            closeDatabase();
            return fileInfo;
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
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + C.DB.FILE_INFO + " where fileName=? and url=?",
                new String[]{fileInfo.getFileName(), fileInfo.getUrl()});
        boolean exist = cursor.moveToNext();
        cursor.close();
        closeDatabase();
        return exist;
    }
}
