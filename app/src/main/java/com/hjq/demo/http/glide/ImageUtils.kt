package com.hjq.demo.http.glide

import android.content.ContentValues
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Rect
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.hjq.demo.other.AppConfig
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ImageUtils {

    private const val TAG = "ImageUtils"

    fun getValidAvatar(inputAvatar: String?): String {
        if (inputAvatar != null && inputAvatar.contains("thirdwx.qlogo.cn")) {
            return inputAvatar.replace("https://thirdwx.qlogo.cn", "http://wx.qlogo.cn")
        } else if (inputAvatar != null && !inputAvatar.contains(AppConfig.hostUrl)) {
            return AppConfig.hostUrl + inputAvatar
        }
        return inputAvatar ?: ""
    }

    fun getRealPathByURI(contentUri: Uri, context: Context): String? {
        val res: String?
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = context.contentResolver.query(contentUri, proj, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val column_index = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                res = it.getString(column_index)
                return res
            }
        }
        return null
    }

    fun createImagePathUri(context: Context): Uri? {
        val status = Environment.getExternalStorageState()
        val values = ContentValues(3).apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, getImageName())
            put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        }
        return if (status == Environment.MEDIA_MOUNTED) {
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        } else {
            context.contentResolver.insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, values)
        }
    }

    fun getImageName(): String {
        val timeFormatter = SimpleDateFormat("yyyyMMdd", Locale.CHINA)
        val time = System.currentTimeMillis()
        return timeFormatter.format(Date(time))
    }

    fun compressBmpToFile(file: File, height: Int, width: Int) {
        val bmp = decodeSampledBitmapFromFile(file.path, height, width)
        val baos = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        try {
            FileOutputStream(file).use { fos ->
                fos.write(baos.toByteArray())
                fos.flush()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getImageBitmap(path: String): Bitmap? {
        val file = File(path)
        return if (file.exists()) {
            BitmapFactory.decodeFile(path)
        } else {
            null
        }
    }

    fun compressBitmap(image: Bitmap, maxkb: Int): Bitmap {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        var options = 100
        while (baos.toByteArray().size / 1024 > maxkb) {
            baos.reset()
            if (options - 10 > 0) {
                options -= 10
            }
            image.compress(Bitmap.CompressFormat.JPEG, options, baos)
        }
        val isBm = ByteArrayInputStream(baos.toByteArray())
        return BitmapFactory.decodeStream(isBm, null, null)
    }

    fun bmpToByteArray(bitmap: Bitmap?, needRecycle: Boolean, maxkb: Int): ByteArray? {
        if (bitmap == null) return null
        return try {
            val output = ByteArrayOutputStream()
            var options = 50
            bitmap.compress(Bitmap.CompressFormat.PNG, options, output)
            var realSize = output.toByteArray().size
            options = maxkb / realSize
            while (realSize > maxkb) {
                output.reset()
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, output)
                if (options > 0) {
                    options /= 2
                } else {
                    break
                }
                realSize = output.toByteArray().size
            }
            val result = output.toByteArray()
            if (needRecycle) {
                bitmap.recycle()
            }
            output.close()
            result
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun bmpToByteArray(bmp: Bitmap, needRecycle: Boolean): ByteArray {
        val i: Int
        val j: Int
        if (bmp.height > bmp.width) {
            i = bmp.width
            j = bmp.width
        } else {
            i = bmp.height
            j = bmp.height
        }
        val localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565)
        val localCanvas = Canvas(localBitmap)
        localCanvas.drawBitmap(bmp, Rect(0, 0, i, j), Rect(0, 0, i, j), null)
        if (needRecycle) bmp.recycle()
        val localByteArrayOutputStream = ByteArrayOutputStream()
        localBitmap.compress(Bitmap.CompressFormat.JPEG, 100, localByteArrayOutputStream)
        localBitmap.recycle()
        val arrayOfByte = localByteArrayOutputStream.toByteArray()
        return try {
            localByteArrayOutputStream.close()
            arrayOfByte
        } catch (e: Exception) {
            arrayOfByte
        }
    }

    fun decodeSampledBitmapFromResource(res: Resources, resId: Int, reqWidth: Int, reqHeight: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(res, resId, options)
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeResource(res, resId, options)
    }

    fun decodeSampledBitmapFromFile(filepath: String, reqWidth: Int, reqHeight: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filepath, options)
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(filepath, options)
    }

    fun decodeSampledBitmapFromBitmap(bitmap: Bitmap, reqWidth: Int, reqHeight: Int): Bitmap {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, baos)
        val data = baos.toByteArray()
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeByteArray(data, 0, data.size, options)
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeByteArray(data, 0, data.size, options)
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val picheight = options.outHeight
        val picwidth = options.outWidth
        var targetheight = picheight
        var targetwidth = picwidth
        var inSampleSize = 1
        if (targetheight > reqHeight || targetwidth > reqWidth) {
            while (targetheight >= reqHeight && targetwidth >= reqWidth) {
                inSampleSize += 1
                targetheight = picheight / inSampleSize
                targetwidth = picwidth / inSampleSize
            }
        }
        Log.i("===", "终于压缩比例:$inSampleSize 倍")
        Log.i("===", "新尺寸:$targetwidth*$targetheight")
        return inSampleSize
    }

    fun readBitmapDegree(path: String): Int {
        var degree = 0
        try {
            val exifInterface = ExifInterface(path)
            val orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            degree = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return degree
    }

    fun rotateBitmapByDegree(bm: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val returnBm = try {
            Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, matrix, true)
        } catch (e: OutOfMemoryError) {
            null
        }
        val result = if (returnBm == null) bm else returnBm
        if (bm != result) {
            bm.recycle()
        }
        return result
    }

    fun saveBitmapToLocal(mBitmap: Bitmap, fileName: String) {
        try {
            val file = File(fileName)
            if (file.exists()) {
                file.delete()
            }
            file.createNewFile()
            FileOutputStream(file).use { fos ->
                mBitmap.compress(Bitmap.CompressFormat.PNG, 80, fos)
                fos.flush()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveBitmap(context: Context, bitName: String, mBitmap: Bitmap): String? {
        var path: String? = null
        if (mBitmap != null) {
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
                val f = File(Environment.getExternalStorageDirectory().toString() + File.separator + "images/")
                val fileName = Environment.getExternalStorageDirectory().toString() + File.separator + "images/" + bitName + ".png"
                path = fileName
                try {
                    if (!f.exists()) {
                        f.mkdirs()
                    }
                    val file = File(fileName)
                    file.createNewFile()
                    FileOutputStream(file).use { fos ->
                        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                        fos.flush()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                val f = File(context.filesDir.toString() + File.separator + "images/")
                Log.i(TAG, "本地存储路径:" + context.filesDir + File.separator + "images/" + bitName + ".png")
                path = context.filesDir.toString() + File.separator + "images/" + bitName + ".png"
                try {
                    if (!f.exists()) {
                        f.mkdirs()
                    }
                    val file = File(path)
                    file.createNewFile()
                    FileOutputStream(file).use { fos ->
                        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                        fos.flush()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return path
    }

    fun deleteFile(context: Context, bitName: String) {
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            val dirFile = File(Environment.getExternalStorageDirectory().toString() + File.separator + "images/" + bitName + ".png")
            if (dirFile.exists()) {
                dirFile.delete()
            }
        } else {
            val f = File(context.filesDir.toString() + File.separator + "images/" + bitName + ".png")
            if (f.exists()) {
                f.delete()
            }
        }
    }

    fun imageZoom(bitMap: Bitmap): Bitmap {
        val maxSize = 31.00
        val baos = ByteArrayOutputStream()
        bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        val mid = b.size / 1024.0
        return if (mid > maxSize) {
            val i = mid / maxSize
            zoomImage(bitMap, bitMap.width / Math.sqrt(i), bitMap.height / Math.sqrt(i))
        } else {
            bitMap
        }
    }

    fun zoomImage(bgimage: Bitmap, newWidth: Double, newHeight: Double): Bitmap {
        val width = bgimage.width.toFloat()
        val height = bgimage.height.toFloat()
        val matrix = Matrix()
        val scaleWidth = (newWidth / width).toFloat()
        val scaleHeight = (newHeight / height).toFloat()
        matrix.postScale(scaleWidth, scaleHeight)
        return Bitmap.createBitmap(bgimage, 0, 0, width.toInt(), height.toInt(), matrix, true)
    }

    fun isDark(context: Context): Boolean {
        return (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }
}
