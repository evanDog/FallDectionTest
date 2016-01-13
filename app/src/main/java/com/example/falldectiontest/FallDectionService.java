package com.example.falldectiontest;


import java.util.Random;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class FallDectionService extends Service 
{
	SensorManager sensorManager;
	Sensor sensor;
    static float[] values = new float[3];
    static float[] init = new float[] { 0.0f, 0.0f, 0.0f };//初始化三軸數值矩陣
    static float x, y, z;
	long lastUpdateTime = 0;
	
	private String fallPlace;
	private int randomPlace;
	private Random randomNumber;
	private String place[] = {"2E0E39C6","3E1C3AC6","4E827BEC","4E5A7BEC","43F508C7",
			"B34D09C7","EEA37BEC","EEC47BEC","AE037DEC","AE737BEC","7E7839C6","5E9A78EC",
			"CEB67AEC","3EFB7BEC","7EFD37C6"};
	
	//private Handler handler = new Handler();
	private NotificationManager myNotiManager;
	Bundle bundle = new Bundle();
	String infoText = "";
	Intent intentThis = new Intent();
	
	private MediaPlayer player;
	
	private int count;
	private String alert = "系統偵測到您跌倒了，系統將在十五秒後傳送跌倒訊息至病人安全監控中心，若是誤會，請按取消鍵\n";
	private String remainTime = "剩餘時間：";
	private String second = "秒";
	private String fallCheck ="No";
	private String fallDetector = "Yes";
	AlertDialog.Builder dialog;
	AlertDialog mDialog;
	String toast;
	Toast timerNotice;
	CountDownTimer myCount;
	
	@Override
	public void onCreate() {
		Log.d("2", "onCreate()");
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public void onDestroy() 
	{
		Log.d("3", "onDestroy()");
		// TODO Auto-generated method stub
		sensorManager.unregisterListener(myAccelerometerListener);
		setNotification("跌倒偵測系統已關閉");
		//下面這個不要
		//handler.removeCallbacks(showInfo);
		
		super.onDestroy();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("4", "onStartCommand()");
		// TODO Auto-generated method stub
		initSensor();
		setNotification("跌倒偵測系統已啟動");
		//player = MediaPlayer.create(this, R.raw.alert);
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}
	
	@Override
	public IBinder onBind(Intent arg0) 
	{
		Log.d("5", "onBind()");
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public void initSensor()
	{
		Log.d("7", "initSensor()");
		sensorManager =	(SensorManager)getSystemService( SENSOR_SERVICE );
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(myAccelerometerListener, sensor,
                SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	SensorEventListener myAccelerometerListener = new SensorEventListener(){ 
        
        //复写onSensorChanged方法 
        public void onSensorChanged(SensorEvent sensorEvent)
        {
        	Log.d("8", "onSensorChanged()");

            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) 
            {
            	
                if (init[0] == 0 && init[1] == 0 && init[2] == 0) 
                {
                	Log.d("8-1", "second if");
                	//若init陣列值為零，把偵測到的三軸值指定給init陣列
                    init[0] = sensorEvent.values[0];
                    init[1] = sensorEvent.values[1];
                    init[2] = sensorEvent.values[2];
                }
                //若init陣列值不為零，把偵測到的值指定給values陣列
                Log.d("8-2", "first if");
                values = sensorEvent.values;
                //得到加速度值的變化量
                x = init[0] - values[0];
                y = init[1] - values[1];
                z = init[2] - values[2];
            }
            //輸出抓取到的三軸
            //Log.d("service", Float.toString(x));
            
            
            long currentUpdateTime = System.currentTimeMillis(); // 取得目前系統時間
            long timeInterval = currentUpdateTime - lastUpdateTime; // 變化時間 = 目前系統時間-lastUpdateTime(值為0)
            
            if (timeInterval < 300) // 判斷目前時間是否小於300毫秒
            {
            	Log.d("8-3", "timeInterval < 300");
            	lastUpdateTime = currentUpdateTime; // 把目前時間指定給lastUpdateTime
            }
            else 
            {
            	Log.d("8-4", "timeInterval > 300");
            	//當加速度值超過閥值
                if (Math.sqrt(Math.pow(x,2)+Math.pow(y,2)+Math.pow(z,2)) > 15 && fallDetector == "Yes") 
                {
                	fallDetector = "No";//暫時停止偵測跌倒
                	fallCheck = "No";//目前使用者未按下取消警告視窗按鈕
                	count = 15;
                	mediaPlayerCreater();
                    player.start();
                    
                    //亂數產生跌倒地點
                    //randomPlace = randomNumber.nextInt(14);
                   // fallPlace = place[randomPlace];
                    Log.d("8-5", "callDialog");
                    
                    //跳出警告視窗(全局通告)
                    dialog = new AlertDialog.Builder(FallDectionService.this);
                	dialog.setTitle("警告");
                	dialog.setMessage(alert);
                	dialog.setIcon(android.R.drawable.ic_dialog_info);
                	dialog.setCancelable(false);
                	dialog.setNeutralButton("取消警鈴", new DialogInterface.OnClickListener() 
                	{					
    					@Override
    					public void onClick(DialogInterface dialog, int which) 
    					{
    						// TODO Auto-generated method stub
    						player.stop();
    						fallCheck ="Yes";//確認按下關閉按鈕
    						fallDetector = "Yes";//重新開始跌倒偵測
    						myCount.cancel();//取消倒數計時
    						
    						//暫時性的防止重複挑出對話框的防錯機制
    						//Intent intent = new Intent(FallDectionService.this, FallDectionService.class);
    						//stopService(intent);
    						
    						Log.d("8-4-1","callStopService");
    						
    						//Intent intent = new Intent(FallDectionService.this, FallDectionService.class);
    						//sensorManager.unregisterListener(myAccelerometerListener);
    						//stopService(intent);
    						//Log.d("8-4-1","endcallStopService");
    						
    					}
    				});
                	
                	mDialog=dialog.create();  
                    mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//设定为系统级警告，关键  
                    mDialog.show();
                    
                    
                    
                	//倒數計時開始
                    myCount = new CountDownTimer(15000,1000){
                        
                        @Override
                        public void onFinish() 
                        {
                            // TODO Auto-generated method stub
                        	
                        	//fallCheck為No代表使用者未按下關閉視窗按鈕                        
                        	if(fallCheck == "No")
                        	{
                        		//送出資料至server程式寫在這裡
                        		
                        		//儲存跌倒地點的字串為fallPlace
                        		
                        		//顯示跌倒訊息已送出
                        		mDialog.dismiss();
                        		dialog.setMessage("已將跌倒訊息送出");
                        		mDialog=dialog.create();  
                        		mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//设定为系统级警告，关键  
                        		mDialog.show();
                        		Log.d("timer","onFinish sent");
                        	}
                        	
                        	
                        	
                        	Log.d("timer","onFinish");
                        }

                        @Override
                        public void onTick(long millisUntilFinished) 
                        {
                            // TODO Auto-generated method stub
                            
                            if(fallCheck == "No")
                            {
                            	count--;
                            	toast = remainTime+count+second;
                            	timerNotice = Toast.makeText(FallDectionService.this, toast, Toast.LENGTH_SHORT); 
                            	timerNotice.show();
                            	Handler handler = new Handler();
                            	handler.postDelayed(new Runnable() 
                            	{
                            		@Override
                            		public void run() 
                            		{
                            			timerNotice.cancel(); 
                            		}
                            	}, 500);
                            }
                            Log.d("timer","onTick");
                        }
                        
                    }.start();
                	
                
                    
                   
         	

                }
                
            }
            
        } 
        //复写onAccuracyChanged方法 
        public void onAccuracyChanged(Sensor sensor , int accuracy)
        { 
            
        } 
    }; 
	
	
	
	/*用不到
    private final void register() 
    {
    	Log.d("9", "register()");
        sensorManager.registerListener(this, sensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private final void unRegister() 
    {
    	Log.d("10", "unRegister()");
    	sensorManager.unregisterListener(this);
        x = 0;
        y = 0;
        z = 0;
    }
    */
	
    public void mediaPlayerCreater()
    {
    	player = MediaPlayer.create(this, R.raw.alert);
    }
    
    //setNotification方法
	public void setNotification(String s)
	{
		Log.d("11", "setNotification()");
	    myNotiManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	    
	    setNotiType(R.drawable.ic_launcher, s);
	}
	
	//與在手機上顯示三軸值有關的方法
	private void setNotiType(int iconId, String text)
	{
		
		Log.d("12", "setNotiType()");
	    Intent notifyIntent=new Intent(this,MainActivity.class);  
	    notifyIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
	     
	    PendingIntent appIntent=PendingIntent.getActivity(this,0,
	                                                      notifyIntent,0);
	    
	    Notification myNoti=new Notification();
	    
	    myNoti.icon=iconId;
	    
	    myNoti.tickerText=text;
	    
	    myNoti.defaults=Notification.DEFAULT_SOUND;
	    
	    myNoti.setLatestEventInfo(this,"Event Selected!",text,appIntent);
	    
	    myNotiManager.notify(0,myNoti);
	 }


}
