package com.ec.www.utils;

import com.annimon.stream.Stream;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

/**
 * Created by huang on 2017/5/27.
 */

public class SplashUtils {

    /**
     * 连接字符串产生存储文件名以'-'分割(会把原有'-'替换成'_')
     *
     * @param key 字符串数组
     * @return 联合好的字符串
     */
    public static String getStoreFilename(String... key) {
        StringBuilder builder = new StringBuilder();
        Stream.of(key).filter(value -> value != null && value.length() > 0)
                .forEach(value -> {
                    value = value.replace('-', '+');
                    builder.append(value);
                    builder.append('-');
                });
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    /**
     * getLoadList的重载提供一个默认新旧文件比较器实现
     * 默认比较数值大小
     *
     * @param dir              开屏图片存储的文件目录
     * @param keys             将要展示的key标识集合 例如:{asidAwdhuWHiHidhwaih_Jasuh-14923245,oihaiudhidwhuWHiHidhwaih_Jasuh-14923245}
     * @param downloadCallback 本地不存在或有更新时需要实现的下载回调(会提供key标识)
     * @return 可以展示的图片列表(不需要下载能立即加载的图片列表)
     */
    public static ArrayList<File> getLoadList(File dir, Collection<String> keys,
                                              Consumer<String> downloadCallback) {
        return getLoadList(dir, keys, downloadCallback, (s, s2) -> Integer.valueOf(s) > Integer.valueOf(s2));
    }

    /**
     * 获取开屏图片加载列表(会下载到本地本地不存在的或者过期的将被删除并重新下载)
     *
     * @param dir              开屏图片存储的文件目录
     * @param keys             将要展示的key标识集合 例如:{asidAwdhuWHiHidhwaih_Jasuh-14923245,oihaiudhidwhuWHiHidhwaih_Jasuh-14923245}
     * @param downloadCallback 本地不存在或有更新时需要实现的下载回调(会提供key标识)
     * @param newOldComp       比较图片是否过期的比较回调 返回true则更新本地内容
     * @return 可以展示的图片列表(不需要下载能立即加载的图片列表)
     */
    public static ArrayList<File> getLoadList(File dir, Collection<String> keys,
                                              Consumer<String> downloadCallback,
                                              BiFunction<String, String, Boolean> newOldComp) {
        ArrayList<File> list = new ArrayList<>();

        HashMap<String, String> imgKeyTimeMap = getLocalFileMap(dir);

        Stream.of(keys)
                .filter(value -> value != null && value.length() > 0)
                .forEach(value -> {
                    //检查文件是否存在
                    String[] newImg = splitKey(value);
                    if (imgKeyTimeMap.containsKey(newImg[0])) {
                        File delFile = new File(dir, getStoreFilename(newImg[0], imgKeyTimeMap.get(newImg[0])));
                        try {
                            //比较文件新旧就文件删除并重新下载
                            //newOldComp 返回true 则更新文件
                            if (newOldComp.apply(newImg[1], imgKeyTimeMap.get(newImg[0]))) {
                                delFile.delete();
                                //文件不存在下载回调
                                try {
                                    downloadCallback.accept(value);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                list.add(delFile);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        //文件不存在下载回调
                        try {
                            downloadCallback.accept(value);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        return list;
    }

    private static HashMap<String, String> getLocalFileMap(File dir) {
        //格式化文件列表方便后期比较
        String[] fileList = dir.list();
        HashMap<String, String> imgKeyTimeMap = new HashMap<>();
        for (String str : fileList) {
            if (str.contains("-")) {
                String[] split = splitKey(str);
                imgKeyTimeMap.put(split[0], split[1]);
            }
        }
        return imgKeyTimeMap;
    }

    /**
     * 将字符串以'-'分割成两部分
     *
     * @param str 将要分割的字符串
     * @return 分割后形成的数组
     */
    public static String[] splitKey(String str) {
        if (!str.contains("-")) {
            throw new RuntimeException("illegal file key!");
        }
        return str.split("-", 2);
    }


}
