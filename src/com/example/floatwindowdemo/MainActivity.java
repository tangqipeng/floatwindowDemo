package com.example.floatwindowdemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;

import com.example.floatwindowdemo.service.AppStatusService;

public class MainActivity extends Activity {

	public static Point phonePoint;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		phonePoint = getHeightAndWidth(this);
		
		/**
		 * 启动监听系统运行状态的service
		 */
		Intent service = new Intent();
		service.setClass(MainActivity.this, AppStatusService.class);	
		startService(service);
	}

	public  Point getHeightAndWidth(Activity activity){
		int width = activity.getWindowManager().getDefaultDisplay().getWidth();
		int height = activity.getWindowManager().getDefaultDisplay().getHeight();
		Point size = new Point(width,height);
		return size;
	}
}
