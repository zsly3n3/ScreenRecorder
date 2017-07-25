此项目是用于android 5.0 api 进行录制,分别录制屏幕和音效,最后合成mp4文件。

1.所用权限

android.permission.MOUNT_UNMOUNT_FILESYSTEMS

android.permission.RECORD_AUDIO

android.permission.WRITE_EXTERNAL_STORAGE

android.permission.READ_EXTERNAL_STORAGE
  

2.只需调用tools类,必须先要执行initRecorder方法。

  int initRecorder(String dirName,Activity activity) 没有则创建文件夹,传入Activity便于播放.返回功能是否可用,0为不可用
  
  int startRecorder() 返回功能是否可用,0为不可用
  
  void stopRecorder() 停止录制,合成视频
  
  String playVideo() 直接播放和返回字符串信息
  
  
  
