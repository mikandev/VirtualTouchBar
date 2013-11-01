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

public class BootVirtualTouchBarActivity extends Activity 
{
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    
		Intent intent = new Intent(BootVirtualTouchBarActivity.this, TouchBarService.class);
		startService(intent);
		this.finish();
  
    }
       
}