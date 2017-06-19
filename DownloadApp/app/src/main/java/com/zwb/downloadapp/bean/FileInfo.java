package com.zwb.downloadapp.bean;

import java.io.Serializable;

/**
 * Created by zwb
 * Description 下载的文件信息
 * Date 17/6/18.
 */

public class FileInfo implements Serializable{

    private int id;
    private String url;
    private String fileName;
    private long length;//文件总长度
    private long completed;//完成的长度

    public FileInfo() {
    }

    public FileInfo(int id, String url, String fileName, long length, long completed) {
        this.id = id;
        this.url = url;
        this.fileName = fileName;
        this.length = length;
        this.completed = completed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getCompleted() {
        return completed;
    }

    public void setCompleted(long completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", fileName='" + fileName + '\'' +
                ", length=" + length +
                ", completed=" + completed +
                '}';
    }
}
