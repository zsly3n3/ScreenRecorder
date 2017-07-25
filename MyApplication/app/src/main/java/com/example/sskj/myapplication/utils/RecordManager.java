package com.example.sskj.myapplication.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

/**
 * Created by Sskj on 2017/7/11.
 */

public  class RecordManager {


    public static MixureRecordManager mixureRecordManager = null;

    private static String mDir = "";

    /**
     * 游戏录屏是否可用
     */
    public static boolean isAvailable(){
        boolean tf = true;
        int sdkVersion;
        try {
            sdkVersion = Integer.valueOf(Build.VERSION.SDK_INT);
        } catch (NumberFormatException e) {
            sdkVersion = 0;
        }
        if(sdkVersion<21){
            tf = false;
        }
        return  tf;
    }



    /**
     * 获取相应权限
     */
    public static void getPermission(){
        if(mixureRecordManager != null){
            mixureRecordManager.getScreenRecordPermission();
        }
    }



    /**
     * 开始录制
     */
    public static void startRecorder(int resultCode, Intent data){
        if(mixureRecordManager != null){
            RecordManager.cleanCache();

            mixureRecordManager.startRecord(resultCode,data);
        }
    }

    /**
     * 停止录制
     */
    public static void stopRecorder(){
        if(mixureRecordManager != null){
            mixureRecordManager.stopRecord();
        }
    }

    /**
     * 播放最近一次的视频
     */
    public static String playLastAudio(Activity activity){
        String str = "";
        if(mixureRecordManager != null) {
            int state = mixureRecordManager.getRecordState();
            switch (state){
                case MixureRecordManager.Recording:
                    str = "视频录制中..";
                    break;
                case MixureRecordManager.Muxing:
                    str = "视频处理中..请稍后";
                    break;
            }
            if (str.equals("")) {
                str = play(activity);
            }
        }else{
            str =  play(activity);
        }
        return str;
    }

    public static String play(Activity activity){
        String str = "";
        String path = RecordManager.getVideoPath("_mux.mp4");
        if(path.equals("")){
            path =  RecordManager.getVideoPath(".mp4");
        }
        if(!path.equals("")){
            Uri uri = Uri.parse(path);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "video/mp4");
            activity.startActivity(intent);
        }else{
            str = "没有视频可提供播放";
        }
        return str;
    }

    /**
     * 创建UUID
     */
    public static String genUUID(){
        UUID uuid = UUID.randomUUID();
        return  uuid.toString();
    }

    /**
     * 获取文件夹路径
     */
    public static String getPath(){
        return  mDir;
    }
    
    /**
     *  初始化一些单例数据,文件夹等
     */
    public static void initData(Context context,boolean isRecordMic,String dirName){
        mDir = Environment.getExternalStorageDirectory().getPath()+"/"+dirName;
        File path1 = new File(mDir);
        if (!path1.exists()) {
            //若不存在，创建目录，可以在应用启动的时候创建
            path1.mkdirs();
        }
        mixureRecordManager = MixureRecordManager.getInstance(new VideoSize(),context);
        mixureRecordManager.setRecordAudio(isRecordMic);
    }

    /**
     * 清除本地缓存
     */
    public static void cleanCache() {
        File dir = new File(mDir);
        if (dir.exists()){
            for (File file : dir.listFiles()) {
                file.delete();
            }
        }
    }

    public static String getVideoPath(String temp) {
        String path = "";
        File dir = new File(mDir);
        if (dir.exists()){
            for (File file : dir.listFiles()) {
                String file_path = file.getAbsolutePath();
                if (file.isFile()&&RecordManager.getFileSize(file)!=0&&file_path.indexOf(temp)>=0){
                    path = file_path;
                    break;
                }
            }
        }
        return path;
    }

    /**
     * 获取指定文件大小　
     */
    public static long getFileSize(File file)
    {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                size = fis.available();
            }catch(Exception e){
            }
        }
        if (size == 0){
            file.delete();
        }
        return size;
    }
}
