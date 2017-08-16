package com.example.shang.crashtest;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shang on 2017/8/16.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/CrashTest/log/";
    private static final String FILE_NAME = "crash";
    private static final String FILE_NAME_SUFFIX = ".trace";

    private static final String TAG = "CrashHandler";
    private static CrashHandler sInstance = new CrashHandler();

    private Context mContext;


    static CrashHandler getInstance(){
        return sInstance;
    }

    void init(Context context){
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }


    @Override
    public void uncaughtException(Thread t, Throwable e) {
        try {
            // 导出异常信息到sdcard中
            dumpExceptionToSDCard(e);
            // 上次到服务器，有开发人员分析日志从而解决异常
            uploadExceptionToServer();
        }catch (IOException e1){
            e1.printStackTrace();
        }

    }

    private void dumpExceptionToSDCard(Throwable e) throws IOException {
        // 如果没有sdcard或者无法使用
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.w(TAG,"sdcard unmounted,skip dump exception");
            return;
        }
        File dir = new File(PATH);
        Log.i("xyz","dir = "+ dir);
        if (!dir.exists()){
            dir.mkdirs();
        }
        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(current));
        File file = new File(PATH+FILE_NAME+time+FILE_NAME_SUFFIX);
        Log.i("xyz","file = "+ file);
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            pw.println(time);
            dumpPhoneInfo(pw);
            pw.println();
            e.printStackTrace(pw);
            pw.close();
        } catch (Exception e1) {
            Log.e(TAG, "dump crash info failed");
        }

    }

    private void dumpPhoneInfo(PrintWriter pw) throws PackageManager.NameNotFoundException {
        PackageManager packageManager = mContext.getPackageManager();
        PackageInfo info = packageManager.getPackageInfo(mContext.getPackageName(),PackageManager.GET_ACTIVITIES);
        // App 版本号
        pw.print("App Version: ");
        pw.print(info.versionName);
        pw.print("_");
        pw.println(info.versionCode);
        // Android 版本号
        pw.print("OS Version: ");
        pw.print(Build.VERSION.RELEASE);
        pw.print("_");
        pw.println(Build.VERSION.SDK_INT);
        // 手机制造商
        pw.print("Vendor: ");
        pw.println(Build.MANUFACTURER);
        // 手机型号
        pw.print("Model: ");
        pw.println(Build.MODEL);
        // CPU 架构
        pw.print("CPU ABI: ");
        pw.println(Build.CPU_ABI);

    }


    private void uploadExceptionToServer() {
        // TODO Something
    }
}
