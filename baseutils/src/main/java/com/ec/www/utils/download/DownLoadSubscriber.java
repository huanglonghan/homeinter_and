package com.ec.www.utils.download;

import io.reactivex.observers.DefaultObserver;

public abstract class DownLoadSubscriber extends DefaultObserver<DownloadInfo> {

    protected DownloadInfo downloadInfo;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onComplete() {
        onCompleted(downloadInfo);
    }

    @Override
    public void onNext(DownloadInfo downloadInfo) {
        this.downloadInfo = downloadInfo;
    }

    @Override
    public void onError(Throwable e) {

    }

    public abstract void onCompleted(DownloadInfo downloadInfo);

}
