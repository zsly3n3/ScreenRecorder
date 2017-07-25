package com.example.sskj.myapplication;


import android.app.Activity;
import android.content.Intent;

import com.example.sskj.myapplication.utils.RecordManager;

public class tools {
    public static Activity mActivity = null;


    //录制前初始化数据,返回功能是否可用
    public static int initRecorder(String dirName,Activity activity){
        mActivity = activity;
        boolean tf = RecordManager.isAvailable();
        if(tf){
            RecordManager.initData(mActivity,true,dirName);
        }
        int rs = tf ? 1 : 0;
        return  rs;
    }
    //开始录制获取权限,返回功能是否可用
    public static int startRecorder(){
        boolean tf = RecordManager.isAvailable();
        if(tf){
            RecordManager.getPermission();
        }
        int rs = tf ? 1 : 0;
        return rs;
    }
    //结束录制
    public static void stopRecorder(){
        boolean tf = RecordManager.isAvailable();
        if(tf){
            RecordManager.stopRecorder();
        }
    }
    //播放视频
    public static String playVideo(){
        String rs = "";
        boolean tf = RecordManager.isAvailable();
        if(tf){
            rs = RecordManager.playLastAudio(mActivity);
        }
        return rs;
    }

    public static void startRecorder(int resultCode, Intent data){
        RecordManager.startRecorder(resultCode,data);
    }
}
