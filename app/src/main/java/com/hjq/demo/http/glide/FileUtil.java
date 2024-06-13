package com.hjq.demo.http.glide;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.UUID;

public class FileUtil {

//    public static void setFileSaveListener(FileSaveListener f) {
//        fileSaveListener = f;
//    }

    static FileSaveListener fileSave;
    private static Context mContext;
    private static String filePath;
    private static Bitmap mBitmap;
    private static String mSaveMessage;
    private final static String TAG = "PictureActivity";
    private static ProgressDialog mSaveDialog = null;

    public static void donwloadImg(Context context, String filePaths, FileSaveListener fileSaveListener) {
        fileSave = fileSaveListener;
        filePath = filePaths;
        mContext = context;
        mSaveDialog = ProgressDialog.show(context, "保存图片", "请稍候", true);
//        mSaveMessage = context.getResources().getString(R.string.faild);
        new Thread(saveFileRunnable).start();
    }

    static public String getFileName(String pathandname) {

        int start = pathandname.lastIndexOf("/");
        int end = pathandname.lastIndexOf(".");
        if (start != -1 && end != -1) {
//            return pathandname.substring(start + 1, end);
            return pathandname.substring(start + 1, pathandname.length());
        } else {
            return null;
        }

    }

    private static Runnable saveFileRunnable = new Runnable() {
        @Override
        public void run() {
            File f = null;
            try {
                if (!TextUtils.isEmpty(filePath)) { //网络图片
                    // 对资源链接
                    URL url = new URL(filePath);
                    //打开输入流
                    InputStream inputStream = url.openStream();
                    //对网上资源进行下载转换位图图片
                    mBitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                }
                f = saveFile(mContext, mBitmap);
//                mSaveMessage = context.getString(R.string.hassave);
            } catch (IOException e) {
//                mSaveMessage = context.getString(R.string.savefaild);
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Message m = new Message();
            m.obj = f;
            messageHandler.sendMessage(m);

            fileSave.saved(f);
//            messageHandler.sendMessage(messageHandler.obtainMessage());
        }
    };

    private static Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mSaveDialog.dismiss();
//            Log.d(TAG, mSaveMessage);
//            fileSave.saved((File) msg.obj);
//            DebugLogUtil.getInstance().Debug(msg.obj.toString());
//            Toast.makeText(context, mSaveMessage, Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 保存图片
     *
     * @param bm
     * @throws IOException
     */
    public static File saveFile(Context context, Bitmap bm) throws IOException {
        File dirFile = new File(Environment.getExternalStorageDirectory().getPath());
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        String fileName = UUID.randomUUID().toString() + ".jpg";
        File myCaptureFile = new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/" + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
        //把图片保存后声明这个广播事件通知系统相册有新图片到来
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(myCaptureFile);
        intent.setData(uri);
        context.sendBroadcast(intent);

        return myCaptureFile;
    }


    /*
     * 保存文件，文件名为当前日期
     */
    public static boolean saveBitmap(Context context, Bitmap bitmap, String bitName) {
        String fileName;
        File file;
        String brand = Build.BRAND;

        if (brand.equals("xiaomi")) { // 小米手机brand.equals("xiaomi")
            fileName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/" + bitName;
        } else if (brand.equalsIgnoreCase("Huawei")) {
            fileName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/" + bitName;
        } else { // Meizu 、Oppo
            fileName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/" + bitName;
        }
//        fileName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/" + bitName;
        if (Build.VERSION.SDK_INT >= 29) {
//            boolean isTrue = saveSignImage(bitName, bitmap);
            saveSignImage(context, bitName, bitmap);
            return true;
//            file= getPrivateAlbumStorageDir(NewPeoActivity.this, bitName,brand);
//            return isTrue;
        } else {
            Log.v("saveBitmap brand", "" + brand);
            file = new File(fileName);
        }
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
// 格式为 JPEG，照相机拍出的图片为JPEG格式的，PNG格式的不能显示在相册中
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                out.flush();
                out.close();
// 插入图库
                if (Build.VERSION.SDK_INT >= 29) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                    Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                } else {
                    MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), bitName, null);

                }

            }
        } catch (FileNotFoundException e) {
            Log.e("FileNotFoundException", "FileNotFoundException:" + e.getMessage().toString());
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            Log.e("IOException", "IOException:" + e.getMessage().toString());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            Log.e("IOException", "IOException:" + e.getMessage().toString());
            e.printStackTrace();
            return false;

// 发送广播，通知刷新图库的显示

        }
//        if(Build.VERSION.SDK_INT >= 29){
//            copyPrivateToDownload(this,file.getAbsolutePath(),bitName);
//        }
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));

        return true;

    }


    //将文件保存到公共的媒体文件夹
//这里的filepath不是绝对路径，而是某个媒体文件夹下的子路径，和沙盒子文件夹类似
//这里的filename单纯的指文件名，不包含路径
    private static void saveSignImage(/*String filePath,*/Context context, String fileName, Bitmap bitmap) {
        try {
            //设置保存参数到ContentValues中
            ContentValues contentValues = new ContentValues();
            //设置文件名
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            //兼容Android Q和以下版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //android Q中不再使用DATA字段，而用RELATIVE_PATH代替
                //RELATIVE_PATH是相对路径不是绝对路径
                //DCIM是系统文件夹，关于系统文件夹可以到系统自带的文件管理器中查看，不可以写没存在的名字
                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/");
                //contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Music/signImage");
            } else {
                contentValues.put(MediaStore.Images.Media.DATA, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath());
            }
            //设置文件类型
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/JPEG");
            //执行insert操作，向系统文件夹中添加文件
            //EXTERNAL_CONTENT_URI代表外部存储器，该值不变
            Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            if (uri != null) {
                //若生成了uri，则表示该文件添加成功
                //使用流将内容写入该uri中即可
                OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                    outputStream.flush();
                    outputStream.close();
                }
            }
        } catch (Exception e) {
        }
    }

    static public String getBestString(String old) {
        String newStr = "";
        if (old == null) {
            newStr = "-";
        } else {
            newStr = old.replace("null", "-");
        }

        return newStr;

    }

}
