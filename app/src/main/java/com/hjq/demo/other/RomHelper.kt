package com.hjq.demo.other

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import timber.log.Timber

class RomHelper {

    companion object {

        fun getInstalledAppList(ctx: Context) {
            val list = ctx.packageManager.getInstalledPackages(PackageManager.MATCH_APEX)
            val myList = mutableListOf<PackageInfo?>()
            val preFix = "com.android";
            val preFix1 = "com.mediatek";
            val preFix2 = "com.google";
            val preFix3 = "com.wapi";
            val preFix4 = "android";
            val preFix5 = "com.incar";
//        过滤掉系统应用
            for (app in list) {
                val appName = ctx.packageManager.getApplicationLabel(app.applicationInfo!!)
                val pkg = app.packageName;
                val isAndroid = pkg.startsWith(preFix);
                val isGoogle = pkg.startsWith(preFix2);
                val isMediatek = pkg.startsWith(preFix1);
                val is3 = pkg.startsWith(preFix3);
                val is4 = pkg.startsWith(preFix4);
                val is5 = pkg.startsWith(preFix5);
                val preInstall =
                    app.applicationInfo!!.flags and ApplicationInfo.FLAG_SYSTEM !== 0;// preInstall = true 是预装
                Timber.i("$pkg");
                if (preInstall && !isAndroid && !isGoogle && !isMediatek && !is3 && !is4 && !is5) {
                    myList.add(app)
                    Timber.w("OK : ${appName}")
                } else {
                    Timber.w("过滤了: ${appName}")
                }
            }

        }

    }
}