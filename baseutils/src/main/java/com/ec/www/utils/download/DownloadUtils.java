package com.ec.www.utils.download;

import com.ec.www.base.AbstractApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadUtils {

    private HashMap<String, Call> mDownCalls; //用来存放各个下载的请求
    private OkHttpClient mClient; //OKHttpClient;
    private String mSavePath;

    private static class DOWNLOAD_MANAGER_REQ {
        static {
            DOWNLOAD_UTILS = new DownloadUtils();
        }

        private static final DownloadUtils DOWNLOAD_UTILS;
    }

    private DownloadUtils() {
        init();
    }

    public static DownloadUtils getInstance() {
        return DownloadUtils.DOWNLOAD_MANAGER_REQ.DOWNLOAD_UTILS;
    }

    public String getSavePath() {
        return mSavePath;
    }

    public DownloadUtils setSavePath(String savePath) {
        mSavePath = savePath;
        return this;
    }

    private void init() {
        mSavePath = AbstractApplication.getContext().getCacheDir().getPath();
        mDownCalls = new HashMap<>();
        mClient = new OkHttpClient.Builder().build();
    }


    public DownloadUtils download(String url, DownLoadSubscriber downLoadSubscriber) {
        download(url.substring(url.lastIndexOf("/")), url, downLoadSubscriber);
        return this;
    }

    /**
     * 开始下载
     *
     * @param fileName           存储时的文件名
     * @param url                下载请求的网址
     * @param downLoadSubscriber 用来回调的接口
     */
    public DownloadUtils download(String fileName, String url, DownLoadSubscriber downLoadSubscriber) {
        Observable.just(url)
                .filter(s -> !mDownCalls.containsKey(s))//call的map已经有了,就证明正在下载,则这次不下载
                .flatMap(s -> Observable.just(createDownInfo(fileName, s)))
                .map(this::getRealFileName)//检测本地文件夹,生成新的文件名
                .flatMap(DownloadOnSubscribe::new)//下载
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())//在主线程回调
                .subscribeOn(Schedulers.io())//在子线程执行
                .subscribe(downLoadSubscriber);//添加观察者
        return this;
    }

    public DownloadUtils cancel(String url) {
        Call call = mDownCalls.get(url);
        if (call != null) {
            call.cancel();//取消
        }
        mDownCalls.remove(url);
        return this;
    }


    /**
     * 创建DownInfo
     *
     * @param url 请求网址
     * @return DownInfo
     */
    private DownloadInfo createDownInfo(String fileName, String url) {
        DownloadInfo downloadInfo = new DownloadInfo(url);
        long contentLength = getContentLength(url);//获得文件大小
        downloadInfo.setTotal(contentLength);
        downloadInfo.setFileName(fileName);
        return downloadInfo;
    }

    private DownloadInfo getRealFileName(DownloadInfo downloadInfo) {
        String fileName = downloadInfo.getFileName();
        long downloadLength = 0, contentLength = downloadInfo.getTotal();
        File file = new File(mSavePath, fileName);
        if (file.exists()) {
            //找到了文件,代表已经下载过,则获取其长度
            downloadLength = file.length();
        }
        //之前下载过,需要重新来一个文件
        int i = 1;
        while (downloadLength >= contentLength) {
            int dotIndex = fileName.lastIndexOf(".");
            String fileNameOther;
            if (dotIndex == -1) {
                fileNameOther = fileName + "(" + i + ")";
            } else {
                fileNameOther = fileName.substring(0, dotIndex)
                        + "(" + i + ")" + fileName.substring(dotIndex);
            }
            File newFile = new File(mSavePath, fileNameOther);
            file = newFile;
            downloadLength = newFile.length();
            i++;
        }
        //设置改变过的文件名/大小
        downloadInfo.setProgress(downloadLength);
        downloadInfo.setFileName(file.getName());
        downloadInfo.setPath(file.getAbsolutePath());
        return downloadInfo;
    }

    private class DownloadOnSubscribe extends Observable<DownloadInfo> {
        private DownloadInfo downloadInfo;

        public DownloadOnSubscribe(DownloadInfo downloadInfo) {
            this.downloadInfo = downloadInfo;
        }

        @Override
        protected void subscribeActual(Observer<? super DownloadInfo> observer) {
            String url = downloadInfo.getUrl();
            long downloadLength = downloadInfo.getProgress();//已经下载好的长度
            long contentLength = downloadInfo.getTotal();//文件的总长度
            //初始进度信息
            observer.onNext(downloadInfo);
            Request request = new Request.Builder()
                    //确定下载的范围,添加此头,则服务器就可以跳过已经下载好的部分
                    .addHeader("RANGE", "bytes=" + downloadLength + "-" + contentLength)
                    .url(url)
                    .build();
            Call call = mClient.newCall(request);
            mDownCalls.put(url, call);//把这个添加到call里,方便取消
            Response response;
            try {
                response = call.execute();
            } catch (IOException e) {
                observer.onError(e);
                return;
            }

            File file = new File(mSavePath, downloadInfo.getFileName());
            InputStream is = null;
            FileOutputStream fileOutputStream = null;
            try {
                is = response.body().byteStream();
                fileOutputStream = new FileOutputStream(file, true);
                byte[] buffer = new byte[2048];//缓冲数组2kB
                int len;
                while ((len = is.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, len);
                    downloadLength += len;
                    downloadInfo.setProgress(downloadLength);
                    observer.onNext(downloadInfo);
                }
                fileOutputStream.flush();
                mDownCalls.remove(url);
            } catch (IOException e) {
                observer.onError(e);
                return;
            } finally {
                //关闭IO流
                IOUtil.closeAll(is, fileOutputStream);
            }
            observer.onComplete();//完成
        }
    }

    /**
     * 获取下载长度
     *
     * @param downloadUrl
     * @return
     */
    private long getContentLength(String downloadUrl) {
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();
        try {
            Response response = mClient.newCall(request).execute();
            if (response != null && response.isSuccessful()) {
                long contentLength = response.body().contentLength();
                response.close();
                return contentLength == 0 ? DownloadInfo.TOTAL_ERROR : contentLength;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return DownloadInfo.TOTAL_ERROR;
    }


}
