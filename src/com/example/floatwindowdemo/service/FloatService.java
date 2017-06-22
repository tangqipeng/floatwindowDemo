package com.example.floatwindowdemo.service;


import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.floatwindowdemo.MainActivity;
import com.example.floatwindowdemo.MyApplication;
import com.example.floatwindowdemo.R;

/**
 * 
 * @author     : 李凯(LiK)
 * @createDate �?015-6-12 上午9:06:56
 * @description:【功能描述�?购物车悬浮窗
 */
public class FloatService extends Service {

	WindowManager wm = null;
	WindowManager.LayoutParams wmParams = null;
	View view;//悬浮窗view
	private float mTouchStartX;
	private float mTouchStartY;
	private float x;
	private float y;
	int state;
	ImageView iv;
	private float StartX;
	private float StartY;
	int delaytime=1000;//延迟时间
	@Override
	public void onCreate() {
		Log.d("FloatService", "onCreate");
		super.onCreate();
		view = LayoutInflater.from(this).inflate(R.layout.floating, null);
		iv = (ImageView) view.findViewById(R.id.img2);
		iv.setVisibility(View.GONE);
		createView();
		handler.postDelayed(task, delaytime);//延时delayMillis毫秒 将Runnable插入消息列队�?
											//Runnable将在handle绑定的线程中运行�?
	}

	private void createView() {
		
		//MODE_APPEND 的功能是判断是否有该文件，如果有在后面添加，而不是擦�?
        //MODE_PRIVATE 该文件只能被创建他的应用访问（控制访问权限）
		SharedPreferences shared = getSharedPreferences("float_flag",
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = shared.edit();
		editor.putInt("float", 1);
		editor.commit();
		// 获取WindowManager
		wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		// 设置LayoutParams(全局变量）相关参�?
		wmParams = ((MyApplication) getApplication()).getMywmParams();
		wmParams.type = 2002;//获取系统级窗�?
		wmParams.flags = 8;//flags = 8 �?flags = FLAG_NOT_FOCUSABLE 表示不能获取不能获得按键输入焦点
		                   //设置了WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE，弹出的View收不到Back键的事件
		//此处悬浮窗的位置必须为左上角，�?过更改wmParams.x，y的初始�?来设置最终view首次显示的位�?
		wmParams.gravity = Gravity.LEFT | Gravity.TOP; // 调整悬浮窗口至左上角
		
		
		// 以屏幕左上角为原点，设置x、y初始�?
		wmParams.x = MainActivity.phonePoint.x;
		wmParams.y = MainActivity.phonePoint.y/2;
		// 设置悬浮窗口长宽数据
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.format = 1;
		wm.addView(view, wmParams);

		view.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
			
				DisplayMetrics dm = new DisplayMetrics();
			 	// 获取相对屏幕的坐标，即以屏幕左上角为原点
				x = event.getRawX();
				y = event.getRawY() - 25; // 25是系统状态栏的高�?
				Log.i("lk", "currP"+"currX" + x + "====currY" + y);// 调试信息
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					state = MotionEvent.ACTION_DOWN;
					StartX = x;
					StartY = y;
					// 获取相对View的坐标，即以此View左上角为原点
					mTouchStartX = event.getX();
					mTouchStartY = event.getY();
					Log.i("lK", "startP"+"startX" + mTouchStartX + "====startY"
							+ mTouchStartY);// 调试信息
					break;
				case MotionEvent.ACTION_MOVE:
					state = MotionEvent.ACTION_MOVE;
					updateViewPosition();
					break;

				case MotionEvent.ACTION_UP:
					state = MotionEvent.ACTION_UP;

					updateEndViewPosition();
					showImg();
					mTouchStartX = mTouchStartY = 0;
					break;
				}
				return true;
			}
		});

		iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent serviceStop = new Intent();
				serviceStop.setClass(FloatService.this, FloatService.class);
				stopService(serviceStop);
			}
		});

	}

	public void showImg() {
		if (Math.abs(x - StartX) < 1.5 && Math.abs(y - StartY) < 1.5
				&& !iv.isShown()) {
			iv.setVisibility(View.VISIBLE);
		} else if (iv.isShown()) {
			iv.setVisibility(View.GONE);
		}
	}

	private Handler handler = new Handler();
	private Runnable task = new Runnable() {
		public void run() {
			// TODO Auto-generated method stub
//			dataRefresh();
			handler.postDelayed(this, delaytime);
			wm.updateViewLayout(view, wmParams);
		}
	};

	public void dataRefresh() {
//		tx.setText("" + memInfo.getmem_UNUSED(this) + "KB");
//		tx1.setText("" + memInfo.getmem_TOLAL() + "KB");
	}

	private void updateViewPosition() {
		// 更新浮动窗口位置参数
		wmParams.x = (int) (x - mTouchStartX);
		wmParams.y = (int) (y - mTouchStartY);
		wm.updateViewLayout(view, wmParams);
	}

	private void updateEndViewPosition(){
		wmParams.x = (int) (x - mTouchStartX);
		wmParams.y = (int) (y - mTouchStartY);
		if(wmParams.x<MainActivity.phonePoint.x/2){
			wmParams.x = 0;
		}else{
			wmParams.x = MainActivity.phonePoint.x;
		}
		wm.updateViewLayout(view, wmParams);
	}
	@Override
	public void onStart(Intent intent, int startId) {
		Log.d("FloatService", "onStart");
//		setForeground(true);
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		handler.removeCallbacks(task);
		Log.d("FloatService", "onDestroy");
		wm.removeView(view);
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}	
}
