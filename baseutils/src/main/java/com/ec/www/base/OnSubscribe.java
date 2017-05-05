package com.ec.www.base;

import android.accounts.NetworkErrorException;
import android.util.SparseIntArray;

import com.ec.www.base.http.HttpResponse;
import com.ec.www.base.http.response.IDispose;
import com.ec.www.utils.NetworkUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by huang on 2017/5/3.
 */

public class OnSubscribe<T> implements ObservableTransformer<T, T> {

    private HttpResponse mResponse;
    private SparseIntArray mRepeatArray = new SparseIntArray(5);
    private static final String Tag = "onSubscribe";


    public OnSubscribe(HttpResponse response) {
        mResponse = response;
    }

    @Override
    public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
        int resultCode = mResponse.getIDispose().getResultSupport().resultCode;
        return upstream.filter(t -> {
            int requestId = mRepeatArray.get(resultCode, IDispose.DEFAULT);
            if (!NetworkUtils.isNetworkConnected()) {
                mResponse.onError(new NetworkErrorException());
            }

            if (requestId == IDispose.DEFAULT) {
                return false;
            } else {
                //请求重复 查找tag
                StackTraceElement[] elements = new Throwable().getStackTrace();
                String method = null;
                int index;
                //遍历查找onSubscribe
                if (elements[0].getMethodName().equals(Tag)) {
                    method = elements[1].getMethodName();
                } else {
                    for (int i = 0; i < elements.length; i++) {
                        if (elements[i].getMethodName().equals(Tag)) {
                            index = i + 1;
                            if (index < elements.length) {
                                method = elements[index].getMethodName();
                            }
                        }
                    }
                }
                mResponse.onRepeat(method);
            }
            return true;
        }).subscribeOn(Schedulers.io())
                .doOnComplete(()->{
                    mRepeatArray.put(resultCode, resultCode);
                    mResponse.addCompleteCallBack(perform -> {
                        if (perform != null) {
                            mRepeatArray.delete(perform.resultCode);
                        }
                    });
                }).observeOn(AndroidSchedulers.mainThread());
    }
}
