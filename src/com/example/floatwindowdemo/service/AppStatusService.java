package com.example.floatwindowdemo.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * 
 * @author     : 李凯(LiK)
 * @createDate �?015-6-11 下午6:03:19
 * @description:【功能描述�?通过监听线程来获取app状�?
 */
public class AppStatusService extends Service {
    private static final String TAG = "AppStatusService";  
    private ActivityManager activityManager;  
    private String packageName;  
  
    @Override  
    public IBinder onBind(Intent intent) {  
        return null;  
    }  
  
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {  
//        activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);  
        packageName = this.getPackageName();  
        new Thread() {  
            public void run() {  
                try {  
                    while (true) { 
                    	//线程循环休眠时间越短 循环越快，程序运行到后台时与购物车悬浮窗消失的时间差越短
                        Thread.sleep(100);  
//                        if (isAppOnForeground()) {  
                        if (isFrount(getApplicationContext())) {
                            Log.i(TAG, "true");  
                        } else {  
                            Log.i(TAG, "false");  
                        }  
                    }  
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
            }  
        }.start();  
        return super.onStartCommand(intent, flags, startId);  
    }  

    public  boolean isFrount(Context context) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())) {
				Log.i(TAG, "此appimportace ="
						+ appProcess.importance
						+ ",context.getClass().getName()="
						+ context.getClass().getName());
				if (appProcess.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					Log.i(TAG, "处于后台"
							+ appProcess.processName);
					Intent service = new Intent(getApplicationContext(),FloatService.class);
					stopService(service);
					Log.i(TAG, "处于后台购物车Service停止");
					return false;
				} else {
					Log.i(TAG, "处于前台"
							+ appProcess.processName);
					Intent service = new Intent(getApplicationContext(),FloatService.class);
					startService(service);
					Log.i(TAG, "处于前台购物车Service启动");
					return true;
				}
			}
		}
		return false;
	}
}
