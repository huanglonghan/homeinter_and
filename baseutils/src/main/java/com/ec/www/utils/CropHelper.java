package com.ec.www.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by huang on 2017/4/7.
 */

public class CropHelper {

    private int PHOTO_REQUEST_GALLERY = 0x001;
    private int PHOTO_REQUEST_CUT = 0x002;

    public void selectImage(Fragment fragment) {
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
        StartUtils.startActivityForResult(fragment, PHOTO_REQUEST_GALLERY, genSelectImgIntent());
    }

    public void selectImage(Activity activity) {
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
        StartUtils.startActivityForResult(activity, PHOTO_REQUEST_GALLERY, genSelectImgIntent());
    }

    private Function<Intent, Intent> genSelectImgIntent() {
        return (intent) -> {
            // 激活系统图库，选择一张图片
            intent.setAction(Intent.ACTION_PICK);
            intent.setType("image/*");
            return intent;
        };
    }


    public boolean onActivityResult(Fragment fragment, int requestCode, Intent data, Consumer<String> completeCallback) {
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                cropImg(fragment, data.getData());
            }
            return true;
        } else if (requestCode == PHOTO_REQUEST_CUT) {
            // 从剪切图片返回的数据
            if (data != null) {
                Bitmap bitmap = data.getParcelableExtra("data");
                if (bitmap == null) return true;
                String path = fragment.getContext().getCacheDir().getPath() + "/" + UUID.randomUUID().toString();
                File tempFile = new File(path);
                try {
                    FileOutputStream outputStream = new FileOutputStream(tempFile);
                    if (bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream)) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    completeCallback.accept(path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        return false;
    }

    public boolean onActivityResult(Activity activity, int requestCode, Intent data, Consumer<String> completeCallback) {
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                cropImg(activity, data.getData());
            }
            return true;
        } else if (requestCode == PHOTO_REQUEST_CUT) {
            // 从剪切图片返回的数据
            if (data != null) {
                Bitmap bitmap = data.getParcelableExtra("data");
                if (bitmap == null) return true;
                String path = activity.getCacheDir().getPath() + "/" + UUID.randomUUID().toString();
                File tempFile = new File(path);
                try {
                    FileOutputStream outputStream = new FileOutputStream(tempFile);
                    if (bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream)) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    completeCallback.accept(path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        return false;
    }

    private void cropImg(Activity activity, Uri uri) {
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        StartUtils.startActivityForResult(activity, PHOTO_REQUEST_CUT, genCropIntent(uri));
    }

    private void cropImg(Fragment fragment, Uri uri) {
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        StartUtils.startActivityForResult(fragment, PHOTO_REQUEST_CUT, genCropIntent(uri));
    }

    private Function<Intent, Intent> genCropIntent(Uri uri) {
        return (intent) -> {
            // 裁剪图片意图
            intent.setAction("com.android.camera.action.CROP");
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("crop", "true");
            // 裁剪框的比例，1：1
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            // 裁剪后输出图片的尺寸大小
            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);

            intent.putExtra("outputFormat", "JPEG");// 图片格式
            intent.putExtra("noFaceDetection", true);// 取消人脸识别
            intent.putExtra("return-data", true);
            return intent;
        };
    }
}
