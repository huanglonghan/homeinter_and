package com.ec.www.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.ec.www.base.AbstractApplication;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.functions.Function;

/**
 * Created by huang on 2017/3/3.
 */

public class DownloadUtil {

    private DownloadManager downloadManager;

    private static class DOWNLOAD_MANAGER_REQ {
        static {
            DOWNLOAD_UTILS = new DownloadUtil();
        }

        private static final DownloadUtil DOWNLOAD_UTILS;
    }

    private DownloadUtil() {
        init();
    }

    public static DownloadUtil getInstance() {
        return com.ec.www.utils.DownloadUtil.DOWNLOAD_MANAGER_REQ.DOWNLOAD_UTILS;
    }

    private void init() {
        downloadManager = (DownloadManager) AbstractApplication
                .getContext()
                .getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public long request(String url, Function<DownloadManager.Request, DownloadManager.Request> callback) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        if (callback != null) {
            try {
                request = callback.apply(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return downloadManager.enqueue(request);
    }

    public long request(String url) {
        return request(url, null);
    }

    public long cancel(long... ids) {
        return downloadManager.remove(ids);
    }

    public Cursor query(long... ids) {
        return downloadManager.query(new DownloadManager.Query().setFilterById(ids));
    }

    public ArrayList<DownloadInfo> queryInfo(long... ids) {
        ArrayList<DownloadInfo> infos = new ArrayList<>();
        DownloadInfo info;
        Cursor cursor = query(ids);
        if (!cursor.moveToFirst()) return null;
        do {
            info = new DownloadInfo();
            info.status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            info.address = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
            info.bytes_downloaded = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            info.bytes_total = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            info.title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
            info.description = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION));
            info.id = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID));
            //info.filename = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
            String downloadFileLocalUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
            if (downloadFileLocalUri != null) {
                File mFile = new File(Uri.parse(downloadFileLocalUri).getPath());
                info.filename = mFile.getAbsolutePath();
            }

            info.url = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

        } while (cursor.moveToNext());
        return infos;
    }

    public DownloadInfo queryInfo(long id) {
        Cursor cursor = query(id);
        DownloadInfo info = new DownloadInfo();
        if (!cursor.moveToFirst()) return null;
        info.address = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
        info.bytes_downloaded = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
        info.bytes_total = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
        info.title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
        info.description = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION));
        info.id = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID));
        //info.filename = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
        String downloadFileLocalUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
        if (downloadFileLocalUri != null) {
            File mFile = new File(Uri.parse(downloadFileLocalUri).getPath());
            info.filename = mFile.getAbsolutePath();
        }

        info.url = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
        return info;
    }

    public class DownloadInfo {
        public String address;
        public int status;
        public long bytes_downloaded;
        public long bytes_total;
        public String title;
        public String description;
        public long id;
        public String filename;
        public String url;
    }
}
