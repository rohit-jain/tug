package com.example.myfirstapp;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ChatHeadService extends Service {

  private WindowManager windowManager;
  private ImageView chatHead;
  private RelativeLayout relativeLayout;
  private Button button1;
  
  @Override public IBinder onBind(Intent intent) {
    // Not used
    return null;
  }

  @Override public void onCreate() {
    super.onCreate();
    
    windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
 // Creating a new RelativeLayout
    relativeLayout = new RelativeLayout(this);
    RelativeLayout.LayoutParams relparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    relparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
    RelativeLayout.LayoutParams relparams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    relparams1.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
    relparams1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
    button1=new Button(this);
    button1.setLayoutParams(relparams);
    button1.setText("Close");
    button1.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
    button1.setOnClickListener(handleOnClick(button1));
    chatHead = new ImageView(this);
    //ImageView image = (ImageView) findViewById(R.id.up);
    chatHead.setImageResource(R.drawable.up2);
    chatHead.setAlpha((float)0.5);
    //chatHead.setScaleType(ImageView.ScaleType.CENTER_CROP);

    WindowManager.LayoutParams params = new WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.TYPE_PHONE,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT);

    params.gravity = Gravity.TOP | Gravity.LEFT;
    params.x = 0;
    params.y = 0;
    relativeLayout.addView(chatHead,relparams);
    relativeLayout.addView(button1,relparams1);
    windowManager.addView(relativeLayout, params);
  }
  
  View.OnClickListener handleOnClick(final Button button) {
	    return new View.OnClickListener() {
	        public void onClick(View v) {
	        	Log.v("myapp","close");
		        stopService(new Intent(getBaseContext(), ChatHeadService.class));
	        }
	    };
	}
  
  @Override
  public void onDestroy() {
    super.onDestroy();
    

    //Toast.makeText(getApplicationContext(),"Turned off" ,Toast.LENGTH_LONG).show();
    if (chatHead != null) relativeLayout.removeView(chatHead);
    if (chatHead != null) windowManager.removeView(relativeLayout);
  }
  	
}
