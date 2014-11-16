package com.example.myfirstapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private PairBluetoothService mService = null;
	
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;
	
	private boolean mIsBound;
	 private final Handler mHandler = new Handler() {};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //doBindService();
		//Intent i=new Intent(getBaseContext(), PairBluetoothService.class);
	    //startService(i);
		setContentView(R.layout.activity_main);
		getWindow().getDecorView().setBackgroundColor(Color.RED);
	    
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	// Method to start the service
	   public void startService(View view) {
	      //startService(new Intent(getBaseContext(), ChatHeadService.class));
		   doBindService();
		   Intent i=new Intent(getBaseContext(), PairBluetoothService.class);
		   startService(i);
   	       

	   }
	   
	   private ServiceConnection mConnection = new ServiceConnection() {
		    @Override
		    public void onServiceConnected(ComponentName componentName, IBinder iBinder)
		    {
		        mService = ((PairBluetoothService.LocalBinder)iBinder).getInstance();
		        mService.setHandler(mHandler);
		    }

		    @Override
		    public void onServiceDisconnected(ComponentName componentName)
		    {
		        mService = null;
		    }
		};
	   
	   private void doBindService()
	   {
	       // Establish a connection with the service.  We use an explicit
	       // class name because we want a specific service implementation that
	       // we know will be running in our own process (and thus won't be
	       // supporting component replacement by other applications).
	       bindService(new Intent(this,PairBluetoothService.class), mConnection, Context.BIND_AUTO_CREATE);
	       mIsBound = true;
	   }
	   
	   private void doUnbindService()
	   {
	       if (mIsBound)
	       {
	           // Detach our existing connection.
	           unbindService(mConnection);
	           mIsBound = false;
	       }
	   }
	   
	// Method to stop the service
	   public void stopService(View view) {
	       // stopService(new Intent(getBaseContext(), ChatHeadService.class));
		   Log.v("myapp","stopping service");
		   doUnbindService();
		   stopService(new Intent(getBaseContext(), PairBluetoothService.class));
	        Toast.makeText(getApplicationContext(),"Deactivated! Stay Safe!" ,Toast.LENGTH_SHORT).show();

	   }
	   
	   @Override
		protected void onDestroy()
		{
		   super.onDestroy();
		   try{
			   Log.v("myapp","Destroying Activity");
		   doUnbindService();
		   stopService(new Intent(getBaseContext(), PairBluetoothService.class));
		   }
		   catch(Exception e)
		   {
			   e.printStackTrace();
		   }
		}
}

