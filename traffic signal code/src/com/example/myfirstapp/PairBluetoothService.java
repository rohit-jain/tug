package com.example.myfirstapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

public class PairBluetoothService extends Service{
	
	private BluetoothAdapter BA;
	private Set<BluetoothDevice>pairedDevices;
	//private ArrayAdapter mArrayAdapter;
	private ListView lv;
	//private static final String ACTION="android.bluetooth.device.action.FOUND";
	private BroadcastReceiver mReceiver;
	private static final UUID MY_UUID =  UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private int mState;
	private Handler mHandler;
	private final IBinder mIBinder = new LocalBinder();
	private AcceptThread at;
	private ConnectedThread ctd;
    private BluetoothSocket connectedSocket;
    //private BluetoothDevice connectedDevice;
    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    /*
    public PairBluetoothService(Context context, Handler handler) {
        BA = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
    }
	*/
	@Override public IBinder onBind(Intent intent) {
	    // Not used
	    return mIBinder;
	  }
	
	public class LocalBinder extends Binder
	{
	    public PairBluetoothService getInstance()
	    {
	        return PairBluetoothService.this;
	    }
	}

	public void setHandler(Handler handler)
	{ 
	   mHandler = handler;
	}

	  @Override public void onCreate() {
	    super.onCreate();
	    if(BA==null)
	    {
	    	BA = BluetoothAdapter.getDefaultAdapter();
	    }
        mState = STATE_NONE;
        //mHandler = handler;
        Toast.makeText(getApplicationContext(),"Tugging Activated" ,Toast.LENGTH_SHORT).show();
		on();

	  }

	  public void on(){
	      if (!BA.isEnabled()) {
	    	  Boolean b=BA.enable();
			  Log.v("myApp", "turning on");
			  b=true;
	    	  while(b)
	    		 {
	    		  if(BA.getState() == BluetoothAdapter.STATE_ON)
	    		  {
    		      Log.v("myapp","bluetooth on");

	    		  /*
	    		  Boolean a=BA.startDiscovery();
	    		  Log.v("myApp", ""+BA.getState()+BA.isEnabled()+b);
	    		  pairedDevices = BA.getBondedDevices();
	    		  Log.v("myApp", ""+pairedDevices.size());

		    	  ArrayList list = new ArrayList();
		          for(BluetoothDevice bt : pairedDevices)
		          {   list.add(bt.getName());
		          	Log.v("myapp",""+bt.getName());
		          //Toast.makeText(getApplicationContext(),bt.getName(),Toast.LENGTH_SHORT).show();
		          }
		          */
		          connectServer();
		          //connectClient();
		         		          
		          b=false;
	    		  }
	    		  
	    		 }
	      }
	      else{
		    	Log.v("myapp","bluetooth already on");

	         
	         connectServer();
	         }
	   }
	  
	  public void connectServer() {
	      ConnectionTaskServer task = new ConnectionTaskServer();
	      task.execute();
	  }
	 
	  class ConnectionTaskServer extends AsyncTask<Void,Void,Void> {
		    
		    //private final WeakReference<View> viewReference;

		    public ConnectionTaskServer() {
		        //viewReference = new WeakReference<View>(view);
		    }

		    @Override
		    // Actual download method, run in the task thread
		    protected Void doInBackground(Void... params) {
		         // params comes from the execute() call: params[0] is the url.
		    	 at=new AcceptThread();
		         at.run();
		         return null;
		    }

		    @Override
		    // Once the image is downloaded, associates it to the imageView
		    protected void onPostExecute(Void results) {
		    	Log.v("myapp","taskexecuted");
		    	return;
		    }
		}
	  

	  
	  private class AcceptThread extends Thread {
		    private final BluetoothServerSocket mmServerSocket;
		 
		    public AcceptThread() {
		        // Use a temporary object that is later assigned to mmServerSocket,
		        // because mmServerSocket is final
		    	Log.v("myapp","accepting now");
		        BluetoothServerSocket tmp = null;
		        try {
		            // MY_UUID is the app's UUID string, also used by the client code
		            tmp = BA.listenUsingInsecureRfcommWithServiceRecord("myapplication", MY_UUID);
		        } catch (IOException e) { }
		        mmServerSocket = tmp;
		    }
		 
		    public void run() {
		        BluetoothSocket socket = null;
		        // Keep listening until exception occurs or a socket is returned
		        while (true) {
		            try {
		            	Log.v("myapp","Listening");
		            	while(socket==null)
		                {socket = mmServerSocket.accept();}
		            } catch (IOException e) {
		            	Log.v("myapp","Error in Listening");

		            	break;
		            }
		            // If a connection was accepted
		            if (socket != null) {
		            	Log.v("myapp","connection accepted");
		                // Do work to manage the connection (in a separate thread)
		                connectedServer(socket);
		                try {
		                	Log.v("myapp","close connection");
							mmServerSocket.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Log.v("myapp",e.toString());
						}
		                break;
		            }
	            	Log.v("myapp","no request");

		        }
		    }
		 
		    /** Will cancel the listening socket, and cause the thread to finish */
		    public void cancel() {
		        try {
		        	Log.v("myapp","closing socket");
		            mmServerSocket.close();
		        } catch (IOException e) { }
		    }
		}
	  
	  private void connectedServer(BluetoothSocket socket)
	  {
		   ctd=new ConnectedThread(socket);
		   //connectedSocket=socket;
		  ctd.run();
		  
	  }
	  
	  
	  private void reEstablish()
	  {
		  //if(connectedSocket!=null)
		  //{
		  connectedSocket=null;
			  ctd.cancel();
			  at.cancel();
			  Log.v("myapp","reEstablishing input stream");
			  at=new AcceptThread();
		      at.run();
			  //ctd=new ConnectedThread(connectedSocket);
			  //ctd.run();
		  //}
	  }
	  
	  private class ConnectedThread extends Thread {
		    private final BluetoothSocket mmSocket;
		    private final InputStream mmInStream;
		    private final OutputStream mmOutStream;
		 
		    public ConnectedThread(BluetoothSocket socket) {
		        mmSocket = socket;
		        InputStream tmpIn = null;
		        OutputStream tmpOut = null;
		 
		        // Get the input and output streams, using temp objects because
		        // member streams are final
		        try {
		            tmpIn = socket.getInputStream();
		            tmpOut = socket.getOutputStream();
		        } catch (IOException e) { }
		 
		        mmInStream = tmpIn;
		        mmOutStream = tmpOut;
		    }
		 
		    public void run() {
		    	Log.v("myapp","managing connection");
		    	ListeningTaskServer lts=new ListeningTaskServer();
		    	lts.execute();
		        		    }
			  class ListeningTaskServer extends AsyncTask<Void,Void,Void> {
				    
				    //private final WeakReference<View> viewReference;

				    public ListeningTaskServer() {
				        //viewReference = new WeakReference<View>(view);
				    }

				    @Override
				    // Actual download method, run in the task thread
				    protected Void doInBackground(Void... params) {
				         // params comes from the execute() call: params[0] is the url.
				    	byte[] buffer = new byte[1024];  // buffer store for the stream
				        int bytes; // bytes returned from read()
				 
				        // Keep listening to the InputStream until an exception occurs
				        while (true) {
				            try {
				                // Read from the InputStream
				            	if(mmInStream.available()>0)
				                {
				            		bytes = mmInStream.read(buffer);
						    	Log.v("myapp","reading"+bytes+new String(buffer));
						    	Log.v("myapp","showing alert");
						    	startService(new Intent(getBaseContext(), ChatHeadService.class));
						    	Log.v("myapp","back in reading loop");
				                }
				            	else
				            	{
				            		Log.v("myapp","Sleep - Stream not available");
				            		SystemClock.sleep(100);
				            		Log.v("myapp","Awake");
				            		//reEstablish();
				            	}
				                // Send the obtained bytes to the UI activity
				                //Toast.makeText(getApplicationContext(),new String(buffer) ,Toast.LENGTH_LONG).show();

				                //mHandler.obtainMessage(MainActivity.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
				            } catch (IOException e) {
				                break;
				            }
				        }
	 
				         return null;
				    }

				    @Override
				    // Once the image is downloaded, associates it to the imageView
				    protected void onPostExecute(Void results) {
				    	Log.v("myapp","taskexecuted");
				    	return;
				    }
				}
		    /* Call this from the main activity to send data to the remote device */
		    public void write(byte[] buffer) {
		        try {
		            mmOutStream.write(buffer);
		            Toast.makeText(getApplicationContext(),new String(buffer) ,Toast.LENGTH_LONG).show();
		            //mHandler.obtainMessage(MainActivity.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
		        } catch (IOException e) { }
		    }
		 
		    /* Call this from the main activity to shutdown the connection */
		    public void cancel() {
		        try {
		            mmSocket.close();
		        } catch (IOException e) { }
		    }
		}

	  
	  	  
	  @Override
	  public void onDestroy() {
	    super.onDestroy();
	    //Log.v("myapp","destroying bluetooth activity");
	    try {
	    	if(mReceiver!=null)
			unregisterReceiver(mReceiver);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	    try{
		    BA.disable();
	    }
	    catch (Exception e) 
	    {
	    	e.printStackTrace();
	    }
	    try{
		    if(at!=null)
	    	{at.cancel();}
		}
	    catch (Exception e) 
	    {
	    	e.printStackTrace();
		}
	    
	    try{
		    if(ctd!=null)
		    	{ctd.cancel();}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	    //Toast.makeText(getApplicationContext(),"Destroying bluetooth service" ,Toast.LENGTH_SHORT).show();
	   
	  }

} 