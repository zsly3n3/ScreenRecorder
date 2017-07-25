package com.example.sskj.myapplication.utils;

import android.content.Context;
import android.content.Intent;
import java.io.*;
import java.nio.ByteBuffer;


import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Handler;
import android.os.Looper;
import java.lang.Runnable;
//import android.util.Log;
//import android.widget.Toast;



/**
 * Created by Basti031 on 2015/11/23.
 */
public class MixureRecordManager {

    //是否开启麦克风录制
    private  boolean isRecordAudio;

    //录制状态
    private int RecordState;
    //录制状态枚举
    public static final int Finished = 0;
    public static final int Recording = 1;
    public static final int Muxing =2;

    private static final String suffix_audio = ".amr";
    public static final String suffix_video = ".mp4";
    public static final String suffix_muxfile = "_mux.mp4";


    private  String currentFileID ="";//保存当前文件ID

    public  Context context;
    private AudioManager audioManager;
    private ScreenRecordManager screenRecordManager;

    private static MixureRecordManager mInstance;

    //private  static String TAG = "zsly3n";



    private MixureRecordManager(VideoSize size,Context context){
        setRecordState(Finished);
        audioManager = AudioManager.getInstance();
        screenRecordManager = ScreenRecordManager.getInstance(size,context);
    }

    public static MixureRecordManager getInstance(VideoSize size,Context context){
        if (mInstance == null){
            synchronized (MixureRecordManager.class){}
            if (mInstance == null){
                mInstance = new MixureRecordManager(size,context);
            }
        }
        return mInstance;
    }

    //录屏获取权限
    public void getScreenRecordPermission(){
        screenRecordManager.getPermission();
    }

    public void startRecord(int resultcode ,Intent data){
        setRecordState(Recording);
        currentFileID = RecordManager.genUUID();
        String videoName = currentFileID + suffix_video;
        String dir = RecordManager.getPath();
        if (isRecordAudio){
            String audioName = currentFileID + suffix_audio;
            audioManager.prepareAudio(dir,audioName);
        }
        screenRecordManager.startRecord(dir,videoName,resultcode,data);
    }

    public void stopRecord(){
        setRecordState(Muxing);
        audioManager.release();
        screenRecordManager.stopRecord();
        if (isRecordAudio&&!currentFileID.equals("")){
            String dir = RecordManager.getPath();
            String audioName = currentFileID + suffix_audio;
            String videoName = currentFileID + suffix_video;
            File audio_file = new File(dir,audioName);
            File video_file = new File(dir,videoName);
            if(audio_file.exists()&&video_file.exists()&&RecordManager.getFileSize(audio_file) != 0&&RecordManager.getFileSize(video_file) != 0){
                mux();
            }
            else{
                setRecordState(Finished);
            }
        }
    }


    /**
     * 获取录制状态
     */
    public int getRecordState(){
        return RecordState;
    }
    /**
     * 是否开启麦克风录制
     */
    public void setRecordAudio(boolean tf){
        isRecordAudio = tf;
    }

    private void mux() {
        new Thread(new Runnable(){
            public void run(){
                try {
                    Thread.sleep(1000);//1秒
                    muxing(screenRecordManager.fileAbsolutePath, audioManager.fileAbsolutePath);
                } catch (InterruptedException e) {
                    String muxFilePath = RecordManager.getPath() + "/" +currentFileID+suffix_muxfile;
                    String[] paths = {audioManager.fileAbsolutePath,muxFilePath};
                    deleteFiles(paths);
                    setRecordState(Finished);
                }
            }
        }).start();


    }

    private void muxing(String videoFilePath, String audioFilePath) {
        String [] paths = new String[2];
        String outputFile = "";
        try {
            String muxFileName = currentFileID+suffix_muxfile;
            File file = new File(RecordManager.getPath(),muxFileName);
            file.createNewFile();
            outputFile = file.getAbsolutePath();


            MediaExtractor videoExtractor = new MediaExtractor();
            videoExtractor.setDataSource(videoFilePath);

            MediaExtractor audioExtractor = new MediaExtractor();
            audioExtractor.setDataSource(audioFilePath);

            // Log.d(TAG, "Video Extractor Track Count " + videoExtractor.getTrackCount());
            // Log.d(TAG, "Audio Extractor Track Count " + audioExtractor.getTrackCount());

            MediaMuxer muxer = new MediaMuxer(outputFile, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

            videoExtractor.selectTrack(0);
            MediaFormat videoFormat = videoExtractor.getTrackFormat(0);
            int videoTrack = muxer.addTrack(videoFormat);

            audioExtractor.selectTrack(0);
            MediaFormat audioFormat = audioExtractor.getTrackFormat(0);
            int audioTrack = muxer.addTrack(audioFormat);



            boolean sawEOS = false;
            int frameCount = 0;
            int offset = 100;
            int sampleSize = 256 * 1024;
            ByteBuffer videoBuf = ByteBuffer.allocate(sampleSize);
            ByteBuffer audioBuf = ByteBuffer.allocate(sampleSize);
            MediaCodec.BufferInfo videoBufferInfo = new MediaCodec.BufferInfo();
            MediaCodec.BufferInfo audioBufferInfo = new MediaCodec.BufferInfo();


            videoExtractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
            audioExtractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC);

            muxer.start();

            while (!sawEOS) {
                videoBufferInfo.offset = offset;
                videoBufferInfo.size = videoExtractor.readSampleData(videoBuf, offset);


                if (videoBufferInfo.size < 0 || audioBufferInfo.size < 0) {

                    sawEOS = true;
                    videoBufferInfo.size = 0;

                } else {
                    videoBufferInfo.presentationTimeUs = videoExtractor.getSampleTime();
                    videoBufferInfo.flags = videoExtractor.getSampleFlags();
                    muxer.writeSampleData(videoTrack, videoBuf, videoBufferInfo);
                    videoExtractor.advance();

                    frameCount++;

                }
            }

            //Toast.makeText(context, "frame:" + frameCount, Toast.LENGTH_SHORT).show();


            boolean sawEOS2 = false;
            int frameCount2 = 0;
            while (!sawEOS2) {
                frameCount2++;

                audioBufferInfo.offset = offset;
                audioBufferInfo.size = audioExtractor.readSampleData(audioBuf, offset);

                if (videoBufferInfo.size < 0 || audioBufferInfo.size < 0) {

                    sawEOS2 = true;
                    audioBufferInfo.size = 0;
                } else {
                    audioBufferInfo.presentationTimeUs = audioExtractor.getSampleTime();
                    audioBufferInfo.flags = audioExtractor.getSampleFlags();
                    muxer.writeSampleData(audioTrack, audioBuf, audioBufferInfo);
                    audioExtractor.advance();
                }
            }

//            Toast toast = Toast.makeText(context, "frame:" + frameCount2, Toast.LENGTH_LONG);
//            showToast(toast,100);

            muxer.stop();
            muxer.release();

            paths[0]=screenRecordManager.fileAbsolutePath;
            paths[1]=audioManager.fileAbsolutePath;
            //Log.d(TAG, "finished try mux");
        } catch (IOException e) {
            //Log.d(TAG, "Mixer Error 1 " + e.getMessage());
            paths[0]=audioManager.fileAbsolutePath;
            paths[1]=outputFile;
        } catch (Exception e) {
            //Log.d(TAG, "Mixer Error 2 " + e.getMessage());
            paths[0]=audioManager.fileAbsolutePath;
            paths[1]=outputFile;
        }

        deleteFiles(paths);
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                //已在主线程中，可以更新UI
                setRecordState(Finished);
            }
        });
        //Log.d(TAG, "finished mux");
    }

    /**
     * 设置录制状态
     */
    private void setRecordState(int state){
        RecordState = state;
    }

    /**
     * 删除指定文件
     */
    private static void deleteFiles(String[] paths) {
        for (String path : paths) {
            File file = new File(path);
            if (file.exists()){
                file.delete();
            }
        }
    }

    private boolean isMainThread() {
        return Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId();
    }

}


