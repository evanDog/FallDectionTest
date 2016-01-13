package com.example.falldectiontest;

import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.util.Log;

public class MainActivity extends Activity {
	
	private FallDectionService fallDectionService = null;
	Button startButton, stopButton;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.d("mainActivity","onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViewComponent();
        
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void setupViewComponent()
    {
    	startButton = (Button) findViewById(R.id.btn_start);
    	startButton.setOnClickListener(startClickListener);
    	
    	stopButton = (Button) findViewById(R.id.btn_stop);
    	stopButton.setOnClickListener(stopClickListener);
    	
    }
    
    
    private Button.OnClickListener startClickListener = new Button.OnClickListener() 
	{
		
		public void onClick(View arg0) 
		{
	    	Log.d("1-1", "startService");
			//啟動服務
			Intent intent = new Intent(MainActivity.this, FallDectionService.class);
			startService(intent);
		}
	};
    
	private Button.OnClickListener stopClickListener = new Button.OnClickListener() 
	{
		
		public void onClick(View arg0) 
		{
	    	Log.d("1-2", "stopService");
			//啟動服務
			Intent intent = new Intent(MainActivity.this, FallDectionService.class);
			stopService(intent);
		}
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		Log.d("2-1", "onDestroy()");
		Intent intent = new Intent(MainActivity.this, FallDectionService.class);
		stopService(intent);
		
		super.onDestroy();
	}
	
	
    
    
}
