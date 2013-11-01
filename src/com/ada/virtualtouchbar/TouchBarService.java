/**
 * 
 */
/**
 * @author mikan
 *
 */
package com.ada.virtualtouchbar;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

//import com.mstar.tv.service;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.os.RemoteException;
import android.view.IWindowManager;
import android.os.ServiceManager;
import android.view.InputDevice;
import android.util.DisplayMetrics;
import android.app.Activity;

public class TouchBarService extends Service 
{
	private static final String TAG = "TouchBarService";
	//浮动窗口布局
    LinearLayout mFloatLayout;
    LinearLayout showlistbar;
    LinearLayout showbackbar;
    WindowManager.LayoutParams wmParams;
    //浮动窗口设置布局的参数对象
	WindowManager mWindowManager;
	
	Button mFloatView;
	Button mVolumedown;
	Button mVolumeup;
	Button mChanneldown;
	Button mChannelup;
	Button mMute;
	Button mBack;
	
	long mDownTime;
    int mCode;
    int mTouchSlop;
	
	private IWindowManager mWinManager = null;
	
	public Context mContext;
	
	Handler handler=new Handler();
	Runnable runnable=new Runnable(){
	@Override
	public void run() {
	// TODO Auto-generated method stub
		mFloatView.setText("触控");
		showlistbar.setVisibility(View.GONE);		///隐藏	
		showbackbar.setVisibility(View.GONE);
//        mVolumedown.setVisibility(Button.INVISIBLE);
//        mVolumeup.setVisibility(Button.INVISIBLE);
//		mChanneldown.setVisibility(Button.INVISIBLE);
//		mChannelup.setVisibility(Button.INVISIBLE);
//		mMute.setVisibility(Button.INVISIBLE);
//		mBack.setVisibility(Button.INVISIBLE);

		handler.removeCallbacks(runnable);	//执行一次后停止
		}
	};

	
	
	@Override
	public void onCreate() 
	{
		// TODO Auto-generated method stub
		super.onCreate();
		Log.i(TAG, "oncreat");
		createFloatView();
        //Toast.makeText(TouchBarService.this, "create TouchBarService", Toast.LENGTH_LONG);		
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO Auto-generated method stub
		return null;
	}

	private void createFloatView()
	{
		wmParams = new WindowManager.LayoutParams();
		//获取的是WindowManagerImpl.CompatModeWrapper
		mWindowManager = (WindowManager)getApplication().getSystemService(getApplication().WINDOW_SERVICE);
		//设置window type 
		wmParams.type = LayoutParams.TYPE_PHONE; 
		//设置图片格式，效果为背景透明 
        wmParams.format = PixelFormat.RGBA_8888; 
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶 
        wmParams.gravity = Gravity.LEFT | Gravity.TOP; 
        
        DisplayMetrics metric = new DisplayMetrics();
        
        mWindowManager.getDefaultDisplay().getMetrics(metric);
        
        int bar_width = metric.widthPixels;
        int bar_height = metric.heightPixels;
    
        wmParams.x = bar_width - 100;  // 屏幕宽度（像素）
        wmParams.y = bar_height/2 - 40;  // 屏幕高度（像素）

        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局 
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_layout, null);
        //添加mFloatLayout 
        mWindowManager.addView(mFloatLayout, wmParams);
        
        Log.i(TAG, "mFloatLayout-->left" + mFloatLayout.getLeft());
        Log.i(TAG, "mFloatLayout-->right" + mFloatLayout.getRight());
        Log.i(TAG, "mFloatLayout-->top" + mFloatLayout.getTop());
        Log.i(TAG, "mFloatLayout-->bottom" + mFloatLayout.getBottom());      
        
        //浮动窗口按钮
        mFloatView = (Button)mFloatLayout.findViewById(R.id.float_id);        
        mVolumedown = (Button)mFloatLayout.findViewById(R.id.float_volumedown);
        mVolumeup = (Button)mFloatLayout.findViewById(R.id.float_volumeup);
        mChanneldown= (Button)mFloatLayout.findViewById(R.id.float_channeldown);
        mChannelup= (Button)mFloatLayout.findViewById(R.id.float_channelup);
        mMute= (Button)mFloatLayout.findViewById(R.id.float_mute);
        mBack= (Button)mFloatLayout.findViewById(R.id.float_back);
        
//        mVolumedown.setVisibility(Button.INVISIBLE);
//        mVolumeup.setVisibility(Button.INVISIBLE);
//		mChanneldown.setVisibility(Button.INVISIBLE);
//		mChannelup.setVisibility(Button.INVISIBLE);
//		mMute.setVisibility(Button.INVISIBLE);
//		mBack.setVisibility(Button.INVISIBLE);
		
		showlistbar=(LinearLayout)mFloatLayout.findViewById(R.id.listbar);
		showlistbar.setVisibility(View.GONE);		//首先隐藏
		
		showbackbar=(LinearLayout)mFloatLayout.findViewById(R.id.backbar);
		showbackbar.setVisibility(View.GONE);		//首先隐藏
        
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        
        Log.i(TAG, "Width/2--->" + mFloatView.getMeasuredWidth()/2);
        Log.i(TAG, "Height/2--->" + mFloatView.getMeasuredHeight()/2);

        
        //设置监听浮动窗口的触摸移动 
        mFloatView.setOnTouchListener(new OnTouchListener() 
        {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{
				// TODO Auto-generated method stub
				//getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
				if(wmParams.x <= (int) event.getRawX() 
						&& (int) event.getRawX() <= wmParams.x + 100 
						&& wmParams.y <= (int) event.getRawY() 
						&& (int) event.getRawY() <= wmParams.y + 80)
					;	///在按钮的范围内点击，不变化
				else
				{
					wmParams.x = (int) event.getRawX(); // - mFloatView.getMeasuredWidth()/2 //2013-09-29
					wmParams.y = (int) event.getRawY();// - (mFloatView.getMeasuredHeight()*3) - 15;// - mFloatView.getMeasuredHeight()/2 - 25
		             //刷新 
		            mWindowManager.updateViewLayout(mFloatLayout, wmParams);//2013-09-29
				}
				return false;//此处必须返回false，否则OnClickListener获取不到监听
			}
		});	
              
        mFloatView.setOnClickListener(new OnClickListener() 
        {
			
			@Override
			public void onClick(View v) 
			{
				
				if(showlistbar.getVisibility()==View.GONE) //查看现在隐身与否
				{
					
					showlistbar.setVisibility(View.VISIBLE);	///则显示
					showbackbar.setVisibility(View.VISIBLE);
					mFloatView.setText("返回");
					handler.postDelayed(runnable, 15000);	//15 秒后执行隐藏
				}
				else
				{
					showlistbar.setVisibility(View.GONE);		///则隐藏	
					showbackbar.setVisibility(View.GONE);
					mFloatView.setText("触控");
					handler.removeCallbacks(runnable);	
				}
				// TODO Auto-generated method stub
//				if (mFloatView.getText() == "-")
//				{ 		//此处隐藏
//					mFloatView.setText("~");
//			        mVolumedown.setVisibility(Button.INVISIBLE);
//			        mVolumeup.setVisibility(Button.INVISIBLE);
//					mChanneldown.setVisibility(Button.INVISIBLE);
//					mChannelup.setVisibility(Button.INVISIBLE);
//					mMute.setVisibility(Button.INVISIBLE);
//					mBack.setVisibility(Button.INVISIBLE);
//					handler.removeCallbacks(runnable);	//手动还原马上停止线程
//				}else
//				{		///此处进行显示
//					mFloatView.setText("-");
//					mVolumedown.setVisibility(Button.VISIBLE);
//					mVolumeup.setVisibility(Button.VISIBLE);
//					mChanneldown.setVisibility(Button.VISIBLE);
//					mChannelup.setVisibility(Button.VISIBLE);
//					mMute.setVisibility(Button.VISIBLE);
//					mBack.setVisibility(Button.VISIBLE);
//					handler.postDelayed(runnable, 15000);	//8 秒后执行隐藏
//				}
				//Toast.makeText(TouchBarService.this, "onClick", Toast.LENGTH_SHORT).show();
			}
		});
        
        mVolumedown.setOnClickListener(new OnClickListener() 
        {
			
			@Override
			public void onClick(View v)
			{
				handler.removeCallbacks(runnable);	
				//发送音量减键值
				msendkeyEvent(KeyEvent.KEYCODE_VOLUME_DOWN);
//                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//                audioManager.adjustSuggestedStreamVolume(
//                    AudioManager.ADJUST_LOWER,
//                    AudioManager.STREAM_MUSIC,
//                    AudioManager.FLAG_SHOW_UI | AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
				//Toast.makeText(TouchBarService.this, "onClick volume-", Toast.LENGTH_SHORT).show();
			}
        });
        
        mVolumeup.setOnClickListener(new OnClickListener() 
        {
			
			@Override
			public void onClick(View v)
			{
				handler.removeCallbacks(runnable);	
				//发送音量加键值
				msendkeyEvent(KeyEvent.KEYCODE_VOLUME_UP);
//	        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//	        audioManager.adjustSuggestedStreamVolume(
//	        AudioManager.ADJUST_RAISE,
//	        AudioManager.STREAM_MUSIC,
//	        AudioManager.FLAG_SHOW_UI | AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
				//Toast.makeText(TouchBarService.this, "onClick volume+", Toast.LENGTH_SHORT).show();
			}
        });
        
        mChanneldown.setOnClickListener(new OnClickListener() 
        {
			
			@Override
			public void onClick(View v)
			{
				handler.removeCallbacks(runnable);	
				//发送频道减键值
				msendkeyEvent(KeyEvent.KEYCODE_CHANNEL_DOWN);
			}
        });
        
        mChannelup.setOnClickListener(new OnClickListener() 
        {
			
			@Override
			public void onClick(View v)
			{
				handler.removeCallbacks(runnable);	
				//发送频道加键值
				//tosendEvent(KeyEvent.KEYCODE_CHANNEL_UP, 166);
				msendkeyEvent(KeyEvent.KEYCODE_CHANNEL_UP);
			}
        });     
        
        mMute.setOnClickListener(new OnClickListener() 
        {
			
			@Override
			public void onClick(View v)
			{
				handler.removeCallbacks(runnable);	
				//发送静音键值
				msendkeyEvent(KeyEvent.KEYCODE_VOLUME_MUTE);
			}
        });    
        
        mBack.setOnClickListener(new OnClickListener() 
        {
			
			@Override
			public void onClick(View v)
			{
				handler.removeCallbacks(runnable);	
				//发送退出键值
				msendkeyEvent(KeyEvent.KEYCODE_BACK);
			}
        });
	}

	private void msendkeyEvent(int keyCode){
		int eventCode = keyCode;
		long now = SystemClock.uptimeMillis();
				try{
					KeyEvent down = new KeyEvent(now, now, KeyEvent.ACTION_DOWN, eventCode, 0);
					KeyEvent up = new KeyEvent(now, now, KeyEvent.ACTION_UP, eventCode, 0);  
					 (IWindowManager.Stub
							 .asInterface(ServiceManager.getService(Context.WINDOW_SERVICE)))  
							 .injectInputEventNoWait(down);
					 (IWindowManager.Stub
							 .asInterface(ServiceManager.getService(Context.WINDOW_SERVICE)))
							 .injectInputEventNoWait(up);
				}catch(RemoteException e){
					 Log.i(TAG, "DeadOjbectException");  
				}
				handler.postDelayed(runnable, 5000);	//5 秒后执行隐藏
	}

	@Override
	public void onDestroy() 
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		if(mFloatLayout != null)
		{
			//移除悬浮窗口 
			mWindowManager.removeView(mFloatLayout);
		}
	}
	
}
