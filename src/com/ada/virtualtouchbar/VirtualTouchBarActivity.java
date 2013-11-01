/**
 * 
 */
/**
 * @author mikan
 *
 */


package com.ada.virtualtouchbar;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.AlertDialog;
import android.content.DialogInterface;


public class VirtualTouchBarActivity extends Activity 
{
//	//
//	LinearLayout mFloatLayout;
	//创建主窗口
	WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
    WindowManager mWindowManager;

    //** Called when the activity is first created. 
   
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //创建按钮
        Button mcreate = (Button)findViewById(R.id.create_id);
        
        //销毁按钮
        Button mremove = (Button)findViewById(R.id.remove_id);

        Button mabout = (Button)findViewById(R.id.about_id);
        
        

        
        //创建按钮绑定监听
        mcreate.setOnClickListener(new OnClickListener() 
        {
			
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				Intent intent = new Intent(VirtualTouchBarActivity.this, TouchBarService.class);
				startService(intent);
				finish();
			}
		});
        
        mremove.setOnClickListener(new OnClickListener() 
        {
			
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(VirtualTouchBarActivity.this, TouchBarService.class);
				stopService(intent);
			}
		});
        
        mabout.setOnClickListener(new OnClickListener() 
        {
			
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
			    
			    //关于
			    AlertDialog.Builder builder;
				builder = new AlertDialog.Builder(VirtualTouchBarActivity.this);
		        builder.setTitle("关于触控")    //标题
		            .setMessage("程序: 触控\n"
		            		+ "作者: Mikan\n"
		            		+ "邮箱: 782381791@QQ.com"     /// 		+ "公司:深圳市安卓安科技有限公司\n"
		            		)    //对话框显示内容
		        	.setPositiveButton("确定",   
		       new DialogInterface.OnClickListener(){  
		       public void onClick(DialogInterface dialoginterface, int i){   
		                                         //按钮事件   
		              }   
		       }).show();

			}
		});
        
    }
    
//    private void uninstallApp(String packageName)
//    {
//    	Uri packageURI = Uri.parse("package:"+packageName);
//    	Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
//    	startActivity(uninstallIntent);
//    }
    
}