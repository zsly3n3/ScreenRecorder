package com.example.sskj.myapplication.utils;

import android.media.MediaRecorder;
//import android.util.Log;
import java.io.File;
import java.io.IOException;

/**
 * 录音工具类
 * Created by Basti031 on 2015/11/23.
 */
public class AudioManager {

    private MediaRecorder mMediaRecorder;

    private static AudioManager mInstance;

    public String fileAbsolutePath;

    //单例模式
    public static AudioManager getInstance(){
        if (mInstance== null){
            synchronized (AudioManager.class){
                if (mInstance == null){
                    mInstance = new AudioManager();
                }
            }
        }
        return mInstance;
    }

    //AudioManager准备
    public void prepareAudio(String mDir,String mFileName){
        try {

            File file = new File(mDir,mFileName);
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setOutputFile(file.getAbsolutePath());
            fileAbsolutePath = file.getAbsolutePath();
            //来源
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            //格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            //编码方式
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public void release(){
        if (mMediaRecorder!=null){
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

}
