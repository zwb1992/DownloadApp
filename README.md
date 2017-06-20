# DownloadApp
断点下载

##效果展示

![这里写图片描述](https://github.com/zwb1992/DownloadApp/blob/master/DownloadApp/images/downloadapp.gif)


步骤：
1，第一次下载是：获取文件长度已经当前下载进度，保存进入数据库

2，再次下载是，从数据库中读出上次下载的进度，并使用下载框架添加
 builder.addHeader("RANGE", "bytes=" + fileInfo.getCompleted() + "-" + fileInfo.getLength()); 跳过上次下载的进度
 
3，使用RandomAccessFile的seek方法，跳过上次下载过的进度，从下一次下载的位置开始保存

4,实时发送广播更新UI，并在下载完成之后删除数据库数据



##多线程断点下载

![这里写图片描述](https://github.com/zwb1992/DownloadApp/blob/master/DownloadApp/images/multipleDownloadapp.gif)
步骤：


1，修改DownloadService，使用一个Map<String, Call> callMap;来存放所以请求

2，修改DbHelper以及FileInfoDAO，使他们单例化，并且在FileInfoDAO中使用AtomicInteger，添加如下几个方法：

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
	
	这样可以保证在数据库被线程操作时不会被关闭，不会抛出异常
	
3，新增MultipleDownloadActivity类，在其中使用ListView显示需要下载的内容，并且在其中使用Hnadler每500毫秒更新一个adapter，防止开启的线程过多导致更新UI太频繁造成的卡顿