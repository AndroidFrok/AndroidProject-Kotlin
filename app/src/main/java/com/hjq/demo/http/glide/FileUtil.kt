package com.hjq.demo.http.glide

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import java.util.UUID

object FileUtil {

    private var fileSave: FileSaveListener? = null
    private var mContext: Context? = null
    private var filePath: String? = null
    private var mBitmap: Bitmap? = null
    private val TAG = "PictureActivity"
    private var mSaveDialog: ProgressDialog? = null

    fun downloadImg(context: Context, filePaths: String, fileSaveListener: FileSaveListener) {
        fileSave = fileSaveListener
        filePath = filePaths
        mContext = context
        mSaveDialog = ProgressDialog.show(context, "保存图片", "请稍候", true)
        Thread(saveFileRunnable).start()
    }

    fun getFileName(pathandname: String): String? {
        val start = pathandname.lastIndexOf("/")
        val end = pathandname.lastIndexOf(".")
        return if (start != -1 && end != -1) {
            pathandname.substring(start + 1, pathandname.length)
        } else {
            null
        }
    }

    private val saveFileRunnable = Runnable {
        var f: File? = null
        try {
            if (!TextUtils.isEmpty(filePath)) {
                val url = URL(filePath)
                val inputStream: InputStream = url.openStream()
                mBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
            }
            f = saveFile(mContext!!, mBitmap!!)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val m = Message()
        m.obj = f
        messageHandler.sendMessage(m)
        fileSave?.saved(f)
    }

    private val messageHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            mSaveDialog?.dismiss()
        }
    }

    @Throws(IOException::class)
    fun saveFile(context: Context, bm: Bitmap): File {
        val dirFile = File(Environment.getExternalStorageDirectory().path)
        if (!dirFile.exists()) {
            dirFile.mkdir()
        }
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val myCaptureFile = File(Environment.getExternalStorageDirectory().path + "/DCIM/Camera/" + fileName)
        BufferedOutputStream(FileOutputStream(myCaptureFile)).use { bos ->
            bm.compress(Bitmap.CompressFormat.JPEG, 80, bos)
            bos.flush()
        }
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val uri = Uri.fromFile(myCaptureFile)
        intent.data = uri
        context.sendBroadcast(intent)
        return myCaptureFile
    }

    fun saveBitmap(context: Context, bitmap: Bitmap, bitName: String): Boolean {
        val fileName: String
        val file: File
        val brand = Build.BRAND
        fileName = when {
            brand == "xiaomi" || brand.equals("Huawei", ignoreCase = true) -> {
                Environment.getExternalStorageDirectory().path + "/DCIM/Camera/" + bitName
            }
            else -> {
                Environment.getExternalStorageDirectory().path + "/DCIM/" + bitName
            }
        }
        if (Build.VERSION.SDK_INT >= 29) {
            saveSignImage(context, bitName, bitmap)
            return true
        } else {
            Log.v("saveBitmap brand", "" + brand)
            file = File(fileName)
        }
        if (file.exists()) {
            file.delete()
        }
        return try {
            FileOutputStream(file).use { out ->
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                    out.flush()
                    out.close()
                    if (Build.VERSION.SDK_INT >= 29) {
                        val values = ContentValues()
                        values.put(MediaStore.Images.Media.DATA, file.absolutePath)
                        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                        context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                    } else {
                        MediaStore.Images.Media.insertImage(context.contentResolver, file.absolutePath, bitName, null)
                    }
                }
            }
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://$fileName")))
            true
        } catch (e: FileNotFoundException) {
            Log.e("FileNotFoundException", "FileNotFoundException:" + e.message.toString())
            e.printStackTrace()
            false
        } catch (e: IOException) {
            Log.e("IOException", "IOException:" + e.message.toString())
            e.printStackTrace()
            false
        } catch (e: Exception) {
            Log.e("IOException", "IOException:" + e.message.toString())
            e.printStackTrace()
            false
        }
    }

    private fun saveSignImage(context: Context, fileName: String, bitmap: Bitmap) {
        try {
            val contentValues = ContentValues()
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/")
            } else {
                contentValues.put(MediaStore.Images.Media.DATA, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path)
            }
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/JPEG")
            val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                    outputStream.flush()
                    outputStream.close()
                }
            }
        } catch (e: Exception) {
            // Handle exception
        }
    }

    fun getBestString(old: String?): String {
        return if (old == null) {
            "-"
        } else {
            old.replace("null", "-")
        }
    }
}
