����Ŀ������android 5.0 api ����¼��,�ֱ�¼����Ļ����Ч,���ϳ�mp4�ļ���

1.����Ȩ��
  <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>
  <uses-permission android:name="android.permission.RECORD_AUDIO"/>
  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
  <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>

2.ֻ�����tools��,������Ҫִ��initRecorder������
  int initRecorder(String dirName,Activity activity) û���򴴽��ļ���,����Activity���ڲ���.���ع����Ƿ����,0Ϊ������
  int startRecorder() ���ع����Ƿ����,0Ϊ������
  void stopRecorder() ֹͣ¼��,�ϳ���Ƶ
  String playVideo() ֱ�Ӳ��źͷ����ַ�����Ϣ
  
  