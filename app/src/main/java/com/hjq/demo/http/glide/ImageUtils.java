package com.hjq.demo.http.glide;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.hjq.demo.other.AppConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 图片处理的工具类
 *
 * @author yaozu
 */
public class ImageUtils {

    private static final String TAG = ImageUtils.class.getSimpleName();

    static public String getValidAvatar(String inputAvatar) {
        if (inputAvatar != null && inputAvatar.contains("thirdwx.qlogo.cn")) {
            inputAvatar = inputAvatar.replace("https://thirdwx.qlogo.cn", "http://wx.qlogo.cn");
        } else if (!inputAvatar.contains(AppConfig.INSTANCE.getHostUrl())) {
            inputAvatar =AppConfig.INSTANCE.getHostUrl()+ inputAvatar;
        }

        return inputAvatar;
    }

    /**
     * 合伙人  1   1
     * 设计师  1  1  粉丝量  1
     * 依据Uri获取路径
     *
     * @param contentUri
     * @return
     */
    public static String getRealPathByURI(Uri contentUri, Context context) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri,
                proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    /**
     * 创建一条图片地址uri,用于保存拍照后的照片
     *
     * @param context
     * @return 图片的uri
     */
    public static Uri createImagePathUri(Context context) {
        Uri imageFilePath = null;
        String status = Environment.getExternalStorageState();

        // ContentValues是我们希望这条记录被创建时包括的数据信息
        ContentValues values = new ContentValues(3);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, getImageName());
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        if (status.equals(Environment.MEDIA_MOUNTED)) {// 推断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
            imageFilePath = context.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } else {
            imageFilePath = context.getContentResolver().insert(
                    MediaStore.Images.Media.INTERNAL_CONTENT_URI, values);
        }
        Log.i("", "生成的照片输出路径：" + imageFilePath.toString());
        return imageFilePath;
    }

    static public String getImageName() {
        SimpleDateFormat timeFormatter = new SimpleDateFormat(
                "yyyyMMdd", Locale.CHINA);//_HHmmss
        long time = System.currentTimeMillis();
        String imageName = timeFormatter.format(new Date(time));
        return imageName;
    }

    /**
     * 图片压缩
     *
     * @param
     * @param file
     */
    public static void compressBmpToFile(File file, int height, int width) {
        Bitmap bmp = decodeSampledBitmapFromFile(file.getPath(), height, width);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
		/*while (baos.toByteArray().length / 1024 > 30) {
			baos.reset();
			if (options - 10 > 0) {
				options = options - 10;
				bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
			}
			if (options - 10 <= 0) {
				break;
			}
		}*/
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将图片变成bitmap
     *
     * @param path
     * @return
     */
    public static Bitmap getImageBitmap(String path) {
        Bitmap bitmap = null;
        File file = new File(path);
        if (file.exists()) {
            bitmap = BitmapFactory.decodeFile(path);
            return bitmap;
        }
        return null;
    }


//=================================图片压缩方法===============================================

    /**
     * 质量压缩
     *
     * @param image
     * @param maxkb
     * @return
     * @author ping 2015-1-5 下午1:29:58
     */
    public static Bitmap compressBitmap(Bitmap image, int maxkb) {
        //L.showlog(压缩图片);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 质量压缩方法，这里100表示不压缩。把压缩后的数据存放到baos中
        image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        int options = 100;
        // 循环推断假设压缩后图片是否大于(maxkb)50kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > maxkb) {
            // 重置baos即清空baos
            baos.reset();
            if (options - 10 > 0) {
                // 每次都降低10
                options -= 10;
            }
            // 这里压缩options%。把压缩后的数据存放到baos中
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        // 把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        // 把ByteArrayInputStream数据生成图片
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }

    /**
     * Bitmap转换成byte[]并且进行压缩,压缩到不大于maxkb
     *
     * @param bitmap
     * @param maxkb
     * @return
     */
    public static byte[] bmpToByteArray(Bitmap bitmap, final boolean needRecycle, int maxkb) {
        if (bitmap == null) {
            return null;
        }
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] result = null;
            int options = 50;
            bitmap.compress(Bitmap.CompressFormat.PNG, options, output);

            int realSize = output.toByteArray().length;

            options = maxkb / realSize;
            while (realSize > maxkb) {
                output.reset(); //清空output
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, output);
                if (options > 0) {
                    options /= 2;
                } else {
                    break;
                }
                realSize = output.toByteArray().length;
            }
            result = output.toByteArray();
            if (needRecycle) {
                bitmap.recycle();
            }
            output.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {

        int i;
        int j;
        if (bmp.getHeight() > bmp.getWidth()) {
            i = bmp.getWidth();
            j = bmp.getWidth();
        } else {
            i = bmp.getHeight();
            j = bmp.getHeight();
        }

        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);

        while (true) {
            localCanvas.drawBitmap(bmp, new Rect(0, 0, i, j), new Rect(0, 0, i, j), null);
            if (needRecycle)
                bmp.recycle();
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                return arrayOfByte;
            } catch (Exception e) {
                //F.out(e);
            }
            i = bmp.getHeight();
            j = bmp.getHeight();
        }
    }

    /**
     * @param res
     * @param resId
     * @param reqWidth  所需图片压缩尺寸最小宽度
     * @param reqHeight 所需图片压缩尺寸最小高度
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res,
                                                         int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * @param filepath  图片路径
     * @param reqWidth  所需图片压缩尺寸最小宽度
     * @param reqHeight 所需图片压缩尺寸最小高度
     * @return
     */
    public static Bitmap decodeSampledBitmapFromFile(String filepath, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filepath, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filepath, options);
    }

    /**
     * @param bitmap
     * @param reqWidth  所需图片压缩尺寸最小宽度
     * @param reqHeight 所需图片压缩尺寸最小高度
     * @return
     */
    public static Bitmap decodeSampledBitmapFromBitmap(Bitmap bitmap,
                                                       int reqWidth, int reqHeight) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, baos);
        byte[] data = baos.toByteArray();

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    /**
     * 计算压缩比例值(改进版 by touch_ping)
     * <p>
     * 原版2>4>8...倍压缩
     * 当前2>3>4...倍压缩
     *
     * @param options   解析图片的配置信息
     * @param reqWidth  所需图片压缩尺寸最小宽度O
     * @param reqHeight 所需图片压缩尺寸最小高度
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {

        final int picheight = options.outHeight;
        final int picwidth = options.outWidth;

        int targetheight = picheight;
        int targetwidth = picwidth;
        int inSampleSize = 1;

        if (targetheight > reqHeight || targetwidth > reqWidth) {
            while (targetheight >= reqHeight
                    && targetwidth >= reqWidth) {
                inSampleSize += 1;
                targetheight = picheight / inSampleSize;
                targetwidth = picwidth / inSampleSize;
            }
        }

        Log.i("===", "终于压缩比例:" + inSampleSize + "倍");
        Log.i("===", "新尺寸:" + targetwidth + "*" + targetheight);
        return inSampleSize;
    }

    // 读取图像的旋转度
    public static int readBitmapDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }


    /**
     * 将图片依照某个角度进行旋转
     *
     * @param bm     须要旋转的图片
     * @param degree 旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 依据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片依照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    /**
     * @param mBitmap
     * @param fileName
     */
    public static void saveBitmapToLocal(Bitmap mBitmap, String fileName) {
        if (mBitmap != null) {
            FileOutputStream fos = null;
            try {
                File file = new File(fileName);
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();
                fos = new FileOutputStream(file);
                mBitmap.compress(Bitmap.CompressFormat.PNG, 80, fos);
                fos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * 将下载下来的图片保存到SD卡或者本地.并返回图片的路径(包括文件命和扩展名)
     *
     * @param context
     * @param bitName
     * @param mBitmap
     * @return
     */
    public static String saveBitmap(Context context, String bitName, Bitmap mBitmap) {
        String path = null;
        File f;
        if (mBitmap != null) {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                f = new File(Environment.getExternalStorageDirectory() + File.separator + "images/");
                String fileName = Environment.getExternalStorageDirectory() + File.separator + "images/" + bitName + ".png";
                path = fileName;
                FileOutputStream fos = null;
                try {
                    if (!f.exists()) {
                        f.mkdirs();
                    }
                    File file = new File(fileName);
                    file.createNewFile();
                    fos = new FileOutputStream(file);
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    try {
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                //本地存储路径
                f = new File(context.getFilesDir() + File.separator + "images/");
                Log.i(TAG, "本地存储路径:" + context.getFilesDir() + File.separator + "images/" + bitName + ".png");
                path = context.getFilesDir() + File.separator + "images/" + bitName + ".png";
                FileOutputStream fos = null;
                try {
                    if (!f.exists()) {
                        f.mkdirs();
                    }
                    File file = new File(path);
                    file.createNewFile();
                    fos = new FileOutputStream(file);
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        return path;
    }

    /**
     * 删除图片
     *
     * @param context
     * @param bitName
     */
    public void deleteFile(Context context, String bitName) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File dirFile = new File(Environment.getExternalStorageDirectory() + File.separator + "images/" + bitName + ".png");
            if (!dirFile.exists()) {
                return;
            }

            dirFile.delete();
        } else {
            File f = new File(context.getFilesDir() + File.separator
                    + "images/" + bitName + ".png");
            if (!f.exists()) {
                return;
            }
            f.delete();
        }
    }

    public static Bitmap imageZoom(Bitmap bitMap) {
//图片允许最大空间 单位：KB
        double maxSize = 31.00;
//将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
//将字节换成KB
        double mid = b.length / 1024;
//判断bitmap占用空间是否大于允许最大空间 如果大于则压缩 小于则不压缩
        if (mid > maxSize) {
//获取bitmap大小 是允许最大大小的多少倍
            double i = mid / maxSize;
//开始压缩 此处用到平方根 将宽带和高度压缩掉对应的平方根倍 （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
            bitMap = zoomImage(bitMap, bitMap.getWidth() / Math.sqrt(i),
                    bitMap.getHeight() / Math.sqrt(i));
        }
        return bitMap;
    }


    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
                                   double newHeight) {
// 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
// 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
// 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
// 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }
}
