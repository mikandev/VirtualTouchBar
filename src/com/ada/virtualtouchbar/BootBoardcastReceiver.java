package com.ada.virtualtouchbar;

import android.content.BroadcastReceiver;

import android.content.Context;

import android.content.Intent;

public class BootBoardcastReceiver extends BroadcastReceiver {

	static final String ACTION = "android.intent.action.BOOT_COMPLETED";
	
	@Override
	
	public void onReceive(Context context, Intent intent) {
		
		if (intent.getAction().equals(ACTION))
		{
			Intent mIntent=new Intent(context,BootVirtualTouchBarActivity.class);
			mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(mIntent);

		}

	}
}